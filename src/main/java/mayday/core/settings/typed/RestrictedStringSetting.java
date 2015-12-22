package mayday.core.settings.typed;

import mayday.core.settings.generic.ObjectSelectionSetting;

/**
 * Presents a choice of strings in a drop-down Combobox 
 * @author fb
 *
 */
public class RestrictedStringSetting extends ObjectSelectionSetting<String> {

	public RestrictedStringSetting(String Name, String Description,
			int Default, String... predefined) {
		super(Name, Description, Default, predefined);
	}	
	
	protected RestrictedStringSetting(String Name, String Description,
			String Default, String[] predefined) {
		super(Name, Description, Default, predefined);
	}	
	
	public RestrictedStringSetting setLayoutStyle(LayoutStyle style) {
		super.setLayoutStyle(style);
		return this;
	}
	
	public RestrictedStringSetting clone() {
		return new RestrictedStringSetting(getName(),getDescription(),getStringValue(),predef);
	}

}
