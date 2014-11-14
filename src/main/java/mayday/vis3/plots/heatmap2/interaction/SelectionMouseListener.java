/**
 * 
 */
package mayday.vis3.plots.heatmap2.interaction;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeSet;
import java.util.WeakHashMap;

import mayday.core.Probe;
import mayday.core.plugins.probe.ProbeMenu;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public class SelectionMouseListener extends MouseAdapter {

	protected int lastClickedRow = -1;
	protected int lastDraggedRow = -1;
	protected HeatmapStructure data;

	protected static WeakHashMap<HeatmapStructure, SelectionMouseListener> instances 
			   = new WeakHashMap<HeatmapStructure, SelectionMouseListener>();
	
	public static SelectionMouseListener getListenerInstance(HeatmapStructure struct) {
		SelectionMouseListener sml = instances.get(struct);
		if (sml==null) {
			instances.put(struct, sml=new SelectionMouseListener(struct));
		}
		return sml;
	}
	
	private SelectionMouseListener(HeatmapStructure struct) {
		data = struct;
	}

	public void mouseClicked(MouseEvent e) {

		switch (e.getButton()) {
		case MouseEvent.BUTTON1:
			int clickedRow = data.getRowAtPosition(e.getY());
			Probe pb = data.getProbe(clickedRow);
			if (pb!=null) {
				int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
				
				// first handle shift events
				if (e.isShiftDown()) {
					if (lastClickedRow!=-1) { // finalize shift selection
						TreeSet<Probe> newSelection = new TreeSet<Probe>();
						for (int i=Math.min(lastClickedRow, clickedRow); i<=Math.max(lastClickedRow, clickedRow); ++i) {
							newSelection.add(data.getProbe(i));
						}
						if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
							newSelection.addAll(data.getViewModel().getSelectedProbes());
						}
						data.getViewModel().setProbeSelection(newSelection);
						// no further processing
						return;
					}
				} else {
					lastClickedRow = clickedRow;
				}
				
				// we only come here if no shift selection was finalized				
				if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) 
					data.getViewModel().toggleProbeSelected(pb);
				else 
					data.getViewModel().setProbeSelection(pb);
			}
			break;
		case MouseEvent.BUTTON3:
			ProbeMenu pm = new ProbeMenu(data.getViewModel().getSelectedProbes(), data.getViewModel().getDataSet().getMasterTable());
			pm.getPopupMenu().show((Component)e.getSource(), e.getX(), e.getY()); 
		}			
	}

	public void mouseDragged(MouseEvent e) {
		int clickedRow = data.getRowAtPosition(e.getY());
		Probe pb = data.getProbe(clickedRow);
		if (pb!=null) {
			if (lastDraggedRow != -1) {
				for (int i=Math.min(lastDraggedRow, clickedRow); i<=Math.max(lastDraggedRow, clickedRow); ++i)
					data.getViewModel().selectProbe(data.getProbe(i));
			} else {
				data.getViewModel().selectProbe(pb);
			}
			lastDraggedRow = clickedRow;
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		lastDraggedRow = -1;
	}
}