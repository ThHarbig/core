package mayday.core.math.stattest;

import mayday.core.math.scoring.ScoringResult;
import mayday.core.meta.MIGroup;

public interface StatTestResult extends ScoringResult {
	
	public MIGroup getPValues();
	
	/** If the p values can be taken as corrected (e.g. Rank Product pfp values), indicate this here */
	public boolean pValuesNeedCorrection();
	
}
