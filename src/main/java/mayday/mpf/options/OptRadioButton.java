
package mayday.mpf.options;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/** OptRadioButton represents one choice of a set of choices. The GUI element is a radio button belonging to a 
 * button group. As usual, all radiobuttons in the same buttongroup are exclusive, i.e. only one of them can
 * be selected at any given time.
 * The name of this option will be displayed within its EditArea while the "official" name field remains empty. 
 * @author Florian Battke
 */
public class OptRadioButton extends OptBase {
	
	/** The status of this option. True when it is selected, false otherwise.	 */
	public boolean Value;
	
	protected boolean Default;
	protected JRadioButton rb;
	protected ButtonGroup bg;
	protected String Name;

	/** Creates a new instance with a given name, description and initial value that belongs to a given
	 * ButtonGroup
	 * @param name the name of this option
	 * @param description what this option does
	 * @param parentgroup the ButtonGroup this option will belong to
	 * @param DefaultSelected the initial selection state of this option. Make sure that you set this to true
	 * for exactly one member of the ButtonGroup.
	 */
	public OptRadioButton(String name, String description, ButtonGroup parentgroup, boolean DefaultSelected) {
		super(null, description);
		Default = DefaultSelected;
		Value = Default;
		Name=name;
		bg = parentgroup;
	}

	protected void createEditArea() {
		super.createEditArea();
		rb = new JRadioButton(Name);
		bg.add(rb);
		rb.setSelected(Value);
		this.EditArea.add(rb); 
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#validate()
	 */
	public boolean validate() { 
		return true; // can't be false	
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#accept()
	 */
	public void accept() {
		boolean changed = (rb.isSelected()!=Value);
		Value = rb.isSelected();
		if (changed) postEvent("Value changed");
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() {
		rb.setSelected(Value);
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueToString()
	 */
	public String ValueToString() {
		return Boolean.toString(Value);
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */
	public void ValueFromString(String valueStr) {
		Value = Boolean.parseBoolean(valueStr);
	}

}
