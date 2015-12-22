package mayday.core.io.nativeformat;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import mayday.core.Mayday;
import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.gui.PluginInfoMenuAction;
import mayday.core.io.StorageNode;
import mayday.core.pluginrunner.GenericPluginRunner;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;


/**
 * THis class holds a central repository of recently used files as well as the currently open file.
 * It also provides actions and menus to work on these files..
 * @author battke
 *
 */
public class FileRepository {
	
	private static String name;
	private static JMenu mostRecent = new JMenu("Open recent");
	
	static {
		setName(null);
	}
	
	public static boolean hasFile() {
		return name!=null;
	}
	
	public static String getName() {
		return name;
	}
	
	public static void setName(String Name) {
		name = Name;
		refreshMRU(Name);

		enableFileDependentMenus(hasFile());
		
		String title = MaydayDefaults.PROGRAM_NAME;
		if (hasFile())
			title+=" ["+new File(getName()).getName()+"]";
		else
			title+=" (unsaved project)";
		Mayday.sharedInstance.setTitle(title);
	}
	
	public static void enableFileDependentMenus(boolean en) {
		StoreNamedFileAction.instance.setEnabled(en);
		FileCloseAction.instance.setEnabled(en);
	}
	
	public static void refreshMRU(String Name) {
		Preferences mruNode = MaydayDefaults.getMostRecentFiles();
		LinkedList<String> mrus = new LinkedList<String>();
		for (int i=0; i!=mruNode.getChildren().size(); ++i)
			mrus.add("");
		for (StorageNode s : mruNode.getChildren()) {
			mrus.set(Integer.parseInt(s.Name), s.Value);			
		}
		if (Name!=null)
			mrus.remove(Name);
		
		while (mrus.size()>9)
			mrus.removeLast();

		if (Name!=null)
			mrus.add(0, Name);

		mruNode.clear();
		int i=0;
		for (String m : mrus)
			mruNode.put(""+(i++), m);
		
		
		// update Menu
		mostRecent.removeAll();
		for (String m : mrus)
			mostRecent.add(new LoadNamedFileAction(m));
		mostRecent.setEnabled(mostRecent.getItemCount()>0);
	}
	
	@SuppressWarnings("serial")
	public static JMenu getMenu() {
		JMenu theMenu = new JMenu("File");
		theMenu.setMnemonic( KeyEvent.VK_F );		

		// ========= OPEN
		JMenuItem openFile = new JMenuItem(new FileLoadAction());
		theMenu.add(openFile);	    
		theMenu.add(mostRecent);
		theMenu.add(new JSeparator());

		// ========= SAVE
		theMenu.add(StoreNamedFileAction.instance);
		theMenu.add(FileStoreAction.instance);		
		theMenu.add(new JSeparator());
		
		// ========== QUICKSNAP
		theMenu.add(new QuickLoadAction());
		theMenu.add(QuickStoreAction.instance );
		theMenu.add(new JSeparator());
		
		// ========== Additional plugins for this menu
		Set<PluginInfo> additionalPlugins = PluginManager.getInstance().getPluginsFor(Constants.MC_FILE);
		if (additionalPlugins.size()>0) {
			for (PluginInfo pli : additionalPlugins) {
				PluginInfoMenuAction pima = new PluginInfoMenuAction(pli) {

					@Override
					public void actionPerformed(ActionEvent e) {
						GenericPluginRunner gpr = new GenericPluginRunner(getPlugin());
						gpr.execute();
					}
					
				};
				theMenu.add(pima);
			}
			theMenu.addSeparator();
		}

		// ========= CLOSE		
		theMenu.add(FileCloseAction.instance);
		theMenu.add(new JSeparator());

		// ========= EXIT
		JMenuItem temp = new JMenuItem(new ExitAction());
		temp.setMnemonic( KeyEvent.VK_X );
		theMenu.add(temp);

		return theMenu;
	}
	
	public static String getMostRecentFile() {
		refreshMRU(null);
		if (mostRecent.getMenuComponentCount()==0)
			return null;
		JMenuItem jmi = (JMenuItem)mostRecent.getMenuComponent(0);
		LoadNamedFileAction lnfa = (LoadNamedFileAction)jmi.getAction();
		String name = lnfa.getFileName();
		return name;		
	}
	
}
