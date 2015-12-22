package mayday.core.math.stattest;

import mayday.core.math.scoring.DefaultScoringResult;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

public abstract class AbstractStatTestResult extends DefaultScoringResult implements StatTestResult {

	protected MIGroup pValues;
	
	public AbstractStatTestResult() {
		super(false);
		PluginInfo dMio = PluginManager.getInstance().getPluginFromID("PAS.MIO.Double");
		pValues = new MIGroup(dMio, "p value", null);	
	}

	public MIGroup getPValues() {
		return pValues;
	}

	@Override
	public MIGroup getRawScore() {
		return rawScore;
	}
	
	/** for cloning */
	public void setPValues(MIGroup mg) {
		pValues = mg;
	}

	public abstract boolean pValuesNeedCorrection();

}
