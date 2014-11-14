package mayday.core.plugins.mio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.meta.types.StringListMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.StringSetting;
import mayday.core.tasks.AbstractTask;

public class ConvertToStringListMIO extends AbstractMetaInfoPlugin {
	protected StringSetting splitBySetting;

	public Setting getSetting() {
		if (splitBySetting == null) {
			splitBySetting = new StringSetting("Split by", "Use a regular expression to set the delimiter. Default is \";\".", ";");
				
		}
		return splitBySetting;
	}

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(StringMIO.class);
	}
	
	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		getSetting();
		SettingDialog sdl = new SettingDialog(null, "Convert MIO types", splitBySetting);
		sdl.showAsInputDialog();
		if (!sdl.canceled()) {
			AbstractTask at = new AbstractTask("Splitting MIO") {
				
				@SuppressWarnings("unchecked")
				protected void doWork() throws Exception 	{
					String regex=splitBySetting.getStringValue();
					System.out.println(regex);
					MIType mt = new StringListMIO();
					String tname = "as "+PluginManager.getInstance().getPluginFromClass((Class<? extends AbstractPlugin>)(mt.getClass())).getName();
					String ttype = mt.getType();
					for (MIGroup mg : selection) {
						MIGroup target = miManager.newGroup(ttype, tname, mg);
						for (Entry<Object, MIType> e : mg.getMIOs()) {
							String val= ((StringMIO)e.getValue()).getValue();
							
							String[] tok = val.split(regex);
							List<String> list=new ArrayList<String>();
							for(String t : tok) {
								list.add(t.trim());
							}

							StringListMIO resultMIO=new StringListMIO(list);
							target.add(e.getKey(), resultMIO);
						}
					}
				}

				protected void initialize() {}

			};
			at.start();
		}

	}
	
	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.converttostringlist",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Splits string mios to build a list of shorter strings",
		"Convert to String List MIO");
		return pli;
	}
	
	public static void main(String[] args) {
		String s="0005515 	 protein binding 	 inferred from physical interaction\n0016455 	 RNA polymerase II transcription mediator activity 	 inferred from physical interaction";
		
		String[] tok=s.split("(\\n)");
		for(String t:tok)
			System.out.println("*"+t);
	}
}
