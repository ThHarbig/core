package mayday.vis3.plots.heatmap2;

import java.awt.Component;
import java.awt.Window;
import java.util.Map;

import mayday.core.meta.MIManager;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingDialogMenuItem;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.RelevanceSetting;

public class ColorEnhancementSetting extends HierarchicalSetting {

	public static final String MODE_COLOR = "Add blue color";
	public static final String MODE_OPACITY = "Add transparency";
	
	protected BooleanSetting active;
	protected RestrictedStringSetting mode;
	protected RelevanceSetting relevance;
	protected MIManager mim;
			
	public ColorEnhancementSetting(MIManager mim) {
		super("Color enhancement");
		addSetting(active = new BooleanSetting("Use enhancement",null,false));
		addSetting(mode = new RestrictedStringSetting("Enhancement Method",null,0,new String[]{MODE_COLOR,MODE_OPACITY})
		.setLayoutStyle(mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS));
		addSetting(relevance = new RelevanceSetting(mim));
		this.mim=mim;
	}
	
	public ColorEnhancementSetting clone() {
		ColorEnhancementSetting ces = new ColorEnhancementSetting(mim);
		ces.fromPrefNode(toPrefNode());
		return ces;			
	}
	
	public RelevanceSetting getProvider() {
		return relevance;
	}
	
	public boolean asColor() {
		return mode.getStringValue().equals(MODE_COLOR);
	}
	
	public boolean isActive() {
		return active.getBooleanValue();
	}

	public Component getMenuItem( Window parent ) {
		return new SettingDialogMenuItem(this, parent);
	}	

	@Override
	public boolean updateChildrenFromEditors( Map<Setting, SettingComponent> editors, boolean failSilently ) {
		// only validate all the settings if the enhancement is active
		SettingComponent sc = editors.get(active);
		if (sc!=null && !sc.updateSettingFromEditor(failSilently))
			return false;
		
		boolean allOK = super.updateChildrenFromEditors(editors, !active.getBooleanValue() || failSilently);
		if (active.getBooleanValue())
			return allOK;
		
		return true; //don't care if inactive
	}

	
}
