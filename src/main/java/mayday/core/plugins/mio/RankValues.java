/*
 * Created on Jan 24, 2005
 *
 */
package mayday.core.plugins.mio;

import java.util.HashMap;
import java.util.Map.Entry;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.NumericMIO;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.meta.types.IntegerMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.tasks.AbstractTask;

public class RankValues
extends AbstractMetaInfoPlugin
{

	protected BooleanSetting ascending, absolute;
	protected HierarchicalSetting setting;

	public Setting getSetting() {
		if (setting == null) {
			ascending = new BooleanSetting("Rank ascending","If selected, the largest value will have the smallest rank.", true);
			absolute = new BooleanSetting("Use absolute values","If selected, ranks will be computed on the absolute values.", false);
			setting = new HierarchicalSetting("Value ranking").addSetting(ascending).addSetting(absolute);
		}
		return setting;
	}

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(NumericMIO.class);
	}


	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		getSetting();
		SettingDialog sdl = new SettingDialog(null, "Value ranking", setting);
		sdl.showAsInputDialog();
		if (!sdl.canceled()) {
			AbstractTask at = new AbstractTask("Ranking MIO values") {
				@SuppressWarnings("unchecked")
				protected void doWork() throws Exception {
					boolean asc = ascending.getBooleanValue();
					boolean abs = absolute.getBooleanValue();
					String tname = "ranked";
					
					for (MIGroup mg : selection) {
						
						MIGroup target = miManager.newGroup("PAS.MIO.Integer", tname, mg);
						
						Object[] keys = new Object[mg.size()];
						DoubleVector values = new DoubleVector(keys.length);
						int pos = 0;
						
						for (Entry<Object, MIType> e : mg.getMIOs()) {
							keys[pos] = e.getKey();
							NumericMIO nm = (NumericMIO)e.getValue();
							Number n = (Number)nm.getValue();
							double val = n.doubleValue();
							if (abs)
								val = Math.abs(val);
							if (!asc)
								val = -val;
							values.set(pos, val);
							++pos;
						}
						
						values = values.rank();
							
						
						for (int i=0; i!=keys.length; ++i) {
							IntegerMIO tmt = (IntegerMIO)target.add(keys[i]);
							tmt.setValue((int)values.get(i));
						}
					}
				}

				protected void initialize() {
				}

			};
			at.start();
		}

	}

	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.rankvalues",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Produces a ranking of numerical MIO values",
		"Ranking");
		return pli;
	}

}
