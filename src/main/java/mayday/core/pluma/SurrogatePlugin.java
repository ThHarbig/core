package mayday.core.pluma;

public interface SurrogatePlugin<SurrogateObjectType>  {

	public void initializeWithObject(SurrogateObjectType surrogateObject, PluginInfo pli);
	
}
