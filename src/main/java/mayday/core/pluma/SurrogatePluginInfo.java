package mayday.core.pluma;

import java.util.HashMap;


/** This class represents plugins that are not real java classes themselves, either because they are adapted to PLUMA by 
 * other classes or because they are e.g. javascript or R scripts. These will usually be added to Pluma using
 * PluginManager.addLatePlugin(SurrogatePluginInfo). 
 * The second type parameter is only used to ensure compile-time type safety checks for the cast in newInstance().
 * @author battke
 */
public final class SurrogatePluginInfo<SurrogateObjectType, PluginType extends AbstractPlugin & SurrogatePlugin<SurrogateObjectType>> extends PluginInfo {
	
	protected final static String SURROGATE_OBJECT = "SurrogatePluginInfo-Represented-Object";
	
	public SurrogatePluginInfo(
			Class<PluginType> pluginClass,
			SurrogateObjectType surrogateObject,
			String identifier, 			
			String[] dependencies, 
			String masterComponent, 
			HashMap<String, Object> properties,
			String pluginAuthor,
			String pluginEmail,
			String pluginAbout,
			String pluginName
			) throws PluginManagerException 
	{
		super(pluginClass, identifier, dependencies, masterComponent, properties, pluginAuthor, pluginEmail, pluginAbout, pluginName);
		getProperties().put(SURROGATE_OBJECT, surrogateObject);
	}
	
	@SuppressWarnings("unchecked")
	public final AbstractPlugin newInstance0() throws InstantiationException, IllegalAccessException {
		AbstractPlugin result = super.newInstance0();
		SurrogateObjectType o = (SurrogateObjectType)getProperties().get(SURROGATE_OBJECT);
		((SurrogatePlugin<SurrogateObjectType>)result).initializeWithObject(o, this);
		return result;
	}
		
	

}
