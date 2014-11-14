package mayday.core.settings.typed;

import mayday.core.meta.types.IntegerMIO;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.GenericSetting;

public class IntSetting extends GenericSetting {

	protected int min=Integer.MIN_VALUE, max=Integer.MAX_VALUE;
	protected boolean includeMin=true, includeMax=true;
	protected LayoutStyle layoutStyle = LayoutStyle.DEFAULT;
	
	public enum LayoutStyle {
		DEFAULT,
		SLIDER
	}
	
	public IntSetting(String Name, String Description, int Default) {
		super(Name, IntegerMIO.class, Description);
		setIntValue(Default);
	}
	
	public IntSetting(String Name, String Description, int Default, Integer Min, Integer Max, boolean IncludeMin, boolean IncludeMax) {
		this(Name, Description, Default);
		setRange(Min,Max,IncludeMin,IncludeMax);
	}
	
	public SettingComponent getGUIElement() {
		if (layoutStyle==LayoutStyle.SLIDER)
			return new IntSettingComponent_Slider(this);		
		return super.getGUIElement();
	}

	public IntSetting setLayoutStyle(LayoutStyle style) {
		layoutStyle = style;
		return this;
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
		return (super.isValidValue(value) && withinRange(Integer.parseInt(value)));
	}
	
	protected boolean withinRange(int value) {
		return ((includeMin && value>=min) || (!includeMin && value>min)) && ((includeMax && value<=max) || (!includeMax && value<max));
	}
	
	public int getIntValue() {
		return ((IntegerMIO)getValue()).getValue();
	}
	
	public void setIntValue(Integer nv) {
		setValueString(nv.toString());
	}
	
	public IntSetting clone() {
		return new IntSetting(getName(),getDescription(),getIntValue(),min,max,includeMin,includeMax);
	}

	public void setRange(Integer Min, Integer Max, boolean IncludeMin, boolean IncludeMax) {
		min=(Min!=null)?Min:Integer.MIN_VALUE;
		max=(Max!=null)?Max:Integer.MAX_VALUE;
		includeMin=IncludeMin;
		includeMax=IncludeMax;
	}
	
}
