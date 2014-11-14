package mayday.vis3.export;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;

public class RasterExportSetting extends HierarchicalSetting {
	
	protected BooleanSetting textAA;
	protected BooleanSetting graphicsAA;
	
	public RasterExportSetting() {
		super("Anti-Aliasing");
		setLayoutStyle(LayoutStyle.PANEL_HORIZONTAL);
		addSetting(textAA = new BooleanSetting("Text",null,true));
		addSetting(graphicsAA = new BooleanSetting("Graphics",null,true));
	}
	
	public boolean isTextAA() {
		return textAA.getBooleanValue();
	}
	
	public boolean isGraphicsAA() {
		return graphicsAA.getBooleanValue();
	}
	
	public void setAntialiasing(boolean text, boolean graphics) {
		textAA.setBooleanValue(text);
		graphicsAA.setBooleanValue(graphics);
	}
	
	public RasterExportSetting clone() {
		RasterExportSetting cs = new RasterExportSetting();
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}

}
