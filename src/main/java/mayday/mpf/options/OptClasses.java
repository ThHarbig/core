package mayday.mpf.options;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import mayday.core.ClassSelectionModel;
import mayday.core.gui.classes.ClassSelectionDialog;

/** OptBoolean objects represent an option that can either be true or false. As GUI element, a checkbox
 * is used.
 * @author Florian Battke
 */
public class OptClasses extends OptBase {
	
	/** The current value of this option */
	public String Value;
	
	protected String Default;
	protected JLabel classSelection;
	protected JButton Button=new JButton("Change...");
	
	protected ClassSelectionModel csm;
	protected ChangeActionListener CAL = new ChangeActionListener();

	/** Creates a new OptBoolean object with a given name, description and initial value
	 * @param name the name of the option
	 * @param description a string describing what the option means
	 * @param DefaultValue an initial value for the option
	 */
	public OptClasses(String name, String description, String defaultClasses) {
		super(name, description);
		Default = defaultClasses;
		Value=Default;
		csm = new ClassSelectionModel(Value);
	}
	
	public void setEditable(boolean editable) {
		Button.setEnabled(editable);
	}
	

	protected void createEditArea() {
		super.createEditArea();
		classSelection = new JLabel( csm.toString(true) );
		Button.addActionListener(CAL);
		EditArea.add(classSelection);
		EditArea.add(Button);
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
		boolean changed = csm.toString(true)!=Value;
		useCSMValue();
		if (changed) postEvent("Value changed");
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() {
		csm.fromString(Default);
		useCSMValue();
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
		// convert value to canonical format
		csm.fromString(valueStr);
		useCSMValue();
	}
	
	protected void useCSMValue() {
		Value = csm.toString(true);
		if (classSelection!=null) 
			classSelection.setText(Value);
	}
	
	public void showDialog() {
		CAL.actionPerformed(null);
	}
	
	public List<List<Integer>> getClasses() {
		ArrayList<List<Integer>> classes = new ArrayList<List<Integer>> ();
		for (int i=0; i!=csm.getNumClasses(); ++i)
			classes.add(csm.toIndexList(i));
		return classes;
	}
	
	public ClassSelectionModel getClassSelectionModel(){
		return csm;
	}
	
	private class ChangeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			ClassSelectionDialog csd = new ClassSelectionDialog(csm);
			csd.setVisible(true);
			if (csd.isCancelled())
				csm.fromString(Value);
			useCSMValue();
		}
	}
	
	public ClassSelectionModel getModel() {
		return csm;
	}
}
