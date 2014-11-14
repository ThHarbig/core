package mayday.core.settings.typed;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JColorChooser;
import javax.swing.JMenuItem;

import mayday.core.gui.components.ColorPreview;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;

public class ColorSetting extends StringSetting {

	public ColorSetting(String Name, String Description, Color Default) {
		super(Name, Description, Default.getRGB()+"");
	}
		
	public String getValidityHint() {
		return "";
	}
	
	public boolean isValidValue(String value) {
		try {
			Color.decode(value);
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	public Color getColorValue() {
		return new Color(Integer.parseInt(getStringValue()));
	}
	
	public void setColorValue(Color c) {
		setValueString(""+c.getRGB());
	}
	
	public ColorSetting clone() {
		return new ColorSetting(getName(),getDescription(),getColorValue());
	}
	
	public static class ColorSettingComponent extends AbstractSettingComponent<ColorSetting> {

		protected ColorPreview cp;
		
		public ColorSettingComponent(ColorSetting setting) {
			super(setting);
		}

		public boolean updateSettingFromEditor(boolean failSilently) {
			mySetting.setColorValue(cp.getColor());
			return true;
		}

		public void stateChanged(SettingChangeEvent e) {
			if (cp!=null) {
				cp.setColor(mySetting.getColorValue());
			}
		}

		protected String getCurrentValueFromGUI() {
			return null; //never called
		}

		protected Component getSettingComponent() {
			cp = new ColorPreview(mySetting.getColorValue(), true);
			cp.setEditable(true);
			return cp;
		}
		
	}
	
	public SettingComponent getGUIElement() {		
		return new ColorSettingComponent(this);
	}
	
	@SuppressWarnings("serial")
	@Override
	public Component getMenuItem(final Window parent) {
		JMenuItem jmi = new JMenuItem(new AbstractAction(getName()+"...") {
			public void actionPerformed(ActionEvent e) {
				Color lcolor = JColorChooser.showDialog( parent, "Choose Color", getColorValue() );
				if ( lcolor != null ) {
					setColorValue(lcolor);
				}
			}
		});
		return jmi;
	}
	

}
