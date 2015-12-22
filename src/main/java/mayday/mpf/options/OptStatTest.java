package mayday.mpf.options;

import mayday.core.math.stattest.StatTestManager;
import mayday.core.math.stattest.StatTestPlugin;

/* * @author Florian Battke
 */
public class OptStatTest extends OptPagedDropDown implements java.awt.event.ItemListener{
	
	// Find all implemented distance measures
	protected OptInteger MinkowskiParameter;
	
	public OptStatTest() {
		super("Statistical test",
				"Select what method should be used to compute p values",
				new String[]{"No distance measures found"}, 0);
	}

	public StatTestPlugin getStatTest() {
		StatTestPlugin dmt = (StatTestPlugin)getObject();  
		return dmt;		
	}
	
	protected void createEditArea() {
		Options = StatTestManager.values().toArray();
		super.createEditArea();
	}

}
