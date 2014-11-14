/**
 * A cell renderer for all objects we attach MIOs to:
 * - Probes
 * - ProbeLists
 * - DataSets
 * - MIOs
 */
package mayday.core.meta.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.ProbeCellRenderer;
import mayday.core.probelistmanager.gui.cellrenderer.ProbeListCellRenderer;

@SuppressWarnings("serial")
public class MIOExtendableRenderer extends DefaultTableCellRenderer implements TableCellRenderer, ListCellRenderer {

	private static ProbeCellRenderer pcr = new ProbeCellRenderer();
	private static ProbeListCellRenderer plcr = new ProbeListCellRenderer();
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean selected, boolean focused, int row, int col) {
		
		// use default renderer
		return super.getTableCellRendererComponent(table, value, selected,focused,row,col);
	}
	
	private DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
	
	public Component getListCellRendererComponent(JList arg0, Object value, int row, boolean selected, boolean focused) {
		
		if (value instanceof Probe)
			return pcr.getListCellRendererComponent(arg0, value, row, selected, focused);
		
		if (value instanceof ProbeList)
			return plcr.getListCellRendererComponent(arg0, value, row, selected, focused);
		
		return dlcr.getListCellRendererComponent(arg0, value, row, selected, focused);
	}
	
}