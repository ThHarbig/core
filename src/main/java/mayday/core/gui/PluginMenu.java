package mayday.core.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import mayday.core.gui.components.MenuOverflowLayout;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerListener;
import mayday.core.pluma.prototypes.ApplicableFunction;
import mayday.core.pluma.prototypes.MenuMakingPlugin;
import mayday.core.pluma.prototypes.MenuPlugin;

/** PluginMenu automatically updates a menu's content on each access taking into account
 * - the current selection of objects if it is a context menu
 * - the applicability of the menu items to the current selection
 * - the plugins registered for this menu (might change via pluginmanager.addlateplugin
 * @author battke
 *
 */
@SuppressWarnings("serial")
public abstract class PluginMenu<SelectableObjectType> extends JMenu implements PluginManagerListener {

	protected String[] MCs;
	protected boolean needUpdateContents, needUpdateSelection, flatten;
	public HashSet<Object> alwaysenabled = new HashSet<Object>();
	public List<Object> addedBefore = new LinkedList<Object>();
	public List<Object> addedAfter;
	private final static Object[] EMPTY_SELECTION = new Object[0];

	public PluginMenu(String name, String... MC) {
		super(name);
		setLayout(new MenuOverflowLayout());
		MCs = MC;
		needUpdateContents = true;
		flatten = false;
		PluginManager.getInstance().addPluginManagerListener(this);
	}

	public void setFlatStyle(boolean flatStyle) {
		flatten = flatStyle;
	}

	public void fill() {
		addedAfter = new LinkedList<Object>();
	}

	protected boolean isApplicable(PluginInfo pli, List<SelectableObjectType> selection) {
		if (alwaysenabled.contains(pli))
			return true;
		if (ApplicableFunction.class.isAssignableFrom(pli.getPluginClass())) {
			AbstractPlugin apl = pli.getInstance();
			return ((ApplicableFunction)apl).isApplicable(selection==null?EMPTY_SELECTION:selection.toArray());
		} else {
			return selection==null || selection.size()>0;
		}
	}

	public JPopupMenu getPopupMenu() {
		if (needUpdateContents)
			updateContents();
		else if (needUpdateSelection)
			updateSelection();	
		// this line is necessary to make the menu work both in Menu bar as well as in context click
		super.getPopupMenu().setInvoker(this);
		return super.getPopupMenu();
	}

	protected void addToPopup(JPopupMenu ppm, Object o) {
		if (o instanceof AbstractAction) {
			ppm.add((AbstractAction)o);
		} else if (o instanceof Component) {
			ppm.add((Component)o);	
		} else if (o instanceof JMenuItem) {
			ppm.add((JMenuItem)o);	
		} else if (o instanceof JMenu) {
			ppm.add((JMenu)o);
		}
	}

	protected void updateContents() {
		JPopupMenu ppm = super.getPopupMenu();
		ppm.removeAll();
		for (Object o : addedBefore) {
			addToPopup(ppm,o);
		}
		Set<PluginInfo> plis = new TreeSet<PluginInfo>();
		for (String MC : MCs) {
			plis.addAll( PluginManager.getInstance().getPluginsFor(MC) );
		}
		filter(plis);
		if (flatten) {
			addToMenuFlattened(plis);
		} else {
			addToMenu(this, plis);
		}
		for (Object o : addedAfter) {
			addToPopup(ppm,o);
		}
		needUpdateContents=false;
		updateSelection();
	}

	protected void filter(Set<PluginInfo> pli) {
		// void;
	}

	protected boolean updateSelection(JPopupMenu m, List<SelectableObjectType> selection) {
		boolean anyEnabled = false;
		for (Component c : m.getComponents()) {
			if (c instanceof JMenu) {
				JMenu jm = (JMenu)c;
				boolean en = updateSelection(jm.getPopupMenu(), selection);
				jm.setEnabled(en);
				anyEnabled |= en;
			} else if (c instanceof JMenuItem) {
				JMenuItem oldItem = ((JMenuItem)c);
				boolean en = true;
				Action a = oldItem.getAction();
				if (!alwaysenabled.contains(oldItem)) {
					en = selection==null || selection.size()>0;
					if (alwaysenabled.contains(a)) {
						en = true; 
					} else if (a instanceof PluginInfoMenuAction) {
						en = ( isApplicable(((PluginInfoMenuAction)a).p, selection));
					}
				}					
				a.setEnabled(en);
				anyEnabled |= en;
			} 
		}
		return anyEnabled;
	}

	protected void updateSelection() {
		JPopupMenu ppm = super.getPopupMenu();
		List<SelectableObjectType> selection = getSelection();
		updateSelection(ppm, selection);
		needUpdateSelection=false;
	}

	protected void addToLists(Object o) {
		if (addedAfter==null)
			addedBefore.add(o);
		else
			addedAfter.add(o);
	}

	public JMenuItem add(Action a, boolean alwaysenabled) {
		if (alwaysenabled) 
			this.alwaysenabled.add(a);
		addToLists(a);
		return null;
	}

	public JMenuItem add(JMenuItem jmi, boolean alwaysenabled) {
		if (jmi instanceof JMenu)
			return add((JMenu)jmi,alwaysenabled);
		else 
			return add(jmi.getAction(),alwaysenabled);
	}

	public JMenuItem add(JMenu submenu, boolean alwaysenabled) {
		if (alwaysenabled) { 
			this.alwaysenabled.add(submenu);
			includeEnabled(submenu.getPopupMenu());
		}
		addToLists(submenu);
		return null;
	}

	public void addSeparator() {
		addToLists(new JPopupMenu.Separator());
	}

