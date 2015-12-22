package mayday.mpf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import mayday.mpf.options.OptBase;
import mayday.mpf.options.OptEditableDropDown;
import mayday.mpf.options.OptExternalized;
import mayday.mpf.options.OptInteger;
import mayday.mpf.options.OptMultiline;
import mayday.mpf.options.OptString;


/** OptionEditor creates a window where a Complex Filter can be configured, i.e. where
 * it's name, description, version information and externalized options of subfilters
 * can be set.
 * @author Florian Battke
 */
public class OptionEditor {
	
	private OptString oName;
	private OptEditableDropDown oCategory;
	private OptMultiline oDesc;
	private OptInteger oVer;
	private OptExternalizedList oExtList;
	
	/** Shows the option window for a given instance of ComplexFilter 
	 * @param parent a parent JFrame that we need to display this window modally
	 * @param attachFilter the ComplexFilter to edit
	 * @return true if this window was closed by clicking on "Accept", false otherwise
	 */
	public boolean ShowWindow(JFrame parent, ComplexFilter attachFilter) {
		FilterOptions fop = new FilterOptions(null);
		oName = new OptString("Module name",
				"The name of your pipeline that will be displayed in the MPF Applicator. \n" +
				"This name is also used as the file name.",attachFilter.getName());
		oDesc = new OptMultiline("Module description",
				"A concise description of what this module does, possible including notes \n" +
				"on applicability and the like.",attachFilter.getDescription());
		oCategory = new OptEditableDropDown("Category",
				"Specify the category that you want this module to appear in. Either select an existing\n" +
				"category from the list or create a new one.",FilterClassList.getInstance().getCategories(),attachFilter.getCategory());
		oVer = new OptInteger("Module version:",
				"The module version is needed when your module is integrated into another pipeline. \n" +
				"You must increment this number whenever you change one of the following: \n" +
				"- the number of input or output slots\n" +
				"- the number of options or their ordering\n" +
				"- the meaning of options, that is their semantics\n" +
				"You don't have to change this number if your module still behaves the same way, \n" +
				"e.g. if you only change default values for certain submodules and if that does not \n" +
				"change the way your module works.",
				attachFilter.Version);
		oVer.setBounds(0,null);		
		oExtList = new OptExternalizedList(attachFilter);
				
		fop.add(oName);
		fop.add(oCategory);
		fop.add(oDesc);
		fop.add(oVer);
		fop.add(oExtList);
		
		boolean closedWithAccept = fop.ShowWindow("this pipeline",
				"Here you can specify the name, description and version and externalize submodule options",
				parent);
		
        if (closedWithAccept) {
        	// update Filter settings
			attachFilter.setName(getName());
			attachFilter.setCategory(getCategory());
			attachFilter.setDescription(getDesc());	
			attachFilter.Version = getVersion();
			// The attachFilter's option list is updated in OptExternalizedList.accept();
        }
        
        return closedWithAccept;
	}

	private class OptExternalizedList extends OptBase implements ActionListener {

		private ComplexFilter attachFilter;
		private JButton mOptionsRemove, mOptionsAdd, mOptionsEdit;
		private JList mOptionsList;
		private JPanel mOptionsButtons;
		private DefaultListModel OptionLM;
		private FilterOptions parent;

		public OptExternalizedList(ComplexFilter attach) {
			super("Externalized options",
					"Select which submodule options to externalize. \n" +
					"Externalized options can be modified by users of your pipeline.");
			attachFilter=attach;			
		}
		
		public void notify(FilterOptions pnt) {parent=pnt;};
		
