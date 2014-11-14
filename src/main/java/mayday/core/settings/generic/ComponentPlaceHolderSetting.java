package mayday.core.settings.generic;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JComponent;

import mayday.core.Preferences;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeListener;

public class ComponentPlaceHolderSetting implements Setting {
	
	protected String name;
	protected JComponent comp;

	public ComponentPlaceHolderSetting(String Name, JComponent c) {
		name=Name;
		comp=c;
	}	
		
	public String toString() {
		return getName();
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isValidValue(String value) {
		return true;		
	}

	public SettingComponent getGUIElement() {
		return new AnyComponentSettingComponent();
	}

	public String getValueString() {
		return "";
	}

	public void setValueString(String newValue) {
	}

	public boolean fromPrefNode(Preferences prefNode) {
		return true;
	}

	public Preferences toPrefNode() {
		Preferences myNode = Preferences.createUnconnectedPrefTree(getName(), getValueString());
		return myNode;
	}

	public ComponentPlaceHolderSetting clone() {
		ComponentPlaceHolderSetting gs = new ComponentPlaceHolderSetting(name, comp); //could be problematic to NOT clone the Component
		return gs;
	}


	public void addChangeListener(SettingChangeListener changeListener) {
	}

	public void removeChangeListener(SettingChangeListener changeListener) {
	}


	public String getDescription() {
		return null;
	}
	

	public class AnyComponentSettingComponent implements SettingComponent {

		public JComponent getEditorComponent() {
			return comp;
		}

		public void setBorderVisible(boolean visible) {
		}

		public boolean updateSettingFromEditor(boolean failSilently) {
			return true;
		}

		public Setting getCorrespondingSetting() {
			return ComponentPlaceHolderSetting.this;
		}
		
	}

	public String getValidityHint() {
		return "";
	}
	
	public Component getMenuItem( Window parent ) {
		return null;
	}

}
