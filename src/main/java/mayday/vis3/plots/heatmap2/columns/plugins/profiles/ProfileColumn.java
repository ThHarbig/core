package mayday.vis3.plots.heatmap2.columns.plugins.profiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.vis3.plots.heatmap2.columns.AbstractProbeSelectionColumn;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class ProfileColumn extends AbstractProbeSelectionColumn implements SettingChangeListener {

	protected int col;
	protected ProfileConfiguration config;
	
	public ProfileColumn(HeatmapStructure struct, int column, ProfileConfiguration sett) {
		super(struct);
		col=column;
		config = sett;
		config.getSetting().addChangeListener(this);		
	}
	
	@Override
	public String getName() {
		return data.getViewModel().getDataSet().getMasterTable().getExperimentDisplayName(col);
	}

	@Override
	public void render(Graphics2D graphics, int row, int col, boolean isSelected) {
		
		Probe pb = data.getProbe(row);
		double[] val = config.getProbeValue(pb);
		
		DoubleVector dv = new DoubleVector(val);
		double min = dv.min();
		double max = dv.max();
		
		col = this.col; 
		int colL = col==0?col:col-1;
		int colR = col==val.length-1?col:col+1;
		
		double range = (max-min);
		
		// draw a line so that the expression value is in the center of our box
		Rectangle bounds = graphics.getClipBounds();
		double centerHeight = val[col];
		centerHeight = (centerHeight-min)/range;
		centerHeight *= bounds.height-1;
		centerHeight = Math.round(centerHeight);
		
		double leftHeight = val[colL];
		leftHeight = (leftHeight-min)/range;
		leftHeight *= bounds.height-1;
		leftHeight /= 2d;
		leftHeight += centerHeight/2d;
		leftHeight = Math.round(leftHeight);
		
		double rightHeight = val[colR];
		rightHeight = (rightHeight-min)/range;
		rightHeight *= bounds.height-1;
		rightHeight /= 2d;
		rightHeight += centerHeight/2d;
		rightHeight = Math.round(rightHeight);
		
		centerHeight = bounds.height-1-centerHeight;
		leftHeight = bounds.height-1-leftHeight;
		rightHeight = bounds.height-1-rightHeight;
		
		if (isSelected)
			graphics.setColor(Color.red);
		else
			graphics.setColor(Color.black);
		
		double right = bounds.width;
		double mid = Math.round(right/2d);
		
		graphics.drawLine(0, 		(int)leftHeight,   (int)mid, 	(int)centerHeight);
		graphics.drawLine((int)mid, (int)centerHeight, (int)right, 	(int)rightHeight);
	
	}


	@Override
	public void stateChanged(SettingChangeEvent e) {
		fireChange(UpdateEvent.REPAINT);
	}

	@Override
	public void dispose() {
		config.dispose();
	}
}
