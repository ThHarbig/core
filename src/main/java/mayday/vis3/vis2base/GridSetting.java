/**
 * 
 */
package mayday.vis3.vis2base;

import wsi.ra.plotting.FunctionArea;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;

public class GridSetting extends HierarchicalSetting {

	public BooleanSetting visible = new BooleanSetting("visible",null,true);
	public BooleanSetting onTop = new BooleanSetting("above plot contents","Paint the grid ON TOP OF the plot contents instead of below",false);
	public DoubleSetting xmaj = new DoubleSetting("major",null,1.0);
	public DoubleSetting xmin = new DoubleSetting("minor",null,1.0);
	public DoubleSetting ymaj = new DoubleSetting("major",null,1.0);
	public DoubleSetting ymin = new DoubleSetting("minor",null,0.2);
	
	public GridSetting() {
		super("Grid");
		addSetting(visible);
		addSetting(onTop);
		addSetting(new HierarchicalSetting("Horizontal") 
			.addSetting(xmaj)
			.addSetting(xmin));
		addSetting(new HierarchicalSetting("Vertical")
			.addSetting(ymaj)
			.addSetting(ymin));
	}
	
	public BooleanSetting getVisible() {
		return visible;
	}

	public BooleanSetting getOnTop() {
		return onTop;
	}

	public DoubleSetting getXmaj() {
		return xmaj;
	}

	public DoubleSetting getXmin() {
		return xmin;
	}

	public DoubleSetting getYmaj() {
		return ymaj;
	}

	public DoubleSetting getYmin() {
		return ymin;
	}
	
	public GridSetting clone() {
		return (GridSetting)reflectiveClone();
	}
	
	public void setFromFArea(FunctionArea farea) {
		xmaj.setDoubleValue(farea.getGridEmphX());
		ymaj.setDoubleValue(farea.getGridEmphY());
		xmin.setDoubleValue(farea.getGridX());
		ymin.setDoubleValue(farea.getGridY());
		visible.setBooleanValue(farea.getGridVisible());
		onTop.setBooleanValue(farea.getGridFront());
	}
	
}