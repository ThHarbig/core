package mayday.mpf.options;

import java.awt.event.ItemListener;

import javax.swing.JComboBox;

/** OptDropDown represents an option where the user can choose one value out of a set of values.
 * A Combobox is used as GUI element. The selected value is stored as the index of the String
 * that the user has selected in the combobox. 
 * @author Florian Battke
 */
public class OptDropDown extends OptBase {
	
	/** The index of the currently selected string 	 */
	public int Value;
	
	protected int Default;
	protected JComboBox cb;
	protected Object[] Options;
	protected ItemListener intendedListener;

	/** Creates a new OptDropDown object with a given name, description, list of coices and
	 * the index of the initially selected value 
	 * @param name the name of the option
	 * @param description what the option means
	 * @param options The list of choices presented to the user
	 * @param DefaultIndex the index of the currently selected position in options[]
	 */
	public OptDropDown(String name, String description, Object[] options, int DefaultIndex) {
		super(name, description);
		Default = DefaultIndex;
		Value = Default;
		Options = options;
	}
	
	protected void createEditArea() {
		super.createEditArea();
		cb = new javax.swing.JComboBox(Options);
		if (Options.length>0) cb.setSelectedIndex(Value);
		if (intendedListener!=null) cb.addItemListener(intendedListener);
		this.EditArea.add(cb); 
	}
	
	/** Attaches an ItemListener to this object. Only one listener can be attached, attaching a new
	 * one replaces previously attached listeners. 
	 * @param al The listener to attach
	 */
	public void setItemListener(java.awt.event.ItemListener al){ 		
		if (EditArea!=null) {
			if (intendedListener!=null) cb.removeItemListener(intendedListener);
			cb.addItemListener(al);
		}
		intendedListener = al;
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
		boolean changed = (cb.getSelectedIndex()!=Value);
		Value = cb.getSelectedIndex();
		if (changed) postEvent("Value changed");
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() {
		cb.setSelectedIndex(Value);
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueToString()
	 */
	public String ValueToString() {
		// String-based matching!
		return Options[Value].toString();
	}
	
	public Object getObject() {
		return Options[Value];
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */
	public void ValueFromString(String valueStr) {
		int pos = -1;
		for (int i=0; i!=Options.length; ++i) {
			if (Options[i].toString().equals(valueStr)) { 
				pos = i; 
				break;
			}
		}
		if (pos==-1) throw new RuntimeException("Item \""+valueStr+"\" is not in the list of possible choices.");
		boolean changed = (Value!=pos);
		Value = pos;
		// I have to inform my listeners of this change so that e.g. the RWrapper can adapt it's option list
		// depending on the selected R function
		if (changed)
			this.postEvent("Value loaded from string");
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#getAnnotation()
	 */
	public String getAnnotation() {
		return cb.getItemAt(Value).toString();
	}

}
