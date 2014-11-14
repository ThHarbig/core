package mayday.core.settings.typed;

import mayday.core.meta.types.StringMIO;
import mayday.core.settings.generic.GenericSetting;

public class StringSetting extends GenericSetting {

	protected boolean allowEmpty = true;
	
	public StringSetting(String Name, String Description, String Default) {
		super(Name, StringMIO.class, Description);
		setStringValue(Default);
	}
	
	public StringSetting(String Name, String Description, String Default, boolean AllowEmpty) {
		this(Name, Description, Default);
		allowEmpty = AllowEmpty;
	}
	
	public String getStringValue() {
		return getValueString();
	}
	
	public String getValidityHint() {
		return getName()+" must not be empty";
	}
	
	public boolean isValidValue(String value) {
		return (super.isValidValue(value) && (allowEmpty || value.trim().length()>0));
	}

	public void setStringValue(String nv) {
		setValueString(nv);
	}
	
	public StringSetting clone() {
		return new StringSetting(getName(),getDescription(),getStringValue(),allowEmpty);
	}


}
