package mayday.mpf.options;

import javax.swing.JOptionPane;
import javax.swing.JTextField;


/** OptDouble represents an option containing one double value. A textfield is used as GUI element.
 * This class can also check whether user input falls inside a predefined interval.
 * @author Florian Battke
 */
public class OptDouble extends OptBase {
	
	/** The current value of this option */
	public Double Value;
	
	protected Double max, min;
	
	protected Double Default;
	protected JTextField tf;

	/** Creates a new OptDouble object with a given name, description and initial value
	 * @param name the name of the option
	 * @param description a string describing what the option means
	 * @param DefaultValue an initial value for the option
	 */
	public OptDouble(String name, String description, Double DefaultValue) {
		super(name, description);
		Default = DefaultValue;
		Value=Default;
	}
	
	protected void createEditArea() {
		super.createEditArea();
		tf = new JTextField(Value.toString(), 10);
		EditArea.add(tf);
	}
	
	/** Sets the minimal and maximal value allowed for this option.
	 * If one of the parameters is null then the interval is open on that side. If both are null,
	 * no interval checking is performed
	 * @param min the lowest number allowed
	 * @param max the highest number allowed
	 */
	public void setBounds(Double min, Double max) {
		this.min=min; this.max=max;
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#validate()
	 */
	public boolean validate() {
		try {
		    Double tempValue = Double.parseDouble(tf.getText());
		    if (max!=null && tempValue>max) {
				JOptionPane.showMessageDialog(null, 
						"Please enter a value smaller than or equal to "+max+" for \""+Name+"\"", 
						"Hint", JOptionPane.ERROR_MESSAGE);
				return false;
		    }
		    if (min!=null && tempValue<min) {
				JOptionPane.showMessageDialog(null, 
						"Please enter a value larger than or equal to "+min+" for \""+Name+"\"", 
						"Hint", JOptionPane.ERROR_MESSAGE);
				return false;
		    }
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, 
					"Please enter a double value for \""+Name+"\"", 
					"Hint", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;		
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#accept()
	 */
	public void accept() {
		Double newValue = Double.parseDouble(tf.getText());		
		boolean changed = (newValue!=Value);
		Value = newValue;
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
		return Value.toString();
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */
	public void ValueFromString(String valueStr) {
		Value = Double.parseDouble(valueStr);
	}
}
