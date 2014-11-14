package mayday.core.meta.io.tabular;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import mayday.core.gui.MaydayDialog;
import mayday.core.gui.tablespecials.EditableHeaders.EditableHeaderTable;
import mayday.core.gui.tablespecials.EditableHeaders.EditableHeaderTableColumn;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

@SuppressWarnings("serial")
public class ColumnTypeDialog extends MaydayDialog {

	private EditableHeaderTable table;
	private boolean canceled=true;
	private TreeMap<String, PluginInfo> MITYPES;

	public ColumnTypeDialog(final TableModel tableModel, final TreeMap<String, PluginInfo> MITYPES) {
		setTitle("Define column types");
		this.MITYPES=MITYPES;
		
		ColumnHeader headerPanelPrototype = new ColumnHeader();
		headerPanelPrototype.setComboItems(MITYPES.keySet().toArray());
		Object[] defaultValue = new Object[]{ColumnType.Ignore, PluginManager.getInstance().getPluginFromID("PAS.MIO.String").getName()};
		headerPanelPrototype.setValue(defaultValue);

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
		JButton okButton = new JButton(new AbstractAction("OK"){
			public void actionPerformed(ActionEvent e) {
				canceled=false;
				dispose();
			}
		});
		buttonBox.add(okButton);
		getContentPane().add(buttonBox, BorderLayout.SOUTH);

		this.getRootPane().setDefaultButton(okButton);
		
		for (int i=0; i!=table.getColumnCount(); ++i) {
			EditableHeaderTableColumn col = ((EditableHeaderTableColumn)table.getColumnModel().getColumn(i));
			col.setHeaderValue(defaultValue);
		}
		
		setModal(true);
		
		pack();
		
		setSize(new Dimension(800,600));
		
	}

	public void setVisible(boolean vis) {
		if (vis)
			canceled=true;
		super.setVisible(vis);
	}
	
	public boolean canceled() {
		return canceled;
	}
	
	public ColumnType getColumnType(int columnIndex) {
		table.finish();
		EditableHeaderTableColumn col = ((EditableHeaderTableColumn)table.getColumnModel().getColumn(columnIndex));
		return (ColumnType)((Object[])col.getHeaderValue())[0];
	}
	
	public void setColumnType(int columnIndex, ColumnType type) {
		table.finish();
		EditableHeaderTableColumn col = ((EditableHeaderTableColumn)table.getColumnModel().getColumn(columnIndex));
		((Object[])col.getHeaderValue())[0]=type;
		repaint();
	}
	
	public String getMIOType(int columnIndex) {
		table.finish();
		EditableHeaderTableColumn col = ((EditableHeaderTableColumn)table.getColumnModel().getColumn(columnIndex));
		String type =  (String)((Object[])col.getHeaderValue())[1];
		return MITYPES.get(type).getIdentifier();
	}

}
