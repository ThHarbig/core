package mayday.core.settings.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mayday.core.Preferences;
import mayday.core.settings.AbstractSetting;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingContainer;
import mayday.core.settings.TopMostSettable;

public class SortedExtendableConfigurableObjectListSetting<T> 
	extends AbstractSetting 
	implements SettingContainer, TopMostSettable<SortedExtendableConfigurableObjectListSetting<T>> {

	protected ElementBridge<T> bridge;
	protected ArrayList<T> elements = new ArrayList<T>();
	protected boolean topMost;
	
	public SortedExtendableConfigurableObjectListSetting(String Name, String Description, ElementBridge<T> bridge) {
		super(Name, Description);
		this.bridge = bridge;
	}

	public SortedExtendableConfigurableObjectListSetting<T> setTopMost(boolean tm) {
		topMost = tm;
		return this;
	}


	
	@Override
	public SettingComponent getGUIElement() {
		return new SortedExtendableConfigurableObjectListSettingComponent<T>(this, topMost);
	}
	
	public boolean isValidValue(String value) {
		return false; // no settings contained here		
	}
	
	public String getValueString() {
		return "";
	}

	public void setValueString(String newValue) {
	}

	public boolean fromPrefNode(Preferences prefNode) {
		// first read the element type nodes
		clear0();
		Preferences elementTypes = prefNode.node("elements");
		for (int i=0; i!=elementTypes.childCount(); ++i) {
			Preferences childElement = elementTypes.node(""+i);
			// initialize the elements
			elements.add( bridge.createElementFromIdentifier(childElement.Value) );
		}
		Preferences initNode = prefNode.node("config");
		for (int i=0; i!=elements.size(); ++i) {
			Setting s = bridge.getSettingForElement(elements.get(i));
			if (s!=null) {
				Preferences childInit = initNode.node(""+i);
				s.fromPrefNode(childInit.node(s.getName()));
			}
		}
		// fill the elements' settings
		return true;
	}
	
	public SortedExtendableConfigurableObjectListSetting<T> clone() {
		SortedExtendableConfigurableObjectListSetting<T> cl = new SortedExtendableConfigurableObjectListSetting<T>(this.name, this.description, this.bridge);
		cl.fromPrefNode(this.toPrefNode());
		return cl;
	}



	public Preferences toPrefNode() {
		Preferences myNode = Preferences.createUnconnectedPrefTree(getName(), getDescription());
		// write the element types
		Preferences elementTypes = myNode.node("elements");
		for (int i=0; i!=elements.size(); ++i) {
			Preferences childElement =elementTypes.node(""+i);
			childElement.Value = bridge.createIdentifierFromElement(elements.get(i));
		}
		Preferences initNode = myNode.node("config");
		// write the elements' settings
		for (int i=0; i!=elements.size(); ++i) {
			Setting s = bridge.getSettingForElement(elements.get(i));
			if (s!=null) {
				Preferences childInit = initNode.node(""+i);
				childInit.connectSubtree(s.toPrefNode());
			}
		}
		return myNode;
	}
	
	public ElementBridge<T> getBridge() {
		return bridge;
	}
	
	public List<T> getElements() {
		return Collections.unmodifiableList(elements);
	}
	
	public void addElement(T el) {
		elements.add(el);
		fireChanged();
	}
	
	public void setElements(Collection<T> el) {
		boolean same = (el.size()==elements.size());
			
		if (same) {
			int i=0;
			for (T e : el) {
				if (e!=elements.get(i)) {
					same = false;
					break;
				}
				++i;
			}
		}
		
		if (!same) {
			clear0();	
			elements.addAll(el);
			fireChanged();
		}
	}	
	
	protected void clear0() {
		for (T el : elements)
			bridge.disposeElement(el);
		elements.clear();
	}

	public static interface ElementBridge<T> {
		/* create an element instance from its identifier string */
		T createElementFromIdentifier(String identifier);
		/* get the identifier that can be used to create an instance of the element */
		String createIdentifierFromElement(T element);
		/* get a setting object for the element instance */
		Setting getSettingForElement(T element);
		/* produce a list of element instances that can be added to the list */ 
		Collection<T> availableElementsForAddition(Collection<T> alreadyInList);
		/* dispose of an unneeded element, i.e. removing listeners that were added in availableElementsForAddition() */
		void disposeElement(T element);
		/* get a name for an element for displaying in the list */
		String getDisplayName(T element);
		/* get a tooltip description for an element */
		String getTooltip(T element);
		
	}

}
