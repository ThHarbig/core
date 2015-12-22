package mayday.core.settings.generic;

import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingsPanel;

public class HierarchicalSettingComponent_Panel implements SettingComponent {

	protected HierarchicalSetting mySetting;
	protected SettingsPanel editorComponent;
	protected JScrollPane jsp;
	protected boolean verticalLayout;
	protected boolean useScrollPane = false;
	protected boolean topMost = false;

	public HierarchicalSettingComponent_Panel(HierarchicalSetting s, boolean VerticalLayout, boolean TopMost) {
		mySetting = s;
		verticalLayout = VerticalLayout;
		topMost = TopMost;
	}
	
	public JComponent getEditorComponent() {
		if (editorComponent==null) {
			editorComponent = new SettingsPanel(mySetting.getChildren(), verticalLayout);
			setTopMost(topMost);
		}
		if (useScrollPane) {
			if (jsp==null) 
				jsp = new JScrollPane(editorComponent);
			setTopMost(topMost);
			return jsp;
		}
		return editorComponent;
	}

	public boolean updateSettingFromEditor(boolean failSilently) {
		if (editorComponent!=null) {
			Map<Setting, SettingComponent> editors = new HashMap<Setting, SettingComponent>();
			for (SettingComponent sc : editorComponent.getSettingComponents()) {
				editors.put(sc.getCorrespondingSetting(), sc);
			}
			return mySetting.updateChildrenFromEditors(editors, failSilently);
		}
		return true;
	}

	public void setTopMost(boolean TopMost) {
		topMost = TopMost;		
		JComponent targetComponent = useScrollPane?jsp:editorComponent;
		if (targetComponent==null)
			return;		
		if (topMost)
			targetComponent.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		else
			targetComponent.setBorder(BorderFactory.createTitledBorder(mySetting.getName()));
	}
	
	public void setUseScrollPane(boolean useSP) {
		if (jsp==null && editorComponent!=null) {
			editorComponent.setBorder(BorderFactory.createEmptyBorder());
			jsp = new JScrollPane(editorComponent); 
		}
		useScrollPane = useSP;
	}

	public Setting getCorrespondingSetting() {
		return mySetting;
	}

	
}
