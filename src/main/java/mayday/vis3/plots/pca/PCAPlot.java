package mayday.vis3.plots.pca;


import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;

public class PCAPlot extends PlotPlugin {

	public void init() {
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.incubator.PcaPlot",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"A plot of the first three principal components",
				"Principal Component Plot"
		);
		pli.setIcon("mayday/vis3/pca128.png");
		pli.addCategory("Scatter plots");
		return pli;	
	}

	public Component getComponent() {
		
		BooleanSetting transpose = new BooleanSetting("Transpose matrix",
				"Compute the PCA on the transposed matrix (samples)?\n" +
				"If unchecked, PCA is computed on the original matrix (experiments)",
				false);
		IntSetting numberOfComponents = new IntSetting("Number of components", "How many components should be displayed in scatter plots?", 3);
		HierarchicalSetting hs = new HierarchicalSetting("Principal Component Plot")
		.addSetting(transpose)
		.addSetting(numberOfComponents);
		
		SettingDialog sd = new SettingDialog(null, "Principal Component Plot", hs);
		sd.showAsInputDialog();
		if (sd.canceled())
			return null;
		
		PlotWithLegendAndTitle myComponent;
		myComponent = new PlotWithLegendAndTitle(new PCAPlotComponent(transpose.getBooleanValue(), numberOfComponents.getIntValue()));
		myComponent.setTitledComponent(null);
		return myComponent;
	}

}
