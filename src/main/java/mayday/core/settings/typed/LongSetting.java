package mayday.core.settings.typed;

import mayday.core.meta.types.StringMIO;
import mayday.core.settings.generic.GenericSetting;

public class LongSetting extends GenericSetting {

	protected long min=Long.MIN_VALUE, max=Long.MAX_VALUE;
	protected boolean includeMin=true, includeMax=true;
	
	public LongSetting(String Name, String Description, long Default) {
		super(Name, StringMIO.class, Description);
		setLongValue(Default);
	}
	
	public LongSetting(String Name, String Description, long Default, Long Min, Long Max, boolean IncludeMin, boolean IncludeMax) {
		this(Name, Description, Default);
		setRange(Min,Max,IncludeMin,IncludeMax);
	}
	
	
	public String getValidityHint() {
		return getName()+" must be in the range "
		+ (includeMin?"[":"]")
		+ min
		+","
		+ max
		+ (includeMax?"]":"[");
	}
	
	public boolean isValidValue(String value) {
		return (super.isValidValue(value) && withinRange(Long.parseLong(value)));
	}
	
	protected boolean withinRange(long value) {
		return ((includeMin && value>=min) || (!includeMin && value>min)) && ((includeMax && value<=max) || (!includeMax && value<max));
	}
	
	public long getLongValue() {
		return Long.parseLong(getValueString());
	}
	
	public void setLongValue(Long nv) {
		setValueString(nv.toString());
	}

	public LongSetting clone() {
		return new LongSetting(getName(),getDescription(),getLongValue(),min,max,includeMin, includeMax);
	}

	
	public void setRange(Long Min, Long Max, boolean IncludeMin, boolean IncludeMax) {
		min=(Min!=null)?Min:Long.MIN_VALUE;
		max=(Max!=null)?Max:Long.MAX_VALUE;
		includeMin=IncludeMin;
		includeMax=IncludeMax;
	}
	
}
