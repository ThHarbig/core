package mayday.core.gui.columnparse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import mayday.core.gui.MaydayDialog;
import mayday.core.gui.tablespecials.EditableHeaders.EditableHeaderTable;
import mayday.core.gui.tablespecials.EditableHeaders.EditableHeaderTableColumn;

@SuppressWarnings("serial")
public class ColumnTypeDialog<ColumnType> extends MaydayDialog {

	protected EditableHeaderTable table;
	protected ColumnTypeValidator<ColumnType> val;
	private boolean canceled=true;

	public ColumnTypeDialog(final TableModel tableModel, ColumnTypes<ColumnType> types, ColumnTypeEstimator<ColumnType> est, ColumnTypeValidator<ColumnType> val) {
		setTitle("Define column types");
		this.val = val;
		
		ColumnHeader<ColumnType> headerPanelPrototype = new ColumnHeader<ColumnType>(types);
		headerPanelPrototype.setValue(null);

		table = new EditableHeaderTable(headerPanelPrototype, tableModel);
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{   
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				this.setText("<html>"+
						(value==null || value.toString().equalsIgnoreCase("NA") ||value.toString().length()==0?
								"<span style=\"color:#ff7f7f;\">NA</span>": //font-style:italic;
									value.toString()
						));
				return this;
			}});

		//estimate all types
		for (int i=0; i!=table.getColumnCount(); ++i) {
			EditableHeaderTableColumn col = ((EditableHeaderTableColumn)table.getColumnModel().getColumn(i));
			Object value = est.getType(i); 
			col.setHeaderValue(value);
		}
		
	}
	
	protected void init() {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i=0; i!= table.getColumnCount(); ++i)
			table.getColumnModel().getColumn(i).setMinWidth(100);
		

		JScrollPane jsp = new JScrollPane(table);
		getContentPane().add(jsp, BorderLayout.CENTER);

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(new JButton(new AbstractAction("Cancel"){
			public void actionPerformed(ActionEvent e) {
				canceled=true;
				dispose();
			}
		}));
		buttonBox.add(Box.createHorizontalStrut(5));
		JButton okButton = new JButton(getOKAction());
		buttonBox.add(okButton);
		getContentPane().add(buttonBox, BorderLayout.SOUTH);

		this.getRootPane().setDefaultButton(okButton);
				
		setModal(true);
		
		pack();
		
		setSize(new Dimension(800,600));
	}
	
	public void setVisible(boolean vis) {
		init();
		super.setVisible(true);
	}

	protected AbstractAction getOKAction() {
		return new OKAction();
	}
	
	public boolean canceled() {
		return canceled;
	}
	
	@SuppressWarnings("unchecked")
	public ColumnType getColumnType(int columnIndex) {
		table.finish();
		EditableHeaderTableColumn col = ((EditableHeaderTableColumn)table.getColumnModel().getColumn(columnIndex));
		return (ColumnType)col.getHeaderValue();
	}
	
	public class OKAction extends AbstractAction {
		
		public OKAction() {
			super("OK");
		}
		
		public void actionPerformed(ActionEvent e) {
			LinkedList<ColumnType> ctypes = new LinkedList<ColumnType>(); 
			for (int i=0; i!=table.getColumnCount(); ++i) {
				ctypes.add(getColumnType(i));
			}
			if (val.isValid(ctypes)) {
				canceled=false;
				dispose();
			} else {
				JOptionPane.showMessageDialog(ColumnTypeDialog.this, val.getValidityHint(), "Revise column assignments", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}

	public ColumnTypeValidator<ColumnType> getValidator() {
		return val;
	}
	
}
