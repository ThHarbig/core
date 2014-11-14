package mayday.vis3.plots.profilelogo;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.plots.profilelogo.legend.BinningLegend;

public class ProfileLogoPlugin extends PlotPlugin 
{  

	public void init() 
	{

	}
	
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.datenkrake.ProfileLogo",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"A profile logo plot",
				"Profile Logo"
		);
		pli.setIcon("mayday/vis3/profilelogo128.png");
		return pli;	
	}
	
	public Component getComponent() 
	{
		PlotWithLegendAndTitle myComponent;
		ProfileLogoPlot plp = new ProfileLogoPlot();
		myComponent = new PlotWithLegendAndTitle(plp);
		BinningLegend legend=new BinningLegend(plp.getBinning(),plp);
		legend.setGradient(plp.getGradient());
		myComponent.setLegend(legend);
		return myComponent;
	}
}



