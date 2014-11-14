package mayday.mpf.options;

import javax.swing.JTextField;

/** OptString objects represent string values. A Textfield is used as a gui element.
 * @author Florian Battke
 */
public class OptString extends OptBase {
	
	/** The current value of this option	 */
	public String Value;
	
	protected String Default;
	protected JTextField tf;

	/** Creates a new instance with a given name, description and initial value
	 * @param name The name of the new option
	 * @param description What this option means
	 * @param DefaultValue the initial value
	 */
	public OptString(String name, String description, String DefaultValue) {
		super(name, description);
		if (DefaultValue==null) DefaultValue="";
		Default = DefaultValue;
		Value=Default;
	}
	
	protected void createEditArea() {
		super.createEditArea();
		tf = new JTextField(Value.toString(), 30);
		EditArea.add(tf);
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
		boolean changed = (tf.getText()!=Value);
		Value = tf.getText();
		if (changed) postEvent("Value changed");
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() {
		tf.setText(Value.toString());
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueToString()
	 */
	public String ValueToString() {
		return Value;
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */
	public void ValueFromString(String valueStr) {
		Value = valueStr;	
	}
}
