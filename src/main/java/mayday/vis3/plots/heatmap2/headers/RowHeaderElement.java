package mayday.vis3.plots.heatmap2.headers;

import java.awt.Graphics2D;

import mayday.vis3.plots.heatmap2.data.HeatmapStructure;

public interface RowHeaderElement extends HeaderElement {
	
	/** render this column (or a subpart of it) to the graphics object
	 *  
	 * @param graphics already translated so that the column's top-left corner is (0,0)
	 * graphics should contain a cliprect that defines which part needs to be painted
	 */
	public void render(Graphics2D graphics);
	
	/** you are guaranteed that init will be called before render. return yourself **/
	public RowHeaderElement init(HeatmapStructure struct);


}
