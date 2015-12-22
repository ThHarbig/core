package mayday.core.datasetmanager.gui;

import mayday.core.Mayday;
import mayday.core.MaydayDefaults;
import mayday.core.datasetmanager.DataSetManager;

public class DataSetManagerView {

	private static DataSetManagerViewInterface realInstance;
	
	public static DataSetManagerViewInterface getInstance() {
		if (realInstance==null) {
			changeInstance();
		}
			
		return realInstance;
	}
	
	public static boolean instanceCreated() {
		return realInstance!=null;
	}
	
	public static void changeInstance() {
        boolean mc = MaydayDefaults.Prefs.useNewLayout.getBooleanValue();
        if (mc) 
        	changeInstance(DataSetManagerViewList.class);
        else
        	changeInstance(DataSetManagerViewTabbed.class);
	}
	
	
	public static void changeInstance(Class<? extends DataSetManagerViewInterface> dsmvInstanceClass) {

		if (realInstance!=null && realInstance.getClass().equals(dsmvInstanceClass))
			return;
		
		DataSetManagerViewInterface oldInstance = realInstance;
		
		// remove the old listener
        DataSetManager.singleInstance.removeStoreListener(oldInstance);        
		try {
			realInstance = dsmvInstanceClass.newInstance();
		} catch (Exception e) {
			System.err.println("Cannot initialize DataSetManagerView, which sucks.");
			e.printStackTrace();
		}
		if (oldInstance!=null) {
			realInstance.setProbeListMenu(oldInstance.getProbeListMenu());
			realInstance.setDataSetMenu(oldInstance.getMenu());			
			oldInstance.moveListenersTo(realInstance);
		}
		
		// update the view in Mayday.java
		Mayday.sharedInstance.replaceDataSetManagerView();
	}
	


}
