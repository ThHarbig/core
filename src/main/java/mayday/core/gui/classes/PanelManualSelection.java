package mayday.core.gui.classes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.core.ClassSelectionModel;

@SuppressWarnings("serial")
public class PanelManualSelection extends JPanel {
	
	protected ClassSelectionPanel panel;
	protected ClassSelectionModel partition;
	
	private JTable classTable;
	private IsisTableModel tableModel;

	
	public PanelManualSelection(ClassSelectionPanel panel) {
		super(new BorderLayout());
		this.panel = panel;
		this.partition = panel.getClassPartition();
		setBorder(BorderFactory.createTitledBorder("Manual Class Assignment"));

		tableModel=new IsisTableModel();        
		classTable=new JTable(tableModel);
		//classTable.setPreferredSize(new Dimension(classTable.getPreferredSize().width,100));
		classTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		classTable.addMouseListener(new TableMouseListener());
		//classTable.setModel(tableModel);
		classTable.getColumnModel().getColumn(2).setCellRenderer(new ClassTableRenderer());
		JScrollPane scrollPane = new JScrollPane(classTable);         
		add(scrollPane, BorderLayout.CENTER);        
	}
	

	/**
	 * Adapts the ClassPartition to TableModel
	 * @author symons
	 *
	 */
	private class IsisTableModel extends AbstractTableModel
	{
		private String[] columnNames = {"Number","Name","Class"};

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() 
		{
			return 3;
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() 
		{
			return partition.getNumObjects();			
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) 
		{			
			if(columnIndex==0)
			{
				return rowIndex+1;
			}
			if(columnIndex==1)
			{
				return partition.getObjectName(rowIndex);
			}			
			if(columnIndex==2)
			{
				return partition.getObjectClass(rowIndex);
			}
			return null;
		}
		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		public String getColumnName(int column)
		{
			return columnNames[column];
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int row, int col)
		{
			return col==2?true:false;
		}



		/* (non-Javadoc)
		 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
		 */
		public void setValueAt(Object value, int row, int col) 
		{
			if(col!=2) return;
			if(!(value instanceof String)) return;
			partition.setClass(row, (String)value);
		}		

	}

	/**
	 * @author symons
	 *
	 */
	private class TableMouseListener extends MouseAdapter
	{

		public void mousePressed(MouseEvent e) {
			operate(e);
		}

		public void mouseReleased(MouseEvent e) {
			operate(e);
		}

		public void operate(MouseEvent e) 
		{			
			if (e.isPopupTrigger() && classTable.getSelectedRows().length>0) {

				JPopupMenu popup=new JPopupMenu();

				String newClassName = "Class ";
				int newClassID = 0;

				for(String s:partition.getClassesLabels())
				{
					JMenuItem m=new JMenuItem(s);
					m.addActionListener(new SetClassActionListener(s));
					popup.add(m);	
					while (s.equals(newClassName+newClassID))
						++newClassID;
				}
				JMenuItem m = new JMenuItem(">> NO CLASS <<");
				m.addActionListener(new SetClassActionListener(partition.getNoClassLabel()));
				popup.add(m);

				
				popup.addSeparator();

				m=new JMenuItem("New Class: "+newClassName+newClassID);
				m.addActionListener(new SetClassActionListener(newClassName+newClassID));
				popup.add(m);

				m=new JMenuItem("New Class...");
				m.addActionListener(new CreateWithNameActionListener(newClassName+newClassID));
				popup.add(m);

				popup.addSeparator();
				String oldName = partition.getObjectClass(classTable.getSelectedRow());
				m=new JMenuItem("Rename class \""+oldName+"\"...");
				m.addActionListener(new RenameActionListener(oldName));
				popup.add(m);

				popup.show(e.getComponent(), e.getX(), e.getY());
			}


		}		
	}

	/**
	 * @author symons
	 *
	 */


	/**
	 * @author symons
	 *
	 */
	public class SetClassActionListener implements ActionListener
	{
		private String name;
		public SetClassActionListener(String s)
		{
			name=s;
		}

		public void actionPerformed(ActionEvent e) 
		{
			int[] indices = classTable.getSelectedRows();
			for(int i=0; i!= indices.length; ++i)
			{
				partition.setClass(indices[i], name);
			}
			tableModel.fireTableRowsUpdated(0, classTable.getRowCount()-1);

		}		
	}

	public class RenameActionListener implements ActionListener
	{
		private String name;
		public RenameActionListener(String oldName)
		{
			name=oldName;
		}

		public void actionPerformed(ActionEvent e) 
		{
			String newName = JOptionPane.showInputDialog(PanelManualSelection.this, "Enter a new name for class \""+name+"\"", name);

			if (newName!=null && newName.trim().length()>0 && !newName.trim().equals(name)) {
				newName = newName.trim();
				for(int i=0; i!= partition.getNumObjects(); ++i) {
					if (partition.getObjectClass(i).equals(name)) {
						partition.setClass(i, newName);
					}
				}	
				tableModel.fireTableRowsUpdated(0, classTable.getRowCount()-1);
			}
		}		
	}

	public class CreateWithNameActionListener implements ActionListener
	{
		protected String name;
		public CreateWithNameActionListener(String suggest) {
			name  = suggest;
		}

		public void actionPerformed(ActionEvent e) 
		{
			String newName = JOptionPane.showInputDialog(PanelManualSelection.this, "Enter a name for the new class", name);

			if (newName!=null && newName.trim().length()>0 && !partition.getClassesLabels().contains(newName.trim())) {
				newName = newName.trim();
				int[] indices = classTable.getSelectedRows();
				for(int i=0; i!= indices.length; ++i)
				{
					partition.setClass(indices[i], newName);
				}
				tableModel.fireTableRowsUpdated(0, classTable.getRowCount()-1);
			}
		}		
	}
	
	/**
	 * @author symons
	 *
	 */
	private class ClassTableRenderer extends DefaultTableCellRenderer
	{
		private static final long serialVersionUID = 1L;

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
		 */
		protected void setValue(Object value) 
		{
			super.setValue(value);
		}

		/* (non-Javadoc)
		 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent
		(JTable table, Object value, boolean selected, boolean focused, int row, int column)
		{
			//			if(partition.getNumClasses()==0) return this;
			setEnabled(table == null || table.isEnabled()); // see question above
			int c=partition.getClassNames().indexOf(partition.getObjectClass(row));
			//setBackground(new Color(0xEE,0xEE,0xEE));
			setBackground(new JLabel().getBackground());
			if(c >= 0 ) 
				setBackground(ClassSelectionModel.getColor(c, partition.getNumClasses()));
			//
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			return this;
		}
	}
	
	public void fireChanged() {
		partition = panel.getClassPartition();
		tableModel.fireTableDataChanged();
	}


}
