package mayday.mpf.options;

import java.awt.event.ItemEvent;

import java.util.Vector;

//import javax.swing.JComboBox;

import mayday.mpf.FilterOptions;

/** OptPagedDropDown is an extension of OptDropDown.
 * This class maintains lists of options ("pages"). When the selected OptDropDown value changes, it
 * uses that value as the index of the page to display. All options on that page are made visible,
 * all options on different pages are hidden, and the FilterOptions dialog window is updated.
 * If the OptPagedDropDown is hidden, all contained elements are also hidden.
 * For an example, see BasicNormalize. 
 * @author Florian Battke
 */
public class OptPagedDropDown extends OptDropDown implements java.awt.event.ItemListener{
	
	private Vector<Vector<OptBase>> pages = new Vector<Vector<OptBase>>();
	 
	/** Creates a new OptPagesDropDown object with a given name, description, list of coices and
	 * the index of the initially selected value, i.e. the initially visible page 
	 * @param name the name of the option
	 * @param description what the option means
	 * @param options The list of choices presented to the user
	 * @param DefaultIndex the index of the currently selected position in options[] and the index of the currently selected page
	 */
	public OptPagedDropDown(String name, String description, String[] options, int DefaultIndex) {
		super(name, description, options, DefaultIndex);
		this.setPageCount(options.length);
	}

	protected void setPageCount(int pageCount) {
		pages.setSize(pageCount);
		for (int i=0; i!=pageCount; ++i)
			pages.set(i, new Vector<OptBase>());
	}
	
	/** adds an option to a given page
	 * @param addToPage the page to add to
	 * @param option the option to add
	 */
	public void addOption(int addToPage, OptBase option) {
		pages.get(addToPage).add(option);
		option.setVisible(addToPage==this.VisiblePage);
	}
	
	/** removes all options from a page
	 * @param PageNo the page to clear
	 */
	public void clearPage(int PageNo) {
		pages.get(PageNo).clear();
		if (VisiblePage==PageNo) setVisiblePage(VisiblePage); //update display
	}
	
	public void cancel() {
		super.cancel();
		setVisiblePage(this.Value); // for some reason, setting the index in super.cancel does not call the itemlistener
	}
	
	protected void createEditArea() {
		super.createEditArea();
		this.cb.addItemListener(this);
		this.setVisiblePage(cb.getSelectedIndex());
	}
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#setVisible(boolean)
	 */
	public void setVisible(boolean vis) {
		super.setVisible(vis);
		setVisiblePage( vis ? this.VisiblePage : -1 );
	}
	
	private FilterOptions Parent = null;
	
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#notify(mayday.mpf.FilterOptions)
	 */
	public void notify(FilterOptions parent) {
		Parent = parent;
	}
	
	private int VisiblePage;
	
	/** sets the currently visible page. All options on that page will be made visible,
	 * all options on the other pages will be hidden, the FilterOptions dialog window will be updated.
	 * @param visiblePage the page to switch to
	 */
	public void setVisiblePage(int visiblePage) {
		// Hide old page
		for (int i=0; i!=pages.get(VisiblePage).size(); ++i)
			pages.get(VisiblePage).get(i).setVisible(false);
		if (visiblePage>-1) {
			// Show new page
			VisiblePage = visiblePage;
			for (int i=0; i!=pages.get(VisiblePage).size(); ++i) {
				pages.get(VisiblePage).get(i).setVisible(true);
			}			
		}
		if (Parent!=null) Parent.createOptionList();
	}

	/** Returns the currently visible page
	 * @return the page no of the visible page
	 */
	public int getVisiblePage() {
		return VisiblePage;
	}
	
	/** reacts to an event and changes the currently visible page. This will be called
	 * by the OptDropDown object that we are attached to, and nobody else should call it.
	 */
	public void itemStateChanged(ItemEvent arg0) {
		// we expect to be called by a combobox. The index in the combobox determines which page to show
		// Well, actually, I'd like to use a javax.swing.JComboBox, but Sun's implementation sucks bigtime, i.e.
		// the JComboBox stops working when I change the parent window layout. So I have to use
		// java.awt.Choice, which looks different but at least it works.
		// Or at least I thought so until I saw what happens on MacOS X, where the Choice doesn't even show most
		// of the time. So now it's back to JComboBox, but this time I changed the FilterOptions class to add the
		// components to the JFrame's content pane instead of the root pane, which seems to work.
		if (arg0.getStateChange()==java.awt.event.ItemEvent.SELECTED) {
			if (arg0.getSource() instanceof javax.swing.JComboBox) {
				setVisiblePage(((javax.swing.JComboBox)arg0.getSource()).getSelectedIndex());
			}
		}
	}

}
