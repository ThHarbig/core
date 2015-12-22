/**
 * 
 */
package mayday.core.meta.gui;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import mayday.core.meta.MIType;
import mayday.core.meta.WrappedMIO;

@SuppressWarnings("serial")
public class MIOCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer, ListCellRenderer  {

	/* Efficiently manage cell renderers for multiple mio types
	 */
	
	private static HashMap<Class<? extends MIType>, TableCellRenderer> renderers	=
		new HashMap<Class<? extends MIType>, TableCellRenderer>();
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean selected, boolean focused, int row, int col) {
		
		// value can be MIType, WrappedMIO or any other object (default renderer is used then)
		
		MIType renderobject; 
		
		if (value instanceof MIType) {
			renderobject = (MIType)value;
		} else if (value instanceof WrappedMIO) {
			renderobject = ((WrappedMIO)value).getMio();
		} else
			return super.getTableCellRendererComponent(table, value, selected,focused,row,col);
		
		// now get the right renderer
		TableCellRenderer tcr = renderers.get(renderobject.getClass());
		if (tcr==null) {
			tcr = renderobject.getGUIElement();
			renderers.put(renderobject.getClass(), tcr);
		}
		
		return tcr.getTableCellRendererComponent(table, renderobject, selected, focused, row, col);
	}
	
	private DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
	
	public Component getListCellRendererComponent(JList arg0, Object value, int row, boolean selected, boolean focused) {
		
		try {
			return getTableCellRendererComponent((JTable)null, value, selected, focused, row,0);
		} catch (Exception e) {
			return dlcr.getListCellRendererComponent(arg0, value.toString(), row, selected, focused);
		}
	}
	
}