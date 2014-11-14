package mayday.vis3.graph.dialog;

import javax.swing.JTabbedPane;

import mayday.core.gui.MaydayDialog;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.model.GraphModel;

@SuppressWarnings("serial")
public class ComponentList extends MaydayDialog //implements MouseListener
{
	private ComponentTable table;
	private EdgeTable edgeTable;
	private ComponentTable selectionTable;
	
	public ComponentList(GraphModel model, GraphCanvas parent) 
	{
		this.table=new ComponentTable(parent);
		table.setName("Nodes");
		this.edgeTable=new EdgeTable(parent);
		edgeTable.setName("Edges");
		this.selectionTable=new ComponentTable(parent, parent.getSelectionModel());
		selectionTable.setName("Selected Nodes");
		
		JTabbedPane tabbedPane=new JTabbedPane();
		tabbedPane.add(table);
		tabbedPane.add(selectionTable);
		tabbedPane.add(edgeTable);
		add(tabbedPane);
		setTitle("Node Inspector");
		pack();		
	}
}
