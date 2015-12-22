package mayday.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;



public class PValCorrectionMIO extends AbstractMetaInfoPlugin {
	
	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.pvalcorrection",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Applies a multiple-testing correction to existing p-values",
		"p-value correction");
		return pli;
	}

	protected PluginTypeSetting<PCorrectionPlugin> method;

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(DoubleMIO.class);		
	}

	@Override
	public void run(MIGroupSelection<MIType> input, MIManager miManager) {

		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(PCorrectionPlugin.MC);

		HierarchicalSetting statSetting = new HierarchicalSetting("p-Value correction").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
		.addSetting( method = new PluginTypeSetting<PCorrectionPlugin>("Correction Method",null, (PCorrectionPlugin)plis.iterator().next().getInstance(), plis) );

		Settings s = new Settings(statSetting, PluginInfo.getPreferences("PAS.statistics.correction"));		
		SettingsDialog sd = new SettingsDialog(null, "p-Value correction", s);

		if (sd.showAsInputDialog().canceled())
			return;

		for (MIGroup mg : input) {

			double[] dv = new double[mg.size()];
			Object[] k = new Object[mg.size()];

			int i=0;
			for (Entry<Object, MIType> entry : mg.getMIOs()) {
				dv[i]=((DoubleMIO)entry.getValue()).getValue();
				k[i]=entry.getKey();
				++i;
			}

			List<Double> res = method.getInstance().correct(dv);

			if (res==null)
				return;

			MIGroup rg = miManager.getDataSet().getMasterTable().getDataSet().getMIManager().newGroup(
					"PAS.MIO.Double", 
					PluginManager.getInstance().getPluginFromClass(method.getInstance().getClass()).getName(),
					mg);

			for (i=0; i!=res.size(); ++i) {
				((DoubleMIO)rg.add(k[i])).setValue(res.get(i));
			}

		}
	}	
}
