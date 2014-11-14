package mayday.core.settings.typed;

import mayday.core.Experiment;
import mayday.core.MasterTable;

public class ExperimentSetting extends RestrictedStringSetting {

	public MasterTable mt;
	
	protected ExperimentSetting(String Name, String Description, int index, MasterTable mt) {
		super(Name, Description, index, mt.getExperimentDisplayNames().toArray(new String[0]));
		setLayoutStyle(mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle.LIST);
		this.mt = mt;
	}
	
	public ExperimentSetting(String Name, String Description, Experiment Default) {
		this(Name, Description, Default.getIndex(), Default.getMasterTable());		
	}
	
	public ExperimentSetting(String Name, String Description, MasterTable mt) {
		this(Name, Description, mt.getNumberOfExperiments()>0?0:-1, mt);
	}
	
	public Experiment getExperiment() {
		return mt.getExperiment(getSelectedIndex());
	}
	
	public void setExperiment(Experiment e) {
		setSelectedIndex(e.getIndex());
	}
	
	public ExperimentSetting clone() {
		ExperimentSetting es = new ExperimentSetting(name, description, getExperiment());
		return es;
	}

}
