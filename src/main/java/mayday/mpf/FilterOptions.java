package mayday.mpf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import mayday.core.gui.MaydayDialog;
import mayday.core.io.StorageNode;
import mayday.mpf.options.OptBase;
import mayday.mpf.options.OptExternalized;


/**
 * FilterOptions holds a filter's option list and contains methods for editing filter options
 * @author Florian Battke
 */
public class FilterOptions implements ActionListener {
	
	private Vector<OptBase> Options = new Vector<OptBase>();
	public boolean closedWithAccept;
	private boolean isVisible = false;
	private FilterBase parent;
	
	public FilterOptions(FilterBase parent) {
		this.parent=parent;
	}
	
	/** Adds a new option to the option list
	 * @param fo the option to add
	 */
	public void add(OptBase fo) {
		Options.add(fo);
		fo.setParent(this.parent);
	}
	
	/** Removes a specified option from the option list
	 * This function is used by the RWrapper filter plugin. Be VERY careful when using this function! 
	 * Removing an option changes indizes of all following options in the Options vector and thus
	 * externalization does no longer work and in consequence, your plugin will most likely not
	 * work in Filter Pipelines. Because of the danger, I decided to mark this method deprecated. 
	 * @param fo The option to remove
	 */
	@Deprecated
	public void remove(OptBase fo) {
		Options.remove(fo);
	}
	
	/** Returns the option specified by the given index
	 * @param index the index of the option to return
	 * @return the option
	 */
	public OptBase get(int index) { 
		return Options.get(index);
	}
	
	/** Returns a vector of all options
	 * @return the vector
	 */
	public Vector<OptBase> getValues() {
		return Options;
	}
	
	/** Returns the number of options that can be externalized
	 * i.e. the number of options that can be presented to the user by an enclosing complex filter
	 * @return the number of externalizable options
	 */
	public int externalizableCount() {
		int i=0;
		for (OptBase fo : Options) 
			if (fo.allowExternalize()) ++i;
		return i;
	}
	
	public JDialog getDialog() {
		return mWin;
	}
	
	private JDialog mWin;
	private JButton mOK, mCancel;
	private JPanel OptList = new JPanel();
	
	
	/** Shows the option window 
	 * @param FilterName The name of the filter that is being edited
	 * @param FilterDesc The description of the filter
	 * @param parent A ParentWindow to make us modal
	 * @return true if the dialog was closed by clicking on "OK"
	 */
	public boolean ShowWindow(String FilterName, String FilterDesc, JDialog parent) {
		mWin = new MaydayDialog(parent,"Options for "+FilterName);
		mWin.setModal(true);
		return ShowWindow(FilterName, FilterDesc);
	}
		
	/** Same as ShowWindow(String, String, JDialog) but using a JFrame as parent
	 * @param FilterName The name of the filter that is being edited
	 * @param FilterDesc The description of the filter
	 * @param parent A ParentDialog to make us modal
	 * @return true if the dialog was closed by clicking on "OK"
	 */
	public boolean ShowWindow(String FilterName, String FilterDesc, JFrame parent) {
		mWin = new MaydayDialog(parent,"Options for "+FilterName);
		mWin.	setModal(true);
		return ShowWindow(FilterName, FilterDesc);
	}
	
	public void FillOptionPanel(String FilterName, String FilterDesc, JPanel targetPanel) {
		//JPanel oldOptList = OptList;
		//boolean vis = this.isVisible;
		OptList = targetPanel;
		this.isVisible = true;
		createOptionList();
		//this.isVisible = vis;
		//OptList = oldOptList;
	}
	
