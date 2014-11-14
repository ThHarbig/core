package mayday.vis3.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JMenu;

import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.export.ExportDialog;
import mayday.vis3.export.ExportPlugin;
import mayday.vis3.export.ExportSetting;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;


@SuppressWarnings("serial")
/**
 * this class implements a plotcontainer that can be used for exporting plots from scripts
 * without showing a plot window and requiring user interaction.
 */
public class ScriptablePlotContainer extends Container implements VisualizerMember, PlotContainer {
	
	protected Visualizer visualizer;
	protected Component content; 
	protected String preftitle,title;
	protected HierarchicalSetting viewSetting;
	
	public ScriptablePlotContainer(Component windowContent, Visualizer vis, int w, int h) {
		visualizer = vis;
		content = windowContent;
		viewSetting = new HierarchicalSetting("View")
		.setLayoutStyle(HierarchicalSetting.LayoutStyle.TABBED)
		.setCombineNonhierarchicalChildren(true);
		add(content); 
		content.addNotify();
		setSize(w,h);
	}	
	
	public Graphics getGraphics() {
		BufferedImage bi = new BufferedImage(content.getWidth(), content.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		return bi.getGraphics();
	}
	
	public void setSize(int width, int height) {
		content.setSize(width, height);
	}
	
	protected void computeSize() {
		int newWidth = 800;
		int newHeight = 600;
		content.setSize(newWidth, newHeight);
	}
	
	public Component getContent() {
		return content;
	}
	
	public ViewModel getViewModel() {
		return visualizer.getViewModel();
	}
	
	public Component getWindowContent() {
		return content;
	}

	public JMenu getVisualizerMenu() {
		return getMenu("Visualizer",null);
	}
	
	public HierarchicalSetting getPlotSettings() {
		return viewSetting;
	}

	@Override
	public void closePlot() {
		content.removeNotify();
	}

	@Override
	public String getPreferredTitle() {
		return preftitle;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title=title;
	}

	@Override
	public void toFront() {
		//ignore		
	}

	@Override
	public void addMenu(JMenu jm) {
		// ignore
	}

	@Override
	public void addViewSetting(Setting s, PlotComponent askingObject) {
		viewSetting.addSetting(s); // ignore different components
	}

	@Override
	public JMenu getMenu(String name, PlotComponent askingObject) {
		// ignore
		return new JMenu();
	}

	@Override
	public void setPreferredTitle(String preferredTitle,
			PlotComponent askingObject) {
		preftitle = preferredTitle; //ignore different components		
	}

	public void exportToFile(ExportPlugin fileType, String fileName) throws Exception {
		exportToFile(true, content.getWidth(), content.getHeight(), false, fileType, fileName);
	}
	
	public void exportToFile(boolean visibleAreaOnly, int w, int h, boolean scaleContent, ExportPlugin exporter, String fileName) throws Exception {
		
		if (visibleAreaOnly)
			scaleContent = true;

		Dimension d = ExportDialog.getBaseDimension(content, visibleAreaOnly);
		
		final ExportSetting s = new ExportSetting(!visibleAreaOnly);
		s.setInitialDimension(d);
		s.setTargetFileName(fileName);
		s.setScaleContent(scaleContent);
		s.setTargetDimension(new Dimension(w,h));
		
		exporter.exportComponent(content, s);
	}
	
}