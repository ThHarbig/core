package mayday.core.settings.typed;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;

public class MultilineStringSetting extends StringSetting {

	public MultilineStringSetting(String Name, String Description, String Default) {
		super(Name, Description, Default);
	}
	
	public MultilineStringSetting(String Name, String Description, String Default, boolean AllowEmpty) {
		super(Name, Description, Default, AllowEmpty);
	}
	
	public SettingComponent getGUIElement() {
		return new MultilineStringSettingComponent(this);
	}

	public MultilineStringSetting clone() {
		return new MultilineStringSetting(getName(),getDescription(),getStringValue(),allowEmpty);
	}
	
	public static class MultilineStringSettingComponent extends AbstractSettingComponent<MultilineStringSetting> {

		protected JTextArea field;
		protected JScrollPane jsp;
		
		public MultilineStringSettingComponent(MultilineStringSetting s) {
			super(s);
		}

		protected String getCurrentValueFromGUI() {
			if (field==null)
				return null;
			return field.getText();
		}

		protected Component getSettingComponent() {
			if (field==null) {
				field = new JTextArea(mySetting.getStringValue());
				field.setLineWrap(true);
				field.setFont(UIManager.getFont(this)); // TextArea font should fit other GUI fonts
				field.setWrapStyleWord(true);
				field.setRows(5);
				jsp = new JScrollPane(field);
			}
			return jsp;
		}

		public void stateChanged(SettingChangeEvent e) {
			field.setText(mySetting.getStringValue());
		}
		
	}
	
}
