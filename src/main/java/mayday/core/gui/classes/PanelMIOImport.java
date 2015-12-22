package mayday.core.gui.classes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.NominalMIO;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.meta.gui.MIGroupSelectionPanel.FilterCriteria;

@SuppressWarnings("serial")
public class PanelMIOImport extends JPanel {
	
	protected ClassSelectionPanel panel;
	protected DataSet ds;
	
	public PanelMIOImport(ClassSelectionPanel panel, DataSet ds) {		
		super(new BorderLayout());
		this.panel = panel;
		this.ds = ds;
		setBorder(BorderFactory.createTitledBorder("Import from experiment meta information"));
		add(new JButton(new BrowseAction()), BorderLayout.WEST);
		setMaximumSize(getPreferredSize());
	}
	
	private class BrowseAction extends AbstractAction {

		public BrowseAction() {
			super( "Select MI Group" );
		}

		public void actionPerformed(ActionEvent e) 
		{
			MIGroupSelectionDialog mgs = new MIGroupSelectionDialog(ds.getMIManager(), NominalMIO.class);
			mgs.addFilterCriteria(new FilterCriteria() {
				public boolean pass(MIGroup mg) {
					for (Object o : mg.getObjects())
						if (o instanceof Experiment)
							return true;
					return false;
				}
			});
			
			if (mgs.getSelectableCount()==0)
				JOptionPane.showMessageDialog(null,"No appropriapte (categorical/nominal) meta information found","No Meta-Information",JOptionPane.ERROR_MESSAGE);
				
			mgs.setTitle("Experiment class definition");
			mgs.setDialogDescription("Please experiment meta information for classes.");
			mgs.setVisible(true);
			
			MIGroupSelection<MIType> sel = mgs.getSelection();

			if (mgs.isCanceled() || sel.size()==0)
				return;
						
			MIGroup mg = sel.get(0);
			
			ClassSelectionModel partition = panel.getModel();
			partition.clear();
			for (Experiment ex : ds.getMasterTable().getExperiments()) {
				MIType nm = mg.getMIO(ex);
				String className = null; 
				if (nm!=null) 
					className = nm.toString();
				partition.addObject(ex.getName(), className);
			}
			
			panel.setModel(partition);
		}

	}
	

}
