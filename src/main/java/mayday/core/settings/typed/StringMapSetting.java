package mayday.core.settings.typed;

import java.awt.Component;
import java.util.Map;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMapMIO;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.GenericSetting;
import mayday.core.settings.generic.GenericSettingComponent;

public class StringMapSetting extends GenericSetting {

	public StringMapSetting(String Name, String Description, Map<String,String> Default) {
		super(Name, StringMapMIO.class, Description);
		if (Default!=null)
			setStringMapValue(Default);
	}
	
	public Map<String,String> getStringMapValue() {
		return ((StringMapMIO)getValue()).getValue();
	}
	
	public void setStringMapValue(Map<String,String> value) {
		StringMapMIO smm = new StringMapMIO(value);
		setValueString(smm.serialize(MIType.SERIAL_TEXT));		
	}
	
	@SuppressWarnings("unchecked")
	public SettingComponent getGUIElement() {
		return new GenericSettingComponent<StringMapMIO>(this) {
			protected Component getSettingComponent() {
				if (miRenderer==null) {
					miRenderer = mySetting.getValue().getGUIElement();
//					((StringMapMIORenderer)miRenderer).setLabel(mySetting.getName());
					miRenderer.setEditable(true);
					miRenderer.connectToMIO((StringMapMIO) mySetting.getValue(), (Object)null, (MIGroup)null);
				}
				return miRenderer.getEditorComponent();
			}
			protected boolean needsLabel() {
				return false;
			}
		};
		
	}
	
	public StringMapSetting clone() {
		return new StringMapSetting(getName(),getDescription(),getStringMapValue());
	}
	
}