	protected void includeEnabled(JPopupMenu m) {
		for (Component c : m.getComponents()) {
			if (c instanceof JMenu) {
				JMenu jm = (JMenu)c;
				includeEnabled(jm.getPopupMenu());
			} else {
				alwaysenabled.add(c);
			}
		}
	}

	//	public JMenuItem add(JMenuItem jmi) {
	//		//System.out.println("Adding item  "+jmi.getLabel());
	//		return add(jmi,false);
	//	}
	//	
	//	public JMenuItem add(Action a) {
	//		return add(a,false);
	//	}
	//	
	//	public JMenuItem add(JMenu submenu) {
	//		return add(submenu, false);
	//	}

	public abstract void callPlugin(PluginInfo pli, List<SelectableObjectType> selection);

	protected List<SelectableObjectType> getSelection() {
		return null;
	}

	@SuppressWarnings("unchecked")
	protected void addToMenu(JMenu menu, Collection<PluginInfo> plugins) {
		LinkedList< llitem > items = new LinkedList<llitem>();
		for (PluginInfo pli : plugins) {
			Vector<String> plpath = (Vector<String>)(pli.getProperties().get(Constants.CATEGORIES));
			if (plpath==null)
				items.add(new llitem(pli.getMenuName(),pli));
			else 
				for (String subcat : plpath)
					if (subcat.trim().length()==0)
						items.add(new llitem(pli.getMenuName(),pli));
					else
						items.add(new llitem(subcat.replaceAll("/", "/\0")+"/"+pli.getMenuName(),pli));			
		}
		Collections.sort(items);

		Stack<String> menustack = new Stack<String>();
		Stack<JMenu> menustack2 = new Stack<JMenu>();
		String prefix="";
		for(llitem lli:items) {

			// go up if needed
			String path = lli.getPath();
			while (!path.startsWith(prefix)) {
				prefix = menustack.pop();
				JMenu last = menu;
				menu = menustack2.pop();
				if (last.getMenuComponentCount()>0) 
					menu.add(last);

			}

			if (path.startsWith(prefix) || prefix=="") {
				String remainingpath=path.substring(prefix.length());

				if (remainingpath.contains("/")) { // start new subpath
					String[] submenus = remainingpath.split("/");
					for(int i=0; i<submenus.length-1; ++i) {
						String submenu = submenus[i];
						menustack.push(prefix);
						menustack2.push(menu);
						menu=new JMenu(submenu);
						prefix += submenu+"/";
					}					
				}

				// make submenus if the plugin supports it
				try {
					if (MenuMakingPlugin.class.isAssignableFrom(lli.pli.getPluginClass())) {
						for (JMenuItem jmi : ((MenuMakingPlugin)lli.pli.getInstance()).createMenu())
							menu.add(jmi);
					} else if (MenuPlugin.class.isAssignableFrom(lli.pli.getPluginClass())) {
						// add one level higher
						menustack2.peek().add(((MenuPlugin)lli.pli.getInstance()).getMenu());
					} else {
						menu.add(createPluginInfoMenuAction(lli.pli));
					}
				} catch (Throwable t) {
					System.err.println("Could not add menu item resp. submenu: "+lli.pli);
				}
			} 

		}

		while (prefix.length()>0) {
			prefix = menustack.pop();
			JMenu last = menu;
			menu = menustack2.pop();
			if (last.getMenuComponentCount()>0)
				menu.add(last);
		}

	}

	@SuppressWarnings("unchecked")
	private static class llitem implements Comparable {
		public String path;
		public PluginInfo pli;
		public llitem(String s, PluginInfo p) {
			path=s;
			pli=p;
		}
		public int compareTo(Object arg0) {
			return path.compareTo(((llitem)arg0).path);
		}
		public String getPath() {
			return path.replaceAll("\0", ""); // remove the zero characters that were added for sorting purposes
		}
	}

	protected void addToMenuFlattenedFillList(JMenu source, JMenu target) {
		for (Component jmi : source.getMenuComponents()) {
			// Expand submenus (flatten structure)
			if (jmi instanceof JMenu) {
				AbstractAction aa;
				JMenuItem sep;

				// give us some spacee
				if (target.getMenuComponentCount()>0) {
					aa = new TitleMenuComponentAction("");
					sep = new JMenuItem(aa);
					target.add(sep);
				}

				// add a menu title
				aa = new TitleMenuComponentAction(((JMenu)jmi).getText());
				sep = new JMenuItem(aa);
				target.add(sep);

				// add menu contents				
				addToMenuFlattenedFillList((JMenu)jmi, target);
			}
			else
				target.add(jmi);
		}	
	}

	protected void addToMenuFlattened(Collection<PluginInfo> plugins) {
		JMenu temp = new JMenu();
		addToMenu(temp, plugins);
		addToMenuFlattenedFillList(temp, this);
	}

	private class PluginInfoMenuActionRedirect extends PluginInfoMenuAction {

		public PluginInfoMenuActionRedirect(PluginInfo pli) {
			super(pli);
		}

		public void actionPerformed(ActionEvent e) {
			List<SelectableObjectType> selection = getSelection();
			callPlugin(p, selection==null?Collections.<SelectableObjectType>emptyList():selection);
		}

	}

	public PluginInfoMenuAction createPluginInfoMenuAction(PluginInfo pli)  {
		return new PluginInfoMenuActionRedirect(pli);
	}

	public void setSelectionChanged() {
		needUpdateSelection = true;
	}

	public void pluginAdded(PluginInfo pli) {
		for (String MC : MCs)
			if (pli.getMasterComponent().equals(MC))
				needUpdateContents=true;
	}

	public static class TitleMenuComponentAction extends AbstractAction {
		public TitleMenuComponentAction(String title) {
			super(title);
		}
		public void actionPerformed(ActionEvent e) {}
		public boolean isEnabled() {
			return false;
		}
	}


}


