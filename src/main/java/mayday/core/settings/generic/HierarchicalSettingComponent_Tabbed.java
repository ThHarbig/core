package mayday.core.settings.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.TopMostSettable;

public class HierarchicalSettingComponent_Tabbed implements SettingComponent {

	protected HierarchicalSetting mySetting;
	protected boolean topMost = false;
	protected JTabbedPane pane;
	List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();

	public HierarchicalSettingComponent_Tabbed(HierarchicalSetting s, boolean TopMost) {
		mySetting = s;
		topMost = TopMost;
	}
	
	@SuppressWarnings("unchecked")
	public JComponent getEditorComponent() {
		if (pane==null) {
			pane = new JTabbedPane();
			for (Setting s : mySetting.getChildren()) {
				if (s instanceof TopMostSettable)
					((TopMostSettable) s).setTopMost(true);
				SettingComponent sc = s.getGUIElement();
				settingComponents.add(sc);
				pane.add(s.getName(), sc.getEditorComponent());				
			}
			setTopMost(topMost);
		}
		return pane;
	}
	
	public boolean updateSettingFromEditor(boolean failSilently) {
		if (pane!=null) {
			Map<Setting, SettingComponent> editors = new HashMap<Setting, SettingComponent>();
			for (SettingComponent sc : settingComponents) {
				editors.put(sc.getCorrespondingSetting(), sc);
			}
			return mySetting.updateChildrenFromEditors(editors, failSilently);
		}
		return true;
	}

	public void setTopMost(boolean TopMost) {
		topMost = TopMost;
		if (pane==null)
			return;
	}

	public Setting getCorrespondingSetting() {
		return mySetting;
	}

	
}
