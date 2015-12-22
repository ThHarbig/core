package mayday.mpf;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import mayday.core.MasterTable;
import mayday.core.gui.MaydayFrame;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskCancelledException;
import mayday.core.tasks.TaskStateEvent;

public class Applicator extends MaydayFrame implements WindowListener {
	
	private static final long serialVersionUID = 1L;
	
	//private ProgressMeter progressmeter = new ProgressMeter();
	//private MasterTable masterTable; // the mastertable we will be reintegrating our output into
	private volatile CancellationMessage cMgr = new CancellationMessage();
	private WorkerThread wThread;

	/** The data we get from Mayday (see MPFWrapper) */
	public Vector<MaydayDataObject> InputDataSets = new Vector<MaydayDataObject>();

	/** The data we hand back to Mayday (see MPFWrapper) */
	public Vector<MaydayDataObject> OutputDataSets = new Vector<MaydayDataObject>();
 
	private FilterClassList.Item selectedModule;
	private FilterBase selectedModuleInstance;
	
	private Semaphore semaphore = new Semaphore(1);
	
	private StepPanel[] StepPanels = new StepPanel[4]; 

	@SuppressWarnings("serial")
	private abstract class StepPanel extends JPanel {
		private JPanel CaptionPanel;
		private JLabel CaptionText;
		protected JPanel CenterPanel;
		private JPanel BottomPanel;
		protected JButton NextButton;
		protected JButton BackButton;
		private JPanel ButtonPanel;
		private JSeparator jSeparator2;
		private JSeparator jSeparator1;
		protected int myIndex;

