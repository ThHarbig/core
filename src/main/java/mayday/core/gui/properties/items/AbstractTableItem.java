package mayday.core.gui.properties.items;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;

@SuppressWarnings("serial")
public abstract class AbstractTableItem extends AbstractPropertiesItem {

	protected JTable tableField = new JTable();
	protected EditObjectAction editObjectAction = new EditObjectAction();
	private String Caption; 

	public AbstractTableItem(String caption, TableModel tableModel) {
		super(caption);
		Caption=caption;
		JScrollPane jsp = new JScrollPane(tableField);		
		add(jsp, BorderLayout.CENTER);
		tableField.setModel(tableModel);
		tableField.addMouseListener(getMouseListener());
		tableField.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent arg0) {
				((TitledBorder)AbstractTableItem.this.getBorder()).setTitle(
						Caption+" ("+tableField.getModel().getRowCount()+")");
				AbstractTableItem.this.repaint();
			}
		});
		tableField.setRowSelectionAllowed(true);
		tableField.setColumnSelectionAllowed(false);
		tableField.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				editObjectAction.setEnabled(tableField.getSelectedRow()!=-1);
			}
		});
	}

	protected void initButtons() {
		JButton[] listButtons = getButtons();
		if (listButtons!=null) {
			JPanel buttonPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,0,0,
					GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
			for (JButton b : listButtons) {
				buttonPanel.add(b, gbc);
				gbc.gridx++;
			}
			gbc.weightx=1.0;
			buttonPanel.add(Box.createHorizontalGlue(),gbc);
			this.add(buttonPanel, BorderLayout.SOUTH);
		}
	}


	protected MouseListener getMouseListener() {
		return new ObjectTableMouseListener();
	}

	protected JButton[] getButtons() {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected ArrayList<Object> getTableItems() {
		ArrayList<Object> ret = new ArrayList<Object>();
		for (Object o : ((DefaultTableModel)tableField.getModel()).getDataVector()) {			
			ret.add(((Vector<Object>)o).get(1));
		}
		return ret; 
	}

	public Object getValue() {
		return tableField.getModel();
	}	

	@Override
	public boolean hasChanged() {
		return false; // no use here
	}

	@Override
	public void setValue(Object value) {
		tableField.setModel((TableModel)value);
	}

	public ListSelectionModel getSelection() {
		return tableField.getSelectionModel();
	}

	protected class EditObjectAction extends AbstractAction {
		public EditObjectAction() {			
			super("Edit");	
			setEnabled(false);
		}
		public void actionPerformed(ActionEvent arg0) {
			AbstractPropertiesDialog dlg;
			int[] rows = tableField.getSelectedRows();
			Object[] selected = new Object[rows.length];
			for (int i=0; i!=rows.length; ++i) {
				selected[i] = tableField.getModel().getValueAt(rows[i],1);
			}
			dlg = PropertiesDialogFactory.createDialog(selected);
			if (parent!=null)
				dlg.setModal(parent.isModal());
			dlg.setVisible(true);
		}		
	}

	protected abstract class DeleteObjectAction extends AbstractAction {
		public DeleteObjectAction() {
			super("Delete");	
			setEnabled(false);
		}
		protected abstract void removeFromContainer(LinkedList<Object> obj);
		
		public void actionPerformed(ActionEvent arg0) {
			ListSelectionModel lsm = getSelection();
			ArrayList<Object> listItems = getTableItems();
			LinkedList<Object> objectsToDelete = new LinkedList<Object>();
			for (int i = 0; i!= listItems.size(); ++i)
				if (lsm.isSelectedIndex(i))
					objectsToDelete.add(listItems.get(i));						
			removeFromContainer(objectsToDelete);
		}				
	}

	protected class ObjectTableMouseListener extends MouseInputAdapter {      
		public void mouseClicked( MouseEvent e ) {       
			if ( e.getButton() == MouseEvent.BUTTON1 ) 
				if ( e.getClickCount() == 2 ) 
					if ( tableField.getSelectedRow() != -1 )
						editObjectAction.actionPerformed(null);
		}
	}  
}
