package mayday.core.pluma;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.pluma.filemanager.FMFile;
import mayday.core.settings.Setting;

@SuppressWarnings("unchecked")
public class PluginInfo implements Comparable {
	
	protected Class<? extends AbstractPlugin> PluginClass;
	
	// the identifier of the plugin. Change this ONLY when major changes will break compatibility,
	protected String Identifier;
	
	// the identifiers of all plugins that we depend on. "core" is implicitly added to this list
	protected String[] Dependencies;
	
	// the identifier of the component the plugin belongs to.  
	private String MasterComponent;
	
	// additional properties that are used by the plugin's MasterComponent
	private HashMap<String, Object> Properties;
	
	private boolean initialized = false;
	
	private boolean unmetDependencies = false;
	
	protected String Author, Email, About, Name;
	
	private PluginInfo() {}
	public final static PluginInfo IGNORE_PLUGIN = new PluginInfo(); 
	
	public PluginInfo(
			Class<? extends AbstractPlugin> pluginClass,
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
		// Critical missing values
			
		if (pluginClass==null) 
			throw new PluginManagerException("PluginInfo needs a valid plugin class");

		if (!AbstractPlugin.class.isAssignableFrom(pluginClass)) 
			throw new PluginManagerException("PluginInfo needs a plugin class derived from AbstractPlugin");
		
		if (identifier==null || identifier.equals("")) 
			throw new PluginManagerException("PluginInfo needs a valid identifier");
		
		if (pluginAuthor==null || pluginEmail==null || pluginAbout==null ||
			pluginAuthor.equals("") || pluginEmail.equals("") || pluginAuthor.equals(""))
			throw new PluginManagerException("Plugin Author/Email/About text not set.");
				
		if (pluginName==null || pluginName.equals("")) 
			throw new PluginManagerException("Plugin has no valid name");
		
		// Non-critical missing values
		if (dependencies==null)
			dependencies = new String[0];
		
		if (properties==null)
			properties=new HashMap<String, Object>();
		
		if (masterComponent==null) {
			//System.err.println("PluginManager: Plugin "+identifier+" is not assigned to any MasterComponent");
			masterComponent="";
		}
		
		PluginClass = pluginClass;
		Identifier = identifier;
		Dependencies = dependencies;
		MasterComponent = masterComponent;
		Properties = properties;		
		Author=pluginAuthor;
		Email=pluginEmail;
		About=pluginAbout;
		Name = pluginName;
	}
	
	public final Class<? extends AbstractPlugin> getPluginClass() { 
		return PluginClass;
	}	
	
	public final String getIdentifier() {
		return Identifier;
	}
	
	public final String[] getDependencies() {
		return Dependencies;
	}
	
	public final String getMasterComponent() {
		return MasterComponent;
	}
	
	public final HashMap<String, Object> getProperties() {
		return Properties;
	}

	public final boolean isInitialized() {
		return initialized;
	}

	public final void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public final boolean hasUnmetDependencies() {
		return unmetDependencies;
	}

	public final void setUnmetDependencies(boolean unmetDependencies) {
		this.unmetDependencies = unmetDependencies;
	}
	
	public String toString() {
		StringBuilder deps = new StringBuilder("");
		for (String depends: Dependencies)
			deps.append(depends+",");
		return
		getName()+" "+
		"["+ 
		(initialized ? "I" : "") +
		(unmetDependencies ? "U" : "") +
		"]"+
		Identifier+
		"@"+MasterComponent+
		"{"+
		deps+
		"}";
	}

	public final String getAbout() {
		return About;
	}

	public final String getAuthor() {
		return Author;
	}

	public final String getEmail() {
		return Email;
	}
	
	public final String getName() {
		return Name;
	}

	//* settings may be changed only BEFORE initialization */
	public final void setName(String name) {
		if (isInitialized())
			throw new RuntimeException("Can't change PluginInfo content after init");
		this.Name=name;
	}
	
	public final void setAuthor(String author) {
		if (isInitialized())
			throw new RuntimeException("Can't change PluginInfo content after init");
		this.Author=author;			
	}
	
	public final void setAbout(String about) {
		if (isInitialized())
			throw new RuntimeException("Can't change PluginInfo content after init");
		this.About=about;
	}
	
	public final void setIdentifier(String identifier) {
		if (isInitialized())
			throw new RuntimeException("Can't change PluginInfo content after init");
		this.Identifier=identifier;
	}
	
	public final void addDependencies(String[] deps) {
		if (isInitialized())
			throw new RuntimeException("Can't change PluginInfo content after init");
		String[] newdep = new String[Dependencies.length+deps.length];
		int pos=0;
		for (String dep : Dependencies)
			newdep[pos++] = dep;
		for (String dep : deps)
			newdep[pos++] = dep;
		Dependencies=newdep;
	}
	
	public final void setEmail(String email) {
		if (isInitialized())
			throw new RuntimeException("Can't change PluginInfo content after init");
		this.Email=email;
	}
	
	public final int compareTo(Object o) {
		int cc = 0;
		if (o instanceof PluginInfo) {
			cc = getName().compareTo(((PluginInfo)o).getName());
			if (cc==0)
				cc = getIdentifier().compareTo(((PluginInfo)o).getIdentifier());
		}
		return cc;
	}
	
