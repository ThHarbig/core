package mayday.core.meta.plugins;

import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;


public interface MetaInfoPlugin {

	public final static String ACCEPTABLE_CLASSES = "MIType-Acceptable-Classes";
	public final static String MULTISELECT_HANDLING = "MIType-Multiselect-Handling";
	
	public final static String MULTISELECT_HANDLE_INTERNAL = "call plugin with all selected groups"; //default
	// start the plugin with all selected lists
	
	public final static String MULTISELECT_HANDLE_BY_REPEAT = "call plugin once for every selected group";
	// start the plugin once for each selected list
	
	public final static String MULTISELECT_HANDLE_ASK_USER = "ask user";
	// ask the user what to do
	
	public final static String MULTISELECT_HANDLE_DEFAULT = MULTISELECT_HANDLE_INTERNAL;
	
	public abstract void run(MIGroupSelection<MIType> input, MIManager miManager);

}
