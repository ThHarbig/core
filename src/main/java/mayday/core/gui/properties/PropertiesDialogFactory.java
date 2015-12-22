package mayday.core.gui.properties;

import java.util.HashMap;
import java.util.Set;

import mayday.core.gui.properties.dialogs.AbstractPropertiesDialog;
import mayday.core.gui.properties.dialogs.MultiplePropertiesDialog;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

public class PropertiesDialogFactory {

	public final static String PropertyKey1 = "PropertiesDialog-ObjectClass";
	public final static String PropertyKey2 = "PropertiesDialog-DialogClass";
	
	@SuppressWarnings("unchecked")
	private final static HashMap<Class, Class<? extends AbstractPropertiesDialog>> dialogs
		= new HashMap<Class, Class<? extends AbstractPropertiesDialog>>();
	
	@SuppressWarnings("unchecked")
	public static AbstractPropertiesDialog createDialog(Object propertyObject) {
		if (dialogs.size()==0) 
			init();
		
		try {
			Class<? extends AbstractPropertiesDialog> dlgclass = null;
			// find some matching dialog
			for ( Class c : dialogs.keySet() ) {
				if (c.isAssignableFrom(propertyObject.getClass())) {
					 dlgclass = dialogs.get(c);
					 break;
				}
			}
			// try to find a perfect match
			for ( Class c : dialogs.keySet() ) {
				if (c.equals(propertyObject.getClass())) {
					 dlgclass = dialogs.get(c);
					 break;
				}
			}
			if (dlgclass!=null) {
				AbstractPropertiesDialog dlg = dlgclass.newInstance();
				dlg.assignObject(propertyObject);
				return dlg;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not build Property Dialog ("+e.getClass()+")\n"+e.getMessage());			
		}
		
		throw new RuntimeException("No Property Dialog known for objects of class "+propertyObject.getClass());
		
	}
	
	public static AbstractPropertiesDialog createDialog(Object[] propertyObjects) {
		if (propertyObjects.length==1)
			return createDialog(propertyObjects[0]);
		else 
			return new MultiplePropertiesDialog(propertyObjects);
	}
	
	@SuppressWarnings("unchecked")
	public static void init() {
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(Constants.MC_PROPERTYDIALOG);
		for(PluginInfo pli : plis)
			registerDialogClass((Class)pli.getProperties().get(PropertyKey1),
								(Class)pli.getProperties().get(PropertyKey2));
	}
	
	@SuppressWarnings("unchecked")
	public static void registerDialogClass(Class objectClass, Class<? extends AbstractPropertiesDialog> dlgclass) {
		if (dialogs.containsKey(objectClass))
			throw new RuntimeException("Trying to register another handler for property dialogs of class "+objectClass);
		dialogs.put(objectClass, dlgclass);
	}
	
}
