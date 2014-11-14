package mayday.core.settings;

import java.awt.Component;
import java.awt.Window;

import mayday.core.Preferences;
import mayday.core.settings.events.SettingChangeListener;

public interface Setting {

	/** Returns a helpful (!) description for this settings object, or null */ 
	public String getDescription();
	
	/** Returns the name for this settings object, i.e. for GUI labels */
	public String getName();
	
	/** Returns true if the given value is valid for this kind of setting. Implement complex checks here */
	public boolean isValidValue(String value);
	
	/** returns a GUI element allowing to edit the settings value. */
	public SettingComponent getGUIElement();
	
	/** returns a MenuItem allowing to change the settings value. If the parent is disposed, so are windows opened from the menu item */
	public Component getMenuItem( Window parent );
	
	/** Sets the value of this setting via a String representation (serialization) */
	public void setValueString(String newValue);
	
	/** Returns the value of this setting as a String representation (serialization) */
	public String getValueString();
	
	/** Returns a preference node for this settings object */
	public Preferences toPrefNode();
	
	/** Initializes this setting form a preference node, i.e. 
	 * fills the EXISTING Setting object with stored preference values */
	public boolean fromPrefNode(Preferences prefNode);
	
	/** Create an initialized clone of this object */
	public Setting clone();

	/** Return a description of valid values, i.e. ranges for integers */
	public String getValidityHint();
	
	public void addChangeListener(SettingChangeListener cl);
	public void removeChangeListener(SettingChangeListener cl);
	
}