	public final AbstractPlugin getInstance() {
		return newInstance();
//		AbstractPlugin result = null;
//		if (hasUnmetDependencies()) {
//			System.err.println("PluginManager: Unmet dependencies while instantiating plugin "+getIdentifier());				
//		} else {
//			Boolean disableCaching = getProperties().containsKey(Constants.NO_CACHE_CLASS_INSTANCE);
//			if (disableCaching==null)
//				disableCaching = false;
//			Object cachedInstance = getProperties().get(Constants.CLASS_INSTANCE);
//			if (!disableCaching && cachedInstance!=null)
//				return (AbstractPlugin)cachedInstance;
//			else {
//				result = newInstance();
//				if (!disableCaching)
//					getProperties().put(Constants.CLASS_INSTANCE, result);
//			}
//		}
//		return result;		
	}
	
	public final AbstractPlugin newInstance() {
		AbstractPlugin result = null;
		if (hasUnmetDependencies()) {
			System.err.println("PluginManager: Unmet dependencies while instantiating plugin "+getIdentifier());				
		} else {
			try {
				result = newInstance0(); 
			} catch (Exception e) {
				System.err.println("PluginManager: Error while instantiating plugin "+getIdentifier());
				System.err.println(PluginClass.getCanonicalName()+": Exception "+e.getClass().getCanonicalName()+"\n"+e.getMessage());
			}
		}
		return result;
	}
	
	protected AbstractPlugin newInstance0() throws InstantiationException, IllegalAccessException {
		return ((Class<? extends AbstractPlugin>)getPluginClass()).newInstance();
	}
	
	
	// FRIENDLY HELPER FUNCTIONS 
	
	/* a helper function to make life easier for plugin programmers
	 * Categories can have subcategories, like so: "MainCategory/FirstLevel/SecondLevel"
	 */
	public final void addCategory(String categoryString) {
		Vector<String> categories = (Vector<String>)getProperties().get(Constants.CATEGORIES);
		if (categories==null) {
			categories = new Vector<String>();
			getProperties().put(Constants.CATEGORIES, categories);
		}
		categories.add(categoryString);
	}
	
	public final void replaceCategory(String categoryString) {
		getProperties().remove(Constants.CATEGORIES);
		addCategory(categoryString);
	}
	
	public final void setIcon(String iconPath) {
		getProperties().put(Constants.ICON_PATH, iconPath);
	}
	
	public final void setMenuName(String menuName) {
		getProperties().put(Constants.MENU_NAME, menuName);
	}
	
/*	// if you set this property, your plugin's init() will be called _after_ all plugins
	// that are part of this MC. 
	public void providesMC(String providedMC) {
		getProperties().put(Constants.PROVIDES_MC, providedMC);
	}*/
	
	public final ImageIcon getIcon() {
		ImageIcon ico = null;
		if (getProperties().containsKey(Constants.ICON_PATH)) {
			String resource = (String)getProperties().get(Constants.ICON_PATH); 
			ico = getIcon(resource);
		}
		return ico;
	}
	
	private static HashMap<String, ImageIcon> IconCache = new HashMap<String, ImageIcon>();
	
	public final static ImageIcon getIcon(String resource) {
		// see if we have a cached version of this icon
		ImageIcon ico = IconCache.get(resource);
		if (ico!=null) 
			return ico;
		
		FMFile splash = PluginManager.getInstance().getFilemanager().getFile(resource);
		if (splash!=null) {
			BufferedImage bfr=null;
			InputStream splash_is = splash.getStream();
			if (splash_is!=null) {
				try {
					bfr = ImageIO.read(splash_is);					
					ico = new ImageIcon(bfr);
				} catch (Exception e) {
					System.err.println("Error loading icon \""+resource+"\": "+e.getClass().getCanonicalName());
					e.printStackTrace();
				}
			}
		}
		
		IconCache.put(resource, ico);
		
		return ico;
	}
	
	/** get a scaled instance of an icon */
	public final static ImageIcon getIcon(String resource, int w, int h) {
		// see if we have a cached version of this icon
		ImageIcon ico = getIcon(resource);
		if (ico!=null)
			return new ImageIcon(ico.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));	
		return null;
	}
	
	public final String getMenuName() {
		if (getProperties().containsKey(Constants.MENU_NAME))
			return (String)(getProperties().get(Constants.MENU_NAME));
		return getName();
	}
	
//  if you need to store preferences, store them here    
	public final Preferences getPreferences() {
        return (getPreferences( this.getIdentifier() ));
    }	
	
	public final static Preferences getPreferences(String pluginID) {
		return (MaydayDefaults.Prefs.getPluginPrefs().node( pluginID ));
	}
	
	public final static void loadDefaultSettings(Setting s, String pluginID) {
		PluginManager.getInstance().getPluginFromID(pluginID).loadDefaultSettings(s);		
	}
	
	public final void loadDefaultSettings(Setting s) {
		Preferences storeNode = getPreferences().node("LAST_USED");
		try {
			if (storeNode.keys().length>0)
				s.fromPrefNode(storeNode.node(storeNode.keys()[0]));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public final static void storeDefaultSettings(Setting s, String pluginID) {
		PluginManager.getInstance().getPluginFromID(pluginID).storeDefaultSettings(s);		
	}
	
	public final void storeDefaultSettings(Setting s) {
		getPreferences().node("LAST_USED").connectSubtree(s.toPrefNode());
	}


	
}
