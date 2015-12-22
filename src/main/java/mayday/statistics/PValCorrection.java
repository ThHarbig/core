/*
 * Created on Dec 8, 2004
 *
 */
package mayday.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.typed.MIGroupSetting;

/**
 * @author gehlenbo
 *
 */
public class PValCorrection
extends AbstractPlugin
implements ProbelistPlugin
{
	
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"PAS.statistics.correction",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Applies a multiple-testing correction to existing p-values",
		"p-value correction");
		pli.addCategory(MaydayDefaults.Plugins.SUBCATEGORY_STATISTICS);
		return pli;
	}
	
	protected PluginTypeSetting<PCorrectionPlugin> method;
	protected MIGroupSetting inputMIO;

	public List<ProbeList> run( List<ProbeList> probeLists, MasterTable masterTable )	{
		
		ProbeList uniqueProbes = ProbeList.createUniqueProbeList(probeLists);
		
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(PCorrectionPlugin.MC);
		
		HierarchicalSetting statSetting = new HierarchicalSetting("p-Value correction").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
		.addSetting( inputMIO = new MIGroupSetting("p-Value MIO Group",null,null,masterTable.getDataSet().getMIManager(),false).setAcceptableClass(DoubleMIO.class))
		.addSetting( method = new PluginTypeSetting<PCorrectionPlugin>("Correction Method",null, (PCorrectionPlugin)plis.iterator().next().getInstance(), plis) );
		
		Settings s = new Settings(statSetting, PluginInfo.getPreferences("PAS.statistics.correction"));		
		SettingsDialog sd = new SettingsDialog(null, "p-Value correction", s);
		
		if (sd.showAsInputDialog().canceled())
			return null;
		
		// collect all pvalues now
		ProbeList union = ProbeList.createUniqueProbeList(probeLists);
		MIGroup mg = inputMIO.getMIGroup();
				
		double[] dv = new double[union.getNumberOfProbes()];
		
		for (int i=0; i!=dv.length; ++i) {
			dv[i]=((DoubleMIO)mg.getMIO(uniqueProbes.getProbe(i))).getValue();
		}
		
		List<Double> res = method.getInstance().correct(dv);
		
		if (res==null)
			return null;

		MIGroup rg = masterTable.getDataSet().getMIManager().newGroup(
				"PAS.MIO.Double", 
				PluginManager.getInstance().getPluginFromClass(method.getInstance().getClass()).getName(),
				mg);
		
		for (int i=0; i!=res.size(); ++i) {
			((DoubleMIO)rg.add(uniqueProbes.getProbe(i))).setValue(res.get(i));
		}
			
		return null;
	}

	@Override
	public void init() {
	}
}
