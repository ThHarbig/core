package mayday.vis3.plots.heatmap2.headers.column;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
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

public class HeatmapGradientHeader extends AbstractColumnHeaderPlugin implements SettingChangeListener {

	protected HeatmapStructure data;
	protected int inset=5;
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
		clipRect.y+=inset;			
		if (showLabelling.getBooleanValue()) {
			clipRect.x+=inset;
			clipRect.width-=2*inset;
		}
		graphics.translate(clipRect.x, inset); // start all painting at coordinate 0
		double width = clipRect.getWidth();

		boolean hasColorEnhancement = enhancement!=null && enhancement.isActive();
		boolean useColor = enhancement!=null && enhancement.asColor();
		int colorCount = gradient.getResolution();
		double oneColorWidth = (width/((double)colorCount));
		double heightStep = ( 255.0 / ( (double)MaydayDefaults.DEFAULT_PLOT_COLOR_SCALE_HEIGHT ));

		Rectangle2D.Double r2d = new Rectangle2D.Double( 0, 0, Math.max(oneColorWidth,1.0), 1);

		for ( int i = 0; i <colorCount; ++i ) {
			Color col;
			for ( int j = 0; j <= MaydayDefaults.DEFAULT_PLOT_COLOR_SCALE_HEIGHT; ++j ) {
				col =  gradient.getColor(i);
				if ( hasColorEnhancement ) {
					if ( useColor )
						col = new Color( col.getRed(), col.getGreen(), Math.abs( col.getBlue() - (int)( j * heightStep ) ));
					else
						col = new Color( col.getRed(), col.getGreen(), col.getBlue(), (int)( j * heightStep ) );
				}
				graphics.setColor( col );	        
				r2d.y = j;
				r2d.x = i*oneColorWidth;
				graphics.fill(r2d);
			}
		}

		if (showLabelling.getBooleanValue()) {
			graphics.translate(0, MaydayDefaults.DEFAULT_PLOT_COLOR_SCALE_HEIGHT+2);
			graphics.setColor(Color.black);
			// draw min, max and center indicators		
			drawExtremumIndicator(graphics, gradient.getMin(), gradient, width);
			drawExtremumIndicator(graphics, gradient.getMax(), gradient, width);
			drawExtremumIndicator(graphics, gradient.getMidpoint(), gradient, width);
		}

	}

	protected void drawExtremumIndicator(Graphics2D g, double value, ColorGradient gradient, double innerWidth) {

		double positionInGradient = gradient.mapValueToPercentage(value);
		float realPosition = (float)(innerWidth * positionInGradient);

		drawExtremumIndicator(g, realPosition, 0f);

		AffineTransform at = g.getTransform();

		g.translate(0,5);
		String sValue = new DecimalFormat( "###,##0.00" ).format( value );

		FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
				false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );

		TextLayout sLayout = new TextLayout( sValue, MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );

		if (realPosition + sLayout.getAdvance() > innerWidth + inset)
			realPosition -= sLayout.getAdvance();

		g.translate(realPosition, sLayout.getAscent());
		sLayout.draw( g, 0, 0 );
		g.setTransform(at);
	}

	protected void drawExtremumIndicator( Graphics2D graphics, float tipX, float tipY ) {
		GeneralPath l_path = new GeneralPath( GeneralPath.WIND_NON_ZERO );

		// create outline path (a triangle pointing downwards)
		l_path.moveTo( 3f, 0f );
		l_path.lineTo( 6f, 3f );
		l_path.lineTo( 0f, 3f );
		l_path.lineTo( 3f, 0f );

		tipX -= 3;

		// move outline path to desired location
		graphics.translate( tipX, tipY );

		// draw triangle
		graphics.draw( l_path );

		// re-translate graphics device
		graphics.translate( -tipX, -tipY );
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
		return null;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				"PAS.Heatmap.ColumnHeader.HeatmapGradient",
				null,
				MC_COL,
				null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Show the heatmap gradient as column header",
				"Heatmap Gradient"
				);
	}


	@Override
	public ColumnHeaderElement init(HeatmapStructure struct, AbstractColumnGroupPlugin group) {
		data=struct;
		
		FontRenderContext l_frc = new FontRenderContext( MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.getTransform(), 
				false, MaydayDefaults.DEFAULT_FONT_RENDER_CONTEXT.usesFractionalMetrics() );
		TextLayout sLayout = new TextLayout( "0123456789", MaydayDefaults.DEFAULT_PLOT_SMALL_LEGEND_FONT, l_frc );

		height = (int)(MaydayDefaults.DEFAULT_PLOT_COLOR_SCALE_HEIGHT+2*inset);
		textHeight = (int)(2+sLayout.getDescent()+sLayout.getAscent())+5;
		
		showLabelling = new BooleanSetting("Show values as text",null,true);
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
