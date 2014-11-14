package mayday.core.settings;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import mayday.core.Preferences;
import mayday.core.settings.generic.HierarchicalSetting;

public class Settings {

	protected HierarchicalSetting root;
	protected Preferences prefTreeRoot;
	
	public Settings(HierarchicalSetting settingRoot, Preferences prefTree) {		
		root = settingRoot;
		root.setTopMost(true);
		connectToPrefTree(prefTree);
	}
	
	
	public SettingComponent getSettingComponent() {
		SettingComponent sp = root.getGUIElement();
		return sp;
	}
	
	public SettingsDialog getDialog(Window owner, String title) {
		return new SettingsDialog(owner, title, this);
	}
	
	public SettingsDialog getDialog() {
		return getDialog(null, root.getName());
	}
	
	public JMenu getMenu( Window parent ) {
		JMenu mnu = (JMenu)root.getMenuItem( parent );
		mnu.add(new JSeparator());
		mnu.add(new SettingsDialogMenuItem(this, parent));
		return mnu;
	}
	
	public void addToMenu(JMenu mnu, Window parent) {
		JMenu subM = getMenu(parent);
		for (Component c : subM.getPopupMenu().getComponents()) {
			mnu.add(c);
		}
	}
	
	public void addToMenu(JMenu mnu, Component parent) {
		// find the enclosing window if possible
		addToMenu(mnu, getOutermostJWindow(parent));
	}
	
	protected Window getOutermostJWindow(Component parent) {
		Component comp = parent;
		while (comp!=null && !(comp instanceof Window)) {
			comp=comp.getParent();
		}
		return((Window)comp);
	}
	
	public Component getLoadStoreGUIElement(SettingsDialog sd) {
		return new LoadStoreSettings(sd);
	}
	
	public void connectToPrefTree(Preferences localRoot) {
		if (localRoot == prefTreeRoot)
			return;
		prefTreeRoot = localRoot;
		// load Settings
		Preferences storeNode = prefTreeRoot.node("LAST_USED");
		if (storeNode.keys().length>0)
			root.fromPrefNode(storeNode.node(storeNode.keys()[0])); //exactly ONE child here
	}
	
	public void storeCurrentSettingAsDefault() {
		if (prefTreeRoot!=null) {			
			prefTreeRoot.node("LAST_USED").connectSubtree(root.toPrefNode());
		}
	}
	
	public List<Preferences> getStoredSettings() {
		ArrayList<Preferences> stored = new ArrayList<Preferences>();
		if (prefTreeRoot!=null) {
			Preferences storedNode = prefTreeRoot.node("STORED_SETTINGS");
			for (String key : storedNode.keys()) 
				stored.add(storedNode.node(key));
		}
		return stored;
	}
	
	public Preferences getLastUsedSettings() {
		Preferences storeNode = prefTreeRoot.node("LAST_USED");
		if (storeNode.keys().length>0)
			return storeNode.node(storeNode.keys()[0]);
		return null;
	}
	
	public void removeStoredSetting(String name) {
		if (prefTreeRoot!=null) {		
			Preferences storedNode = prefTreeRoot.node("STORED_SETTINGS");
			storedNode.remove(name);
		}
	}
	
	public void storeCurrentAs(String name) {
		if (prefTreeRoot!=null) {		
			Preferences storedNode = prefTreeRoot.node("STORED_SETTINGS");
			storedNode.node(name).connectSubtree(root.toPrefNode());
		}
	}
	
	public HierarchicalSetting getRoot() {
		return root;
	}

	public Preferences getPrefTreeRoot() {
		return prefTreeRoot;
	}
	
	public Setting getChild(String name, boolean recursive) {
		return root.getChild(name, recursive);
	}
	
}
