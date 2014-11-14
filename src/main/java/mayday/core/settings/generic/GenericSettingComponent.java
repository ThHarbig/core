package mayday.core.settings.generic;

import java.awt.Component;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.events.SettingChangeEvent;

@SuppressWarnings("unchecked")
public class GenericSettingComponent<T extends MIType> extends AbstractSettingComponent<GenericSetting> {

	protected AbstractMIRenderer<T> miRenderer;

	public GenericSettingComponent(GenericSetting s) {
		super(s);
	}

	public void stateChanged(SettingChangeEvent e) {
		if (miRenderer!=null) {
			miRenderer.connectToMIO((T) mySetting.getValue(), (Object)null, (MIGroup)null);
		}
		
	}

	public String getCurrentValueFromGUI() {
		if (miRenderer!=null)
			return miRenderer.getEditorValue();
		else
			return null;
	}


	protected Component getSettingComponent() {
		if (miRenderer==null) {
			miRenderer = mySetting.getValue().getGUIElement();
			miRenderer.setEditable(true);
			miRenderer.connectToMIO((T) mySetting.getValue(), (Object)null, (MIGroup)null);
		}
		return miRenderer.getEditorComponent();
	}


}
