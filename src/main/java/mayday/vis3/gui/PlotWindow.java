package mayday.vis3.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import mayday.core.gui.PluginInfoMenuAction;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.vis3.PlotMenuElementPlugin;
import mayday.vis3.gui.actions.ExportPlotAction;
import mayday.vis3.gui.actions.ExportVisibleAreaAction;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class PlotWindow extends AbstractVisualizerWindow {
	
	public PlotWindow(Component windowContent, Visualizer vis) {
		super(windowContent, vis, "Plot Window");
	}	
	
	public void initContent() {
		add(content);
	}
	
	protected void computeSize() {
		int newWidth = 800;
		int newHeight = 600;

		if (content.getPreferredSize()!=null) {
			int minimumNewWidth=500;
			int contentWidth = content.getPreferredSize().width;
			int insetWidth = getInsets().left+getInsets().right;
			newWidth = Math.min(newWidth,
					Math.max(contentWidth+insetWidth, minimumNewWidth));

			int minimumNewHeight=400;
			int contentHeight = content.getPreferredSize().height;
			int insetHeight = getInsets().top+getInsets().bottom;
			newHeight= Math.min(newHeight,
					Math.max(contentHeight+insetHeight, minimumNewHeight));
		}		
		setSize(newWidth, newHeight);
	}
	
	protected JMenu makeFileMenu() {
		JMenu plot = new JMenu(PlotContainer.FILE_MENU);
		plot.setMnemonic('P');
		plot.add(new ExportPlotAction(content));
		plot.add(new ExportVisibleAreaAction(content));
		
		// gather extra plugins here
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(PlotMenuElementPlugin.MC);
		if (plis.size()>0) {
			plot.add(new JSeparator());
			for (PluginInfo pli : plis) {
				plot.add(new PluginInfoMenuAction(pli) {
					public void actionPerformed(ActionEvent e) {
						((PlotMenuElementPlugin)getPlugin().newInstance()).run(getViewModel(), content);
					}					
				});
			}
		}		
		
		plot.add(new JSeparator());
		plot.add(new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				visualizer.removePlot(PlotWindow.this);
			}
		});
		return plot;
	}
	
}