package mayday.vis3.plots.heatmap2.headers.row;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JWindow;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.AbstractCellBasedRowHeader;
import mayday.vis3.plots.heatmap2.headers.RowHeaderElement;

public class TopLevelProbeListHeader extends AbstractCellBasedRowHeader {

	protected HeatmapStructure data;
	protected MouseListener ml = new SelectionMouseListener();
	
	@Override
	public void renderCell(Graphics2D g, int row) {
		Probe pb = data.getProbe(row);
		if (pb!=null) {
			ProbeList pl = data.getViewModel().getTopPriorityProbeList(pb);
			if (pl!=null) {
				g.setColor(pl.getColor());		
				g.fill(g.getClipBounds());
			}
		}
	}


	@Override
	public int getSize() {
		return 3;
	}


	@Override
	public void render(Graphics2D graphics) {
		renderCells(graphics, data);		
	}

	@Override
	public MouseListener getMouseListener() {
		return ml;
	}

	@Override
	public MouseWheelListener getMouseWheelListener() {
		return null;
	}
	
	@Override
	public MouseMotionListener getMouseMotionListener() {
		return null;
	}

	
	protected class SelectionMouseListener extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			switch (e.getButton()) {
			case MouseEvent.BUTTON1:
				int clickIndex = data.getRowAtPosition(e.getY());
				Probe pb = data.getProbe(clickIndex);
				if (pb!=null) {
					ProbeList pl = data.getViewModel().getTopPriorityProbeList(pb);
					// get all probes for that top level list
					Set<Probe> selection = pl.getAllProbes();
					
					Set<Probe> newSelection;				
					
					int CONTROLMASK = new JWindow().getToolkit().getMenuShortcutKeyMask();
					if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) { 
						// get the direction of the change for toggling
						if (data.isSelected(clickIndex)) {
							newSelection = new TreeSet<Probe>(data.getViewModel().getSelectedProbes());
							newSelection.removeAll(selection);
						} else {
							newSelection = selection;
							selection.retainAll(data.getViewModel().getProbes());
							newSelection.addAll(data.getViewModel().getSelectedProbes());
						}
					} else {
						newSelection = selection;
					}
					data.getViewModel().setProbeSelection(newSelection);
				}
				break;
			case MouseEvent.BUTTON3:
				ProbeMenu pm = new ProbeMenu(data.getViewModel().getSelectedProbes(), data.getViewModel().getDataSet().getMasterTable());
				pm.getPopupMenu().show((Component)e.getSource(), e.getX(), e.getY()); 
			}
		}
	}


	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.RowHeader.TopLevelPL",
				null,
				MC_ROW,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a row header indicating the top-level probelist for each probe",
				"Top-Level ProbeList"
				);
	}


	@Override
	public RowHeaderElement init(HeatmapStructure struct) {
		data=struct;
		return this;
	}


	@Override
	public void dispose() { /* nada */ }

}
