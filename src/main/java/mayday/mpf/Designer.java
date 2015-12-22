package mayday.mpf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import mayday.core.gui.MaydayFrame;
import mayday.mpf.options.OptBase;
import mayday.mpf.options.OptExternalized;
import mayday.mpf.options.OptInteger;

/**
 * Designer provides the graphical framework to edit an instance of ComplexFilter, i.e.
 * addition of new subfilters, removal of subfilters, setting up connections between subfilters,
 * setting option values for subfilters, validating the filter graph, as well as loading and 
 * saving ComplexFilters
 * @author Florian Battke
 */
public class Designer implements ActionListener, /*ItemListener, */WindowListener {
	
	private JFrame mFrame; 

	private DesignerPanel mPanel;
	private FilterClassList FilterList; // one object for every filter found
	
	private ComplexFilter Filter = new ComplexFilter(1,1); // this is the object we are editing.
	private boolean unsavedChanges = false;
	
	private JMenuItem fileOpen, fileSave, fileSaveAs, fileClose, fileNew, optionIO, optionValidate;
	//private JComboBox mAddFilter;
	
    private void createGUI() { 	
        //Create and set up the window.
        mFrame = new MaydayFrame(Constants.Designer.WINDOWCAPTION);
        //mFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	mFrame.addWindowListener(this);
        
        // Create Menu and Button Bar
        JMenuBar menuBar = new JMenuBar();
        	
        	JMenu fileMenu = new JMenu("File");
        		fileNew = new JMenuItem("New");
        		fileNew.addActionListener(this);
        		fileMenu.add(fileNew);
        		
        		fileMenu.addSeparator();
        	
        		fileOpen = new JMenuItem("Open...");
        		fileOpen.addActionListener(this);
        		fileMenu.add(fileOpen);        		
        		
        		fileSave = new JMenuItem("Save");
        		fileSave.addActionListener(this);
        		fileMenu.add(fileSave);
        		
        		fileSaveAs = new JMenuItem("Save as...");
        		fileSaveAs.addActionListener(this);
        		fileMenu.add(fileSaveAs);

        		fileClose = new JMenuItem("Close");
        		fileClose.addActionListener(this);
        		fileMenu.add(fileClose); 
        	menuBar.add(fileMenu);
        	
        	JMenu optionMenu = new JMenu("Options");
        		optionIO = new JMenuItem("Set module options...");
        		optionMenu.add(optionIO);
        		optionIO.addActionListener(this);
        		optionValidate = new JMenuItem("Validate module");
        		optionValidate.addActionListener(this);
        		optionMenu.add(optionValidate);
        	menuBar.add(optionMenu);
        	        	
        	JMenu addModuleMenu = new JMenu("Add module");
				HashMap<String, JMenu> CategoryList = new HashMap<String, JMenu>();
				for (String cat : FilterClassList.getInstance().getCategories()) {
					JMenu catM = new JMenu(cat);
					CategoryList.put(cat, catM);
					addModuleMenu.add(catM);
				}				
				for (FilterClassList.Item fcli : FilterClassList.getInstance().getValues()) {
					JMenu catM = CategoryList.get(fcli.getCategory());
					JMenuItem jmi = new JMenuItem(fcli.toString());
					jmi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							addFilter(((JMenuItem)arg0.getSource()).getText());
						}
					});
					catM.add(jmi);
				}
			menuBar.add(addModuleMenu);

        	/*mAddFilter = new JComboBox();
        	mAddFilter.addItem("Add new module...");
        	for (FilterClassList.Item fb : FilterList.getValues()) mAddFilter.addItem(fb);
        	mAddFilter.setSelectedIndex(0);
        	mAddFilter.addItemListener(this);
        	menuBar.add(mAddFilter); */
        	       	
        mFrame.setJMenuBar(menuBar);
        
        mPanel = new DesignerPanel();

        JScrollPane scrollPane = new JScrollPane(mPanel);
        mFrame.getContentPane().add(scrollPane);
                             
        // create all visual elements etc...
        adoptFilter(Filter);
        
        //Display the window.
        mFrame.pack();
        mFrame.setLocationRelativeTo(null); //center        
        mFrame.setSize(640,480);
    }

    private void makeTitle() {
		mFrame.setTitle(Constants.Designer.WINDOWCAPTION+": "+Filter.getName()
				+ (unsavedChanges?" (unsaved changes)":""));    	
    }
    
    /** Shows or hides this window
     * @param visibility true to show the window, false to hide it
     */
    public void setVisible(boolean visibility) {
    	mFrame.setVisible(visibility);
    }
    
	/** Shows this window in a modal way
	 * @param parent the parent Window to disable while this window is visible
	 */
	public void showModal(java.awt.Window parent) {
		ModalFrameUtil.showAsModal(mFrame,parent);
	}
    
    /* To make a connection, the user clicks on an outgoing slot of one filter,
     * this calls setSendingSlot. Then the user clicks on the incoming slot of
     * another filter, calling setReceivingSlot, the connection is made between
     * the two FilterNode objects. 
     */
    private FilterNode currentSendingNode;
    private int currentSendingSlot;
    
    private void setSendingSlot(FilterNode sendingNode, int sendingSlot) {
    	currentSendingNode=sendingNode; currentSendingSlot=sendingSlot;
    }
    
    private void setReceivingSlot(FilterNode receivingNode, int receivingSlot) { 
    	if (currentSendingNode==null) {
    		JOptionPane.showMessageDialog(mFrame, "Please select an outgoing slot before selecting an incoming slot", "Hint", JOptionPane.ERROR_MESSAGE); 
    	} else if (currentSendingNode==receivingNode) {
    		JOptionPane.showMessageDialog(mFrame, "Cannot connect an object to itself. Select another target.","Hint",JOptionPane.ERROR_MESSAGE);
    	} else {
    		
    		// remove previously existing connections of both objects
    		FilterNode oldReceiver = currentSendingNode.Output[currentSendingSlot].Node;
    		if (oldReceiver!=null) oldReceiver.connectInput(currentSendingNode.Output[currentSendingSlot].Slot,null,-1);
    		FilterNode oldSender = receivingNode.Input[receivingSlot].Node;
    		if (oldSender!=null) oldSender.connectOutput(receivingNode.Input[receivingSlot].Slot,null,-1);
    		
    		// Set up new connection    		
    		currentSendingNode.connectOutput(currentSendingSlot,receivingNode,receivingSlot);
    		receivingNode.connectInput(receivingSlot,currentSendingNode,currentSendingSlot);
   		
    		// deselect current sending slot
    		currentSendingNode=null;
    		// We have unsaved changes
    		setUnsavedStatus(true);
    	}
    }
    
    private void removeSubfilter(FilterNode NodeToRemove) {
    	// ask to be sure
    	if (JOptionPane.showConfirmDialog(mFrame, "Do you really want to remove this object?",
    			"Confirm removal", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
    		// make sure no externalized option still points to this item
    		for (OptBase fo: NodeToRemove.attachedFilter.Options.getValues()) {
    			for (OptBase oe : Filter.Options.getValues()) {
    				if (((OptExternalized)oe).equals(fo)) {    					
    					Filter.Options.getValues().remove(oe);
    					break;
    				}
    			}
    		}
       		Filter.remove(NodeToRemove);
           	mPanel.remove(NodeToRemove.getVisualNode());    		
    		// We have unsaved changes
    		setUnsavedStatus(true);
    	}
    }
    
    @SuppressWarnings("deprecation")
	private void removeBrokenExternalizedOptions() {
		Vector<OptBase> markedForRemoval = new Vector<OptBase>();
		for (OptBase oe : this.Filter.Options.getValues()) {
    		try {
    			((OptExternalized)oe).getEditArea(); //this will fail if the option is broken
    		} catch (Exception e) {
    			markedForRemoval.add(oe);
    		}
    	}
		for (OptBase ob : markedForRemoval)
			this.Filter.Options.remove(ob);
		if (markedForRemoval.size()>0) 
			ExceptionHandler.handle(new Exception("Some externalized options had to be removed as a result of your changes."),this.mFrame);
    }
      
    /* Graph layout code follows 
     * Idea behind the code:
     * - After building the graph, every node has a correct FilterIndex. 
     * - We can determine the X coordinate of any node as the maximum over all
     *   incoming connections, X coordinate of incoming object+width of object+spacer
     * - The Y coordinate is more complicated. We can compute the average Y center point
     *   of all incoming objects and use it as our own Y center point, where Y=YCenter-(YSize/2).
     *   We are bound to get overlapping objects, so we have to check for that, and if we do, we
     *   take the object with higher FilterIndex and move it downward until the overlap is removed.
     */
    private void computeLayout() {
    	int panelX=0, panelY=0;    	
    	for (int i=0; i!=Filter.sortedFilters.size(); ++i) {
    		FilterNode currentNode = Filter.sortedFilters.get(i);
    		VisualNode currentVis = currentNode.getVisualNode();
    		/* ==== Pretty moving upgrade: ===
    		 * This is a bit strange:
    		 * For the overlap calculation, we set each VisualNode to its final position while saving
    		 * the current position in VisualNode.targetX and .targetY. After we have calculated the positions 
    		 * of all nodes, we swap the current and target positions so that moving can start (see below) 
    		 */
    		currentVis.targetX = currentVis.getX();
    		currentVis.targetY = currentVis.getY();
    		// Calculate overlap
    		int Xpos=0; 
    		int Ypos=0;
    		int count=0;
    		for (FilterSlot fs : currentNode.Input) {
    			FilterNode fn = fs.Node;
    			if (fn!=null && fn.getFilterIndex()<currentNode.getFilterIndex()) {
    				VisualNode vn = fn.getVisualNode();
    				Xpos = (Xpos<=(vn.getX()+vn.getWidth())) ? vn.getX()+vn.getWidth() : Xpos; // max
    				Ypos += vn.getY() + (vn.getHeight()/2); // center coord
    				++count;
    			}    			
    		}    
    		if (count>0) Ypos /= count;
    		Xpos += 30;
    		Ypos -= currentVis.getHeight()/2;
    		if (Ypos<30) Ypos=30;
    		// Set coords
    		currentVis.setLocation(Xpos,Ypos);
    		// Remove overlap: check all smaller indices
    		for (int j=0; j!=currentNode.getFilterIndex(); ++j) {
    			FilterNode fn = Filter.sortedFilters.get(j);
    			VisualNode vn = fn.getVisualNode();
    			java.awt.Rectangle intersection = vn.getBounds().intersection(currentVis.getBounds());
    			// in case of overlap, move this object downwards
    			if (!intersection.isEmpty()) {
    				currentVis.setLocation(currentVis.getX(),vn.getY()+vn.getHeight()+20);
    			}    			
    		}
    		// Keep maximum X and Y values
    		panelX = panelX>currentVis.getX()+currentVis.getWidth() ? panelX : currentVis.getX()+currentVis.getWidth();
    		panelY = panelY>currentVis.getY()+currentVis.getHeight() ? panelY : currentVis.getY()+currentVis.getHeight();
    	}
    	// Set correct size for scrollable area
    	mPanel.setPreferredSize(new java.awt.Dimension(panelX+30, panelY+30));
    	mPanel.revalidate();
    	/* === Pretty moving ====
    	 * now we can set the target locations for all nodes correctly and make them move */
    	for (FilterNode fn : Filter.sortedFilters) {
    		VisualNode vn = fn.getVisualNode();
    		// we only want the node to move if it is not being displayed for the first time right now
    		if (vn.targetX!=0 || vn.targetY!=0) { // here, target still means source, I know it's odd, but we save variables
    			int tX = vn.targetX, tY=vn.targetY;
    			vn.targetX = vn.getX(); vn.targetY=vn.getY();
    			vn.setLocation(tX,tY);
    		} else {
    			vn.targetX=vn.getX(); vn.targetY = vn.getY(); // no movement please
    		}
    		
    	}
    	mPanel.startMoving();
    }
    
    /** returns the filter currently edited by this Designer 
     * @return the attached filter
     */
    public ComplexFilter getFilter() {
    	return Filter;
    }
    
    /** sets the ComplexFilter instance that this Designer edits
     * @param f the ComplexFilter instance to edit
     */
    public void adoptFilter(ComplexFilter f) {
    	if (f==null) f=new ComplexFilter(1,1); // we don't allow null filters 
		mPanel.removeAll();
		
		// We add this complex filter to the Recursion prevention stack
		ComplexFilter.RecursionSteps.clear();
		ComplexFilter.RecursionSteps.push(f.nameOnDisk);
		
		Filter = f;
		// Build the filter
        try {
        	Filter.buildGraph();
        	this.computeLayout();
        } catch (Exception e) {
        	ExceptionHandler.handle(e, mFrame);
        }
		// connect to necessary events
		Filter.globalIn.attachedFilter.Options.get(0).addActionListener(this);
		Filter.globalOut.attachedFilter.Options.get(0).addActionListener(this);
		// Do some special magic for the input/output dummies
		Filter.globalIn.getVisualNode().setVisibility(true,false);
		Filter.globalOut.getVisualNode().setVisibility(true,false);
		// show all new nodes and register listeners
		for (FilterNode fn : Filter.Filters) {
			VisualNode vn = fn.getVisualNode();
			mPanel.add(vn);
			vn.addActionListener(this);
		}
		setUnsavedStatus(false);
		fileSave.setEnabled(Filter.nameOnDisk!=null);
    }
    
    private boolean Save(String Name) {
    	try {
    		File parent = new File(Constants.FILTERPATH);
    		if (!parent.exists())
    			parent.mkdirs();
        	File file = new File(Constants.FILTERPATH+Name);    	
    		if (file.exists()) { // Prevent accidental overwrite
    			if (JOptionPane.showConfirmDialog(mFrame, 
    					"Do you really want to overwrite the existing file \""+file.getName()+"\"?",
    					"Confirm file overwrite", 
    					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)
    				!=JOptionPane.YES_OPTION) 
    				return false;
    		}
    		BufferedWriter wr = new BufferedWriter ( new FileWriter (file));
    		Filter.SaveToStream(wr);
    		wr.flush();
    		wr.close();    	
    		Filter.nameOnDisk=Name;
    		JOptionPane.showMessageDialog(mFrame, "File was saved as "+file.getAbsolutePath()+".","File saved",JOptionPane.INFORMATION_MESSAGE);
    		// We have no more unsaved changes
    		setUnsavedStatus(false);    		
    		this.FilterList.update(file.getAbsolutePath());
    		return true;    		
    	} catch (Exception e) {
    		ExceptionHandler.handle(e,mFrame);
    	}
		return false;
    }
    
    
    
    private boolean SaveDialog() {
    	//first we show the options window
		OptionEditor oed = new OptionEditor();
		if (oed.ShowWindow(mFrame, Filter)) {
			makeTitle();
			String filename = Filter.getName()+Constants.FILEEXT;
			filename.replaceAll(File.pathSeparator,"_"); 
			return Save(filename);
		}
		return false;
    }
    
    private boolean GenericSave() {
    	if (Filter.nameOnDisk==null) 
    		return SaveDialog();
    	else
    		return Save(Filter.nameOnDisk);
    }
    
    private int AskToSave() {
    	return JOptionPane.showConfirmDialog(mFrame, "Do you want to save changes before proceeding?",
    			"Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    }
    
    private void closeWindow() {
		// Filter should be valid before user closes
		try { Filter.validateGraph(); }
		catch (Throwable t) {
			if (JOptionPane.showConfirmDialog(mFrame,
					"This module does not validate:\n\n"+t.getMessage()+"\n\nDo you still want to close?",
					"Validation failed",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
					)!=JOptionPane.YES_OPTION) return;
		}
		int choice = (unsavedChanges ? AskToSave() : JOptionPane.NO_OPTION); //if nothing changed, we don't want to save
		if (choice==JOptionPane.YES_OPTION) 
			if (!GenericSave()) return; //wanted to save but didn't ==> don't proceed
		if (choice!=JOptionPane.CANCEL_OPTION && choice!=JOptionPane.CLOSED_OPTION) {
			// Remove this filter from the recursion prevention stack!
			ComplexFilter.RecursionSteps.clear(); 
			mFrame.dispose();
		}			
    }
    
    private void setUnsavedStatus(boolean isUnsaved) {
    	unsavedChanges=isUnsaved;
    	makeTitle();
    }
    	
	public void actionPerformed(ActionEvent AE)  {		
		boolean layoutChanged=false;
		
		// VisualNode actions
		if (AE.getSource() instanceof FilterNode) {
			FilterNode fn = (FilterNode)AE.getSource();
			String cmd = AE.getActionCommand();
			if (cmd==VisualNode.OptionClicked) {
				fn.attachedFilter.ShowOptions(mFrame);
				// We MAY have unsaved changes
	    		setUnsavedStatus(true);
	    		// And furthermore, we may have a problem with externalized options that point
	    		// to options that no longer exist (as with the RWrapper dynamically creating options)
	    		removeBrokenExternalizedOptions();
			} else 
			if (cmd==VisualNode.RemoveClicked) {
				this.removeSubfilter(fn);
				layoutChanged=true;
			} else 
			if (cmd.startsWith(VisualNode.SendingSlot)) {
				int SlotNo = Integer.parseInt(cmd.split("=")[1]);
				this.setSendingSlot(fn,SlotNo);
			} else 
			if (cmd.startsWith(VisualNode.ReceivingSlot)) {
				int SlotNo = Integer.parseInt(cmd.split("=")[1]);
				this.setReceivingSlot(fn,SlotNo);
				layoutChanged=true;
			}
		} else {
			// Desinger Menu items
			if (AE.getSource().equals(fileNew)) {
				int choice = (unsavedChanges ? AskToSave() : JOptionPane.NO_OPTION); //if nothing changed, we don't want to save
				if (choice==JOptionPane.YES_OPTION) 
					if (!GenericSave()) return; //wanted to save but didn't ==> don't proceed
				if (choice!=JOptionPane.CANCEL_OPTION && choice!=JOptionPane.CLOSED_OPTION) {
					adoptFilter(new ComplexFilter(1,1));
					layoutChanged=true;
				}
			} else
			if (AE.getSource().equals(fileSave)) {
				Save(Filter.nameOnDisk);
			} else
			if (AE.getSource().equals(fileSaveAs)) {
				SaveDialog();
				fileSave.setEnabled(Filter.nameOnDisk!=null);
			} else
			if (AE.getSource().equals(fileOpen)) {
				int choice = (unsavedChanges ? AskToSave() : JOptionPane.NO_OPTION); //if nothing changed, we don't want to save
				if (choice==JOptionPane.YES_OPTION) 
					if (!SaveDialog()) return; //wanted to save but didn't ==> don't proceed
				if (choice!=JOptionPane.CANCEL_OPTION && choice!=JOptionPane.CLOSED_OPTION) {
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new File(Constants.FILTERPATH));
					if (fc.showOpenDialog(mFrame) == JFileChooser.APPROVE_OPTION) {
						try {
							ComplexFilter.RecursionSteps.clear(); 
							adoptFilter(new ComplexFilter(fc.getSelectedFile().getCanonicalPath()));
							layoutChanged=true;
						} catch (Exception e) {
							ExceptionHandler.handle(e,mFrame);
						}
					}
				}
			} else
			if (AE.getSource().equals(fileClose)) {
				closeWindow();
			} else
			if (AE.getSource().equals(optionIO)) {
				OptionEditor oed = new OptionEditor();
				if (oed.ShowWindow(mFrame, Filter)) {
		    		setUnsavedStatus(true);
				}
			} else
			if (AE.getSource().equals(optionValidate)) {
				try {
					Filter.validateGraph();
					JOptionPane.showMessageDialog(mFrame, "Validation was successful",
							"Validation", JOptionPane.INFORMATION_MESSAGE);
				} catch (Throwable t) {
					ExceptionHandler.handle(t,mFrame);
				}
			}
			// Complex filter input/output
			if (Filter!=null && Filter.globalIn!=null & Filter.globalOut!=null) {
				if (AE.getSource().equals(Filter.globalIn.attachedFilter.Options.get(0))) { // Input size changed
					Filter.InputSize = ((OptInteger)AE.getSource()).Value;
					Filter.InputData = new MaydayDataObject[Filter.InputSize];
					Filter.globalIn.attachedFilter.OutputSize = Filter.InputSize;
					Filter.globalIn.attachedFilter.OutputData = new MaydayDataObject[Filter.InputSize];
					Filter.globalIn.resetIOSizes();
					layoutChanged = true;
		    		setUnsavedStatus(true);
				} else
				if (AE.getSource().equals(Filter.globalOut.attachedFilter.Options.get(0))) { // Output size changed
					Filter.OutputSize = ((OptInteger)AE.getSource()).Value;
					Filter.OutputData = new MaydayDataObject[Filter.OutputSize];
					Filter.globalOut.attachedFilter.InputSize = Filter.OutputSize;
					Filter.globalOut.attachedFilter.InputData = new MaydayDataObject[Filter.OutputSize];
					Filter.globalOut.resetIOSizes();
					layoutChanged = true;
					unsavedChanges=true;
				}				
			}
		}
		
		if (layoutChanged) {
			try { Filter.buildGraph(); } catch (Throwable t) {ExceptionHandler.handle(t,mFrame);}
			computeLayout();
		}
	}
	
	public void windowClosing(WindowEvent arg0) {
		closeWindow();
	}

	public void windowIconified(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowActivated(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}

	public void addFilter(String FilterName) {
		try {
			FilterClassList.Item theItem = FilterList.getItemByName(FilterName);
			FilterBase newFilter = theItem.newInstance();
			FilterNode newNode = new FilterNode(newFilter);
			newNode.getVisualNode().addActionListener(this);
			Filter.add(newNode);
			mPanel.add(newNode.getVisualNode());
			//mAddFilter.setSelectedIndex(0);				
			Filter.buildGraph();
			computeLayout();
			setUnsavedStatus(true);
		} catch (Throwable t) {ExceptionHandler.handle(t, mFrame);} ;
	}
	
	/*
	public void itemStateChanged(ItemEvent arg0) {
		// Add a new filter object to this filter
		if (arg0.getSource().equals(mAddFilter) && arg0.getStateChange()==ItemEvent.SELECTED
			&& arg0.getItem() instanceof FilterClassList.Item) {
			try {
				FilterBase newFilter = ((FilterClassList.Item)arg0.getItem()).newInstance(FilterList);
				FilterNode newNode = new FilterNode(newFilter);
				newNode.getVisualNode().addActionListener(this);
				Filter.add(newNode);
				mPanel.add(newNode.getVisualNode());
				mAddFilter.setSelectedIndex(0);				
				Filter.buildGraph();
				computeLayout();
				setUnsavedStatus(true);
			} catch (Throwable t) {ExceptionHandler.handle(t, mFrame);} ;
		}
	}*/
	
	/** Creates a new instance of Designer using a given instance of FilterClassList as source for available subfilters
	 */
	public Designer() {
		FilterList = FilterClassList.getInstance();		
		createGUI(); 
	}
	 
	/** The main method provides test code for standalone debugging
	 * @param args filename
	 * @deprecated
	 */
	public static void main(String[] args) {
		Designer D = new Designer();
		try {
			if (args.length>0) {
				D.adoptFilter(new ComplexFilter(args[0]));
			}
		} catch (Throwable t) { ExceptionHandler.handle(t,D.mFrame); } 
		D.setVisible(true);
	}

}