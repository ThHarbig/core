package mayday.vis3.gui.menu;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import mayday.core.settings.Setting;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.structures.maps.MultiHashMap;

/** a class that collects all menu items (swing- and settings-based) for a plot window.
 * The "real" JMenu is built as soon as the popup is shown for the first time, by overloading
 * JMenu:getPopupMenuOrigin
 * @author fb
 *
 */

@SuppressWarnings("serial")
public class ViewSettingsMenu extends JMenu {
	
	protected MultiHashMap<Object, Component> viewMenuItems;
	protected MultiHashMap<Object, Setting> viewSettings;	
	protected MultiHashMap<Object, Component> enhanceMenuItems;
	protected Window parent;
	protected HashMap<Object, String> titles;
	protected boolean isMenuFinalized = false;
	protected MenuManager menuManager;
	
	public ViewSettingsMenu(
			MenuMapAdapter viewMenuItems,
			MultiHashMap<Object, Setting> viewSettings,	
			MenuMapAdapter enhanceMenuItems,
			Window parent,
			HashMap<Object, String> titles,
			MenuManager menuManager
	) {
		super("View");
		this.viewMenuItems = viewMenuItems.getMap();
		this.viewSettings = viewSettings;	
		this.enhanceMenuItems = enhanceMenuItems.getMap();
		this.parent = parent;
		this.titles = titles;
		this.menuManager = menuManager;
	}

	
	protected String findUniqueTitle(String title, Set<String> competitors) {
		int add=0;
		String suggested = title;
		
		boolean satisfied = true;
		do {
			if (!satisfied)
				suggested = title+" "+(++add);
			satisfied = !competitors.contains(suggested);
		} while (!satisfied);
		return suggested;
	}

	protected void finalizeMenu() {
		if (isMenuFinalized)
			return;
		isMenuFinalized = true;
		
		// make sure we have unique titles for the submenus
		Set<String> titlesSoFar = new HashSet<String>();
		for (Object o : new HashSet<Object>(titles.keySet())) {
			String otitle = titles.get(o);
			otitle = findUniqueTitle(otitle, titlesSoFar);
			titles.put(o, otitle);
			titlesSoFar.add(otitle);
		}
		
		// sort by title if possible
		Set<Object> askers = new TreeSet<Object>(new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				String s1 = titles.get(o1);
				String s2 = titles.get(o2);
				if (s1==null && s2==null)
					return 0;
				if (s1==null)
					return -1; // sort NULL item to front
				if (s2==null)
					return 1;
				return s1.compareTo(s2);
			}
		});
		
		
		askers.addAll(viewMenuItems.keySet());
		askers.addAll(viewSettings.keySet());
		
		if (askers.size()>1) {
			// create a hierarchical setting and add all child settings to it
			Settings masterSetting = new Settings(
					new HierarchicalSetting(menuManager.getPreferredTitle())
					.setLayoutStyle(LayoutStyle.TREE)
					.setCombineNonhierarchicalChildren(true), 
					null);
			
			for (Object asker : askers) {
				List<Setting> sett = viewSettings.get(asker, true);
				String title = titles.get(asker);
				if (sett.size()>0) {
					HierarchicalSetting subSetting;
					// === special handling of the "null" asker, which will always be added at the menu root
					if (asker==null)
						subSetting = masterSetting.getRoot();
					else {
						title = titles.get(asker);
						subSetting = new HierarchicalSetting(title)  
										.setLayoutStyle(LayoutStyle.TABBED)
										.setCombineNonhierarchicalChildren(true);
						masterSetting.getRoot().addSetting(subSetting);
					}
					for (Setting s : sett)
						subSetting.addSetting(s);
				}
			}
			
			// add these all to the menu
			if (masterSetting.getRoot().getChildren().size()>0)
				masterSetting.addToMenu(this, parent);

			boolean needSep = (getMenuComponentCount()>0);

			// now add sub menus for all other settings
			for (Object asker : askers) {
				List<Component> vcomps = viewMenuItems.get(asker, true);
				List<Component> ecomps = enhanceMenuItems.get(asker, true);
				if (vcomps.size()+ecomps.size()>0) {
					
					if (needSep) {
						addSeparator();
						needSep = false;
					}
					
					JMenu subMenu;
					// === special handling of the "null" asker, which will always be added at the menu root
					if (asker==null)
						subMenu = this;
					else {
						String title = titles.get(asker);
						subMenu = new JMenu(title);
						add(subMenu);
					}
					
					if (vcomps.size()==0) { // add enhance settings directly here
						for (Component c : ecomps)
							subMenu.add(c);
					} else {
						addMenuElements( subMenu , asker );
					}
					
				}
					
			}
			
		} else if (askers.size()==1){
			//add all settings of the SINGLE asker directly to the menu
			Object asker = askers.iterator().next();
			List<Setting> settings = viewSettings.get(asker, true);
			if (settings.size()>0) {
				Settings masterSetting = new Settings(
						new HierarchicalSetting(menuManager.getPreferredTitle())
						.setLayoutStyle(LayoutStyle.TABBED)
						.setCombineNonhierarchicalChildren(true), 
						null);
				for (Setting s : settings) 				
					masterSetting.getRoot().addSetting(s);
				masterSetting.addToMenu(this, parent);
			}
			//add the legacy settings now			
			addMenuElements( this, asker);			
		}
	}
	
	protected Point getPopupMenuOrigin() {
		finalizeMenu();
		return super.getPopupMenuOrigin();
	}
	
	protected void addMenuElements( JMenu menu, Object asker ) {
		List<Component> vcomps = viewMenuItems.get(asker, true);
		List<Component> ecomps = enhanceMenuItems.get(asker, true);
		// add separator before new entries
		if (vcomps.size()+ecomps.size()>0 && menu.getMenuComponentCount()>0 
				&& !(menu.getMenuComponent(menu.getMenuComponentCount()-1) instanceof JSeparator))
			menu.add(new JSeparator());
		// add enhance menu if not empty
		if (ecomps.size()>0) {
			JMenu enhanceMenu=new JMenu("Enhance");
			for (Component c : ecomps)
				enhanceMenu.add(c);			
			menu.add(enhanceMenu);
		}
		for (Component c : vcomps)
			menu.add(c);
	}
	
	
}
