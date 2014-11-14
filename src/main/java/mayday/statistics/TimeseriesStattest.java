/*
 * Created on Dec 8, 2004
 *
 */
package mayday.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.average.IAverage;
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
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.AveragingSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.tasks.AbstractTask;
import mayday.statistics.TTest.TTestPlugin;

/**
 * @author gehlenbo
 *
 */
public class TimeseriesStattest
extends AbstractPlugin implements ProbelistPlugin
{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.statistics.timerseriesstattest",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Computes statistical tests in windows and aggregates p values",
		"Time-Series statistics");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_STATISTICS);
		return pli;
	}
	
	protected PluginTypeSetting<StatTestPlugin> stattest;
	protected PluginTypeSetting<PCorrectionPlugin> correction_inner;
	protected PluginTypeSetting<PCorrectionPlugin> correction_outer;
	protected AveragingSetting aggregationmethod;
	
	protected StringSetting mioname;
	protected BooleanHierarchicalSetting filter;
	protected DoubleSetting filterPVal;
	
	protected SelectableHierarchicalSetting windowingmode;
	
	protected HierarchicalSetting window_fixed_moving;
	protected IntSetting window_fixed_moving_leftsize;
	protected IntSetting window_fixed_moving_rightsize;
	
	protected HierarchicalSetting window_inside_outside;
	protected IntSetting window_inside_outside_size;
	
	protected HierarchicalSetting window_move_class_separator;
	protected IntSetting window_move_class_separator_minimalsize;

	protected HierarchicalSetting window_fixed_allpairs;
	protected IntSetting window_fixed_allpairs_leftsize;
	protected IntSetting window_fixed_allpairs_rightsize;

	
	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )	{
		
		HierarchicalSetting statSetting = new HierarchicalSetting("Time-Series statistics").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
		.addSetting( mioname = new StringSetting("MIO Group name","","p-value"))
		.addSetting( stattest = new PluginTypeSetting<StatTestPlugin>("Pairwise statistical test", null, new TTestPlugin(), StatTestPlugin.MC))
		.addSetting( correction_inner = new PluginTypeSetting<PCorrectionPlugin>("Inner correction",null, new None(), PCorrectionPlugin.MC) )
		.addSetting( windowingmode = new SelectableHierarchicalSetting("Windowing Mode", null, 0, new Object[]{
				window_move_class_separator = new HierarchicalSetting("Move class separator")
					.addSetting( window_move_class_separator_minimalsize = new IntSetting("Minimal class size", null, 3, 3, null, true, false) )
				,
				window_inside_outside = new HierarchicalSetting("Move single window, compare inside vs outside")
					.addSetting( window_inside_outside_size = new IntSetting("Inner window size", null, 3, 3, null, true, false))
				,
				window_fixed_moving = new HierarchicalSetting("Move two adjacent windows")
					.addSetting( window_fixed_moving_leftsize = new IntSetting("Left window size", null, 3, 3, null, true, false)) 
					.addSetting( window_fixed_moving_rightsize = new IntSetting("Right window size", null, 3, 3, null, true, false)),
				window_fixed_allpairs = new HierarchicalSetting("Move two windows freely, testing all pairs")
					.addSetting( window_fixed_allpairs_leftsize = new IntSetting("First window size", null, 3, 3, null, true, false)) 
					.addSetting( window_fixed_allpairs_rightsize = new IntSetting("Second window size", null, 3, 3, null, true, false)),				
			}).setLayoutStyle(SelectableHierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
		)
		.addSetting( aggregationmethod = new AveragingSetting())
		.addSetting( correction_outer = new PluginTypeSetting<PCorrectionPlugin>("Outer correction",null, new None(), PCorrectionPlugin.MC) )
		.addSetting( filter = new BooleanHierarchicalSetting("Create ProbeList of significant probes",null,false)
			.addSetting(filterPVal = new DoubleSetting("pValue threshold",null, 0.05, 0d,1d,true,true)) )
		;
	
		aggregationmethod.setSelectedIndex(1); // set to minimum
		
		Settings s = new Settings(statSetting, PluginInfo.getPreferences("PAS.statistics.timerseriesstattest"));		
		SettingsDialog sd = new SettingsDialog(null, "Time-Series statistics", s);
		
		sd.showAsInputDialog();
		
		if (sd.canceled())
			return null;

		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);
		
		final HashMap<Object, double[]> conv = new HashMap<Object,double[]>();
		for (Probe probe : uniqueProbes.getAllProbes())
			conv.put(probe, probe.getValues());
		
		final Collection<ClassSelectionModel> windows = createWindows(masterTable);

		final StatTestPlugin stp = stattest.getInstance();
		final DoubleMatrix pvals = new DoubleMatrix(conv.size(), windows.size(), true);

		// create CSM from windows, run tests, correct with inner correction method
		
		AbstractTask at = new AbstractTask("Computing pValues for windows") {

			@Override
			protected void doWork() throws Exception {
				int col=0;
				for (ClassSelectionModel csm : windows) {
					// ============= per-probe p-values for this windowing
					StatTestResult str = stp.runTest(conv, csm);
					MIGroup rawp = str.getPValues();
					int row=0;
					for (Object opb : conv.keySet())
						pvals.setValue(row++, col, ((DoubleMIO)rawp.getMIO(opb)).getValue());
					++col;
					setProgress(10000*col/windows.size());
				}
			}

			@Override
			protected void initialize() {
			}
		};
		at.start();
		at.waitFor();
		
		// ============= Inner correction: correct for multiple testing in number of windows
		PCorrectionPlugin icc = correction_inner.getInstance();
		if (icc.getClass()!=None.class) {
			for (int row=0; row!=pvals.nrow(); ++row) {
				List<Double> corrected = icc.correct(pvals.getRow(row).toArrayUnpermuted());
				pvals.setRow(row, corrected);
			}
		}
		
		// ============= Summary method decided which value to use. Minimum is the obvious choice here
		IAverage summary = aggregationmethod.getSummaryFunction();
		AbstractVector summaries = pvals.apply(0, summary, "getAverage");
		
		
		// ============= Outer correction: correct for multiple testing in number of probes
		PCorrectionPlugin occ = correction_outer.getInstance();
		List<Double> corrected = occ.correct(summaries.asList());
		
		//  ============= add final pvalue mio group
		MIManager mim = masterTable.getDataSet().getMIManager();
		MIGroup res = mim.newGroup("PAS.MIO.Double",  mioname.getStringValue());
		int row=0;
		for (Object opb : conv.keySet()) {
			res.add(opb, new DoubleMIO(corrected.get(row++)));
		}
		
		//  ============= apply filtering
		MIGroup filterGroup = res;
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
	
	protected Collection<ClassSelectionModel> createWindows(MasterTable mt) {
		ArrayList<ClassSelectionModel> csms = new ArrayList<ClassSelectionModel>();
		
		if (windowingmode.getObjectValue()==window_move_class_separator) {
			int minsize = window_move_class_separator_minimalsize.getIntValue();
			int startindex=minsize;
			int endindex = mt.getNumberOfExperiments()-minsize+1;
			for (int i=startindex; i!=endindex; ++i) {
				// set separator at i
				ClassSelectionModel csm = new ClassSelectionModel(mt);
				for (int j=0; j!=i; ++j)
					csm.setClass(j, "Group 1");
				for (int j=i; j!=mt.getNumberOfExperiments(); ++j)
					csm.setClass(j, "Group 2");
				csms.add(csm);
			}
		} else if (windowingmode.getObjectValue()==window_fixed_moving) {
			int winsize1 = window_fixed_moving_leftsize.getIntValue();
			int winsize2 = window_fixed_moving_rightsize.getIntValue();
			int winsize = winsize1+winsize2;
			int startindex=0;
			int endindex = mt.getNumberOfExperiments()-winsize+1;
			for (int i=startindex; i!=endindex; ++i) {
				ClassSelectionModel csm = new ClassSelectionModel(mt);		
				// set left window to class 1, right to class 2, leave rest unassigned
				for (int j=i; j!=i+winsize1; ++j)					
					csm.setClass(j, "Group 1");
				for (int j=i+winsize1; j!=i+winsize; ++j)
					csm.setClass(j, "Group 2");
				csms.add(csm);
			}
		} else if (windowingmode.getObjectValue()==window_inside_outside) {
			int winsize = window_inside_outside_size.getIntValue();
			int startindex=0;
			int endindex = mt.getNumberOfExperiments()-winsize+1;
			for (int i=startindex; i!=endindex; ++i) {
				// set all elements to class 2, then set inner window to class 1
				ClassSelectionModel csm = new ClassSelectionModel(mt);				
				for (int j=0; j!=mt.getNumberOfExperiments(); ++j)					
					csm.setClass(j, "Group 2");
				for (int j=i; j!=i+winsize; ++j)
					csm.setClass(j, "Group 1");
				csms.add(csm);
			}
		} else if (windowingmode.getObjectValue()==window_fixed_allpairs) {
			int winsize1 = window_fixed_allpairs_leftsize.getIntValue();
			int winsize2 = window_fixed_allpairs_rightsize.getIntValue();
			int winsize = winsize1+winsize2;
			int startindex=0;
			int endindex = mt.getNumberOfExperiments()-winsize+1;
			for (int i=startindex; i!=endindex; ++i) {
				for (int j=i+winsize1; j!=mt.getNumberOfExperiments()-winsize2+1; ++j) {
					ClassSelectionModel csm = new ClassSelectionModel(mt);		
					// set left window to class 1, right to class 2, leave rest unassigned
					for (int k=i; k!=i+winsize1; ++k)					
						csm.setClass(k, "Group 1");
					for (int k=j; k!=j+winsize2; ++k)
						csm.setClass(k, "Group 2");
					csms.add(csm);			
				}
			}
		}
		
		return csms; 
	}

	@Override
	public void init() {
	
	}
	

}
