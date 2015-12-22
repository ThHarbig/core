package mayday.vis3.export;

import java.awt.Dimension;

import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;

public class LinkedDimensionSetting extends HierarchicalSetting {
	
	public IntSetting width, height;
	public BooleanSetting linkDimensions;

	public LinkedDimensionSetting() {
		super("Target Dimensions");
		addSetting(width = new IntSetting("Width",null,0));
		addSetting(height = new IntSetting("Height",null,0));
		addSetting(linkDimensions = new BooleanSetting("Keep aspect ratio",null, true));
	}
	
	
	public SettingComponent getGUIElement() {
		return new LinkedDimensionSettingComponent(this);		
	}
	
	public void setTargetDimension( Dimension d ) {
		height.setIntValue(d.height);
		width.setIntValue(d.width);
	}
	
	public Dimension getTargetDimension() {
		return new Dimension(width.getIntValue(), height.getIntValue());
	}
	
	public LinkedDimensionSetting clone() {
		LinkedDimensionSetting cs = new LinkedDimensionSetting();
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
}


