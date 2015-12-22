package mayday.vis3.plots.heatmap2.data;

import java.awt.Component;
import java.awt.Window;
import java.util.Map;

import mayday.core.meta.MIManager;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.SettingDialogMenuItem;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.RelevanceSetting;

public class RowHeightEnhancementSetting extends HierarchicalSetting {

	protected BooleanSetting active;
	protected RelevanceSetting relevance;
	protected IntSetting maxH;
	protected MIManager mim;
			
	public RowHeightEnhancementSetting(MIManager mim) {
		super("Row height enhancement");
		addSetting(active = new BooleanSetting("Scale rows",null,false));
		addSetting(relevance = new RelevanceSetting(mim));
		this.mim = mim;
	}
	
	public void setMaximumHeight(int h) {
		maxH.setIntValue(h);
	}
	
	public RowHeightEnhancementSetting clone() {
		RowHeightEnhancementSetting ces = new RowHeightEnhancementSetting(mim);
		ces.fromPrefNode(toPrefNode());
		return ces;			
	}
	
	public double rowHeight(Object o) {
		if (isActive())
			return relevance.getRelevance(o);
		return 1;
	}
	
	protected boolean isActive() {
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
