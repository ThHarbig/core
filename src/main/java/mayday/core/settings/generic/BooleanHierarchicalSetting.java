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
import mayday.core.settings.typed.BooleanSetting;

public class BooleanHierarchicalSetting extends BooleanSetting implements SettingChangeListener, SettingContainer {
	
	public enum LayoutStyle {
		PANEL_HORIZONTAL,
		PANEL_VERTICAL,
		PANEL_FOLDUP
	}
	
	protected HashMap<String, Setting> childrenMap = new HashMap<String, Setting>();
	protected ArrayList<Setting> children = new ArrayList<Setting>();
	protected LayoutStyle layoutStyle;
	protected boolean isTopMost;
	
	public BooleanHierarchicalSetting(String Name, String Description, boolean Default, LayoutStyle Layout, boolean TopMost) {
		super(Name,Description,Default);
		this.layoutStyle = Layout;
		this.isTopMost=TopMost;
	}	
	
	public BooleanHierarchicalSetting setLayoutStyle(LayoutStyle style) {
		layoutStyle=style;
		return this;
	}
	
	public BooleanHierarchicalSetting(String Name, String Description, boolean Default) {
		this(Name, Description, Default, LayoutStyle.PANEL_VERTICAL, false);
	}
	
	public BooleanHierarchicalSetting addSetting(Setting childSetting) {
		children.add(childSetting);
		childrenMap.put(childSetting.getName(), childSetting);
		childSetting.addChangeListener(this);
		return this;
	}

	public BooleanHierarchicalSetting setTopMost(boolean TopMost) {
		isTopMost = TopMost;
		return this;
	}
	
	
	public SettingComponent getGUIElement() {
		switch (layoutStyle) {
		case PANEL_HORIZONTAL:
			return new BooleanHierarchicalSettingComponent_Panel(this, false, isTopMost);
		case PANEL_VERTICAL:
			return new BooleanHierarchicalSettingComponent_Panel(this, true, isTopMost);
		case PANEL_FOLDUP:
			return new BooleanHierarchicalSettingComponent_Foldup(this);
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
	
	public Component getMenuItem( Window parent ) {
		JMenu subMenu = new JMenu(getName());
		JMenuItem mymi = (JMenuItem)super.getMenuItem(parent);
		mymi.setText("Activate");
		subMenu.add(mymi);
		for (Setting child : children) {
			Component cmi = child.getMenuItem( parent );
			if (cmi!=null)
				subMenu.add(cmi);			
		}			
		return subMenu;
	}

	public BooleanHierarchicalSetting clone() {
		BooleanHierarchicalSetting gs = new BooleanHierarchicalSetting(getName(), getDescription(), getBooleanValue(), layoutStyle, isTopMost);
		for (Setting childSetting : children) {
			gs.addSetting(childSetting.clone());
		}
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
			if (child instanceof BooleanHierarchicalSetting) {
				s = ((BooleanHierarchicalSetting)child).getChild(name, true);
				if (s!=null)
					return s;
			}
		}
		return null;
	}

	public String getValidityHint() {
		return "";
	}


	public void stateChanged(SettingChangeEvent e) {
		// pass on
		e.addSource(this);
		fireChanged(e);		
	}
	

}
