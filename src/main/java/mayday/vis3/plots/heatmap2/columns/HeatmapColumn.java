package mayday.vis3.plots.heatmap2.columns;

import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.interaction.UpdateListener;

public interface HeatmapColumn {
	
	/** render one cell of this column to the graphics object
	 *  
	 * @param graphics already translated so that the cells's top-left corner is (0,0).
	 * the size of the cell can be inferred from the graphics.getClipBounds()
	 * @param isSelected if the current cell is part of the selection
	 */
	public void render(Graphics2D graphics, int row, int col, boolean isSelected);
	
	public String getName();
	
	/** the desired width of the column, in scale-free units or pixels.
	 * @return positive value as a multiple of the default column width (depending on current scale),
	 * negative value as an exact number of pixels
	 */
	public double getDesiredWidth(); 
	 
	
	public MouseListener getMouseListener();
	public MouseWheelListener getMouseWheelListener();
	public MouseMotionListener getMouseMotionListener();

	
	public void addUpdateListener(UpdateListener ul);
	public void removeUpdateListener(UpdateListener ul);
	
	public AbstractColumnGroupPlugin getGroup();
	public void setGroup(AbstractColumnGroupPlugin hcg);
	
	/** remove the column from view --> please unregister all your listeners */
	public void dispose();

}
