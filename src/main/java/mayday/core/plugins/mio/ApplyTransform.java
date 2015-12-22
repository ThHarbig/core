/*
 * Created on Dec 8, 2004
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
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.methods.ManipulationMethodSetting;
import mayday.vis3.model.ManipulationMethodSingleValue;
import mayday.vis3.model.manipulators.None;

/**
 * @author gehlenbo
 *
 */
public class ApplyTransform extends AbstractMetaInfoPlugin
{
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.mio.applytransformation",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Transforms input values using a data manipulation.",
				"Apply data manipulation method");
		return pli;
	}
	
	protected Class<? extends MIType> getMIOClass() {
		return NumericMIO.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(MIGroupSelection<MIType> input, MIManager miManager) {

		ManipulationMethodSetting mioManipulator = new ManipulationMethodSetting("Select transformation", null, new None(), true);
		SettingDialog sd = new SettingDialog(null, "Transform meta information", mioManipulator);
		sd.showAsInputDialog();
		if (sd.canceled()) 
			return;
			
		ManipulationMethodSingleValue manip = (ManipulationMethodSingleValue)mioManipulator.getInstance();
		
		for (MIGroup mg : input) {

			MIGroup rg = miManager.newGroup(
					"PAS.MIO.Double", 
					PluginManager.getInstance().getPluginFromClass((Class<? extends AbstractPlugin>) manip.getClass()).getName(),
					mg);
			
			for (Entry<Object, MIType> entry : mg.getMIOs()) {
				DoubleMIO dm2 = new DoubleMIO(manip.manipulate(((Number)((NumericMIO)entry.getValue()).getValue()).doubleValue()));
				rg.add(entry.getKey(), dm2);
			}

		}		
	}

	@Override
	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(getMIOClass());		
	}
	
}
