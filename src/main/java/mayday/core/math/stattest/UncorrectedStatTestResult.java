package mayday.core.math.stattest;


public class UncorrectedStatTestResult extends AbstractStatTestResult {
	
	public boolean pValuesNeedCorrection() {
		return true;
	}

}
