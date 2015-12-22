package mayday.core.settings.typed;

import mayday.core.meta.types.DoubleMIO;
import mayday.core.settings.generic.GenericSetting;

public class DoubleSetting extends GenericSetting {

	protected double min=Double.NEGATIVE_INFINITY, max=Double.POSITIVE_INFINITY;
	protected boolean includeMin=true, includeMax=true;
	
	public DoubleSetting(String Name, String Description, double Default) {
		super(Name, DoubleMIO.class, Description);
		setDoubleValue(Default);
	}
	
	public DoubleSetting(String Name, String Description, double Default, Double Min, Double Max, boolean IncludeMin, boolean IncludeMax) {
		this(Name, Description, Default);
		setRange(Min, Max, IncludeMin, IncludeMax);
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
		return (super.isValidValue(value) && withinRange(Double.parseDouble(value)));
	}
	
	protected boolean withinRange(double value) {
		return ((includeMin && value>=min) || (!includeMin && value>min)) && ((includeMax && value<=max) || (!includeMax && value<max));
	}
	
	public double getDoubleValue() {
		return ((DoubleMIO)getValue()).getValue();
	}
	
	public void setDoubleValue(Double nv) {
		setValueString(nv.toString());
	}
	
	public DoubleSetting clone() {
		return new DoubleSetting(getName(),getDescription(),getDoubleValue(),min,max,includeMin, includeMax);
	}
	
	public void setRange(Double Min, Double Max, boolean IncludeMin, boolean IncludeMax) {
		min=(Min!=null)?Min:Double.NEGATIVE_INFINITY;
		max=(Max!=null)?Max:Double.POSITIVE_INFINITY;
		includeMin=IncludeMin;
		includeMax=IncludeMax;
	}

}
