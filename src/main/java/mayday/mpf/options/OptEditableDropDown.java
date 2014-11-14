package mayday.mpf.options;

public class OptEditableDropDown extends OptDropDown {

	public String Value;
	
	public OptEditableDropDown(String name, String description,	String[] options, String DefaultValue) {
		super(name, description, options, 0);
		Value=DefaultValue;
	}
	
	public void accept() {
		String newValue = (String)cb.getSelectedItem();
		boolean changed = (newValue!=Value);
		Value = newValue;
		if (changed) postEvent("Value changed");
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() {
		cb.setSelectedItem(Value);
	}

	public String ValueToString() {
		return Value;
	}

	protected void createEditArea() {
		super.Value=0; 
		super.createEditArea();
		this.cb.setEditable(true);
		cb.setSelectedItem(Value);
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */
	public void ValueFromString(String valueStr) {
		Value=valueStr;
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#getAnnotation()
	 */
	public String getAnnotation() {
		return Value;
	}

}
