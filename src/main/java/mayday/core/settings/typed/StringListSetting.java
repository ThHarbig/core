package mayday.core.settings.typed;

import java.awt.Component;
import java.util.List;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.GenericSetting;
import mayday.core.settings.generic.GenericSettingComponent;

public class StringListSetting extends GenericSetting {

	public StringListSetting(String Name, String Description, List<String> Default) {
		super(Name, StringListMIO.class, Description);
		if (Default!=null)
			setStringListValue(Default);
	}
	
	public List<String> getStringListValue() {
		return ((StringListMIO)getValue()).getValue();
	}
	
	public void setStringListValue(List<String> value) {
		StringListMIO smm = new StringListMIO(value);
		setValueString(smm.serialize(MIType.SERIAL_TEXT));		
	}
	
	@SuppressWarnings("unchecked")
	public SettingComponent getGUIElement() {
		return new GenericSettingComponent<StringListMIO>(this) {
			protected Component getSettingComponent() {
				if (miRenderer==null) {
					miRenderer = mySetting.getValue().getGUIElement();
//					((StringMapMIORenderer)miRenderer).setLabel(mySetting.getName());
					miRenderer.setEditable(true);
					miRenderer.connectToMIO((StringListMIO) mySetting.getValue(), (Object)null, (MIGroup)null);
				}
				return miRenderer.getEditorComponent();
			}
			protected boolean needsLabel() {
				return false;
			}
		};
		
	}
	
	public StringListSetting clone() {
		return new StringListSetting(getName(),getDescription(),getStringListValue());
	}
	
}
