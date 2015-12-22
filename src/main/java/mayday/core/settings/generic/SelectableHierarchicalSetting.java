package mayday.core.settings.generic;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import mayday.core.Preferences;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingContainer;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;

public class SelectableHierarchicalSetting extends ObjectSelectionSetting<Object> implements SettingChangeListener, SettingContainer {
	
	public enum LayoutStyle {
		PANEL_HORIZONTAL,
		PANEL_VERTICAL,
		COMBOBOX
	}
	
	protected HashMap<String, Setting> childrenMap = new HashMap<String, Setting>();
	protected ArrayList<Setting> children = new ArrayList<Setting>();
	protected boolean childrenAsSubmenus;
	protected boolean isTopMost;
	protected int defaultIndex;
	protected LayoutStyle layoutStyle;
	
	public SelectableHierarchicalSetting(String Name, String Description, int Default, Object[] predefined, LayoutStyle Layout, boolean TopMost) {
		super(Name,Description,Default,predefined);
		childrenAsSubmenus = true;
		this.layoutStyle = Layout;
		this.isTopMost = TopMost;
		this.defaultIndex=Default;
		setPredefined(predefined);
	}	
	
	public void setPredefined(Object[] predefined) {
		predef = predefined;
		for (Object o : predefined)
			if (o instanceof Setting)
				addSetting((Setting)o);
		if (predef.length>defaultIndex)
			setSelectedIndex(defaultIndex);
		else if (predef.length>0)
			setSelectedIndex(0);
	}
	
	public SelectableHierarchicalSetting setLayoutStyle(LayoutStyle style) {
		layoutStyle=style;
		return this;
	}
	
	public SelectableHierarchicalSetting(String Name, String Description, int Default, Object[] predefined) {
		this(Name, Description, Default, predefined, LayoutStyle.PANEL_VERTICAL, false);
	}
	
	protected SelectableHierarchicalSetting addSetting(Setting childSetting) {
		children.add(childSetting);
		childrenMap.put(childSetting.getName(), childSetting);
		childSetting.addChangeListener(this);
		return this;
	}

	public SettingComponent getGUIElement() {
		switch (layoutStyle) {
		case PANEL_HORIZONTAL:
			return new SelectableHierarchicalSettingComponent_Panel(this, false, isTopMost);
		case PANEL_VERTICAL:
			return new SelectableHierarchicalSettingComponent_Panel(this, true, isTopMost);
		case COMBOBOX:
			return new SelectableHierarchicalSettingComponent_Combobox(this, isTopMost);
		}
		return null;		
	}

	public boolean fromPrefNode(Preferences prefNode) {
		super.fromPrefNode(prefNode);
		for (Setting childSetting : children) {
			if (prefNode.getChild(childSetting.getName())!=null) {
				Preferences subPref = prefNode.node(childSetting.getName());
				childSetting.fromPrefNode(subPref);
			}
		}
		return true;
	}

	public Preferences toPrefNode() {		
		Preferences myNode = super.toPrefNode();
		for (Setting childSetting : children) {
			myNode.connectSubtree(childSetting.toPrefNode());
		}
		return myNode;
	}
	

	public SelectableHierarchicalSetting clone() {
		SelectableHierarchicalSetting gs = new SelectableHierarchicalSetting(getName(), getDescription(), defaultIndex, predef, layoutStyle, isTopMost);
		for (Setting childSetting : children) {
			gs.addSetting(childSetting.clone());
		}
		gs.childrenAsSubmenus = this.childrenAsSubmenus;
		gs.fromPrefNode(this.toPrefNode());
		return gs;
	}
 
	public Setting getChild(String name) {
		return childrenMap.get(name);
	}
	
	public List<Setting> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	/** Returns the FIRST occurrence of a given setting name */
	public Setting getChild(String name, boolean recursive) {
		Setting s = getChild(name);
		if (s!=null || !recursive)
			return s;
		for (Setting child : children) {
			if (child instanceof SelectableHierarchicalSetting) {
				s = ((SelectableHierarchicalSetting)child).getChild(name, true);
				if (s!=null)
					return s;
			}
		}
		return null;
	}

	public String getValidityHint() {
		return "";
	}

//	public Component getMenuItem( Window parent ) {
//		if (childrenAsSubmenus) {
//			for (Object o : predef) {
//				final Object weg = o;
//				Component c;
//				if (o instanceof Setting) {
//					c = ((Setting)o).getMenuItem(parent);											
//				} else {
//					c = new JButton(o.toString());				
//				}
//				if (c instanceof JMenu) {
//					c.addMouseListener(new MouseAdapter() {
//						public void mouseClicked(MouseEvent me) {
//							setStringValue(weg.toString());
//						}
//					});	
//				} else if (c instanceof AbstractButton){
////					((AbstractButton)c).updateUI(UIManager.getUI(target)"CheckBoxMenuItemUI");
//					((AbstractButton)c).addActionListener(new ActionListener() {
//						public void actionPerformed(ActionEvent e) {
//							setStringValue(weg.toString());							
//						}						
//					});
//				}
//	
//				mnu.add(c);
//			}
//			mnu.add(targetPanel);
//			return mnu;
//		} else {
//			return new SettingDialogMenuItem(this,parent);
//		}
//	}
	
//	public Component getMenuItem( Window parent ) {
//		JMenu subMenu = new JMenu(getName());
//		if (childrenAsSubmenus) {
//			
//		} else {
//			subMenu.add(DetachableSettingPanel(this,parent));
//		}
//		return subMenu;
//	}
	
	public Component getMenuItem( Window parent ) {
		JMenu subMenu = (JMenu)super.getMenuItem(parent);
		if (childrenAsSubmenus) {			
			for (Setting child : children) {
				Component cmi = child.getMenuItem( parent );
				if (cmi!=null) {
					if (cmi instanceof JMenuItem) {
						((JMenuItem)cmi).setText("Configure "+((JMenuItem)cmi).getText());
					}
					subMenu.add(cmi);
				}
			}			
			
		} 
		return subMenu;
	}


	public void stateChanged(SettingChangeEvent e) {
		// pass on
		e.addSource(this);
		fireChanged(e);		
	}
	
	public SelectableHierarchicalSetting setChildrenAsSubmenus( boolean casm ) {
		childrenAsSubmenus = casm;
		return this;
	}
	
	public boolean isChildrenAsSubmenus() {
		return childrenAsSubmenus;
	}
	
	public SelectableHierarchicalSetting setTopMost(boolean TopMost) {
		isTopMost = TopMost;
		return this;
	}
	
}
