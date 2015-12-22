package mayday.core.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import mayday.core.gui.components.ExcellentBoxLayout;

@SuppressWarnings("serial")
public class SettingsPanel extends JPanel {

	List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();
	
	public SettingsPanel(List<Setting> settings, boolean verticalLayout) {
		super(new ExcellentBoxLayout(verticalLayout,3));
		fill(settings, verticalLayout);
	}
	
	public SettingsPanel(List<Setting> settings) {
		this(settings, true);
	}
	
	public List<SettingComponent> getSettingComponents() {
		return Collections.unmodifiableList(settingComponents);
	}
	
	protected void fill(List<Setting> settings, boolean verticalLayout) {
		for (Setting setting : settings) {
			SettingComponent sc = setting.getGUIElement();
			settingComponents.add(sc);
			add(sc.getEditorComponent());
		}
	}
	
}
