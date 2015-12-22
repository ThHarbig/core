package mayday.vis3.vis2base;



import java.awt.Color;
import java.awt.Component;
import java.awt.Window;

import wsi.ra.chart2d.DGrid;
import mayday.core.settings.AbstractSetting;
import mayday.core.settings.SettingDialogMenuItem;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;

public class ChartSetting extends HierarchicalSetting {

	protected GridSetting grid = new GridSetting();
	protected IntSetting alpha = new IntSetting("Opacity (alpha)",
			"Slide to left for more transparency.",100,1,100,true,true)
			.setLayoutStyle(mayday.core.settings.typed.IntSetting.LayoutStyle.SLIDER); 
	protected IntSetting skiplabels = new IntSetting("Show X-axis labels every n positions","",1);
	protected BooleanSetting antialias = new BooleanSetting("Use anti-aliasing","Anti-aliasing is slow, but gives nicer plots.",false);
	protected IntSetting chartFontSize = new IntSetting("Font size", null, 12, 1, null, true, false);
	protected VisibleRectSetting visibleRect;
	
	protected ColorSetting col_fg, col_bg, col_grid;
	protected HierarchicalSetting colors;
	
	public ChartSetting(ChartComponent cp) {
		super("Chart Settings",LayoutStyle.PANEL_VERTICAL, true);		
		addSetting(new HierarchicalSetting("layout",LayoutStyle.PANEL_HORIZONTAL,true)
			.addSetting(grid)
			.addSetting(visibleRect = new VisibleRectSetting(cp))
			)
		.addSetting(chartFontSize)
		.addSetting(skiplabels)
		.addSetting(colors = new HierarchicalSetting("Colors", LayoutStyle.PANEL_HORIZONTAL, false)
			.addSetting(col_fg = new ColorSetting("Axes",null, Color.black))
			.addSetting(col_grid = new ColorSetting("Grid",null, DGrid.DEFAULT_COLOR))
			.addSetting(col_bg = new ColorSetting("Background",null, Color.white))
			)
		.addSetting(alpha)
		.addSetting(antialias);
		setChildrenAsSubmenus(false);
		grid.setFromFArea(cp.farea);
	}
	
	public GridSetting getGrid() {
		return grid;
	}

	public IntSetting getAlpha() {
		return alpha;
	}
	
	public BooleanSetting getAntialias() {
		return antialias;
	}

	public VisibleRectSetting getVisibleRect() {
		return visibleRect;
	}
	
	public IntSetting getFontSize() {
		return chartFontSize;
	}
	
	public ChartSetting clone() {
		ChartSetting cs = new ChartSetting(this.visibleRect.c);
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
	
	public IntSetting getExperimentLabelSkip() {
		return skiplabels;
	}

	public AbstractSetting getColors() {
		return colors;
	}

	public ColorSetting getColorForeground() {
		return col_fg;
	}

	public ColorSetting getColorBackground() {
		return col_bg;
	}

	public ColorSetting getColorGrid() {
		return col_grid;
	}
	
	public Component getMenuItem( Window parent ) {
		return new SettingDialogMenuItem( this, parent );
	}
}
