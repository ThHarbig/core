package mayday.core.settings.generic;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;

import mayday.core.Preferences;
import mayday.core.settings.AbstractSetting;
import mayday.core.settings.DetachableSettingPanel;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingContainer;
import mayday.core.settings.TopMostSettable;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;

public class HierarchicalSetting extends AbstractSetting implements SettingChangeListener, SettingContainer, TopMostSettable<HierarchicalSetting> {
	
	public enum LayoutStyle {
		PANEL_HORIZONTAL,
		PANEL_VERTICAL,
		TABBED,
		TREE
	}
	
	protected HashMap<String, Setting> childrenMap = new HashMap<String, Setting>();
	protected ArrayList<Setting> children = new ArrayList<Setting>();
	protected boolean childrenAsSubmenus;
	protected boolean isTopMost;
	protected LayoutStyle layoutStyle;
	protected boolean combineNonhierarchicalChildren;
	
	protected HierarchicalSetting combinedChildren;
	protected ArrayList<Setting> nonCombinedChildren;
	
	
	public HierarchicalSetting(String Name, LayoutStyle Layout, boolean TopMost) {
		super(Name,"");
		childrenAsSubmenus = true;
		this.layoutStyle = Layout;
		this.isTopMost = TopMost;
		this.combineNonhierarchicalChildren = false;
	}	
	
	public HierarchicalSetting(String Name) {
		this(Name, LayoutStyle.PANEL_VERTICAL, false);
	}
	
	public HierarchicalSetting addSetting(Setting childSetting) {
		children.add(childSetting);
		childrenMap.put(childSetting.getName(), childSetting);
		childSetting.addChangeListener(this);
		return this;
	}

	public boolean isValidValue(String value) {
		return false; // no settings contained here		
	}
	
	public HierarchicalSetting setLayoutStyle(LayoutStyle style) {
		layoutStyle = style;
		return this;
	}

	protected void applySettingsCombination() {
		boolean isCombined = nonCombinedChildren!=null;
		
		boolean hasSimpleChildren = false;
		boolean hasComplexChildren = false;
		if (combineNonhierarchicalChildren) {
			for (Setting s : children) {
				hasComplexChildren |= s instanceof SettingContainer;
				hasSimpleChildren |= !(s instanceof SettingContainer);
			}
		}
		boolean doCombine = (hasSimpleChildren&&hasComplexChildren) && combineNonhierarchicalChildren;  
		
		if (isCombined && !doCombine) {
			nonCombinedChildren = null;
			combinedChildren = null;
		}
		if (!isCombined && doCombine) {
			nonCombinedChildren = new ArrayList<Setting>();
			combinedChildren = new HierarchicalSetting("Miscellaneous");
			nonCombinedChildren.add(combinedChildren);
			for (Setting s : children) {
				if (s instanceof SettingContainer)
					nonCombinedChildren.add(s);
				else
					combinedChildren.addSetting(s);
			}
		}
	}
	
	public SettingComponent getGUIElement() {
		// combine children if so desired
		applySettingsCombination();		
		switch (layoutStyle) {
		case PANEL_HORIZONTAL:
			return new HierarchicalSettingComponent_Panel(this, false, isTopMost);
		case PANEL_VERTICAL:
			return new HierarchicalSettingComponent_Panel(this, true, isTopMost);
		case TABBED:
			return new HierarchicalSettingComponent_Tabbed(this, isTopMost);
		case TREE:
			return new HierarchicalSettingComponent_Tree(this, isTopMost);
		}
		return null;		
	}

	public String getValueString() {
		return "";
	}

	public void setValueString(String newValue) {
	}

	public boolean fromPrefNode(Preferences prefNode) {
		for (Setting childSetting : children) {
			if (prefNode.getChild(childSetting.getName())!=null) {
				Preferences subPref = prefNode.node(childSetting.getName());
				childSetting.fromPrefNode(subPref);
			}
		}
		return true;
	}

