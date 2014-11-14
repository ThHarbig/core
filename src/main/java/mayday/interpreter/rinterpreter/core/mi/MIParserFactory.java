package mayday.interpreter.rinterpreter.core.mi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

public class MIParserFactory {

	public final static String KEY_PARSEDTYPE = "MIParserFactory-parsedType";
	public final static String MC_MITYPEPARSER = "MIO Parser";
	
	private static Map<String, PluginInfo> parserClasses =
		new HashMap<String, PluginInfo>();
	
	@SuppressWarnings("unchecked")
	public static MITypeParser createParser(String mioType) throws MIOParserNotFoundException {
		
		if (parserClasses.size()==0)
			init();
		
		PluginInfo parserPlugin;
		try {
			if ((parserPlugin=parserClasses.get(mioType))!=null) {
				return (MITypeParser)parserPlugin.getInstance(); // only one instance needed
			}
		} catch (Exception e) {
			throw new MIOParserNotFoundException("Could not create MIType parser for MIOs of type "+mioType
					+":("+e.getClass()+")\n"+e.getMessage());			
		}
		
		throw new MIOParserNotFoundException("No MIType parser known for MIOs of type "+mioType);
		
	}
	
	public static void init() {
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(MC_MITYPEPARSER);
		for(PluginInfo pli : plis)
			registerParser((String)pli.getProperties().get(KEY_PARSEDTYPE),
								pli);
	}
	
	public static void registerParser(String mioType, PluginInfo parserPlugin) {
		parserClasses.put(mioType, parserPlugin);
	}
	
}
