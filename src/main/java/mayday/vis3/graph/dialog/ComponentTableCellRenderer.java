package mayday.vis3.graph.dialog;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class ComponentTableCellRenderer extends JComponent implements TableCellRenderer
{
	private CanvasComponent component;
	
	public ComponentTableCellRenderer()
	{		
	}
	
	public ComponentTableCellRenderer(CanvasComponent comp) 
	{
		this.component=comp;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		if(value instanceof CanvasComponent)
			return new ComponentTableCellRenderer((CanvasComponent) value);
		
		return new JLabel(value.toString());
	}
	
	public void paint(Graphics g)
	{
		if(component!=null)
		{
			g.translate( (getWidth()-component.getWidth())/2,2);
			component.paint(g);
		}
		
	}
	
	
}
