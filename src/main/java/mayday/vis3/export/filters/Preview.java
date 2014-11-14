package mayday.vis3.export.filters;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.vis3.export.ExportPlugin;
import mayday.vis3.export.ExportSetting;
import mayday.vis3.export.RasterExportSetting;

@PluginManager.IGNORE_PLUGIN
public class Preview extends ExportPlugin {

	protected RasterExportSetting rasterSettings = new RasterExportSetting();
	
	protected BufferedImage l_buffer;
	
	public void exportComponent(Component plotComponent, ExportSetting settings) throws Exception {
		// generate the plot
		l_buffer = new BufferedImage( settings.getDimension().width, settings.getDimension().height, BufferedImage.TYPE_INT_ARGB );
		Graphics2D graphicsCanvas = l_buffer.createGraphics();        
		
		Object AAHint = RenderingHints.VALUE_ANTIALIAS_OFF;
		Object AATHint = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
		
		graphicsCanvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, AAHint);
		graphicsCanvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, AATHint);
		
		exportComponentToCanvas(plotComponent, graphicsCanvas, settings);
	}

	public void init() {
	}

	@Override
	public Setting getSetting() {
		return rasterSettings; 
	}

	public BufferedImage getImage() {
		return l_buffer;
	}

	@Override
	public String getFormatName() {
		return null;
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return null;
	}

	

}
