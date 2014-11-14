package mayday.vis3.export;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.RepaintManager;

import mayday.core.pluma.AbstractPlugin;

public abstract class ExportPlugin extends AbstractPlugin {

	public static final String MC = "Graphics Export Filter";

	public abstract void exportComponent( Component plotComponent, ExportSetting settings) throws Exception;
	
	public abstract String getFormatName();
	
	
	protected static void exportComponentToCanvas(Component plotComponent, Graphics2D canvas, ExportSetting settings) throws Exception {
		Exception error = null;
		try {
			prepareComponent(settings, plotComponent);
			prepareCanvas(canvas, settings, plotComponent);
			plotComponent.paint( canvas );			
		} catch (Exception t) {
			error=t;
		} finally {
			restoreComponent(plotComponent);
		}
		if (error!=null)
			throw error;
	}
	
	protected static void prepareComponent(ExportSetting settings, Component plotComponent) {
		RepaintManager.currentManager(plotComponent).setDoubleBufferingEnabled(false);
		plotComponent.setVisible(false);
		Dimension exportSize;
		if (settings.scaleContent()) {
			exportSize = settings.getBaseDimension();
		} else {
			exportSize = settings.getDimension();
		}
		plotComponent.setSize(exportSize);
		plotComponent.validate();
	}
	
	protected static void prepareCanvas(Graphics2D canvas, ExportSetting settings, Component plotComponent) {		
		if (settings.scaleContent()) {
			double curW = plotComponent.getSize().getWidth();
			double curH = plotComponent.getSize().getHeight();
			Dimension tgtDim = settings.getDimension();
			double tgtW = tgtDim.width;
			double tgtH = tgtDim.height;
			double scaleW = tgtW/curW;
			double scaleH = tgtH/curH;	
			canvas.scale(scaleW, scaleH);
		}
	}
	
	protected static void restoreComponent(Component plotComponent) {
		RepaintManager.currentManager(plotComponent).setDoubleBufferingEnabled(true);
		plotComponent.getParent().validate();	// restore component's size to the one parent wants	
		plotComponent.setVisible(true);
	}
	

}
