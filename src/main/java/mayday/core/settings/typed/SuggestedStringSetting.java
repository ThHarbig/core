package mayday.core.settings.typed;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComboBox;

import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;


public class SuggestedStringSetting extends StringSetting {

	protected String[] predef;
	
	public SuggestedStringSetting(String Name, String Description, int Default, String[] predefined) {
		super(Name, Description, predefined[Default].toString());
		predef = predefined;
	}
	
	protected SuggestedStringSetting(String Name, String Description, String Default, String[] predefined) {
		super(Name, Description, Default);
		predef = predefined;
	}
	
	public SettingComponent getGUIElement() {
		return new SuggestedStringSettingComponent(this);
	}
	
	public String[] getPredefinedValues() {
		return predef;
	}
	
	public SuggestedStringSetting clone() {
		return new SuggestedStringSetting(getName(), getDescription(), getStringValue(), predef); 
	}
	
	
	public class SuggestedStringSettingComponent extends AbstractSettingComponent<SuggestedStringSetting> {
		
		protected JComboBox cb;

		public SuggestedStringSettingComponent(SuggestedStringSetting setting) {
			super(setting);
		}

		public void stateChanged(SettingChangeEvent e) {
			setSelected();
		}
		
		public void setSelected() {
			String newVal = mySetting.getStringValue();
			int i;
			for (i=0; i!=cb.getItemCount(); ++i) {
				if (cb.getItemAt(i).toString().equals(newVal)) {
					cb.setSelectedIndex(i);
					break;
				}
			}
			if (i==cb.getItemCount()) {
				cb.addItem(newVal);
				cb.setSelectedItem(newVal);				
			}
				
		}

		protected String getCurrentValueFromGUI() {
			if (cb==null)
				return null;
			return cb.getSelectedItem().toString();
		}

		@Override
		protected Component getSettingComponent() {
			if (cb==null) {
				cb = new JComboBox(mySetting.getPredefinedValues());
				cb.setEditable(true);
				cb.setMaximumSize(new Dimension(Integer.MAX_VALUE,cb.getPreferredSize().height));
			}
			setSelected();
			return cb;
		}

		
	}

}
