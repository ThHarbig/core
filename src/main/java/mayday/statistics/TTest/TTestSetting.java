package mayday.statistics.TTest;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;

public class TTestSetting extends HierarchicalSetting {

	protected BooleanSetting equalVariance, paired;
	
	public TTestSetting(String Name) {
		super(Name);
		addSetting( equalVariance = new BooleanSetting("Equal variance","Assume equal variance (homoscedastic)? Uncheck to use an heteroscedastic test", true) );
		addSetting( paired = new BooleanSetting("Paired","Compute a paired t test?\nCalculations are performed between corresponding experiments in the two specified classes.\nUncheck to compute an unpaired ttest",false ));
	}

	public boolean isEqualVairance() {
		return equalVariance.getBooleanValue();		
	}
	
	public boolean isPaired() {
		return paired.getBooleanValue();
	}
	
	public TTestSetting clone() {
		TTestSetting cs = new TTestSetting(name);
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
	
}
