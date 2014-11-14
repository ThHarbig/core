package mayday.core.settings.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListModel;

import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.IntListSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

/** WARNING: Serialization of this class saves the indices of the selected objects, not the list of available objects itself! */
public class ExtendableObjectListSetting<T> extends IntListSetting {
	
	protected List<T> available;
	
	protected LinkedList<ExtendableObjectListSettingComponent> cachedSC = new LinkedList<ExtendableObjectListSettingComponent>();
	
	public ExtendableObjectListSetting(String Name, String Description, T[] choice) {
		this(Name, Description, Arrays.asList(choice));
	}

	public ExtendableObjectListSetting(String Name, String Description, List<T> choice) {
		super(Name, Description, null);
		available = choice;
	}
	
	public void addChoice(T choice, boolean addToList) {
		available.add(choice);
		if (addToList)
			for (ExtendableObjectListSettingComponent sc : cachedSC)
				sc.addElement(choice);			
	}
	
	public List<T> getSelection() {
		LinkedList<T> ret = new LinkedList<T>();
		for (int i : getIntegerListValue()) {
			ret.add(available.get(i));
		}
		return ret;
	}
	
	public void setSelection(List<T> sel) {
		LinkedList<Integer> ret = new LinkedList<Integer>();
		for (T t : sel)
			if (available.indexOf(t)>=0)
				ret.add(available.indexOf(t));
		setIntegerListValue(ret);				
	}
	
	public ExtendableObjectListSetting<T> clone() {
		ExtendableObjectListSetting<T> eols = new ExtendableObjectListSetting<T>(getName(),getDescription(),available);
		eols.fromPrefNode(toPrefNode());
		return eols;
	}
	
	
	public SettingComponent getGUIElement() {
		ExtendableObjectListSettingComponent eolsc = new ExtendableObjectListSettingComponent(this);
		cachedSC.add(eolsc);
		return eolsc;
	}
	
	protected class ExtendableObjectListSettingComponent extends AbstractMutableListSettingComponent<ExtendableObjectListSetting<T>, T> {

		public ExtendableObjectListSettingComponent(
				ExtendableObjectListSetting<T> s) {
			super(s);
		}

		@Override
		protected String elementToString(T element) {
			return ""+mySetting.available.indexOf(element);
		}

		@Override
		protected Iterable<T> elementsFromSetting(ExtendableObjectListSetting<T> mySetting) {
			return mySetting.getSelection();
		}

		@Override
		protected T getElementToAdd(Collection<T> alreadyPresent) {
			Map<String, T> av = new TreeMap<String, T>();
			for (T element : available)
				if (!alreadyPresent.contains(element))
					av.put(renderListElement(element), element);						 
			RestrictedStringSetting available = new RestrictedStringSetting("Select an element to add",null, 0, av.keySet().toArray(new String[0]));
			SettingDialog sd = new SettingDialog(null, "Select an element to add", available);
			sd.showAsInputDialog();
			if (!sd.canceled()) 
				return av.get(available.getStringValue());
			return null;
		}

		@Override
		protected String renderListElement(T element) {
			return element.toString();
		}
		
		public void addElement(T element) {
			((DefaultListModel)theList.getModel()).addElement(element);
		}

		@Override
		protected String renderToolTip(T element) {
			return null;
		}

	
		
	}
	
}