		public StepPanel(String Caption, int index) {
			super(new BorderLayout());
			myIndex=index;
			// TOP PANEL
			{
				CaptionPanel = new JPanel();
				add(CaptionPanel, BorderLayout.NORTH);
				BoxLayout CaptionPanelLayout = new BoxLayout(CaptionPanel,	javax.swing.BoxLayout.Y_AXIS);
				CaptionPanel.setLayout(CaptionPanelLayout);
				CaptionPanel.setDoubleBuffered(false);
				CaptionPanel.setEnabled(false);
				CaptionPanel.setBorder(BorderFactory.createEmptyBorder(	5,	5,	5,	5));
				{
					CaptionText = new JLabel();
					CaptionPanel.add(CaptionText);
					CaptionText.setText(Caption);
				}
				{
					jSeparator1 = new JSeparator();
					CaptionPanel.add(jSeparator1);
				}
			}
			//CENTER PANEL
			CenterPanel = new JPanel();
			add(CenterPanel, BorderLayout.CENTER);
			initCenterPanel();			
			//BOTTOM PANEL
			{
				BottomPanel = new JPanel();
				BoxLayout BottomPanelLayout = new BoxLayout(BottomPanel, javax.swing.BoxLayout.Y_AXIS);
				BottomPanel.setLayout(BottomPanelLayout);
				add(BottomPanel, BorderLayout.SOUTH);
				BottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				{
					jSeparator2 = new JSeparator();
					BottomPanel.add(jSeparator2);
					ButtonPanel = new JPanel();
					BottomPanel.add(ButtonPanel);
					FlowLayout ButtonPanelLayout = new FlowLayout();
					ButtonPanelLayout.setAlignment(FlowLayout.RIGHT);
					ButtonPanel.setLayout(ButtonPanelLayout);
					{
						BackButton = new JButton();
						ButtonPanel.add(BackButton);
						BackButton.setText("< Back");
					}
					{
						NextButton = new JButton();
						ButtonPanel.add(NextButton);
						NextButton.setText("Next >");
					}
				}
			}
			
			NextButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					NextButtonHandler();
				}
			});		
			
			BackButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					BackButtonHandler();
				}
			});
		}
		
		protected void NextButtonHandler() {
			theCardLayout.next(Applicator.this.getContentPane());
			StepPanels[myIndex+1].update();  //update only on clicking "NEXT", not when going "BACK"
			Applicator.this.getRootPane().setDefaultButton(		
					StepPanels[myIndex+1].NextButton
			);
		}
		
		protected void BackButtonHandler() {
			theCardLayout.previous(Applicator.this.getContentPane());
		}
		
		protected abstract void initCenterPanel();

		@SuppressWarnings("unused")
		public void setCaptionText(String captionText) {
			CaptionText.setText(captionText);
		}				 
		
		public void update() {}
	}

	private class Step1Panel extends StepPanel {
		private static final long serialVersionUID = 1L;
		private JButton DeleteModuleButton;
		private JTextArea ModuleInfoLabel;
		private JScrollPane jScrollPane1;
		private JLabel jLabel2;
		private JLabel ExpectedInputsLabel;
		private JLabel DiscardedInputsLabel;
		private JLabel BatchInfoLabel;
		private JLabel SelectedInputsLabel;
		private JLabel jLabel1;
		private JPanel JobInfoPanel;
		private JPanel ModuleInfoPanel;
		private JButton NewModuleButton;
		private JTree ModuleTree;
		private DefaultMutableTreeNode ModuleRoot;
		private JButton ModuleEditButton;

		public Step1Panel() {
			super("Step 1 - Select a module",0);
			BackButton.setText("Cancel");
			NextButton.setEnabled(false);
		}

		protected void BackButtonHandler() {
			Applicator.this.dispose();
		}
		
		protected void NextButtonHandler() {
			try {
				selectedModuleInstance = selectedModule.newInstance();
				if (selectedModuleInstance instanceof ComplexFilter) ((ComplexFilter)selectedModuleInstance).validateGraph();
				selectedModuleInstance.setCancellationMessage(cMgr); // 060518: I set this here so the filter can check whether it's called from applicator
				super.NextButtonHandler();
			} catch (Exception e) {
				String msg = "\""+selectedModule.toString()+"\" can't be used:\n"+e.getMessage();
				ExceptionHandler.handle(new Exception(msg),Applicator.this);
			}
		}
		
		private void initModuleList() {
			ModuleRoot.removeAllChildren();
			
			TreeMap<String, DefaultMutableTreeNode> CategoryList = new TreeMap<String, DefaultMutableTreeNode>();
			TreeMap<String, DefaultMutableTreeNode> RootNodeFilters = new TreeMap<String, DefaultMutableTreeNode>();
			
			for (FilterClassList.Item fcli : FilterClassList.getInstance().getValues()) {
				// root category filters
				if (fcli.getCategory().trim().length()==0) {
					
					RootNodeFilters.put(fcli.toString(), new DefaultMutableTreeNode(fcli));
					
				} else {
					
					// find category if already there
					DefaultMutableTreeNode category = CategoryList.get(fcli.getCategory());
					if (category==null) {
						category = new DefaultMutableTreeNode(fcli.getCategory());
						CategoryList.put(fcli.getCategory(), category);
					}
					category.add(new DefaultMutableTreeNode(fcli));
					
				}
			}
			for (DefaultMutableTreeNode catNode : CategoryList.values())
				ModuleRoot.add(catNode);
			for (DefaultMutableTreeNode rnfNode : RootNodeFilters.values())
				ModuleRoot.add(rnfNode);
			
			((DefaultTreeModel)ModuleTree.getModel()).nodeStructureChanged(ModuleRoot);
			ModuleTree.expandRow(0);
		}
		
		private void ModuleSelectionChanged(FilterClassList.Item theSelectedModule) {
			selectedModule = theSelectedModule;
			SelectedInputsLabel.setText(""+InputDataSets.size());
			
			if (selectedModule==null) {
				NextButton.setEnabled(false);
				DeleteModuleButton.setEnabled(false);
				ExpectedInputsLabel.setText("-");
				BatchInfoLabel.setText("Please select a module first");
				DiscardedInputsLabel.setText("");
				ModuleInfoLabel.setText("\n\n\n\t\t(No module selected)");
				ModuleEditButton.setVisible(false);
			} 
			else 
			{
				DeleteModuleButton.setEnabled(selectedModule.isComplex);

				ModuleEditButton.setVisible(true);					
				if (selectedModule.isComplex) {
					ModuleEditButton.setText("Edit this pipeline");
					ModuleEditButton.setEnabled(true);
				} else {
					ModuleEditButton.setText("This is a basic processing module");
					ModuleEditButton.setEnabled(false);
				}
				
				ExpectedInputsLabel.setText(""+selectedModule.InputSize);
				
				int n = selectedModule.InputSize;
				int m = InputDataSets.size();
				String actionString ="";
				if (m>=2*n) actionString += "The module will be run "+m/n+" times in batch mode.";
				if (m>=n && m<2*n) actionString += "The module will be run once.";
				if (m<n) actionString += "Not enough input probe lists to run this module.";
				String discardString = "";
				if (m>n && m%n!=0) discardString+=+m%n + " probe list(s) will be ignored.";
				
				NextButton.setEnabled( (m>=n) );
				BatchInfoLabel.setText(actionString);
				DiscardedInputsLabel.setText(discardString);
				ModuleInfoLabel.setText(selectedModule.getDescription());
			}
		}
		
		private void HandleEditButton() {
			try {
				Designer D = new Designer();
		    	D.adoptFilter((ComplexFilter)selectedModule.newInstance());
		    	D.showModal(Applicator.this);
		    	initModuleList();
			} catch (Throwable t) {
				ExceptionHandler.handle(t,Applicator.this);
			}
		}
		
		private void HandleNewButton() {
			Designer D = new Designer();
	    	D.showModal(Applicator.this);
	    	initModuleList();
		}
		
		private void HandleDeleteButton() {
	    	if (JOptionPane.showConfirmDialog(Applicator.this,
	    			"Do you really want to delete \""+selectedModule.toString()+"\"?",
	    			"Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
	    		==JOptionPane.YES_OPTION) {
	    		try {
	    			File tmp = new File(selectedModule.filename);
	    			if (!tmp.delete()) 
	    				throw new Exception("Reason unknown.");
	    			else {
	    				JOptionPane.showMessageDialog(Applicator.this,
	    						"\""+selectedModule.toString()+"\" was deleted successfully.",
	    						"File deleted", JOptionPane.INFORMATION_MESSAGE);
	    				FilterClassList.getInstance().remove(selectedModule);
	    				selectedModule=null;
	    				((DefaultTreeModel)ModuleTree.getModel()).removeNodeFromParent(
	    						(DefaultMutableTreeNode)ModuleTree.getLastSelectedPathComponent()
	    				);
	    			}
	    		} catch (Exception e) {
	    			ExceptionHandler.handle(new Exception("File could not be deleted: "+e.getMessage()),Applicator.this);
	    		}
	    	}

		}
		
		protected void initCenterPanel() {
			{
				GridBagLayout CenterPanel1Layout = new GridBagLayout();
				CenterPanel1Layout.columnWidths = new int[] {7, 7, 7};
				CenterPanel1Layout.rowHeights = new int[] {7, 7, 7, 7};
				CenterPanel1Layout.columnWeights = new double[] {0.1, 0.1, 0.8};
				CenterPanel1Layout.rowWeights = new double[] {0.9, 0.2, 0.1, 0.0};
				CenterPanel.setLayout(CenterPanel1Layout);
				{
					ModuleRoot = new DefaultMutableTreeNode("Processing Modules");
					ModuleTree = new JTree(ModuleRoot);
					CenterPanel.add(new JScrollPane(ModuleTree), new GridBagConstraints(0, 0, 2, 3, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 0, 0));
					ModuleTree.addTreeSelectionListener(new TreeSelectionListener() {
						public void valueChanged(TreeSelectionEvent arg0) {
							Object selectedNode = ModuleTree.getLastSelectedPathComponent();
							if (selectedNode!=null) {
								Object selected = ((DefaultMutableTreeNode)selectedNode).getUserObject();
								if (selected instanceof FilterClassList.Item) 
									Step1Panel.this.ModuleSelectionChanged((FilterClassList.Item)selected);
							}
							else Step1Panel.this.ModuleSelectionChanged(null);
						}
					});
					initModuleList();
				}
				{
					NewModuleButton = new JButton();
					CenterPanel.add(NewModuleButton, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
					NewModuleButton.setText("New pipeline");
					NewModuleButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							HandleNewButton();
						}
					});
				}
				{
					DeleteModuleButton = new JButton();
					CenterPanel.add(DeleteModuleButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
					DeleteModuleButton.setText("Delete");
					DeleteModuleButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							HandleDeleteButton();
						}
					});
				}
				{
					ModuleInfoPanel = new JPanel();
					BorderLayout ModuleInfoPanelLayout = new BorderLayout();
					ModuleInfoPanel.setLayout(ModuleInfoPanelLayout);
					CenterPanel.add(ModuleInfoPanel, new GridBagConstraints(2, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
					ModuleInfoPanel.setBorder(BorderFactory.createTitledBorder(null, "Module Information", TitledBorder.LEADING, TitledBorder.TOP));
					{
						jScrollPane1 = new JScrollPane();
						ModuleInfoPanel.add(jScrollPane1, BorderLayout.CENTER);
						jScrollPane1.setBorder(new EmptyBorder(0,0,0,0));
						{
							ModuleInfoLabel = new JTextArea();
							jScrollPane1.setViewportView(ModuleInfoLabel);
							ModuleInfoLabel.setText("No module selected");
							ModuleInfoLabel.setOpaque(false);
							ModuleInfoLabel.setEditable(false);
							ModuleInfoLabel.setFont(UIManager.getFont(this)); // TextArea font should fit other GUI fonts
							ModuleInfoLabel.setLineWrap(true);
							ModuleInfoLabel.setWrapStyleWord(true);
						}
					}
					{
						JPanel ModuleEditPanel = new JPanel(new BorderLayout());
						ModuleInfoPanel.add(ModuleEditPanel, BorderLayout.SOUTH);
						{
							ModuleEditButton = new JButton("Edit");
							ModuleEditPanel.add(ModuleEditButton, BorderLayout.EAST);
							ModuleEditButton.setVisible(false);
							ModuleEditButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent arg0) {
									HandleEditButton();
								}
							});
						}
					}
				}
				{
					JobInfoPanel = new JPanel();
					GridBagLayout JobInfoPanelLayout = new GridBagLayout();
					JobInfoPanelLayout.rowWeights = new double[] {0.1, 0.1, 0.1, 0.1};
					JobInfoPanelLayout.rowHeights = new int[] {7, 7, 7, 7};
					JobInfoPanelLayout.columnWeights = new double[] {0.0, 0.1};
					JobInfoPanelLayout.columnWidths = new int[] {7, 7};
					JobInfoPanel.setLayout(JobInfoPanelLayout);
					CenterPanel.add(JobInfoPanel, new GridBagConstraints(2, 2, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
					JobInfoPanel.setBorder(BorderFactory.createTitledBorder("Job information"));
					//Zeile 1
					{
						ExpectedInputsLabel = new JLabel();
						JobInfoPanel.add(ExpectedInputsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
						ExpectedInputsLabel.setText("1");
						ExpectedInputsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
					}
					{
						jLabel1 = new JLabel();
						JobInfoPanel.add(jLabel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
						jLabel1.setText("Module input slot(s)");
					}
					//Zeile 2
					{
						SelectedInputsLabel = new JLabel();
						JobInfoPanel.add(SelectedInputsLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
						SelectedInputsLabel.setText("1");
						SelectedInputsLabel.setHorizontalAlignment(SwingConstants.TRAILING);
					}
					{
						jLabel2 = new JLabel();
						JobInfoPanel.add(jLabel2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
						jLabel2.setText("Selected probe list(s)");
					}
					//Zeile 3
					{
						JLabel ArrowLabel = new JLabel();
						JobInfoPanel.add(ArrowLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
						ArrowLabel.setText("=>");
						ArrowLabel.setHorizontalAlignment(SwingConstants.RIGHT);
					}					
					{
						BatchInfoLabel = new JLabel();
						JobInfoPanel.add(BatchInfoLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
						BatchInfoLabel
							.setText("The module will be run ONCE");
					}
					//Zeile 4
					{
						DiscardedInputsLabel = new JLabel();
						JobInfoPanel.add(DiscardedInputsLabel, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
						DiscardedInputsLabel
							.setText("0 input probe lists will be ignored");
					}
				}
			}
			
		}
	}
	
	private class Step2Panel extends StepPanel {
		private static final long serialVersionUID = 1L;
		private JScrollPane jScrollPane1;
		private JPanel jPanel1;
		private JButton MoveUpButton;
		private JList SlotSelectionList;
		private JButton MoveDownButton;
		private DefaultListModel SlotSelectionListModel;
		
		public Step2Panel() {
			super("Step 2: Assign probe lists to input slots",1);
		}
		
		private void updateSlotAssignments() {
			SlotSelectionListModel.removeAllElements();
			int job=0; int slot=0;
			for (int i=0; i!=InputDataSets.size(); ++i) {
				// we pretend to be counting from 1
				int assignJob = ((job+1)*selectedModule.InputSize>InputDataSets.size())? -1 : job+1;
				//(job*selectedModule.InputSize < InputDataSets.size()) ? job+1 : -1;
				//if  assignJob=-1;
				SlotSelectionListModel.addElement(
						new SlotAssignmentItem(
								assignJob,  									//job
								slot+1,											//slotno
								selectedModuleInstance.getInputSlotName(i),		//slotname
								i,												//mapsto probelist i
								InputDataSets));								//all input probelists
				++slot;
				if (slot==selectedModule.InputSize) {slot=0; ++job;}
			}
		}
		
		public void update() {
			updateSlotAssignments();
			if (selectedModule.InputSize==1) //nothing to do here
				NextButtonHandler();
		}
		
		protected void initCenterPanel() {
			{
				CenterPanel.setLayout(new BorderLayout());
				jScrollPane1 = new JScrollPane();
				jScrollPane1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				CenterPanel.add(jScrollPane1, BorderLayout.CENTER);
				{
					SlotSelectionListModel = new DefaultListModel();
					SlotSelectionList = new JList();
					jScrollPane1.setViewportView(SlotSelectionList);
					SlotSelectionList.setModel(SlotSelectionListModel);
					SlotSelectionList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				}
			}
			{
				jPanel1 = new JPanel();
				CenterPanel.add(jPanel1, BorderLayout.SOUTH);
				{
					MoveUpButton = new JButton();
					jPanel1.add(MoveUpButton);
					MoveUpButton.setText("Move up");
					MoveUpButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							moveItems(-1);
						}
					});
				}
				{
					MoveDownButton = new JButton();
					jPanel1.add(MoveDownButton);
					MoveDownButton.setText("Move down");
					MoveDownButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							moveItems(+1);
						}
					});
				}
			}
		}
		
		private void moveItems(int direction) {
			if (SlotSelectionList.getSelectedIndex()==-1)
				return; // nothing selected
			
			if (direction==1) { //move down
				if (!(SlotSelectionList.getMaxSelectionIndex()+1<SlotSelectionListModel.size())) 
					return; //no more room to move
				for (int i=SlotSelectionList.getMaxSelectionIndex(); i>=SlotSelectionList.getMinSelectionIndex(); --i) {
					SlotAssignmentItem selSI = (SlotAssignmentItem)SlotSelectionListModel.get(i);
					SlotAssignmentItem belowSI  = (SlotAssignmentItem)SlotSelectionListModel.get(i+1);
					selSI.swapWith(belowSI);
				}
			} else { // move up
				if (!(SlotSelectionList.getMinSelectionIndex()>0)) 
					return; //no more room to move
				for (int i=SlotSelectionList.getMinSelectionIndex(); i<=SlotSelectionList.getMaxSelectionIndex(); ++i) {
					SlotAssignmentItem selSI = (SlotAssignmentItem)SlotSelectionListModel.get(i);
					SlotAssignmentItem aboveSI  = (SlotAssignmentItem)SlotSelectionListModel.get(i-1);
					selSI.swapWith(aboveSI);
				}				
			}
			SlotSelectionList.getSelectionModel().setSelectionInterval(
					SlotSelectionList.getMinSelectionIndex()+direction,
					SlotSelectionList.getMaxSelectionIndex()+direction
			);
			SlotSelectionList.repaint();
		}
		
		private class SlotAssignmentItem {
			public int mappingToIndex=0;
			private Vector<MaydayDataObject> PtrInputDataSets;
			private int jobID, slotID;
			private String slotName;

			public String toString() {
				return
					(jobID==-1) ?
						"Unassigned:  "
						+PtrInputDataSets.get(mappingToIndex).getName()
					:
						"Job="+jobID+", Slot=\""+slotName+"\" ("+slotID+")  <--  "
						+PtrInputDataSets.get(mappingToIndex).getName();
			}
			public SlotAssignmentItem(int job, int slot, String slotname, int mapsTo, Vector<MaydayDataObject> ids) {
				PtrInputDataSets = ids;
				jobID = job; slotID=slot; mappingToIndex=mapsTo;
				slotName = slotname;
			}
			public void swapWith(SlotAssignmentItem i) {
				int temp = i.mappingToIndex;
				i.mappingToIndex = this.mappingToIndex;
				this.mappingToIndex = temp;
			}
			public MaydayDataObject getCorrespondingInput() {
				return PtrInputDataSets.get(mappingToIndex);
			}
		}

		
	}

	private class Step3Panel extends StepPanel {
		private static final long serialVersionUID = 1L;
		private JPanel ModuleInfoPanel;
		private JPanel OptionListPanel;
		private JScrollPane jScrollPane1;
	    private JTextArea ModuleInfoLabel;
		
		public Step3Panel() {
			super("Step 3 - Set module options",2);
			NextButton.setText("Start!");
		}

		private void updateOptionList() {
			selectedModuleInstance.Options.FillOptionPanel(
					selectedModule.toString(),
					selectedModule.getDescription(),
					OptionListPanel
			);
		}
		
		public void update() {
			updateOptionList();
			ModuleInfoLabel.setText(selectedModule.getDescription());
		}
		
		protected void initCenterPanel() {
			CenterPanel.setLayout(new BorderLayout());
			CenterPanel.setBorder(new EmptyBorder(5,5,5,5));
			OptionListPanel = new JPanel();
			JScrollPane OptionListScrollPane = new JScrollPane(OptionListPanel);
			OptionListScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Module Options", TitledBorder.LEADING, TitledBorder.TOP));

			ModuleInfoPanel = new JPanel(new BorderLayout());			
			ModuleInfoPanel.setBorder(BorderFactory.createTitledBorder(null, "Module Information", TitledBorder.LEADING, TitledBorder.TOP));
			{
				jScrollPane1 = new JScrollPane();
				jScrollPane1.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
				ModuleInfoPanel.add(jScrollPane1, BorderLayout.CENTER);
				{
					ModuleInfoLabel = new JTextArea();
					jScrollPane1.setViewportView(ModuleInfoLabel);
					ModuleInfoLabel.setText("No module selected");
					ModuleInfoLabel.setOpaque(false);
					ModuleInfoLabel.setEditable(false);
					ModuleInfoLabel.setFont(UIManager.getFont(this)); // TextArea font should fit other GUI fonts
					ModuleInfoLabel.setLineWrap(true);
					ModuleInfoLabel.setWrapStyleWord(true);
				}
			}
			CenterPanel.add(
					new JSplitPane(JSplitPane.VERTICAL_SPLIT, ModuleInfoPanel, OptionListPanel),
					BorderLayout.CENTER);
		}

		protected void NextButtonHandler() {
			selectedModuleInstance.Options.closedWithAccept=false;
			selectedModuleInstance.Options.acceptAll();
			
			if (selectedModuleInstance.Options.closedWithAccept) {
				wThread = new WorkerThread(Applicator.this);
				wThread.start();
			}
		}

		protected void BackButtonHandler() {
			super.BackButtonHandler();
			if (selectedModule.InputSize==1) 
				StepPanels[myIndex-1].BackButtonHandler();
		}
		
	}
	
	/*
	private class Step4Panel extends StepPanel {
		private static final long serialVersionUID = 1L;
		private JScrollPane jScrollPane1;
		private JTextArea LogArea;
		public myLogWriter LogWriter; 
		
		public Step4Panel() {
			super("Run your jobs and review the log",3);
			NextButton.setText("Cancel");
			BackButton.setVisible(false);
			LogWriter = new myLogWriter();
		}
		
		
		public void update() { // Start the work as soon as we get here
			LogArea.removeAll();
			cMgr.cancelRequested=false;
			wThread = new WorkerThread(Applicator.this);
			wThread.start();
		}
		
		public void jobFinished() {
			NextButton.setText("Close");
		}
		
		protected void NextButtonHandler() {
			if (NextButton.getText()=="Cancel"){
		    	if (JOptionPane.showConfirmDialog(Applicator.this, "Do you really want to cancel?",
		    			"Confirm cancel", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
					LogWriter.writeLine("\nCancelling...\n");
					cMgr.cancelRequested=true;
					LogWriter.writeLine("Waiting for the current process to finish...\n");
					// and now we wait...
		    	}
			} else
			// ... or close this plugin
			{
				Applicator.this.dispose();
			}
		}
		
		
		protected void initCenterPanel() {
			CenterPanel.setLayout(new BorderLayout());
			CenterPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			LogArea = new JTextArea();
			jScrollPane1 = new JScrollPane(LogArea);
			CenterPanel.add(jScrollPane1, BorderLayout.CENTER);
			{
				LogArea.setEditable(false);
				LogArea.setWrapStyleWord(true);
				LogArea.setLineWrap(true);
				LogArea.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
				LogArea.setFont(UIManager.getFont(this)); // TextArea font should fit other GUI fonts
			}
			JProgressBar pb = progressmeter.getProgressBar();
			pb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			CenterPanel.add(pb,BorderLayout.SOUTH);
		}
		
		private class myLogWriter extends ProgressMeter.LogWriter {
			public myLogWriter() {
				progressmeter.super();
			};
			public void writeLine(String line) {
				synchronized (LogArea) {
					LogArea.append(line);
					LogArea.setCaretPosition(LogArea.getDocument().getLength());
					LogArea.repaint();
				}				
			}
		}

	}*/
	
	private class WorkerThread extends AbstractTask {
		Applicator myApp;
		public WorkerThread(Applicator app) {
			super("MPF: "+app.selectedModule.toString());
			myApp = app;
		}
		public void doWork() {
			Applicator.this.setVisible(false);
			myApp.runJobs(this, new myPM());
			myApp.wThread = null; // see windowClosing();
			Applicator.this.dispose(); // signalling, via semaphore
		}
		protected void initialize() {
			cMgr.cancelRequested=false;		
		}
		public void setTaskState(int state) {
			if (state==TaskStateEvent.TASK_CANCELLED) {
				cMgr.cancelRequested=true;
			}
			super.setTaskState(state);
		}
		
		// map the MPF ProgressMeter to the new AbstractTask progress info
		private class myPM extends ProgressMeter {
			public myPM() {
				logwriter = new LogWriter() {
					public void writeLine(String line) {
						writeLog(line);
					}
				};
			};
			protected void update(String msg) {
				setProgress((int)Math.round(Percentage*10000), msg);
			}	
			
		}
	}


	


	private CardLayout theCardLayout; 
	
	public void runJobs(WorkerThread wt, ProgressMeter progressmeter) {

		// = ((Step4Panel)StepPanels[3]).LogWriter;
		DefaultListModel SlotLM = ((Step2Panel)StepPanels[1]).SlotSelectionListModel;

		ProgressMeter.LogWriter theLog = progressmeter.logwriter;
		
		int NrOfErrors=0;

		theLog.writeLine("Starting...\n\n");

		long preExecutionTime=System.currentTimeMillis();

		// 1. Reorder InputDataSets according to Slot assignments
		Vector<MaydayDataObject> reorderedInput = new Vector<MaydayDataObject>();
		for (int i=0; i!=SlotLM.size(); ++i) {
			reorderedInput.addElement( ((Step2Panel.SlotAssignmentItem)SlotLM.get(i)).getCorrespondingInput() );
		}

		// 2. Split input into jobs
		int jobCount = reorderedInput.size() / selectedModule.InputSize; // integer division is what we want
		int dataSetNo = 0;

		// 2b. I have to create GUI objects for all Options of this filter, to make sure their visibility
		// is set correctly, because visibility is used to decide which Options to include in MIO annotation)
		selectedModuleInstance.Options.initializeVisibility();

		// 3. Run job(s)
		for (int jobNo=0; jobNo!=jobCount && !cMgr.cancelRequested; ++jobNo) {

			long preJobTime = System.currentTimeMillis();

			progressmeter.statusChanged(((double)jobNo)/((double)jobCount), "Running job "+(jobNo+1)+" of "+jobCount+" ");
			theLog.writeLine("Job "+(1+jobNo)+": Preparing input\n");
			// 3a. Assign data to input slots
			for (int slotNo=0; slotNo!=selectedModuleInstance.InputSize; ++slotNo) {
				selectedModuleInstance.InputData[slotNo]=reorderedInput.get(dataSetNo);
				++dataSetNo;
			}
			// 3b. Run the filter
			theLog.writeLine("Job "+(1+jobNo)+": Executing...\n");
			try {
				selectedModuleInstance.ProgressMeter = 
					new ProgressMeter(progressmeter,1.0/((double)jobCount),((double)jobNo)/((double)jobCount));
				selectedModuleInstance.execute();
				theLog.writeLine("Job "+(1+jobNo)+": Finished.\n");

				if (cMgr.cancelRequested) {
					wt.processingCancelRequest(); 
					theLog.writeLine("Cleaning up...\n");
					NrOfErrors = jobCount - jobNo;
					// clean up after cancel
					for (int i=0; i!=selectedModuleInstance.InputSize; ++i)
						if (selectedModuleInstance.InputData[i]!=null) selectedModuleInstance.InputData[i].dismiss();
					for (int i=0; i!=selectedModuleInstance.OutputSize; ++i)
						if (selectedModuleInstance.OutputData[i]!=null)  selectedModuleInstance.OutputData[i].dismiss();
				} else {
					// 3c. Return all output to mayday. Naming outputs "InputName [FilterName:OutputSlot]"
					for (int slotNo=0; slotNo!=selectedModuleInstance.OutputSize; ++slotNo) {
						selectedModuleInstance.OutputData[slotNo].addToAnnotation(selectedModuleInstance.getAnnotationAsMap());
						selectedModuleInstance.OutputData[slotNo].setNameModifier(selectedModuleInstance.getName());
						selectedModuleInstance.OutputData[slotNo].setSlotName(selectedModuleInstance.getOutputSlotName(slotNo));
						OutputDataSets.addElement(selectedModuleInstance.OutputData[slotNo]);
					}
				}
			} catch (Throwable e) {
				if (e instanceof OutOfMemoryError) {
					theLog.writeLine("OUT OF MEMORY!\n"+ e.getMessage()+"\n");
					NrOfErrors=jobCount-jobNo;
				} else if (e instanceof TaskCancelledException) {
					wt.processingCancelRequest();
				} else {
					theLog.writeLine("Job "+(1+jobNo)+": could not be completed: \n"+e.getMessage()+"\n");
					e.printStackTrace();
					NrOfErrors++;
				}
				// clean up after error
				theLog.writeLine("Cleaning up...\n");
				wt.setTaskState(TaskStateEvent.TASK_FAILED);
				for (int i=0; i!=selectedModuleInstance.InputSize; ++i)
					if (selectedModuleInstance.InputData[i]!=null) selectedModuleInstance.InputData[i].dismiss();
				for (int i=0; i!=selectedModuleInstance.OutputSize; ++i)
					if (selectedModuleInstance.OutputData[i]!=null)  selectedModuleInstance.OutputData[i].dismiss();
				if (e instanceof OutOfMemoryError) {
					System.gc();
					break;
				}
			}

			long postJobTime = System.currentTimeMillis();
			long elapsedJobTime = postJobTime - preJobTime;
			theLog.writeLine("Job execution time: "+formatTime(elapsedJobTime/1000)+"\n\n");
		}

		// 4. We're done
		progressmeter.statusChanged(1, "Finished");
		theLog.writeLine("Done. " +
			((NrOfErrors==0)
					? "All jobs completed successfully\n"
					: NrOfErrors+" jobs failed to complete (see above).\n")
			);

		if (cMgr.cancelRequested)
			return; 
		
		// make sure we're not killed after this
		wt.processingCancelRequest();
		
		
		// 5. Now we integrate our results into the global mayday data structures
		// with pretty progress indicators running alongside
		progressmeter.statusChanged(0,"Returning data to Mayday");
		double pmscale = (1.0/OutputDataSets.size());
		double pmbl = 0;
		int i=0;
		try {
			for (MaydayDataObject mdo : this.OutputDataSets) {
				ProgressMeter subpm = new ProgressMeter(progressmeter, pmscale, pmbl);
				mdo.addToAnnotation("Processing Module", this.selectedModule.toString());
				mdo.reintegrateIntoMayday(subpm);
				++i;
				pmbl+=pmscale;
			}
		} catch (OutOfMemoryError e) {
			theLog.writeLine("OUT OF MEMORY!\nNot all of your results could be returned properly." +
					"Try working with smaller data sets or configure your Java VM to use a larger memory size.\n" +
					"("+e.getMessage()+")");
			// remove all non-integrated output
			while (OutputDataSets.size()>i)
				OutputDataSets.remove(OutputDataSets.size()-1);
		}
		catch (Throwable t) {
			if (t instanceof TaskCancelledException) {
				theLog.writeLine("User cancelled during data return!\n" +
						"This can corrupt Mayday's internal data structures, because " +
						"some of the data is now in Mayday, while some isn't.\n");
			} else {
				theLog.writeLine("An error occurred: "+t.getMessage()+"\nAborting. ");
			}
			OutputDataSets.clear();
		}


		long postExecutionTime = System.currentTimeMillis();
		long elapsedTime = postExecutionTime - preExecutionTime;

		theLog.writeLine("Total execution time: "+formatTime(elapsedTime/1000));
		progressmeter.statusChanged(1,"Done.");
		
		if (NrOfErrors>0)
			wt.setTaskState(TaskStateEvent.TASK_FAILED);
		//((Step4Panel)StepPanels[3]).jobFinished();
	}
	
	private String formatTime(long timeInSeconds) {
	      long days, hours, minutes, seconds;
	      days =   timeInSeconds / 3600 * 24;
	      timeInSeconds -= (days*3600*24);
	      hours = timeInSeconds / 3600;
	      timeInSeconds -= (hours * 3600);
	      minutes = timeInSeconds / 60;
	      seconds = timeInSeconds - (minutes*60);
	      return
	    		   (days>0 ? days + "days, " : "")
	    		 + (hours>0 ? hours + ":" : "")
	    		 + (minutes<10 ? "0"+minutes : minutes ) + ":"
	    		 + (seconds<10 ? "0"+seconds : seconds );
	}
	

	public Applicator(MasterTable mt) {
//		this.masterTable=mt;
		initGUI();
	}

	/** Use this function to start in Step3Panel (for import plugins) **/
	public Applicator(MasterTable mt, FilterBase selectModuleInstance, Vector<MaydayDataObject> theInputData) {
//		this.masterTable=mt;
		this.initGUI();
		this.selectedModuleInstance = selectModuleInstance;
		this.selectedModule = FilterClassList.getInstance().getItemByName(selectModuleInstance.getName());
		this.InputDataSets = theInputData;
		theCardLayout.show(this.getContentPane(), "2");
		StepPanels[1].update();  
		StepPanels[2].BackButton.setVisible(false);
	}
	
	private void acquireSemaphore() {
		try { this.semaphore.acquire(); } catch (InterruptedException e) {}		
	}
	
	public void showModal(JFrame parent) { 
		// 070203 fb: Added semaphores to block here in case showAsModal doesn't block
		acquireSemaphore();
		setVisible(true);
		//ModalFrameUtil.showAsModal(this,parent);
		acquireSemaphore(); // blocks until the window is closed, see windowClosing()
		this.semaphore.release();
	}

	
	private void initGUI() {
		try {			
			theCardLayout = new CardLayout();
			StepPanels[0]=new Step1Panel();
			StepPanels[1]=new Step2Panel();
			StepPanels[2]=new Step3Panel();
			//StepPanels[3]=new Step4Panel();
			getContentPane().setLayout(theCardLayout);
			getContentPane().add(StepPanels[0],"1");
			getContentPane().add(StepPanels[1],"2");
			getContentPane().add(StepPanels[2],"3");
			//getContentPane().add(StepPanels[3],"4");
			this.setSize(600, 600);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("MPF Applicator");
			setLocationRelativeTo(null);
			addWindowListener(this); // prevent accidental window closing 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dispose() {
		this.semaphore.release(); // wake up waiting MPFWrapper instance
		super.dispose();
	}
	
	public void windowClosing(WindowEvent arg0) {
		if (wThread==null) { //only close if no job is running
			this.dispose();  
		}
		else StepPanels[3].NextButtonHandler(); //else pretend the user pressed "CANCEL"
	}

	public void windowOpened(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowActivated(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	
}