		protected void createEditArea() {
			super.createEditArea();
			JPanel mOptionsPanel = this.EditArea;
			OptionLM = new DefaultListModel();
			mOptionsList = new JList(OptionLM);
			mOptionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			for (int i=0; i!=attachFilter.Options.getValues().size(); ++i) {
				((OptExternalized)attachFilter.Options.get(i)).makeName();
				OptionLM.add(i,attachFilter.Options.get(i));
			}
			JScrollPane mOptionListScroller = new JScrollPane(mOptionsList);
			mOptionsPanel.add(mOptionListScroller);
			mOptionsButtons = new JPanel();
			mOptionsButtons.setLayout(new BoxLayout(mOptionsButtons, BoxLayout.Y_AXIS));
			mOptionsAdd = new JButton("Add");
			mOptionsAdd.addActionListener(this);
			mOptionsButtons.add(mOptionsAdd);
			mOptionsEdit = new JButton("Edit");
			mOptionsEdit.addActionListener(this);
			mOptionsButtons.add(mOptionsEdit);
			mOptionsRemove = new JButton("Remove");
			mOptionsRemove.addActionListener(this);
			mOptionsButtons.add(mOptionsRemove);
			mOptionsPanel.add(mOptionsButtons, BorderLayout.EAST);
			mOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		
		public boolean validate() {	return true; }

		public void accept() { 
			attachFilter.Options = new FilterOptions(attachFilter);
			for (int i=0; i!=OptionLM.size(); ++i) 				
				attachFilter.Options.add((OptExternalized)OptionLM.get(i));
		}

		public void cancel() {}
		// I don't need to do anything here, because this object will not be used again.
		// FilterOptions will create a new instance instead.

		public String ValueToString() {return null;} //will never be used

		public void ValueFromString(String valueStr) {} //will never be used
		
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource().equals(mOptionsRemove) && mOptionsList.getSelectedValue()!=null) {
				OptionLM.remove(mOptionsList.getSelectedIndex());
			}
			else
			if (arg0.getSource().equals(mOptionsAdd)) {
				// Show popup menu with possible choices for externalizable options
				JPopupMenu pm = new JPopupMenu("Select an option");
				for (FilterNode fn : attachFilter.sortedFilters) {
					if (fn.attachedFilter.Options.externalizableCount()>0) {
						JMenu M = new JMenu("("+fn.getFilterIndex()+") "+fn.attachedFilter.getName());
						for (int i=0; i!=fn.attachedFilter.Options.getValues().size(); ++i) {
							OptBase fo = fn.attachedFilter.Options.get(i);
							if (fo.allowExternalize()) {
								JMenuItem MI = new JMenuItem(fo.Name);
								MI.setActionCommand(fn.getFilterIndex()+","+i); // send filterindex+optionindex
								MI.addActionListener(this);
								M.add(MI);							
							}
						}
						M.addActionListener(this);
						pm.add(M);					
					}
				}
				pm.pack();
				pm.show(mOptionsAdd,0,(int)mOptionsAdd.getBounds().getHeight());
			} 
			else 
			if (arg0.getSource().equals(mOptionsEdit) && mOptionsList.getSelectedValue()!=null) {
				((OptExternalized)mOptionsList.getSelectedValue()).editMetaOptions(this.parent.getDialog());
			}		
			else 
			if (arg0.getSource() instanceof JMenuItem) {
				String[] indices = arg0.getActionCommand().split(",");
				int FilterIndex = Integer.parseInt(indices[0]);
				int OptionIndex = Integer.parseInt(indices[1]);
				OptExternalized foe = new OptExternalized(attachFilter, FilterIndex, OptionIndex);
				int i;
				for (i=0; i!=OptionLM.getSize(); ++i) {
					OptExternalized candidate = (OptExternalized)OptionLM.get(i);
					if (candidate.equals(foe)) {
						ExceptionHandler.handle(new Exception("This option is already in the list"),this.parent.getDialog());
						i=-1;
						break;					
					}
				}
				if (i!=-1) {
					OptionLM.addElement(foe);
					foe.editMetaOptions(this.parent.getDialog());
					mOptionsList.setSelectedValue(foe,true);				
				}	
			}
		}	
	}
	
	/** Returns the (possible changed) name of the ComplexFilter
	 * @return the name
	 */
	public String getName() {
		return ((oName.Value!=null)? oName.Value : "");	
	}
	
	/** Returns the (possible changed) description of the ComplexFilter
	 * @return the new description 
	 */
	public String getDesc() {
		return ((oDesc.Value!=null)? oDesc.Value : "");
	}
	
	/** Returns the (possible changed) version number of the ComplexFilter
	 * @return the new version number
	 */
	public int getVersion() {
		return oVer.Value;
	}
	
	public String getCategory() {
		return oCategory.Value;
	}

}
