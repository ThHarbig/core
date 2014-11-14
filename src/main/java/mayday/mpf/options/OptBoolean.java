package mayday.mpf.options;

import javax.swing.JCheckBox;

/** OptBoolean objects represent an option that can either be true or false. As GUI element, a checkbox
 * is used.
 * @author Florian Battke
 */
public class OptBoolean extends OptBase {
	
	/** The current value of this option */
	public Boolean Value;
	
	
	protected Boolean Default;
	protected JCheckBox cb;

	/** Creates a new OptBoolean object with a given name, description and initial value
	 * @param name the name of the option
	 * @param description a string describing what the option means
	 * @param DefaultValue an initial value for the option
	 */
	public OptBoolean(String name, String description, Boolean DefaultValue) {
		super(name, description);
		Default = DefaultValue;
		Value=Default;
	}
	
	protected void createEditArea() {
		super.createEditArea();
		cb = new JCheckBox();
		cb.setSelected(Value);
		EditArea.add(cb);
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#validate()
	 */
	public boolean validate() {
		return true;		
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#accept()
	 */
	public void accept() {
		boolean changed = (cb.isSelected()!=Value);
		Value = cb.isSelected();
		if (changed) postEvent("Value changed");
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() {
		cb.setSelected(Value);
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueToString()
	 */
	public String ValueToString() {
		return Value.toString();
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */
	public void ValueFromString(String valueStr) {
		Value = Boolean.parseBoolean(valueStr);
	}
}