	public Preferences toPrefNode() {
		Preferences myNode = Preferences.createUnconnectedPrefTree(getName(), getDescription());
		for (Setting childSetting : children) {
			myNode.connectSubtree(childSetting.toPrefNode());
		}
		return myNode;
	}
	

	public HierarchicalSetting clone() {
		HierarchicalSetting clonedSetting;
		clonedSetting = new HierarchicalSetting(getName());
		
		clonedSetting.childrenAsSubmenus=childrenAsSubmenus;
		clonedSetting.isTopMost=isTopMost;
		clonedSetting.layoutStyle=layoutStyle;
		clonedSetting.combineNonhierarchicalChildren=combineNonhierarchicalChildren;
		
		if (children!=null) {
			for (Setting childSetting : children) {
				clonedSetting.addSetting(childSetting.clone());
			}
		}
		
		return clonedSetting;
	}

	public Setting getChild(String name) {
		return childrenMap.get(name);
	}
	
	public List<Setting> getChildren() {
		if (nonCombinedChildren!=null)
			return Collections.unmodifiableList(nonCombinedChildren);
		else
			return Collections.unmodifiableList(children);
	}
	
	/** Returns the FIRST occurrence of a given setting name */
	public Setting getChild(String name, boolean recursive) {
		Setting s = getChild(name);
		if (s!=null || !recursive)
			return s;
		for (Setting child : children) {
			if (child instanceof HierarchicalSetting) {
				s = ((HierarchicalSetting)child).getChild(name, true);
				if (s!=null)
					return s;
			}
		}
		return null;
	}

	public String getValidityHint() {
		return "";
	}

	public Component getMenuItem( Window parent ) {
		JMenu subMenu = new JMenu(getName());
		if (childrenAsSubmenus) {			
			for (Setting child : children) {
				Component cmi = child.getMenuItem( parent );
				if (cmi!=null)
					subMenu.add(cmi);
			}			
			
	} else {
			subMenu.add(new DetachableSettingPanel(this,parent));
		}
		return subMenu;
	}

	public void stateChanged(SettingChangeEvent e) {
		// pass on child events
		e.addSource(this);
		fireChanged(e);
	}
	
	/**
	 * If true, child objects are represented by submenus, if false they are rendered as if in a dialog 
	 * @param casm whether to render children as submenus in menus
	 * @return the object itself for further changes
	 */
	public HierarchicalSetting setChildrenAsSubmenus( boolean casm ) {
		childrenAsSubmenus = casm;
		return this;
	}
	
	public boolean isChildrenAsSubmenus() {
		return childrenAsSubmenus;
	}
	
	public HierarchicalSetting setTopMost(boolean TopMost) {
		isTopMost = TopMost;
		return this;
	}
	
	/** If true, "simple" child settings are grouped together in one subelement, IF there are also "complex" children.
	 * If false, or there are _only_ "simple" children, no grouping is done. This only affects dialogs, not menus.
	 * @param cnc whether to group simple children together in the presence of complex ones
	 * @return the object itself for further changes
	 */
	public HierarchicalSetting setCombineNonhierarchicalChildren(boolean cnc) {
		combineNonhierarchicalChildren = cnc;
		return this;
	}
	
	/** override this method tro add special validation code. It is called from all HierarchicalSettingComponent classes 
	 * on updateSettingsFromEditor(boolean)
	 * @param editors - a mapping of settings objects and (possible modified) editors for them
	 * @param failSilently - if false, wrong input will raise an explanatory message box 
	 * @return true if all updates could be performed 
	 * */
	public boolean updateChildrenFromEditors( Map<Setting, SettingComponent> editors, boolean failSilently ) {
		// default behaviour is to try updating everything
		for ( SettingComponent sc : editors.values() )
			if (!sc.updateSettingFromEditor(failSilently))
				return false;		
		return true;
	}
	
}
