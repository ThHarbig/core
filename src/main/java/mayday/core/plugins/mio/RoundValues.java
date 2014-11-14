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
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.tasks.AbstractTask;

public class RoundValues extends AbstractMetaInfoPlugin {

	protected IntSetting precission;
	protected RestrictedStringSetting roundMethod;
	protected HierarchicalSetting setting;
	
	private String[] methods = {"round", "floor", "ceil"};
	
	public Setting getSetting() {
		if (setting == null) {
			precission = new IntSetting("Rounding precission","Define the number of digits after the comma.", 2, 0, 24, true, true);
			roundMethod = new RestrictedStringSetting("Round Method", null, 0, methods);
			setting = new HierarchicalSetting("Value ranking").addSetting(roundMethod).addSetting(precission);
		}
		return setting;
	}
	
	@Override
	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		getSetting();
		SettingDialog sdl = new SettingDialog(null, "Round meta-information values", setting);
		sdl.showAsInputDialog();
		
		if (!sdl.canceled()) {
			AbstractTask at = new AbstractTask("Ranking MIO values") {
				@SuppressWarnings({ "rawtypes" })
				protected void doWork() throws Exception {
					int p = precission.getIntValue();
					int rMethod = roundMethod.getSelectedIndex();
					String tname = "rounded with precission: " + Integer.toString(p);
					double prec = Math.pow(10, p);
					
					for (MIGroup mg : selection) {
						MIGroup target = miManager.newGroup("PAS.MIO.Double", tname, mg);
						
						Object[] keys = new Object[mg.size()];
						DoubleVector values = new DoubleVector(keys.length);
						int pos = 0;
						
						for (Entry<Object, MIType> e : mg.getMIOs()) {
							keys[pos] = e.getKey();
							NumericMIO nm = (NumericMIO)e.getValue();
							Number n = (Number)nm.getValue();
							
							double val = n.doubleValue();
							double roundedVal = 0;
							
							switch(rMethod) {
							case 0: 
								roundedVal = Math.round(val * prec) / prec;
								break;
							case 1:
								roundedVal = Math.floor(val * prec) / prec;
								break;
							case 2:
								roundedVal = Math.ceil(val * prec) / prec;
								break;
							default:
								roundedVal = Math.round(val * prec) / prec;
							} 

							values.set(pos, roundedVal);
							++pos;
						}	
						
						for (int i=0; i!=keys.length; ++i) {
							DoubleMIO tmt = (DoubleMIO)target.add(keys[i]);
							tmt.setValue((double)values.get(i));
						}
					}
				}

				protected void initialize() {
				}

			};
			at.start();
		}
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.roundvalues",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Günter Jäger",
				"jaeger@informatik.uni-tuebingen.de",
				"Round numerical MIO values",
		"Round");
		return pli;
	}

	@Override
	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(NumericMIO.class);
	}
}
