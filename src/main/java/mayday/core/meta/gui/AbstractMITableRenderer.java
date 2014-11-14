package mayday.core.meta.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mayday.core.meta.MIType;


@SuppressWarnings("serial")
public abstract class AbstractMITableRenderer<T extends MIType> extends AbstractMIRenderer<T> {

	protected MIOTableModel tableModel = new MIOTableModel();
	protected JTable tableField = new JTable(tableModel);
	private JPanel thePanel = new JPanel();
	private AddRowAction addRowAction = new AddRowAction();
	private DelRowAction delRowAction = new DelRowAction();
	protected JButton addRowButton = new JButton(addRowAction);
	protected JButton delRowButton = new JButton(delRowAction);
	
	public AbstractMITableRenderer() {
		thePanel.setLayout(new BorderLayout());
		JScrollPane jsp = new JScrollPane(tableField);
		thePanel.add(jsp,BorderLayout.CENTER);
		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());  
		b.add(addRowButton);
		b.add(Box.createHorizontalStrut(5));
		b.add(delRowButton);
		thePanel.add(b, BorderLayout.SOUTH);
		thePanel.setPreferredSize(new Dimension(100,100));
	}

	public abstract String getEditorValue();

	//private JLabel label = new JLabel();
	


	@Override
	public void setEditable(boolean editable) {
		tableModel.setEditable(editable);
		addRowAction.setEnabled(editable);
		delRowAction.setEnabled(editable);
	}

	public abstract void setEditorValue(String serializedValue);
	
	public JPanel getEditorComponent() {
		return thePanel;
	}

	protected static class MIOTableModel extends DefaultTableModel {
		private boolean isModelEditable = false;
		private Vector<Integer> nonEditableColumns = new Vector<Integer>();
		
		public void setEditable(boolean editable) {
			isModelEditable = editable;
		}
		public boolean isCellEditable(int a, int b) {
			return isModelEditable && isColumnEditable(b);
		}
		public boolean isColumnEditable(int col) {
			return (!nonEditableColumns.contains(col));
		}
		public void addNonEditableColumn(int col) {
			nonEditableColumns.add(col);
		}
	}
	
	protected class AddRowAction extends AbstractAction {
		public AddRowAction() {
			super("Add");
			setEnabled(false);
		}
		public void actionPerformed(ActionEvent arg0) {
			tableModel.setRowCount(tableModel.getRowCount()+1);
		}		
	}

	protected class DelRowAction extends AbstractAction {
		public DelRowAction() {
			super("Remove selected");
			setEnabled(false);
		}
		public void actionPerformed(ActionEvent arg0) {
			while (tableField.getSelectedRow()!=-1)
				tableModel.removeRow(tableField.getSelectedRow());			
		}		
	}
	
}
