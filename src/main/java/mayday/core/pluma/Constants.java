package mayday.core.pluma;

public class Constants {

	// properties keys	
	public static final String CLASS_INSTANCE = "PluginManager-ClassInstance";
	public static final String MISSING_DEPENDENCIES = "PluginManager-MissingDependencies";
	public static final String CATEGORIES = "PluginManager-Categories";
	public static final String ICON_PATH = "PluginManager-IconPath";
	public static final String MENU_NAME = "PluginManager-MenuName";
	//public static final String PROVIDES_MC = "PluginManager-ProvidedMasterComponent";
	public static final String NO_CACHE_CLASS_INSTANCE = "PluginManager-ClassInstance-DisableCaching";

	// some master componentes
	public static final String MC_CORE = "Core";
	public static final String MC_MENUBAR = "MenuBar";
	public static final String MC_HELP = "Help";  // not yet used
	
	// Plugins to put in the "File" menu
	public static final String MC_FILE = "Native-File";
	
	// Session menu plugins
	public static final String MC_SESSION = "Session";

	// DataSet menu plugin
	public static final String MC_DATASET = "DataSet";
	public static final String MC_DATASET_IMPORT = "DataSet-Import";
	public static final String MC_DATASET_EXPORT = "DataSet-Export";

	// ProbeList menu plugins
	public static final String MC_PROBELIST = "ProbeList";
	public static final String MC_PROBELIST_CREATE = "ProbeList-Create";
	public static final String MC_PROBELIST_IMPORT = "ProbeList-Import";
	public static final String MC_PROBELIST_EXPORT = "ProbeList-Export";

	// Probe plugins
	public static final String MC_PROBE = "Probe";

	
	// Plugins that provide services, i.e. the StartBrowser plugin
	public static final String MC_SUPPORT = "Supporting Plugins";
	
	// Meta-Information Types
	public static final String MC_METAINFO = "Meta Information Types";
	public static final String MC_METAINFO_PROCESS = "Meta Information Processing";
	
	// GUI-Enhancements 
	public static final String MC_PROPERTYDIALOG = "Property Dialogs";
	public static final String MC_PLUGGABLEVIEWS = "Pluggable view elements";
	
	//Reveal related plugins
	public static final String MC_REVEAL = "Reveal";
	
}
