package mayday.vis3.plots;

import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.meta.types.TimeseriesMIO;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;

public class PlotTimepointSetting extends HierarchicalSetting {

	StringSetting timepoints;
	BooleanSetting useTimepoints;
	DataSet dataSet;
	
	public PlotTimepointSetting(String Name, DataSet ds, boolean defaultToTrue) {
		super(Name);
		dataSet = ds;
		addSetting( useTimepoints = new BooleanSetting(
				"Use timepoints",
				"If active, time points will be used to position experiments.\n" +
				"Otherwise they will be positioned by their index.",
				false
				) );
		addSetting( timepoints = new StringSetting(
				"Time points",
				"Enter experiment time points, comma separated.\n" +
				"You need not specify all time points (e.g. '1.5,,7,8,,12' works)",
				"",
				true
				) );
		inferTimepoints();
		if (timepoints.getStringValue().trim().length()>0 && defaultToTrue)
			useTimepoints.setBooleanValue(true);
	}
	
	public PlotTimepointSetting clone() {
		PlotTimepointSetting pts = new PlotTimepointSetting(name, dataSet, false);
		pts.fromPrefNode(this.toPrefNode());
		return pts;
	}
	
	public void setDataSet(DataSet ds) {
		dataSet = ds;
		inferTimepoints();
	}

	protected void inferTimepoints() {
		double[] experimentTimepoints = null;

		if (dataSet == null)
			return;

		// try to create a tsm automatically
		TimeseriesMIO tsm = TimeseriesMIO.getForDataSet(dataSet, true, true);
		if (tsm!=null && tsm.applicableTo(dataSet)) {
			experimentTimepoints = new double[tsm.getValue().size()];
			for (int i=0; i!=experimentTimepoints.length; ++i) 
				experimentTimepoints[i] = tsm.getValue().get(i);
		}
			
		applyTimepoints(experimentTimepoints);
	}
	
	protected void applyTimepoints(double[] experimentTimepoints) {
		if (experimentTimepoints!=null) {
			String preset = "";
			for (int i=0; i!=experimentTimepoints.length; ++i) {
				preset+=",";
				double d = experimentTimepoints[i];
				if (d!=(double)i) 
					preset+=d;
			}
			if (preset.length()>0) 
				preset = preset.substring(1);
			timepoints.setStringValue(preset);
		}
	}
	
	public double[] getExperimentTimpoints() {
		double[] experimentTimepoints;		
		experimentTimepoints = new double[dataSet.getMasterTable().getNumberOfExperiments()];
		getExperimentTimepoints(experimentTimepoints);
		return experimentTimepoints;
	}
	
	public boolean getExperimentTimepoints(double[] experimentTimepoints) {
		if (experimentTimepoints.length!=dataSet.getMasterTable().getNumberOfExperiments())
			return false;//
		String[] b = timepoints.getStringValue().trim().split(",");
		// fail safe defaults
		for (int i=0; i!=experimentTimepoints.length; ++i)
			experimentTimepoints[i] = (double)i;
		int i=0;
		for (String bb : b) {
			if (bb.trim().length()>0) {
				try {
					experimentTimepoints[i]=Double.parseDouble(bb.trim());
				} catch(Exception ex) {};
			}
			++i;
		}
		return true;
	}
	
	public void setExperimentTimepoints(double[] experimentTimepoints) {
		applyTimepoints(experimentTimepoints);
	}
	
	protected void updateMIO() {
		double[] experimentTimepoints = getExperimentTimpoints();
		TimeseriesMIO tpm = TimeseriesMIO.getForDataSet(dataSet, false, false);
		List<Double> ld;
		boolean needsAdding;
		if (tpm==null) {
			tpm = new TimeseriesMIO();
			ld = new LinkedList<Double>();
			tpm.setValue(ld);
			needsAdding = true;
		} else {
			ld = tpm.getValue();
			needsAdding = false;
		}
		ld.clear();
		for (double d : experimentTimepoints)
			ld.add(d);
		if (needsAdding)
			TimeseriesMIO.getGroupInstance(dataSet.getMIManager()).add(dataSet,tpm);
	}
		
	public boolean useTimepoints() {
		return useTimepoints.getBooleanValue();
	}
	
	public void setUseTimepoints(boolean v) {
		useTimepoints.setBooleanValue(v);
	}
}
