package mayday.vis3.gui.menu;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import mayday.core.structures.maps.MultiHashMap;

@SuppressWarnings("serial")
public class MenuMapAdapter extends JMenu {
	
	MultiHashMap<Object, Component> menuItems = new MultiHashMap<Object, Component>();
	
	// all additions are routed to these two functions
	
    public JMenuItem add(JMenuItem menuItem, Object askingObject) {
    	menuItems.put(askingObject, menuItem);
    	return menuItem;
    }
    
    public void add(Component menuItem, Object askingObject) {
    	menuItems.put(askingObject, menuItem);
    }
    
    public MenuMapAdapter createCloneWithKnowledgeOf(final Object askingObject) {
		final MenuMapAdapter outer = this;
		
		return new MenuMapAdapter() {
		    public JMenuItem add(JMenuItem menuItem) {
		    	return outer.add(menuItem, askingObject);
		    }
		    
		    public Component add(Component c) {
		    	outer.add(c, askingObject);
		    	return c;
		    }
		};
	}
    
    public MultiHashMap<Object, Component> getMap() {
    	return menuItems;
    }
    
    // 	exception methods here, but they work if we take a clone with extra knowledge, see above
    
    public JMenuItem add(JMenuItem menuItem) {
    	throw new RuntimeException("ViewSettingsMenu can only be added to if the asking object identifies itself");
    }
    
    public Component add(Component c) {
    	throw new RuntimeException("ViewSettingsMenu can only be added to if the asking object identifies itself");	
    }
    
    public Component add(Component c, int index) {
    	// ignore index
    	return add(c);
    }
    
    // add(Action) directs to add(JMenuItem)
    // add(String) directs to add(JMenuItem)
    
    public void addSeparator() {
    	add(new JPopupMenu.Separator());
    }
    
    public void insert(String s, int pos) {
    	// ignore position
    	add(s);
    }
    
    public JMenuItem insert(JMenuItem mi, int pos) {
    	// ignore position
    	return add(mi);
    }
    
    public JMenuItem insert(Action a, int pos) {
    	// ingore position
    	return add(a);
    }
    
    public void insertSeparator(int index) {
    	// ignore position
    	addSeparator();
    }

}
