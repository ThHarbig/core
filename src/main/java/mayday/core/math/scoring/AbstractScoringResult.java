package mayday.core.math.scoring;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

public abstract class AbstractScoringResult implements ScoringResult {

	protected MIGroupSelection<MIType> additionalValues = new MIGroupSelection<MIType>();
	protected MIGroup rawScore;
	
	public AbstractScoringResult() {
		// nothing to do
	}
	
	public MIGroupSelection<MIType> getAdditionalValues() {
		return additionalValues;
	}
	
	public MIGroup addAdditionalValue(String name, String mioType) {
		PluginInfo dMio = PluginManager.getInstance().getPluginFromID(mioType);
		MIGroup mg = new MIGroup(dMio, name, null);
		additionalValues.add(mg);
		return mg;
	}
	
	public MIGroup addAdditionalValue(String name) {
		return addAdditionalValue(name, "PAS.MIO.Double");
	}
	
	/** for cloning */
	public void addAdditionalValue(MIGroup value) {
		additionalValues.add(value);
	}
	
	/** for cloning */
	public void setRawScore(MIGroup value) {
		rawScore = value;
	}

	public MIGroup getRawScore() {
		if (rawScore==null)
			initRawScore();
		return rawScore;
	}
	
	//initialize the Rawscores for use
	public void initRawScore(){
		PluginInfo dMio = PluginManager.getInstance().getPluginFromID("PAS.MIO.Double");
		rawScore = new MIGroup(dMio, "raw score", null);		
	}
	
	public boolean hasRawScore() {
		return rawScore!=null;
	}
	
	
}
