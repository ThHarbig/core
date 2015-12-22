package mayday.vis3.plots.heatmap2.headers.column;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.plots.heatmap2.ColorEnhancementSetting;
import mayday.vis3.plots.heatmap2.columns.plugins.AbstractColumnGroupPlugin;
import mayday.vis3.plots.heatmap2.columns.plugins.HasColorEnhancement;
import mayday.vis3.plots.heatmap2.columns.plugins.HasGradient;
import mayday.vis3.plots.heatmap2.data.HeatmapStructure;
import mayday.vis3.plots.heatmap2.headers.AbstractColumnHeaderPlugin;
import mayday.vis3.plots.heatmap2.headers.ColumnHeaderElement;
import mayday.vis3.plots.heatmap2.interaction.UpdateEvent;

public class SmallGradientHeader extends AbstractColumnHeaderPlugin implements SettingChangeListener {

	protected HeatmapStructure data;
	protected int inset=1;
	protected int height;
	protected int textHeight;
	
	protected ColorEnhancementSetting enhancement = null;
	protected ColorGradient gradient = null;

	
	protected BooleanSetting showLabelling;

	@Override
	public int getSize() {
		return height + (showLabelling.getBooleanValue()?textHeight:0);
	}

	
	public Setting getSetting() {
		return showLabelling;
	}

	@Override
	public void render(Graphics2D graphics, AbstractColumnGroupPlugin group) {
		if (gradient!=null) {
			draw(graphics, gradient, enhancement);
		}
	}

	protected void draw(Graphics2D graphics, ColorGradient gradient, ColorEnhancementSetting enhancement) {

		Rectangle clipRect = graphics.getClipBounds();
		graphics.translate(clipRect.x, inset); // start all painting at coordinate 0

		boolean hasColorEnhancement = enhancement!=null && enhancement.isActive();
		boolean useColor = enhancement!=null && enhancement.asColor();
		int colorCount = gradient.getResolution();
		double innerH = height-2*inset;
		if (showLabelling.getBooleanValue())
			innerH-=2*textHeight;
		double oneColorWidth = (innerH/((double)colorCount));
		double heightStep = ( 255.0 / ( clipRect.width ));

		Rectangle2D.Double r2d = new Rectangle2D.Double( 0, 0, Math.max(oneColorWidth,1.0), 1);

		if (showLabelling.getBooleanValue())
			graphics.translate(0, textHeight);
		
		for ( int i = 0; i <colorCount; ++i ) {
			Color col;
			for ( int j = 0; j <= clipRect.width; ++j ) {
				col =  gradient.getColor(i);

				if ( hasColorEnhancement ) {
					if ( useColor )
						col = new Color( col.getRed(), col.getGreen(), Math.abs( col.getBlue() - (int)( j * heightStep ) ));
					else
						col = new Color( col.getRed(), col.getGreen(), col.getBlue(), (int)( j * heightStep ) );
				}

				graphics.setColor( col );	        
				r2d.x = j;
				r2d.y = i*oneColorWidth;
				graphics.fill(r2d);
			}
		}

		if (showLabelling.getBooleanValue()) {
			graphics.translate(0, -textHeight);
			graphics.setColor(Color.black);
			// draw min, max and center indicators		
			drawExtremumIndicator(graphics, gradient.getMin(), true);
			drawExtremumIndicator(graphics, gradient.getMax(), false);
		}

	}

	protected void drawExtremumIndicator(Graphics2D g, double value, boolean below) {

		AffineTransform at = g.getTransform();

		g.translate(0,5);
		String sValue = new DecimalFormat( "###,##0.00" ).format( value );

		FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
				false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );

		TextLayout sLayout = new TextLayout( sValue, MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );

		int realPosition = below?height-2*inset:inset;

		g.translate(0, realPosition);
		sLayout.draw( g, 0, 0 );
		g.setTransform(at);
	}


	@Override
	public MouseListener getMouseListener() {
		return null;
	}

	@Override
	public MouseMotionListener getMouseMotionListener() {
		return null;
	}

	@Override
	public MouseWheelListener getMouseWheelListener() {
		return new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int CONTROLMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
				if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) {
					if (e.getWheelRotation()>0 && height>2*textHeight)
						height-=20;
					if (e.getWheelRotation()<0 && height<1000)
						height+=20;
					fireChange(UpdateEvent.SIZE_CHANGE);
				}
			}

		};
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.ColumnHeader.SmallGradient",
				null,
				MC_COL,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Show a small vertical gradient as column header",
				"Heatmap Gradient, small"
				);
	}


	@Override
	public ColumnHeaderElement init(HeatmapStructure struct, AbstractColumnGroupPlugin group) {
		data=struct;
		
		FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
				false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );
		TextLayout sLayout = new TextLayout( "0123456789", MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );

		height = (int)(MaydayDefaults.DEFAULT_PLOT_COLOR_SCALE_HEIGHT+2*inset);
		textHeight = (int)(sLayout.getDescent()+sLayout.getAscent());
		
		showLabelling = new BooleanSetting("Show values as text",null,false);
		showLabelling.addChangeListener(this);		
		
		if (group instanceof HasGradient) 
			gradient = ((HasGradient)group).getGradient();
		
		if (group instanceof HasColorEnhancement) 
			enhancement = ((HasColorEnhancement)group).getEnhancement();

		
		return this;
	}


	@Override
	public void stateChanged(SettingChangeEvent e) {
		fireChange(UpdateEvent.SIZE_CHANGE);		
	}
	
	public void dispose() { /* nada */ }
}
