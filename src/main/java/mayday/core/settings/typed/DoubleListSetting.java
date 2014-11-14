package mayday.core.settings.typed;

import java.util.LinkedList;
import java.util.List;

public class DoubleListSetting extends StringListSetting {

	public DoubleListSetting(String Name, String Description, List<Double> Default) {
		super(Name, Description, null);
		if (Default!=null)
			setDoubleListValue(Default);
	}
	
	public List<Double> getDoubleListValue() {
		return convertToDouble(getStringListValue());
	}
	
	protected List<Double> convertToDouble(List<String> ls) {
		LinkedList<Double> ret = new LinkedList<Double>();
		for (String s : ls)
			try {
				ret.add(Double.parseDouble(s));
			} catch (NumberFormatException nfe) {
				//ignore this value
			}
		return ret;
	}
	
	protected List<String> convertFromDouble(List<Double> ld) {
		LinkedList<String> ret = new LinkedList<String>();
		for (Double s : ld)
			ret.add(s.toString());
		return ret;
	}
	
	public void setDoubleListValue(List<Double> value) {	
		setStringListValue(convertFromDouble(value));
	}
	
	public DoubleListSetting clone() {
		return new DoubleListSetting(getName(),getDescription(),getDoubleListValue());
	}
	
}
