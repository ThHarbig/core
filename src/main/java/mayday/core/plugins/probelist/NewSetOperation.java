package mayday.core.plugins.probelist;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.gui.cellrenderer.ProbeListCellRenderer;



@SuppressWarnings("serial")
public class NewSetOperation extends MaydayDialog
{
	public static class PluginWrapper extends AbstractPlugin implements ProbelistPlugin {

		public void init() {
		}

		@SuppressWarnings("unchecked")
		public PluginInfo register() throws PluginManagerException {
			PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.core.ProbeListSetOperations",
					new String[0],
					Constants.MC_PROBELIST_CREATE,
					new HashMap<String, Object>(),
					"Florian Battke",
					"battke@informatik.uni-tuebingen.de",
					"Performs different set operations on probe lists",
					"Set Operations"
					);
			pli.setMenuName("Using Set Operations...");
			return pli;
		}

		public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
			// use input probe lists to intialize view
			NewSetOperation plsod = new NewSetOperation(
					masterTable.getDataSet().getProbeListManager(), probeLists);
			plsod.setVisible(true);
	        LinkedList<ProbeList> ret = new LinkedList<ProbeList>();
	        if (plsod.resultProbeList!=null) {
	        	ret.add(plsod.resultProbeList);
	        	AbstractPropertiesDialog apd = PropertiesDialogFactory.createDialog(plsod.resultProbeList);
	        	apd.setModal(true);
	        	apd.setVisible(true);
	        }
			return ret;
		}
		
	}
	
	
  private ProbeListManager probeListManager;
	private ProbeList resultProbeList = null;
	private ProbeList probeListA;
	private ProbeList probeListB;
	private JCheckBox unaryOperatorA;
	private JCheckBox unaryOperatorB;
	private JToggleButton binaryOpAND;
	private JToggleButton binaryOpOR;
	private JToggleButton binaryOpXOR;
	private JComboBox probeListViewA;
	private JComboBox probeListViewB;
	private JTextField nameField;
  
  
  public NewSetOperation( ProbeListManager probeListManager, List<ProbeList> selection)
  {
  	this.probeListManager = probeListManager;
  	
  	setModal( true );
  	setTitle( "Set/Logical Operations" );
  	
  	init(selection);
  }
  
  
  protected void init(List<ProbeList> selection)
  {
	  this.setLayout(new GridBagLayout());
	  GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.PAGE_START,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0);
	  	  	  
	  
	  JPanel firstRow = new JPanel();
	  firstRow.setBorder(BorderFactory.createTitledBorder("Create a new ProbeList"));
	  firstRow.setLayout(new BorderLayout());
	  firstRow.add(new JLabel("Name: "), BorderLayout.WEST);
	  firstRow.add((nameField=new JTextField()), BorderLayout.CENTER);
	  this.getContentPane().add(firstRow, gbc);
	  
	  	  
	  JPanel secondRow = new JPanel();
	  secondRow.setLayout(new GridBagLayout());
	  
	  GridBagConstraints gbcA = new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.PAGE_START,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0);
	  GridBagConstraints gbcB = new GridBagConstraints(1,0,1,1,1.0,0.0,GridBagConstraints.PAGE_START,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0);
	  
	  probeListViewA = new JComboBox();
	  probeListViewB = new JComboBox();	
	  probeListViewA.setBackground( Color.white );
	  probeListViewB.setBackground( Color.white );				
	  Object[] probeLists = this.probeListManager.getObjects().toArray();		
	  for ( Object o : probeLists ) {
		  probeListViewA.addItem( o );
		  probeListViewB.addItem( o );
	  }    
	  if (selection.size()>0)
		  probeListViewA.setSelectedItem(selection.get(0));
	  if (selection.size()>1) 
		  probeListViewB.setSelectedItem(selection.get(1));
	  probeListViewA.setRenderer( new ProbeListCellRenderer() );
	  probeListViewB.setRenderer( new ProbeListCellRenderer() );
	  
	  JPanel pnlA = new JPanel();

	  pnlA.setBorder(BorderFactory.createTitledBorder("ProbeList \"A\""));
	  pnlA.add(probeListViewA);
	  pnlA.add(unaryOperatorA = new JCheckBox("invert"));	  
	  secondRow.add(pnlA, gbcA);
	  	  
	  JPanel pnlB = new JPanel();
	  pnlB.setBorder(BorderFactory.createTitledBorder("ProbeList \"B\""));
	  pnlB.add(probeListViewB);
	  pnlB.add(unaryOperatorB = new JCheckBox("invert"));
	  secondRow.add(pnlB, gbcB);
	  
	  gbc.gridy++;
	  this.getContentPane().add(secondRow, gbc);

	  
	  JPanel thirdRow = new JPanel();
	  thirdRow.setBorder(BorderFactory.createTitledBorder("Set operation / Logical operation"));
	  thirdRow.setLayout(new GridBagLayout());
	  GridBagConstraints gbc1 = new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.PAGE_START,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0);
	  GridBagConstraints gbc2 = new GridBagConstraints(1,0,1,1,1.0,0.0,GridBagConstraints.PAGE_START,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0);
	  GridBagConstraints gbc3 = new GridBagConstraints(2,0,1,1,1.0,0.0,GridBagConstraints.PAGE_START,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0);
	  thirdRow.add(binaryOpAND = new JToggleButton("<html>A &cap; B<br><br>A &amp; B"), gbc1);
	  thirdRow.add(binaryOpOR = new JToggleButton("<html>A &cup; B<br><br>A &#124; B"), gbc2);
	  thirdRow.add(binaryOpXOR = new JToggleButton("<html>(A \\ B) &cup; (B \\ A)<br>(A &cup; B) \\ (A &cap B)<br>A xor B"), gbc3);
	  ButtonGroup bg = new ButtonGroup();
	  bg.add(binaryOpAND);
	  bg.add(binaryOpOR);
	  bg.add(binaryOpXOR);
	  binaryOpAND.setSelected(true);
	  
	  gbc.gridy++;
	  this.getContentPane().add(thirdRow,gbc);

	  
	  Box lastRow = Box.createHorizontalBox();
	  lastRow.add(Box.createHorizontalGlue());
	  lastRow.add(new JButton(new CancelAction()));
	  lastRow.add(Box.createHorizontalStrut(5));
	  lastRow.add(new JButton(new OkAction()));
	  gbc.gridy++;
	  this.getContentPane().add(lastRow,gbc);
	  
	  pack();

		setResizable( false );		
  }
  
  
	protected class OkAction
	extends AbstractAction	
	{
		public OkAction()
		{
			super( "OK" );
		}
		
    		
		public void actionPerformed( ActionEvent event )
		{
			
			probeListA = (ProbeList)probeListViewA.getSelectedItem();
			probeListB = (ProbeList)probeListViewB.getSelectedItem();

			String operation = 
				((unaryOperatorA.isSelected()?"!":""))+probeListA.getName() +
				(binaryOpAND.isSelected()?" AND ":"") +
				(binaryOpOR.isSelected()?" OR ":"") +
				(binaryOpXOR.isSelected()?" XOR ":"") +
				((unaryOperatorB.isSelected()?"!":""))+probeListB.getName();
			
			if (unaryOperatorA.isSelected())
				probeListA = probeListA.invert(false);
			
			if (unaryOperatorB.isSelected())
				probeListB = probeListB.invert(false);
			
			Set<Probe> probesA = probeListA.getAllProbes();
			Set<Probe> probesB = probeListB.getAllProbes();
			
			Set<Probe> result = new TreeSet<Probe>();
						
			if (binaryOpAND.isSelected()) {
				result = probesA;
				result.retainAll(probesB);				
			} else 
			if (binaryOpOR.isSelected()) {
				result = probesA;
				result.addAll(probesB);
			} else
			if (binaryOpXOR.isSelected()) {
				Set<Probe> union = new TreeSet<Probe>(probesA);
				Set<Probe> intersect = new TreeSet<Probe>(probesA);
				union.addAll(probesB);
				intersect.retainAll(probesB);
				result = union;
				result.removeAll(intersect);
			}
				
			resultProbeList = new ProbeList(probeListA.getDataSet(), true);
			resultProbeList.setName(nameField.getText());
			resultProbeList.setProbes(result.toArray(new Probe[0]));
			resultProbeList.getAnnotation().setQuickInfo(operation);
			
			// close the dialog window
			dispose();
		}
	}
	
	
	protected class CancelAction
	extends AbstractAction	
	{
		public CancelAction()
		{
			super( "Cancel" );
		}		
		
		
		public void actionPerformed( ActionEvent event )
		{
			// close the dialog window
			dispose();
		}
	}
  
}
