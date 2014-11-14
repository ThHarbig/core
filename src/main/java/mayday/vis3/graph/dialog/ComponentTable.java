package mayday.vis3.graph.dialog;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.components.NodeComponent.NodeUpdate;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.GraphModelEvent;
import mayday.vis3.graph.model.GraphModelListener;
import mayday.vis3.graph.model.GraphModelSelectionListener;
import mayday.vis3.graph.model.SelectionModel;

@SuppressWarnings("serial")
public class ComponentTable extends JPanel implements MouseListener, ListSelectionListener
{
	private GraphModel model;
	private GraphCanvas canvas;

	private JTable componentTable;
	private JTextField query;

	private boolean selectionMode;


	public ComponentTable(GraphCanvas viewer,SelectionModel selection) 
	{
		this.model=viewer.getModel();
		this.canvas=viewer;	

		componentTable=new JTable();
		SelectionProxyModel pm=new SelectionProxyModel(selection);
		componentTable.setModel(pm);
		componentTable.setRowSorter(new TableRowSorter<SelectionProxyModel>(pm));

		selectionMode=true;
		init();
	}

	public ComponentTable(GraphCanvas viewer) 
	{
		this.model=viewer.getModel();
		this.canvas=viewer;		

		componentTable=new JTable();
		ProxyModel pm=new ProxyModel();
		model.addGraphModelListener(pm);
		componentTable.setModel(pm);
		componentTable.setRowSorter(new TableRowSorter<ProxyModel>(pm));
		selectionMode=false;
		init();
	}

	private void init()
	{

		componentTable.getSelectionModel().addListSelectionListener(this);
		componentTable.setDefaultRenderer(CanvasComponent.class, new ComponentTableCellRenderer());
		setLayout(new BorderLayout());
		JScrollPane scroller=new JScrollPane(componentTable);
		add(scroller,BorderLayout.CENTER);

		query=new JTextField(25);
		query.setText("");
		query.getDocument().addDocumentListener(new SearchListener());
		query.setToolTipText("Enter search term here. Regular expressions can be used");
		Box queryBox=Box.createHorizontalBox();
		queryBox.add(new JLabel("Find"));
		queryBox.add(Box.createHorizontalStrut(10));
		queryBox.add(query);
		queryBox.add(Box.createHorizontalGlue());
		add(queryBox,BorderLayout.NORTH);

		componentTable.addMouseListener(this);

		updateCellHeights();

	}	

	private void updateCellHeights()
	{
		for(int i=0; i!= componentTable.getRowCount(); ++i)
		{
			CanvasComponent c=(CanvasComponent)componentTable.getValueAt(i, 1);
			componentTable.setRowHeight(i,c.getHeight()+4);
		}
	}

	private void search()
	{
		String s = query.getText();
		if (s.length() <= 0) {
			return;
		}
		componentTable.clearSelection();
		for(int i=0; i!=componentTable.getRowCount(); ++i)
		{
			try 
			{
				if( componentTable.getValueAt(i, 0).toString().toLowerCase().matches(".*"+s.toLowerCase()+".*") )
				{
					componentTable.scrollRectToVisible(componentTable.getCellRect(i, 0, true));	        		
					componentTable.getSelectionModel().addSelectionInterval(i, i);
					break;        		
				}
			} catch (Exception e) {}//ignore this condition
		}



	}

	private class SearchListener implements DocumentListener
	{

		public void changedUpdate(DocumentEvent e) 
		{
		}

		public void insertUpdate(DocumentEvent e) 
		{
			search();		
		}

		public void removeUpdate(DocumentEvent e) 
		{
			search();				
		}		
	}



	private class ProxyModel extends AbstractTableModel implements GraphModelListener
	{
		public int getColumnCount() 
		{
			return 5;
		}

		public int getRowCount() 
		{
			return model.getComponents().size();
		}

		public Object getValueAt(int row, int column) 
		{
			switch (column) 
			{
			case 0: return model.getComponents().get(row).getLabel();
			case 1: return model.getComponents().get(row);
			case 2: 
				if(model.getNode(model.getComponents().get(row))==null)
					return "Group";
				return model.getNode(model.getComponents().get(row)).getRole();
			case 3: return "("+model.getComponents().get(row).getLocation().x+","+model.getComponents().get(row).getLocation().y+")";
			case 4: return Boolean.valueOf(model.getComponents().get(row).isVisible());
			default:
				return "";
			}
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) 
		{
			if(c==1) return CanvasComponent.class;
			if(c==4) return Boolean.class;
			return String.class;			
		}

