package mayday.vis3.plots.heatmap2.headers.row;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.AbstractCellBasedRowHeader;
import mayday.vis3.plots.heatmap2.headers.RowHeaderElement;
import mayday.vis3.plots.heatmap2.interaction.SelectionMouseListener;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class SelectionIndicationHeader extends AbstractCellBasedRowHeader implements ViewModelListener {

	protected HeatmapStructure data;
	protected SelectionMouseListener ml;

	@Override
	public void renderCell(Graphics2D g, int row) {
		if (data.isSelected(row)) {
			g.setColor(Color.red);		
			g.fill(g.getClipBounds());
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

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
			fireChange(UpdateEvent.REPAINT);
	}

	
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.RowHeader.Selection",
				null,
				MC_ROW,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add a row header indicating the current selection",
				"Selection Indicator"
				);
	}

	@Override
	public RowHeaderElement init(HeatmapStructure struct) {
		data=struct;
		ml = SelectionMouseListener.getListenerInstance(data);
		data.getViewModel().addViewModelListener(this);
		return this;
	}


	@Override
	public void dispose() {
		data.getViewModel().removeViewModelListener(this);
	}

}
