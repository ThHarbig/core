/*
 * Created on Dec 8, 2004
 *
 */
package mayday.statistics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.math.pcorrection.methods.None;
import mayday.core.math.stattest.StatTestPlugin;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.StringSetting;

/**
 * @author gehlenbo
 *
 */
public class StatisticalTest
extends AbstractPlugin
implements ProbelistPlugin
{

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.statistics.stattest",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Applies a statistical test plugin to the probe data",
		"Statistical Test");
		pli.setMenuName("\0Statistical Test (two-sample)");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_STATISTICS);
		return pli;
	}
	
	protected PluginTypeSetting<StatTestPlugin> method;
	protected PluginTypeSetting<PCorrectionPlugin> correction;
	protected ClassSelectionSetting classes;
	protected StringSetting mioname;
	protected BooleanHierarchicalSetting filter;
	protected DoubleSetting filterPVal;

	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )	{
		
		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);
		
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(StatTestPlugin.MC);
		
		HierarchicalSetting statSetting = new HierarchicalSetting("Statistical Test").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
		.addSetting( mioname = new StringSetting("MIO Group name","Group name for statistical test results","p-value"))
		.addSetting( classes = new ClassSelectionSetting( "Classes", null, new ClassSelectionModel( masterTable ), 2, 2, masterTable.getDataSet()))
		.addSetting( method = new PluginTypeSetting<StatTestPlugin>("Test",null, (StatTestPlugin)plis.iterator().next().getInstance(), plis) )
		.addSetting( correction = new PluginTypeSetting<PCorrectionPlugin>("Correction",null, new None(), PCorrectionPlugin.MC) )
		
		.addSetting( filter = new BooleanHierarchicalSetting("Create ProbeList of significant probes",null,false)
			.addSetting(filterPVal = new DoubleSetting("pValue threshold",null, 0.05, 0d,1d,true,true)) )
		;
		
		Settings s = new Settings(statSetting, PluginInfo.getPreferences("PAS.statistics.stattest"));		
		SettingsDialog sd = new SettingsDialog(null, "Statistical Testing", s);
		sd.setModal(true);
		sd.setVisible(true);
		
		if (sd.canceled())
			return null;
		
		StatTestPlugin statTestPlugin = method.getInstance();
		StatTestResult res = statTestPlugin.runTest(uniqueProbes.getAllProbes(), classes.getModel());
		
		if (res==null)
			return null;
		
		MIManager mim = masterTable.getDataSet().getMIManager();
		res.getPValues().setName( "p-value (" + statTestPlugin.getPluginInfo().getName() + ")");
		mim.addGroup(res.getPValues());
		
		MIGroup rawScore = res.getRawScore();
		
		if (rawScore != null) {
			rawScore.setName("Raw Score (Test Statistic Value)");
			mim.addGroupBelow(res.getRawScore(), res.getPValues());
		}
		
		MIGroup filterGroup = res.getPValues();
		
		for (MIGroup mg : res.getAdditionalValues())
			mim.addGroupBelow(mg, res.getPValues());
		
		PCorrectionPlugin pcc = correction.getInstance();
		if (pcc.getClass()!=None.class) {
			filterGroup = pcc.correct(res.getPValues());
			filterGroup.setName(filterGroup.getName() + " (" + statTestPlugin.getPluginInfo().getName() + ")");
			mim.addGroupBelow(filterGroup, res.getPValues());
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
			pl.setName("Significant at p<"+filterPVal.getDoubleValue()+" in "+mioname.getStringValue());
			return Arrays.asList(new ProbeList[]{pl});
		}
		
		return null;
	}

	@Override
	public void init() {
	}
}
 