		public boolean isCellEditable(int row, int col) 
		{
			if (col ==1 || col==3) 
			{
				return false;
			} 
			return true;

		}

		public void setValueAt(Object value, int row, int col) 
		{
			if(col==0)
			{
				model.getComponents().get(row).setLabel(value.toString());
			}
			if(col==2)
			{
				model.getNode(model.getComponents().get(row)).setRole(value.toString());
				if(model.getComponents().get(row) instanceof NodeComponent)
					((NodeComponent)model.getComponents().get(row)).nodeUpdated(NodeUpdate.ROLE);
			}
			if(col==4)
			{
				model.getComponents().get(row).setVisible((Boolean)value);
			}        	
		}

		public String getColumnName(int column)
		{
			switch (column) 
			{
			case 0: return "Name";
			case 1: return "Component";
			case 2: return "Role";
			case 3: return "Position";
			case 4: return "Visible";
			default:
				return "";
			}
		}

		@Override
		public void graphModelChanged(GraphModelEvent event) 
		{
			fireTableChanged(new TableModelEvent(this));
			updateCellHeights();
		}
	}


	private class SelectionProxyModel extends AbstractTableModel implements GraphModelSelectionListener
	{
		private SelectionModel model;

		public SelectionProxyModel(SelectionModel model) 
		{
			this.model=model;
			this.model.addSelectionListener(this);
		}

		public int getColumnCount() 
		{
			return 3;
		}

		public int getRowCount() 
		{
			return model.getSelectedComponents().size();
		}

		public Object getValueAt(int row, int column) 
		{
			switch (column) 
			{
			case 0: return model.getSelectedComponents().get(row).getLabel();
			case 1: return model.getSelectedComponents().get(row);
			case 2: if(model.getSelectedComponents().get(row) instanceof NodeComponent){
				return ((NodeComponent)model.getSelectedComponents().get(row)).getNode().getRole();
			}else
			{
				return "";
			}
			default:
				return "";
			}
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) 
		{
			if(c==1) return CanvasComponent.class;
			return String.class;
		}


		public boolean isCellEditable(int row, int col) 
		{
			if (col ==1) 
			{
				return false;
			} 
			return true;

		}

		public void setValueAt(Object value, int row, int col) 
		{
			if(col==0)
			{
				model.getSelectedComponents().get(row).setLabel(value.toString());
			}
			if(col==2)
			{
				if(model.getSelectedComponents().get(row) instanceof NodeComponent)
				{
					((NodeComponent)model.getSelectedComponents().get(row)).getNode().setRole(value.toString());
				}				
			}      	
		}

		public String getColumnName(int column)
		{
			switch (column) 
			{
			case 0: return "Name";
			case 1: return "Component";
			case 2: return "Role";
			default:
				return "";
			}
		}

		@Override
		public void selectionChanged() 
		{			
			fireTableChanged(new TableModelEvent(this));

			for(int i=0; i!= componentTable.getRowCount(); ++i)
			{
				CanvasComponent c=(CanvasComponent)componentTable.getValueAt(i, 1);
				componentTable.setRowHeight(i,c.getHeight()+4);
			}
		}

	}

	public void mouseClicked(MouseEvent e) 
	{
		int i=componentTable.getSelectedRow();
		i=componentTable.convertRowIndexToModel(i);

		CanvasComponent comp=(CanvasComponent)componentTable.getModel().getValueAt(i, 1);
		canvas.center(comp.getBounds(),true);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent event) 
	{
		//		if(selectionMode)
		//		{
		if(event.isPopupTrigger())
		{
			int i=componentTable.getSelectedRow();
			i=componentTable.convertRowIndexToModel(i);

			CanvasComponent comp=(CanvasComponent)componentTable.getModel().getValueAt(i, 1);
			comp.getMenu().show(this,event.getX(), event.getY());
			event.consume();
		}
		//		}
		//		if(event.isPopupTrigger())
		//		{
		//			int i=componentTable.getSelectedRow();
		//			i=componentTable.convertRowIndexToModel(i);
		//
		//			model.getComponents().get(i).getMenu().show(this,event.getX(), event.getY());
		//			event.consume();
		//		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		if(selectionMode) 
			return;

		canvas.getSelectionModel().clearSelection();

		for(int i: componentTable.getSelectedRows())
		{
			int im=componentTable.convertRowIndexToModel(i);
			canvas.getSelectionModel().select(model.getComponents().get(im));
		}

	}

	@Override
	public void removeNotify() 
	{
		super.removeNotify();
		if(componentTable.getModel() instanceof GraphModelListener)
		{
			model.removeGraphModelListener((GraphModelListener)componentTable.getModel());
		}
	}

}
