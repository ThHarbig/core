package mayday.mpf.options;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/** OptMultiline objects represent string values spanning multiple lines. A TextArea is used as a gui element.
 * @author Florian Battke
 */
public class OptMultiline extends OptBase {
	
	/** The current value of this option	 */
	public String Value;
	
	protected String Default;
	protected JTextArea tf;

	/** Creates a new instance with a given name, description and initial value
	 * @param name The name of the new option
	 * @param description What this option means
	 * @param DefaultValue the initial value
	 */
	public OptMultiline(String name, String description, String DefaultValue) {
		super(name, description);
		if (DefaultValue==null) DefaultValue="";
		Default = DefaultValue;
		Value=Default;
	}
	
	protected void createEditArea() {
		super.createEditArea();
		tf = new JTextArea(Value);
		tf.setLineWrap(true);
		tf.setFont(UIManager.getFont(this.EditArea)); // TextArea font should fit other GUI fonts
		tf.setWrapStyleWord(true);			
		JScrollPane mSp = new JScrollPane(tf);		
		mSp.setPreferredSize(new java.awt.Dimension(200,100));
		mSp.setAlignmentX(Component.LEFT_ALIGNMENT);
		EditArea.add(mSp);
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
