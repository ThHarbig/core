package mayday.core.settings.generic;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/** WARNING: Serialization of this class saves the toString() of the selected object, not the list of available objects itself! */ 
public class ExtendableObjectSelectionSetting<T> extends ObjectSelectionSetting<T> {
	
	public ExtendableObjectSelectionSetting(String Name, String Description,
			int Default, T[] predefined) {
		super(Name, Description, Default, predefined);
	}
	
	protected ExtendableObjectSelectionSetting(String Name, String Description,
			T Default, T[] predefined) {
		super(Name, Description, Default, predefined);
	}
	
	@SuppressWarnings("unchecked")
	public ExtendableObjectSelectionSetting(String Name, String Description,
			int Default, Collection<T> predefined) {
		super(Name, Description, Default, (T[])predefined.toArray());
	}
	
	public void updatePredefined(T[] newPredefined) {
		// needs to fire a change event to make sure all gui components contain the new predefined set
		// but only if it actually changed
		// I'm not using Arrays.equals because I only want to compare objects for identity and not
		// check them for equality, which might be extremely expensive depending on type T
		if (predef==newPredefined)
			return;
		//check for equal content if size identical
		if (predef.length==newPredefined.length) {
			boolean identical = true;
	        for (int i=0; identical && i<predef.length; i++) {
	            Object o1 = predef[i];
	            Object o2 = newPredefined[i];
	            identical &= (o1==o2);
	        }
			if (identical)
				return;
		}

		predef = newPredefined;
		fireChanged();
	}

	@SuppressWarnings("unchecked")
	public void updatePredefined(Collection<T> newPredefined) {
		updatePredefined((T[])newPredefined.toArray());
	}
	
	public Collection<T> getPredefined() {
		return Arrays.asList(predef);
	}
	
	public void addPredefined(T newP) {
		Collection<T> p = new LinkedList<T>(getPredefined());
		p.add(newP);
		updatePredefined(p);
	}

	public ExtendableObjectSelectionSetting<T> clone() {
		return new ExtendableObjectSelectionSetting<T>(getName(),getDescription(),getObjectValue(),predef);
	}
	
}

