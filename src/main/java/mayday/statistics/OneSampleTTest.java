/*
 * Created on Dec 8, 2004
 *
 */
package mayday.statistics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.math.pcorrection.methods.None;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.math.stattest.UncorrectedStatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.statistics.TTest.TTestPlugin;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.TestUtils;

/**
 * @author gehlenbo
 *
 */
public class OneSampleTTest
extends AbstractPlugin
implements ProbelistPlugin
{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.statistics.onesamplettest",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes the one-sample t test",
		"One-Sample t test");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_STATISTICS);
		return pli;
	}
	
	protected TTestPlugin ttest;
	protected PluginTypeSetting<PCorrectionPlugin> correction;
	protected DoubleSetting mu;
	protected StringSetting mioname;
	protected BooleanHierarchicalSetting filter;
	protected DoubleSetting filterPVal;

	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )	{
		
		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);
		
		HierarchicalSetting statSetting = new HierarchicalSetting("One-sample t test").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
		.addSetting( mioname = new StringSetting("MIO Group name","","p-value"))
		.addSetting( mu = new DoubleSetting("Mean for H0", "Set the mean to test against, default=0",0d))
		.addSetting( correction = new PluginTypeSetting<PCorrectionPlugin>("Correction",null, new None(), PCorrectionPlugin.MC) )
		.addSetting( filter = new BooleanHierarchicalSetting("Create ProbeList of significant probes",null,false)
			.addSetting(filterPVal = new DoubleSetting("pValue threshold",null, 0.05, 0d,1d,true,true)) )
		;

		
		Settings s = new Settings(statSetting, PluginInfo.getPreferences("PAS.statistics.onesamplettest"));		
		SettingsDialog sd = new SettingsDialog(null, "One-Sample t-test", s);
		
		sd.showAsInputDialog();
		
		if (sd.canceled())
			return null;

		HashMap<Object, double[]> conv = new HashMap<Object,double[]>();
		for (Probe probe : uniqueProbes.getAllProbes())
			conv.put(probe, probe.getValues());
		
		StatTestResult res = runTest(conv, mu.getDoubleValue());
		
		if (res==null)
			return null;
		
		MIManager mim = masterTable.getDataSet().getMIManager();
		res.getPValues().setName( mioname.getStringValue() );
		mim.addGroup(res.getPValues());
		
		if (res.getRawScore()!=null)
			mim.addGroupBelow(res.getRawScore(), res.getPValues());

		MIGroup filterGroup = res.getPValues();
		
		for (MIGroup mg : res.getAdditionalValues())
			mim.addGroupBelow(mg, res.getPValues());
		
		PCorrectionPlugin pcc = correction.getInstance();
		if (pcc.getClass()!=None.class) {
			mim.addGroupBelow(filterGroup = pcc.correct(res.getPValues()), res.getPValues());
		}
		
		if (filter.getBooleanValue()) {
			double thresh = filterPVal.getDoubleValue();
			ProbeList pl = new ProbeList(masterTable.getDataSet(), true);
			for (Probe pb : uniqueProbes) {
				MIType mt = filterGroup.getMIO(pb);
				if (mt!=null) {
					if (((DoubleMIO)mt).getValue()<thresh)
						pl.addProbe(pb);
				}
			}
			pl.setName("Significant probes "+mioname.getStringValue());
			return Arrays.asList(new ProbeList[]{pl});
		}
		
		return null;
	}
	
	public StatTestResult runTest(Map<Object, double[]> values, double mu) {

		UncorrectedStatTestResult res = new UncorrectedStatTestResult();
		
		TTest tTest= new TTest();
		
		// transform the data into a form that is much easier to work with for RP
		Map<Object, Integer> indexMap = new TreeMap<Object, Integer>();		
		PermutableMatrix data = Algebra.matrixFromMap(values, indexMap);			
		int num_gene = values.size();
		
		double[] tvals = new double[num_gene];
		double[] pvals = new double[num_gene];
		
		for (int i=0; i!=num_gene; ++i) {
			AbstractVector v1 = data.getRow(i);
			try {
				tvals[i] = tTest.t(mu, v1.toArrayUnpermuted());
				pvals[i] = tTest.tTest(mu, v1.toArrayUnpermuted());
			} catch (Exception e) {
				tvals[i] = Double.NaN;
				pvals[i] = Double.NaN;
			}
		}
		
		res.initRawScore();
		MIGroup pgroup = res.getPValues();
		MIGroup tgroup = res.getRawScore();
		
		for (Object o : values.keySet()) {
			int i = indexMap.get(o);
			pgroup.add(o, new DoubleMIO(pvals[i]));
			tgroup.add(o, new DoubleMIO(tvals[i]));
		}
		
		return res;
	}

	@Override
	public void init() {
	}
}
