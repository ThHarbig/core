package mayday.mpf.options;

import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.AWTEventMulticaster;

import javax.swing.*;

import mayday.mpf.FilterBase;
import mayday.mpf.FilterOptions;

/** OptBase is the base class for all filter option types. 
 * @author Florian Battke
 */
public abstract class OptBase {
	
	/** The name of the option. This name is displayed whenever the option GUI is visible */
	public String Name;
	
	/** A string describing what the option does. Could also provide hints to useful values etc. */
	public String Description;
	
	/** All externalizable can be presented to the user by complex filter objects. Such options
	 * that must not be presented by complex filters MUST be marked non-externalizable. For an example,
	 * see RWrapper	 */
	public boolean externalizable=true;
	
	/** visible options are displayed when the FilterOptions method ShowWindow is called, invisible
	 * ones aren't */
	private boolean visible = true;
	
	/** EditArea is a JPanel containing all GUI elements needed to manipulate the option's value */
	protected JPanel EditArea;
	
	protected FilterBase parent;
	
	/** Creates a new Option object with given name and description.
	 * This method MUST be called by all subclasses of OptBase.
	 * @param name The name of the option to be displayed in all dialogs
	 * @param description A string describing what the option does and which values to use.
	 */
	public OptBase(String name, String description) {
		Name=name;
		Description=description;
	}
	
	/** Returns the EditArea for this option, creating it if neccessary.
	 * @return a JPanel containing all GUI elements to manipulate this option's value.
	 */
	public final JPanel getEditArea() {
		// lazy creation, saves time&memory if outside designer mode
		if (EditArea==null) createEditArea();
		return EditArea;
	}
	
	/** notifies the Option, that its gui elements are used by a given instance of FilterOptions.
	 * This is needed by OptPagedDropDown and OptExternalized. When notify is called, this does not 
	 * mean that the option will actually be displayed as this depends on OptBase.visible.
	 * At any given time there will never by more than one FilterOptions object using an Option.
	 * @param parent The FilterOptions object currently using this Option
	 */
	public void notify(FilterOptions parent) {}; // needed by OptPagedDropDown, OptExternalized 
	
	public final String toString() { // needed for displaying in JLists
		return Name;
	}
	
	/** created the EditArea for this option. All subclasses MUST override this function and
	 * MUST call the inherited function before adding any GUI elements to their edit area.
	 */
	protected void createEditArea() { //subclasses override this function
		EditArea = new JPanel(new FlowLayout(FlowLayout.LEFT));
	}
	
	protected boolean calledFromApplicator() {
		if (parent!=null) return parent.calledFromApplicator();
		return false;
	}
	
	protected boolean calledFromDesigner() {
		if (parent!=null) return parent.calledFromDesigner();
		return false;		
	}
	
	public void setParent(FilterBase parent) {
		this.parent=parent;
	}

	/** verifies the current value of GUI input elements to check whether they are legal.
	 * This function MUST be overridden in subclasses to perform the actual checking.
	 * If input is invalid, an instructive message MUST be shown to the user to help him/her
	 * rectify the problem. 
	 * @return true if user input is valid, false otherwise.
	 */
	public abstract boolean validate();  // verifies input, emits error messages and returns false if incorrect
	
	/** Accepts the current value of GUI input elements as the new value of this option.
	 * Implementors of subclasses MUST override this function. You can assume that it is only
	 * called after a successfull call to validate().
	 * Subclasses should also post an event when their value has been changed (i.e. when the
	 * new value is different from the old value) via the postEvent() function.
	 * Example: postEvent("Value Changed")); 
	 */
	public abstract void accept();  // Accept changed value, assuming that validate() went ok, may post event
		// Example: postEvent("Value Changed"));
	
	/** Discards the current value of GUI input elements and resets them to the original value
	 * stored in this option. Subclasses MUST override this function.
	 */
	public abstract void cancel();  // restores displayed values in editarea to stored values of this option
	// fb: 070408 - Info: This is used only when the options are displayed from Designer, not from Applicator 

	/** Returns whether this option my be presented by instances of ComplexFilter.
	 * @return true if this option may be externalized
	 */
	public boolean allowExternalize() {
		return externalizable;
	}
	
	/** Returns whether this option is visible to the user
	 * @return true if the option is visible
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/** Sets the visibility of this option. 
	 * @param vis true if the option should be visible, false otherwise
	 */
	public void setVisible(boolean vis) {
		visible=vis;
	}
	
	/** Returns a string describing the current option value. This is used to create MIO annotations
	 * for Probes and ProbeLists created by a filter. Normally, this is forwarded to ValueToString(), but
	 * subclasses MAY override this function to provide more meaningful results. For an example, see
	 * OptDropDown. 
	 * @return a human-readable representation of the current option value
	 */
	public String getAnnotation() {
		return ValueToString();
	}
	
    /** returns a string representation of the current value of this option for storage.
     * Subclasses MUST overridethis function
     * @return a string representation of the current value 
     */
    public abstract String ValueToString(); // must be overloaded
    
    /** sets the option value based on a string representation created by a previous call to ValueToString()
     * @param valueStr a string representation of the value to be se 
     */
    public abstract void ValueFromString(String valueStr); // must be overloaded. only called BEFORE createEditArea
	
	/* All the event code taken from Java API 1.5 AWTEventMulticaster documentation */
	private ActionListener actionListener = null;
	public final synchronized void addActionListener(ActionListener l) {
        actionListener = AWTEventMulticaster.add(actionListener, l);
    }
    public final synchronized void removeActionListener(ActionListener l) {
          actionListener = AWTEventMulticaster.remove(actionListener, l);
    }
    protected final void postEvent(String ae) {
        ActionListener listener = actionListener;
        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, 0, ae));
        }
    }
    /* end of event creation code */	
}