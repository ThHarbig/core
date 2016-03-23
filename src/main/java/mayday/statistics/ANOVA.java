/*
 * Created on Dec 8, 2004
 *
 */
package mayday.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mayday.core.ClassSelectionModel;
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
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.StringSetting;

import org.apache.commons.math3.distribution.FDistribution;

/**
 * @author gehlenbo
 *
 */
public class ANOVA
extends AbstractPlugin
implements ProbelistPlugin
{

	
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.statistics.anova",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes a simple one-way Analysis of Variance (ANOVA)",
				"ANOVA");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_STATISTICS);
		return pli;
	}
	
	protected ClassSelectionSetting groups;
	protected StringSetting mioname;
	protected BooleanSetting addFstat;
	protected PluginTypeSetting<PCorrectionPlugin> correction;
	protected BooleanHierarchicalSetting filter;
	protected DoubleSetting filterPVal;

	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )	{
		
		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);
		
		HierarchicalSetting statSetting = new HierarchicalSetting("ANOVA").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
		.addSetting( groups = new ClassSelectionSetting("Classes",null, new ClassSelectionModel(masterTable),2,null, masterTable.getDataSet()))
		.addSetting( mioname = new StringSetting("MIO Group name","","ANOVA p-value"))
		.addSetting( addFstat = new BooleanSetting("Add F-statistic as meta information",null, false) )
		.addSetting( correction = new PluginTypeSetting<PCorrectionPlugin>("Correction",null, new None(), PCorrectionPlugin.MC) )
		.addSetting( filter = new BooleanHierarchicalSetting("Create ProbeList of significant probes",null,false)
			.addSetting(filterPVal = new DoubleSetting("pValue threshold",null, 0.05, 0d,1d,true,true)) )
		;

		
		Settings s = new Settings(statSetting, PluginInfo.getPreferences("PAS.statistics.anova"));		
		SettingsDialog sd = new SettingsDialog(null, "ANOVA", s);
		
		sd.showAsInputDialog();
		
		if (sd.canceled())
			return null;

		StatTestResult res = execute(uniqueProbes);
		
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
	

	public StatTestResult execute(ProbeList probes) {		
		
		UncorrectedStatTestResult res = new UncorrectedStatTestResult();
		
		MIGroup pVal = res.getPValues();
		
		MIGroup fVal=null;

		if (addFstat.getBooleanValue())
			fVal = res.addAdditionalValue("F-statistic");
		
		List<List<Integer>> classSelection = getClasses(groups.getModel());
		int before=classSelection.size();
		classSelection=pruneClasses(classSelection);
			
		if(before != classSelection.size()) {
			System.err.println("ANOVA: Some classes were removed since they only contained one experiment");
		}
		
		Integer[][] indices = new Integer[classSelection.size()][];
		
		for (int i=0; i!=classSelection.size(); ++i) {
			if (classSelection.get(i).size()<=2)
				System.err.println("ANOVA"+i+" contains only "+classSelection.get(i).size()+" experiments");
			indices[i] =  classSelection.get(i).toArray(new Integer[0]);
		}

		
		
		//total number of experiments
		int n = 0; 
		for (int i =0; i < indices.length; ++i){
			n=n+indices[i].length;
		}
		
		
		
		// create f-distribution
		FDistribution fDist =
			new FDistribution(classSelection.size()-1, n-classSelection.size());
					//(Groups.Value+3-1),(n-(Groups.Value+3)));
		
		for (Probe pb : probes.toCollection()) {
			
			double[][] values = getAllValues(pb, indices);
			double sqwg= 0.0; // sum of squares within group
			double[][] means=new double[values.length][2]; // means[i][0] having group mean value of group i, means[i][1] number of elements in group i
			double mean = 0.0; // overall mean
			double sum = 0.0; // overall sum
			double sqbg = 0.0; // sum of squares between groups
			double vwg = 0.0; //variance within group
			double vbg = 0.0; // variance between groups
			double fRatio = 0.0; // f-statistic=(vbg/vwg)
			double p = 0.0; // p-value
			for(int i=0; i< values.length; ++i){
			
				means[i][0] = org.apache.commons.math3.stat.StatUtils.mean(values[i]); // get group mean value
				
				for(int h=0; h<values[i].length; ++h){
					sqwg= sqwg + Math.pow((values[i][h]-means[i][0]),2);
					
				}
							
				means[i][1] = values[i].length;
				sum = sum + org.apache.commons.math3.stat.StatUtils.sum(values[i]); // summarize all values to compute mean value
			}
			mean = (sum / n);
			
			sqbg = getSQbg(mean, means);
			
			vwg = (sqwg)/(n-(classSelection.size()));  //vwg = (sqwg)/(n-(Groups.Value+3));
			vbg = sqbg/(classSelection.size()-1);      //vbg = sqbg/(Groups.Value+3-1)
			fRatio= vbg/vwg;

			p = 1.0-fDist.cumulativeProbability(fRatio); // one sided-greater t-test

			
			// write p (and f-) values into MIOs
			// create new MIO for p-value MIO Group
			
			((DoubleMIO)pVal.add(pb)).setValue(p);
			
			if(fVal!=null){
				DoubleMIO fMio = new DoubleMIO(fRatio);
				fVal.add(pb, fMio);
			}
		}
		
		return res;
	}
	
	private List<List<Integer>> getClasses(ClassSelectionModel csm) {
		ArrayList<List<Integer>> classes = new ArrayList<List<Integer>> ();
		for (int i=0; i!=csm.getNumClasses(); ++i)
			classes.add(csm.toIndexList(i));
		return classes;
	}
		
	private List<List<Integer>> pruneClasses(List<List<Integer>> classSelection)
	{
		Iterator<List<Integer>> iter=classSelection.iterator();
			while(iter.hasNext())
		{
			List<Integer> curr=iter.next();
			if (curr.size()<3)
			{
				iter.remove();
			}
		}
		return classSelection;
	}
	
	// get Indexes from string and check if at least two experiments are assigned to each group
	
//	private int[] getIndexes(String indexes) throws Exception{
//		
//		String[] s;
//		s= indexes.split(",");
//		if(s.length < 2) throw new Exception("You need at least 2 experiments per group to do an ANOVA \n seperate experiment numbers with \", \"");
//		int[] ind = new int[s.length];
//		for(int i =0; i< s.length; ++i){
//			ind[i]=(Integer.parseInt(s[i])-1);
//		}
//		
//		return ind;
//	}
	
	//put all values in a matrix, where each row corresponds to a group
	private double[][] getAllValues(Probe pb, Integer[][] indexes){
		double vals[][] = new double[indexes.length][];
		for (int i = 0; i < indexes.length; ++i){
			vals[i] = getVals(pb, indexes[i]);
		}
		return vals;
	}
	
	// extract the values of a probe
	private double[] getVals(Probe pb, Integer[] index){
		double[] d = new double[index.length];
		for (int i = 0; i < index.length; ++i){
			d[i] = pb.getValue(index[i]);
		}
		return d;
		
 	}
	
	// compute sum of squares between groups
	private double getSQbg(double mean, double[][] GroupMeans){
		double sqbg = 0.0;
		for(int i= 0; i < GroupMeans.length; ++i){
			sqbg = sqbg + (GroupMeans[i][1] * Math.pow((GroupMeans[i][0] - mean),2));
		}
		return sqbg;
	}


	@Override
	public void init() {
	}
	
}
