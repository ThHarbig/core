package mayday.vis3.plots.heatmap2.headers.row;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import mayday.core.MasterTableEvent;
import mayday.core.MasterTableListener;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.AbstractCellBasedRowHeader;
import mayday.vis3.plots.heatmap2.headers.RowHeaderElement;
import mayday.vis3.plots.heatmap2.interaction.SelectionMouseListener;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class ProbeNameHeader extends AbstractCellBasedRowHeader implements MasterTableListener {

	protected JLabel theLabel;
	protected HeatmapStructure data;
	protected int hsize;
	protected SelectionMouseListener ml;
	protected Boolean anyCellVisible;

	
	@Override
	public void renderCell(Graphics2D g, int row) {
		Rectangle2D rect = g.getClipBounds();		
		theLabel.setText(data.getProbe(row).getDisplayName());
		theLabel.setSize(theLabel.getPreferredSize());
		if (data.getRowHeight(row)<theLabel.getHeight()) {
			// no rendering here
		} else {
			
			if (data.isSelected(row))
				theLabel.setForeground(Color.red);
			else
				theLabel.setForeground(Color.black);
			
			int xdelta = (int)(rect.getWidth()-theLabel.getWidth());
			int ydelta = (int)(data.getRowHeight(row)-theLabel.getHeight())/2;

			g.translate(xdelta, ydelta);
			theLabel.paint(g);
			g.translate(-xdelta, -ydelta);
			
			anyCellVisible=true;
		}
	}


	@Override
	public int getSize() {
		return hsize;
	}


	@Override
	public void render(Graphics2D graphics) {
		Boolean anyCellWasVisible = anyCellVisible;
		anyCellVisible = false;
		renderCells(graphics, data);		
		if (anyCellWasVisible!=anyCellVisible) {
			if (!anyCellVisible)
				hsize = 0;
			else {
				int max=-1;
				for (int i=0; i!=data.nrow(); ++i) {
					String n = data.getProbe(i).getDisplayName();
					theLabel.setText(n);
					max = Math.max(theLabel.getPreferredSize().width, max);
				}
				hsize = max;
			}
			fireChange(UpdateEvent.SIZE_CHANGE); 
		}		
	}
	
	@Override
	public MouseListener getMouseListener() {
		return ml;
	}

	@Override
	public MouseWheelListener getMouseWheelListener() {
		return null;
	}
	
	public MouseMotionListener getMouseMotionListener() {
		return ml;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.RowHeader.ProbeName",
				null,
				MC_ROW,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Add probe names as row headers",
				"Probe Display Names"
				);
	}
	
	
	@Override
	public RowHeaderElement init(HeatmapStructure struct) {
		theLabel = new JLabel();
		theLabel.setHorizontalAlignment(JLabel.RIGHT);
		theLabel.setBackground(Color.WHITE);
		theLabel.setOpaque(true);
		data=struct;
		ml = SelectionMouseListener.getListenerInstance(data);
		data.getViewModel().getDataSet().getMasterTable().addMasterTableListener(this);
		return this;
	}
	
	public void dispose() { 
		data.getViewModel().getDataSet().getMasterTable().removeMasterTableListener(this);
	}


	@Override
	public void masterTableChanged(MasterTableEvent event) {
		if (event.getChange()==MasterTableEvent.OVERALL_CHANGE) {
			anyCellVisible = null;
			fireChange(UpdateEvent.SIZE_CHANGE);
		}
		
	}
	
}
