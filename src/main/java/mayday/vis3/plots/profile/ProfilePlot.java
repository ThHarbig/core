package mayday.vis3.plots.profile;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class ProfilePlot extends PlotPlugin {

	protected Component myComponent;

	public final static String PLID = "PAS.incubator.ProfilePlot";
	
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				PLID,
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Philipp Bruns",
				"bruns@informatik.uni-tuebingen.de",
				"A profile plot",
				"Profile Plot"
		);
		pli.setIcon("mayday/vis3/profile128.png");
		pli.addCategory("Expression Profiles");
		pli.setMenuName("\0Profile Plot");
		setIsMajorPlot(pli);
		return pli;	
	}

	public Component getComponent() {
		Component myComponent;
		myComponent = new PlotWithLegendAndTitle(new ProfilePlotComponent());
		return myComponent;
	}

}
