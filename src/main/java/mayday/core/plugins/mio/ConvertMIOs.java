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
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.tasks.AbstractTask;

public class ConvertMIOs
extends AbstractMetaInfoPlugin
{

	protected PluginTypeSetting<AbstractPlugin> targetType;

	public Setting getSetting() {
		if (targetType == null) {
			targetType = new PluginTypeSetting<AbstractPlugin>(
					"Target MIO type",null, new StringMIO(), Constants.MC_METAINFO
			);
		}
		return targetType;
	}

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(MIType.class);
	}


	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		getSetting();
		SettingDialog sdl = new SettingDialog(null, "Convert MIO types", targetType);
		sdl.showAsInputDialog();
		if (!sdl.canceled()) {
			AbstractTask at = new AbstractTask("Converting MIO type") {
				@SuppressWarnings("unchecked")
				protected void doWork() throws Exception {
					MIType mt = (MIType)targetType.getInstance();
					String tname = "as "+PluginManager.getInstance().getPluginFromClass((Class<? extends AbstractPlugin>)(mt.getClass())).getName();
					String ttype = mt.getType();
					for (MIGroup mg : selection) {
						MIGroup target = miManager.newGroup(ttype, tname, mg);
						for (Entry<Object, MIType> e : mg.getMIOs()) {
							String ser = e.getValue().serialize(MIType.SERIAL_TEXT);
							MIType tmt = target.add(e.getKey());
							if (!tmt.deSerialize(MIType.SERIAL_TEXT, ser)) {
								writeLog("Could not convert value \""+ser+"\" to type "+ttype+"\n");
								target.remove(e.getKey());
							}
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
				"PAS.mio.converttype",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Converts a MIO group to another type",
		"Convert MIO type");
		return pli;
	}

}
