package mayday.core.settings.events;

import java.util.EventListener;


public interface SettingChangeListener extends EventListener {

	public void stateChanged(SettingChangeEvent e);	
	
}
