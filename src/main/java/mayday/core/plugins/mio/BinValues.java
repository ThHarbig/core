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
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.tasks.AbstractTask;

public class BinValues
extends AbstractMetaInfoPlugin
{

	protected DoubleSetting startBin;
	protected DoubleSetting binSize;
	protected BooleanSetting absolutes;
	protected HierarchicalSetting setting;

	public Setting getSetting() {
		if (setting == null) {
			startBin = new DoubleSetting("Start of first bin",null, 0);
			binSize = new DoubleSetting("Bin size",null, 10);
			absolutes = new BooleanSetting("Bin absolute values", null, false);
			setting = new HierarchicalSetting("Value binning").addSetting(startBin).addSetting(binSize).addSetting(absolutes);
		}
		return setting;
	}

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(NumericMIO.class);
	}


	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		getSetting();
		SettingDialog sdl = new SettingDialog(null, "Bin values", setting);
		sdl.showAsInputDialog();
		if (!sdl.canceled()) {
			AbstractTask at = new AbstractTask("Converting MIO type") {
				@SuppressWarnings("unchecked")
				protected void doWork() throws Exception {
					double start = startBin.getDoubleValue();
					double size = binSize.getDoubleValue();
					boolean abs = absolutes.getBooleanValue();
					String tname = "binned ("+start+" + "+size+")";
					
					for (MIGroup mg : selection) {
						MIGroup target = miManager.newGroup("PAS.MIO.String", tname, mg);
						for (Entry<Object, MIType> e : mg.getMIOs()) {
							NumericMIO nm = (NumericMIO)e.getValue();
							Number n = (Number)nm.getValue();
							double val = n.doubleValue();
							if (abs)
								val = Math.abs(val);
							val -= start;
							size = .3;
							val /= size;
							val = (double)((int)val);
							val *= size;
							val += start; // now val=start of target bin
							String s = val+" - "+(val+size);							
							StringMIO tmt = (StringMIO)target.add(e.getKey());
							tmt.setValue(s);
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
				"PAS.mio.binvalues",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Bins numerical MIO values into predefined bins",
		"Value Binning");
		return pli;
	}

}
