package mayday.core.settings;

import javax.swing.JComponent;

public interface SettingComponent {
	
	public JComponent getEditorComponent();
	
	public boolean updateSettingFromEditor(boolean failSilently);
	
	public Setting getCorrespondingSetting();
	
}
