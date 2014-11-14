package mayday.vis3.gradient;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JMenu;

import mayday.core.meta.MIType;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.DetachableSettingPanel;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.gradient.gui.GradientEditorPanel;
import mayday.vis3.gradient.gui.GradientPreviewPanel;


public class ColorGradientSetting extends StringSetting {
	
	public enum LayoutStyle {
		SMALL,
		FULL
	}
	
	LayoutStyle layout;
	ColorGradient target;
	
	public ColorGradientSetting(String Name, String Description, ColorGradient Target) {
		super(Name, Description, Target.serialize());
		setColorGradient(Target);
		layout = LayoutStyle.SMALL;
	}
	
	public SettingComponent getGUIElement() {		
		switch (layout) {
		case SMALL:
			return new ColorGradientSettingComponent(this);
		case FULL:
			return new GradientEditorPanelSettingComponent(this);
		}
		return null;		
	}
	
	public Component getMenuItem( Window parent ) {
		JMenu subMenu = new JMenu(getName());
		subMenu.add(new DetachableSettingPanel( this, parent));
		return subMenu;
	}
	
	public ColorGradientSetting clone() {		
		ColorGradient cgcopy = new ColorGradient(getColorGradient());
		ColorGradientSetting cs = new ColorGradientSetting(getName(),getDescription(),cgcopy);
		return cs;		
	}
	
	public ColorGradient getColorGradient() {
		double curMin = target.getMin();
		double curMax = target.getMax();
		target.deserialize(getStringValue());
		target.setMin(curMin);
		target.setMax(curMax);
		return target;
	}
	
	@Override
	public void setValueString(String newValue) {
		String oldVal = getValueString();
		if (!representative.deSerialize(MIType.SERIAL_TEXT, newValue))
			throw new RuntimeException("Invalid value \""+newValue+"\" for Setting of type "+getType());
		if (target!=null)
			getColorGradient(); 		// make sure target is updated on loading
		if (oldVal==null || !oldVal.equals(getValueString()))
			fireChanged();
	}
	
	public ColorGradientSetting setLayoutStyle(LayoutStyle style) {
		layout = style;
		return this;
	}
	
	public void setColorGradient(ColorGradient gradient) {
		target = gradient;
		String before = getStringValue();		
		String after;
		setStringValue( after = target.serialize());
		
		if (!after.equals(before))
			fireChanged();
	}
	
//	@SuppressWarnings("serial")
//	public Component getMenuItem( Window parent) {
//		JMenuItem jmi = new JMenuItem(new AbstractAction(getName()+"...") {
//			public void actionPerformed(ActionEvent e) {
//				final ColorGradient gradient = getColorGradient();
//				GradientEditorDialog ged = new GradientEditorDialog(gradient);
//				ged.addChangeListener(new ChangeListener() {
//					public void stateChanged(ChangeEvent e) {
//						setColorGradient(gradient);				
//					}
//				});
//				ged.setVisible(true);
//			}
//		});
//		return jmi;
//	}
	
	
	public class ColorGradientSettingComponent extends AbstractSettingComponent<ColorGradientSetting> {
		
		protected GradientPreviewPanel gradientPanel;
		
		public ColorGradientSettingComponent(ColorGradientSetting s) {
			super(s);
		}

		protected String getCurrentValueFromGUI() {
			return null; // never called 
		}
		
		public boolean updateSettingFromEditor(boolean failSilently) {
			mySetting.setColorGradient(gradientPanel.getGradient());
			return true;
		}

		protected Component getSettingComponent() {
			gradientPanel = new GradientPreviewPanel(mySetting.getColorGradient());
			gradientPanel.setEditable(true);
			return gradientPanel;
		}

		public void stateChanged(SettingChangeEvent e) {
			gradientPanel.setGradient(mySetting.getColorGradient());
		}		
	}

	
	public class GradientEditorPanelSettingComponent extends AbstractSettingComponent<ColorGradientSetting> {
		
		protected GradientEditorPanel gradientPanel;
		protected ColorGradient editedGradient;
		
		public GradientEditorPanelSettingComponent(ColorGradientSetting s) {
			super(s);			
		}

		protected String getCurrentValueFromGUI() {
			return null; // never called 
		}
		
		public boolean updateSettingFromEditor(boolean failSilently) {
			if (gradientPanel!=null) {
				gradientPanel.apply();
				mySetting.setColorGradient(editedGradient);
			}
			return true;
		}

		protected Component getSettingComponent() {		
			editedGradient = mySetting.getColorGradient();
			gradientPanel = new GradientEditorPanel(editedGradient);
			return gradientPanel;
		}

		public void stateChanged(SettingChangeEvent e) {
			if (gradientPanel!=null) {
				gradientPanel.setGradient(mySetting.getColorGradient());
			}
		}
		
		public boolean needsLabel() {
			return false;
		}
		
		
	}

}
