package mayday.mpf.options;

import java.util.Vector;
import javax.swing.*;

import mayday.mpf.ComplexFilter;
import mayday.mpf.FilterNode;
import mayday.mpf.FilterOptions;


/** OptExternalized represents an option that is presented to the user by a ComplexFilter instance.
 * It acts as a wrapper for the externalized subfilter option, giving it a new name and description.
 * The connection is created using the current subfilter index of a subfilter together with the
 * ComplexFilter instance. Internally, the index is replaced by a pointer to maintain the connection
 * when subfilter indices change. The subfilter option itself is addressed by its index in the subfilter's
 * FilterOptions.Options vector.
 * @author Florian Battke
 */
public class OptExternalized extends OptBase {

	private FilterNode SubfilterNode;
	private int OptionIdx;
	private ComplexFilter parentFilter;
	private String shortName;

	private OptExternalized(String name, String description, ComplexFilter attachFilter, int SubfilterIndex, int OptionIndex) {
		super(name, description);
		shortName = name;
		parentFilter = attachFilter;
		SubfilterNode = parentFilter.sortedFilters.get(SubfilterIndex);
		OptionIdx = OptionIndex;
		makeName();
	}

	/** Creates a new OptExternalized object attached for an instance of ComplexFilter attached to a given
	 * subfilter index and whithin that, a given option index.
	 * @param attachFilter the Complex Filter that this object will belong to.
	 * @param SubfilterIndex the subfilter index to attach to
	 * @param OptionIndex the index of the subfilter option that wil be externalized
	 */
	public OptExternalized(ComplexFilter attachFilter, int SubfilterIndex, int OptionIndex) {
		this(attachFilter.sortedFilters.get(SubfilterIndex).attachedFilter.Options.get(OptionIndex).Name,
				attachFilter.sortedFilters.get(SubfilterIndex).attachedFilter.Options.get(OptionIndex).Description,
				attachFilter, SubfilterIndex, OptionIndex);
	}

	/** Creates a new OptExternalized object based on a string representation of the link to the externalized
	 * subfilter option object. This function is needed to load ComplexFilters from disk.
	 * @param attachFilter the ComplexFilter that this object will belong to.
	 * @param fromString the string representation to decode.
	 */
	public OptExternalized(ComplexFilter attachFilter, String fromString) {
		super("",""); // we will set name and description in a moment...
		parentFilter = attachFilter;
		this.ValueFromString(fromString);
	}

	/** creates a meaningful name for the option based on its shortName and the name of the option
	 * it is representing */
	public void makeName() {
		Name = shortName
		+ "  ("+SubfilterNode.attachedFilter.toString()+" ("+SubfilterNode.getFilterIndex()+"): "
    		   +getOption().Name
        + ")";

	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#isVisible()
	 */
	public boolean isVisible() {
		return true;
		//return getOption().isVisible();
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#setVisible(boolean)
	 */
	public void setVisible(boolean vis) {
		getOption().setVisible(vis);
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#notify(mayday.mpf.FilterOptions)
	 */
	public void notify(FilterOptions parent) {
		getOption().notify(parent);
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#createEditArea()
	 */
	protected void createEditArea() {
		this.EditArea = getOption().getEditArea();
	}

	/** returns the shortName of this option, i.e. its name.
	 * @return the name to display
	 */
	public String getShortName() {
		return shortName;
	}

	public OptBase getOption() {
		return SubfilterNode.attachedFilter.Options.get(OptionIdx);
	}

	/** returns a JDialog where users can edit this option instead of editing the externalized
	 * option it represents (which is what getEditArea is for).
	 * @param parent
	 */
	public void editMetaOptions(JDialog parent) {
		FilterOptions fos  = new FilterOptions(this.parent);
		OptString foName = new OptString("Option name","The global name for this option",shortName);
		OptMultiline foDesc = new OptMultiline("Description","Describe what this option does",this.Description);
		fos.add(foName);
		fos.add(foDesc);
		fos.add(new OptSpacer(""));
		fos.add(new OptSpacer("You can specify the\n default value below:"));
		fos.add(getOption());
		boolean wasVisible = getOption().isVisible();
		getOption().setVisible(true);
		fos.ShowWindow("Externalized option","Set the name an description for this option to be presented to users of your pipeline",parent);
		getOption().setVisible(wasVisible);
		shortName = foName.Value;
		Description = foDesc.Value;
		makeName();
	}

	/** checks whether another OptExternalized instance represents the same subfilter option.
	 * @param foe the instance to compare
	 * @return true if both instances link to the same subfilter option.
	 */
	public boolean equals(OptExternalized foe) {
		return (foe.OptionIdx==this.OptionIdx && foe.SubfilterNode==this.SubfilterNode);
	}

	/** checks whether a OptBase instance is represented by this object
	 * @param fob the OptBase instance to compare
	 * @return true when this objects represents the OptBase instance.
	 */
	public boolean equals(OptBase fob) {
		return fob.getEditArea()==this.getEditArea();
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#validate()
	 */
	public boolean validate() {
		return getOption().validate(); }

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#accept()
	 */
	public void accept() {
		getOption().accept();
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() {
		getOption().cancel();
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueToString()
	 */
	public String ValueToString() {
		return wrapString(shortName)+"|"+wrapString(Description)+"|"+SubfilterNode.getFilterIndex()+"|"+OptionIdx;
	}

	private Vector<String> myOwnSplit(String s, char splitMark) {
		Vector<String> temp = new Vector<String>();
		int next=0; // must be -1 so it becomes 0 in the first iteration
		int last=0;
		while ((next=s.indexOf(splitMark,last))>-1) {
			temp.addElement(s.substring(last,next));
			last=next+1;
		}
		if (last!=s.length())
			temp.addElement(s.substring(last,s.length()));
		return temp;
	}

	private static final String unwrapString(String in) {
		return in.replace("~pipe~","|");
	}
	private static final String wrapString(String in) {
		return in.replace("|","~pipe~");
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */
	public void ValueFromString(String valueStr) {
		// for some reason, string.split("|") splits a string into single chars...
		Vector<String> parts = myOwnSplit(valueStr,'|');
		shortName = unwrapString(parts.get(0));
		Description = parts.get(1).equals("null") ? null : unwrapString(parts.get(1));
		SubfilterNode = parentFilter.sortedFilters.get(Integer.parseInt(parts.get(2)));
		OptionIdx = Integer.parseInt(parts.get(3));
		makeName();
	}

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#getAnnotation()
	 */
	public String getAnnotation() {
		return getOption().getAnnotation();
	}

}
