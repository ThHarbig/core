package mayday.core.settings.typed;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;

import mayday.core.meta.MIGroup;
import mayday.core.meta.types.BooleanMIO;
import mayday.core.meta.types.BooleanMIO.BooleanMIRenderer;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.GenericSetting;
import mayday.core.settings.generic.GenericSettingComponent;

public class BooleanSetting extends GenericSetting {

	public BooleanSetting(String Name, String Description, boolean Default) {
		super(Name, BooleanMIO.class, Description);
		setBooleanValue(Default);
	}
	
	public boolean getBooleanValue() {
		return ((BooleanMIO)getValue()).getValue();
	}
	
	public void setBooleanValue(Boolean nv) {
		setValueString(nv.toString());
	}
	
	@SuppressWarnings("unchecked")
	public SettingComponent getGUIElement() {
		return new GenericSettingComponent<BooleanMIO>(this) {
			protected Component getSettingComponent() {
				if (miRenderer==null) {
					miRenderer = mySetting.getValue().getGUIElement();
					((BooleanMIRenderer)miRenderer).setLabel(mySetting.getName());
					miRenderer.setEditable(true);
					miRenderer.connectToMIO((BooleanMIO) mySetting.getValue(), (Object)null, (MIGroup)null);
				}
				return miRenderer.getEditorComponent();
			}
			protected boolean needsLabel() {
				return false;
			}
		};
		
	}
	
	public BooleanSetting clone() {
		return new BooleanSetting(getName(),getDescription(),getBooleanValue());
	}
	
	@SuppressWarnings("serial")
	public Component getMenuItem( final Window parent ) {
		AbstractAction aa = new AbstractAction(getName()) {
			public void actionPerformed(ActionEvent e) {
				setBooleanValue(((JCheckBoxMenuItem)e.getSource()).isSelected());
			}
		};
		aa.putValue(JComponent.TOOL_TIP_TEXT_KEY, getDescription());
		final JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem(aa);
		jcbmi.setSelected(getBooleanValue());
		addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				jcbmi.setSelected(getBooleanValue());
			}
		});
		return jcbmi;
	}


}
