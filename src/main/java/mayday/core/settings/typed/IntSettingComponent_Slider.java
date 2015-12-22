package mayday.core.settings.typed;

import java.awt.Component;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.events.SettingChangeEvent;

public class IntSettingComponent_Slider extends AbstractSettingComponent<IntSetting> {

	protected JSlider slider;
	
	public IntSettingComponent_Slider(IntSetting s) {
		super(s);
	}

	@Override
	protected String getCurrentValueFromGUI() {
		if (slider!=null)
			return ""+slider.getValue();
		return null;
	}

	@Override
	protected Component getSettingComponent() {
		if (slider==null) {
			slider = new JSlider(mySetting.min, mySetting.max, mySetting.getIntValue());
			int perc20 = ( (mySetting.max - mySetting.min)/5);
			int perc5 = ( (mySetting.max - mySetting.min)/20);
			slider.setMajorTickSpacing(perc20);
			slider.setMinorTickSpacing(perc5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.addChangeListener(new ChangeListener() {

				public void stateChanged(ChangeEvent e) {
					slider.setToolTipText(""+slider.getValue());
				}
				
			});
			
		}
		return slider;
	}

	public void stateChanged(SettingChangeEvent e) {
		slider.setValue(mySetting.getIntValue());
	}
	
}