	private class HelpBtn extends JButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final OptBase o;
		public HelpBtn(OptBase fo) { 
			super("?");
			addActionListener(this);
			setMargin(new java.awt.Insets(1,5,1,5));
			setAlignmentY(Component.TOP_ALIGNMENT);
			o=fo;
		}
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane.showMessageDialog(mWin, o.Description, o.Name, JOptionPane.INFORMATION_MESSAGE);			
		}
	}
	
	/** Creates GUI elements for all options in the list
	 */
	public void createOptionList() {
		if (!this.isVisible) return; // avoid initializing EditAreas when it's just a waste of time
		// don't change this, RWrapper.OptIntegerFancy depends on it!
		
		OptList.removeAll();
		OptList.setLayout(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();
		cst.fill = GridBagConstraints.BOTH;
		cst.anchor = GridBagConstraints.FIRST_LINE_START;
		cst.weighty=0.0; // all equal
		cst.gridy=0;
		for (OptBase fo : Options) {
			fo.notify(this);
			JPanel OptionJPanel = fo.getEditArea(); // must call this at least once to initialize components
			if (!fo.isVisible()) continue;
			JPanel NameAndHelpAndOption = new JPanel(new FlowLayout(FlowLayout.LEFT));
			if (fo.Description!=null) NameAndHelpAndOption.add(new HelpBtn(fo));
			JLabel NameLabel = new JLabel();
			NameLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			if (fo instanceof OptExternalized) 
				NameLabel.setText(((OptExternalized)fo).getShortName());
			else
				NameLabel.setText(fo.Name);
			NameLabel.setToolTipText(fo.Description);
			NameAndHelpAndOption.add(NameLabel);
			NameAndHelpAndOption.setAlignmentX(Component.LEFT_ALIGNMENT);
			cst.weightx=0.0; // first column just big enough 
			cst.gridx=0;
			OptList.add(NameAndHelpAndOption, cst);
			cst.gridx=1;
			cst.weightx=1.0; // second column fills
			OptList.add(OptionJPanel, cst); 	
			cst.gridy+=1;
		}
		cst.weighty=1.0; //vertical fill
		OptList.add(Box.createRigidArea(new java.awt.Dimension(0,0)), cst);
		OptList.revalidate();
		if (mWin!=null) mWin.pack();
	}
	
	public void initializeVisibility() {
		for (OptBase fo : Options) fo.getEditArea();		
	}
	
	private boolean ShowWindow(String FilterName, String FilterDesc) {
		return ShowWindow(FilterName, FilterDesc, false);
	}
	
	private boolean ShowWindow(String FilterName, String FilterDesc, boolean noShow) {
		isVisible=true; // set it here so that createOptionList knows what's happening
		// mWin.getContentPane().setLayout(new BoxLayout(mWin.getContentPane(),BoxLayout.Y_AXIS));
		mWin.getContentPane().setLayout(new BorderLayout());
		
		JTextArea mDesc = new JTextArea(FilterDesc);
		mDesc.setEditable(false);
		mDesc.setFont(UIManager.getFont(mWin)); // TextArea font should fit other GUI fonts
		mDesc.setLineWrap(true);
		mDesc.setWrapStyleWord(true);
		JScrollPane mSp = new JScrollPane(mDesc);
		mSp.setPreferredSize(new java.awt.Dimension(200,100));
		
		OptList = new JPanel();
		createOptionList();
		JScrollPane OptSp = new JScrollPane(OptList);
		
		mWin.getContentPane().add(
				new JSplitPane(JSplitPane.VERTICAL_SPLIT, mSp, OptSp),
				BorderLayout.CENTER);
		
		JPanel Buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));				
		mCancel = new JButton("Cancel");
		mCancel.setMnemonic(KeyEvent.VK_ESCAPE);
		mCancel.addActionListener(this);
		mCancel.setToolTipText("Press ESCAPE to cancel");
		Buttons.add(mCancel);		
		mOK = new JButton("OK");
		mOK.addActionListener(this);
		mOK.setMnemonic(KeyEvent.VK_ENTER);
		mOK.setToolTipText("Press CTRL-ENTER to accept changes");		
		Buttons.add(mOK);	
		mWin.getContentPane().add(Buttons, BorderLayout.SOUTH);		
		
		mWin.pack();
		mWin.setLocationRelativeTo(null); //center        
        
		if (!noShow) {
			mWin.setVisible(true);
			this.isVisible = false;
		}
		return closedWithAccept;
	}
		
	public void acceptAll() {
		boolean allAccepted = true;
		for (int i=0; i!=Options.size() && allAccepted; ++i)
			allAccepted &= Options.get(i).validate();
		if (!allAccepted) return; // don't close window
		// If all options validate, accept changes
		for (OptBase fo : Options) fo.accept(); // this can trigger ActionEvents
		closedWithAccept=true;
		if (mWin!=null) mWin.dispose();		
	}
	
	public void cancelAll() {
		for (OptBase fo : Options) fo.cancel();
		closedWithAccept=false;
		if (mWin!=null) mWin.dispose();		
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(mOK)) acceptAll();
		else 
		if (arg0.getSource().equals(mCancel)) cancelAll();
	}

	/** Returns a StorageNode representing all options in this list
	 * @return the StorageNode (with attached subtree)
	 */
	public StorageNode toStorageNode() {
		StorageNode optionRoot = new StorageNode("Options","");
		for (int i=0; i!=Options.size(); ++i) {
			optionRoot.addChild(new Integer(i).toString(), Options.get(i).ValueToString());
		}
		return optionRoot;
			
	}
	
	/** Initializes the option list from a StorageNode created by toStorageNode()
	 * @param sn The StorageNode to load from
	 * @throws Exception If some options can not be restored
	 */
	public void fromStorageNode(StorageNode sn) throws Exception {
		// Exception handling: First we collect exceptions for all options that don't work, then we post
		// a new exception listing all those options that failed. Thus we are sure that all other options
		// are loaded before the exception is handed up the stack.
		String exMessage="";
		// 080311: options need to be processed in the correct order (e.g. for the R plugin to work)
		for (int i=0; i!=sn.getChildren().size(); ++i) {
			StorageNode child = sn.getChild(""+i);
			OptBase opt;
			// A) Determine which option to initialize
			try {
				// Two exceptions can happen here: 
				// 1) child.Name could not be parseable to integer
				// 2) child.Name could specify an index out of range
				opt = this.Options.get(Integer.parseInt(child.Name));
			} catch (Throwable t) {
				exMessage+="- Illegal option index specified: \""+child.Name+"\"\n";
				opt=null;
			}
			// B) initialize this option
			if (opt!=null) {
				// Here we could have e.g. an IllegalArgumentException coming from the option
				try {			
					opt.ValueFromString(child.Value);
				} catch (Throwable t) {
					exMessage+="- Could not initialize option \""+opt.Name+"\". Using default value.\n";
				}				
			}
		}
		if (exMessage!="") 
			throw new Exception("Some option values could not be restored:\n"+exMessage);
	} 	

	
}
