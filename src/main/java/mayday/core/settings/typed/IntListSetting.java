package mayday.core.settings.typed;

import java.util.LinkedList;
import java.util.List;

public class IntListSetting extends StringListSetting {

	public IntListSetting(String Name, String Description, List<Integer> Default) {
		super(Name, Description, null);
		if (Default!=null)
			setIntegerListValue(Default);
	}
	
	public List<Integer> getIntegerListValue() {
		return convertToInteger(getStringListValue());
	}
	
	protected List<Integer> convertToInteger(List<String> ls) {
		LinkedList<Integer> ret = new LinkedList<Integer>();
		for (String s : ls)
			try {
				ret.add(Integer.parseInt(s));
			} catch (NumberFormatException nfe) {
				//ignore this value
			}
		return ret;
	}
	
	protected List<String> convertFromInteger(List<Integer> ld) {
		LinkedList<String> ret = new LinkedList<String>();
		for (Integer s : ld)
			ret.add(s.toString());
		return ret;
	}
	
	public void setIntegerListValue(List<Integer> value) {	
		setStringListValue(convertFromInteger(value));
	}

	public IntListSetting clone() {
		return new IntListSetting(getName(),getDescription(),getIntegerListValue());
	}
	
}
