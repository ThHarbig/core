package mayday.vis3.graph.dialog;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import mayday.core.structures.graph.Edge;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.GraphModelEvent;
import mayday.vis3.graph.model.GraphModelListener;

@SuppressWarnings("serial")
public class EdgeTable extends JPanel implements MouseListener
{
	private GraphModel model;
	private GraphCanvas canvas;

	private JTable componentTable;
	private JTextField query;

	private ProxyModel edgeModel; 
	
	public EdgeTable(GraphCanvas viewer) 
	{
		this.model=viewer.getModel();
		this.canvas=viewer;		
		init();
	}

	private void init()
	{
		componentTable=new JTable();
		edgeModel=new ProxyModel();
		model.addGraphModelListener(edgeModel);
		componentTable.setModel(edgeModel);
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
//		componentTable.setRowSorter(new TableRowSorter<ProxyModel>(edgeModel));
		componentTable.addMouseListener(this);
		
		updateCellHeight();
	}
	
	private void updateCellHeight()
	{
		for(int i=0; i!= edgeModel.edges.size(); ++i)
		{
			componentTable.setRowHeight(i,model.getComponent(edgeModel.edges.get(i).getSource()).getHeight()+4);			
		}		
	}
	
	

	private class ProxyModel extends AbstractTableModel implements GraphModelListener
	{
		List<Edge> edges;
		
		public ProxyModel() {
			this.edges=new ArrayList<Edge>(model.getGraph().getEdges());
			Collections.sort(edges);
		}
		
		public int getColumnCount() 
		{
			return 7;
		}

		public int getRowCount() 
		{
			return edges.size();
		}

		public Object getValueAt(int row, int column) 
		{
			switch (column) 
			{
			case 0: return edges.get(row).getName();
			case 1: return model.getComponent(edges.get(row).getSource()).getLabel();
			case 2: return model.getComponent(edges.get(row).getSource());
			case 3: return model.getComponent(edges.get(row).getTarget()).getLabel();
			case 4: return model.getComponent(edges.get(row).getTarget());
			case 5: return edges.get(row).getRole();
			case 6: return edges.get(row).getWeight();
			default:
				return "";
			}
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) 
		{
			if(c==2 || c==4) return CanvasComponent.class;
			return String.class;
			//            return getValueAt(0, c).getClass();
		}


		public boolean isCellEditable(int row, int col) 
		{
			if (col >=1 && col <=4) 
			{
				return false;
			} 
			return true;

		}

		public void setValueAt(Object value, int row, int col) 
		{
			if(col==0)
			{
				edges.get(row).setName(value.toString());
			}
			if(col==5)
			{
				edges.get(row).setRole(value.toString());
			}
			if(col==6)
			{
				edges.get(row).setWeight(Double.parseDouble(value.toString()));
			}        	
		}

		public String getColumnName(int column)
		{
			switch (column) 
			{
			case 0: return "Name";
			case 1: return "Source";
			case 2: return "Source";
			case 3: return "Target";
			case 4: return "Target";
			case 5: return "Role";
			case 6: return "Weight";
			default:
				return "";
			}
		}

		@Override
		public void graphModelChanged(GraphModelEvent event) 
		{
			this.edges=new ArrayList<Edge>(model.getGraph().getEdges());
			Collections.sort(edges);
			fireTableChanged(new TableModelEvent(this));
			updateCellHeight();
		}
	}
	
	private void search()
	{
        String s = query.getText();
        if (s.length() <= 0) {
            return;
        }
        componentTable.clearSelection();
        for(int i=0; i!=edgeModel.edges.size(); ++i)
        {
        	try 
        	{
	        	if( edgeModel.edges.get(i).getName().toLowerCase().matches(".*"+s.toLowerCase()+".*") ||
	        		edgeModel.edges.get(i).getSource().getName().toLowerCase().matches(".*"+s.toLowerCase()+".*") ||
	        		edgeModel.edges.get(i).getTarget().getName().toLowerCase().matches(".*"+s.toLowerCase()+".*") )
	        	{
	        		componentTable.scrollRectToVisible(componentTable.getCellRect(i, 0, true));	        		
	        		componentTable.getSelectionModel().addSelectionInterval(i, i);
	        		break;        		
	        	}
	        	
	        	
        	} catch (Exception e) {} // do nithing
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
	
	@Override
	public void removeNotify() 
	{
		super.removeNotify();
		if(componentTable.getModel() instanceof GraphModelListener)
		{
			model.removeGraphModelListener((GraphModelListener)componentTable.getModel());
		}
	}
	

	public void mouseClicked(MouseEvent event) 
	{
		Edge e=edgeModel.edges.get(componentTable.getSelectedRow());
		Rectangle r=new Rectangle(model.getComponent(e.getSource()).getBounds());
		r.add(model.getComponent(e.getTarget()).getBounds());
		canvas.center(r,true);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent event) {
		if(event.isPopupTrigger())
		{
			int i=componentTable.getSelectedRow();
			i=componentTable.convertRowIndexToModel(i);

			Edge e=((ProxyModel)componentTable.getModel()).edges.get(i);
			canvas.getEdgeMenu(e).show(this,event.getX(), event.getY());
			event.consume();
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

}
