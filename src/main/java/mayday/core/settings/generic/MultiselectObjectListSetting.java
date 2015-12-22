package mayday.core.settings.generic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import mayday.core.settings.SettingComponent;
import mayday.core.settings.typed.IntListSetting;
/**
 * Presents a choice of strings where several can be selected 
 * Note that the selection is not stored explicitly, but is 
 * kept as a list of integers indicating the position of the selected entities in the predefined list. 
 * @author fb
 *
 */
public class MultiselectObjectListSetting<T> extends IntListSetting {

	protected List<T> predef;
	
	public MultiselectObjectListSetting(String Name, String Description, Collection<T> predefined) {
		super(Name, Description, null);
		predef = new LinkedList<T>(predefined);
	}	

	public MultiselectObjectListSetting<T> clone() {
		MultiselectObjectListSetting<T> c = new MultiselectObjectListSetting<T>(getName(), getDescription(), predef);
		c.fromPrefNode(this.toPrefNode());
		return c;
	}

	public SettingComponent getGUIElement() {
		return new MultiselectObjectListSettingComponent(this);
	}
	
	public List<T> getSelection() {
		LinkedList<T> ret = new LinkedList<T>();
		for (int i : getIntegerListValue()) {
			ret.add(predef.get(i));
		}
		return ret;
	}
	
	public void setSelection(Collection<T> sel) {		
		LinkedList<Integer> ret = new LinkedList<Integer>();
		for (T t : sel) {
			ret.add(predef.indexOf(t));
		}
		setIntegerListValue(ret);
	}
	
	protected class MultiselectObjectListSettingComponent extends AbstractMultiselectListSettingComponent<MultiselectObjectListSetting<T>, T> {

		public MultiselectObjectListSettingComponent(
				MultiselectObjectListSetting<T> s) {
			super(s);
		}

		@Override
		protected Iterable<T> elementsFromSetting(MultiselectObjectListSetting<T> mySetting) {
			return mySetting.predef;
		}

		@Override
		protected String renderListElement(T element) {
			return element.toString();
		}

		@Override
		protected Iterable<Integer> selectedElementsFromSetting(
				MultiselectObjectListSetting<T> mySetting) {
			return mySetting.getIntegerListValue();
		}

		@Override
		protected String renderToolTip(T element) {
			return null;
		}
		
	}
}
