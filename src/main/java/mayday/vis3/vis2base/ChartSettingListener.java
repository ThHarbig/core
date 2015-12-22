package mayday.vis3.vis2base;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;

public class ChartSettingListener {

	public ChartSettingListener(final ChartComponent cc, final ChartSetting cs) {
				
		cs.getGrid()
		.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				cc.setGrid(cs.getGrid().getXmin().getDoubleValue(), cs.getGrid().getYmin().getDoubleValue());
				cc.setGridEmphasize(cs.getGrid().getXmaj().getDoubleValue(), cs.getGrid().getYmaj().getDoubleValue());
				cc.setGridToFront(cs.getGrid().getOnTop().getBooleanValue());
				cc.setGridVisible(cs.getGrid().getVisible().getBooleanValue());
				cc.updatePlot();
			}
		});
		
		cs.getAlpha()
		.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				float f = ((float)cs.getAlpha().getIntValue());
				f/=100;
				cc.farea.setAlpha(f);
				cc.updatePlot();
			}
		});
		
		cs.getAntialias()
		.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				cc.farea.setAntalias(cs.getAntialias().getBooleanValue());
				cc.updatePlot();
			}
		});
		
		cs.getVisibleRect()
		.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {	
				VisibleRectSetting vrs = cs.getVisibleRect();
				if (e.getSource()==vrs.getXmax() || e.getSource()==vrs.getYmax() || e.getSource()==vrs.getXmin() || e.getSource()==vrs.getYmin()) {
					cc.getArea().setVisibleRectangle(
							vrs.getXmin().getDoubleValue(),
							vrs.getYmin().getDoubleValue(),
							vrs.getXmax().getDoubleValue()-vrs.getXmin().getDoubleValue(),
							vrs.getYmax().getDoubleValue()-vrs.getYmin().getDoubleValue());
				} else if (e.getSource()==vrs.getXtitle() || e.getSource()==vrs.getYtitle()) {
					String xtitle = vrs.getXtitle().getStringValue();
					String ytitle = vrs.getYtitle().getStringValue();
					cc.setAxisTitleDirectly(xtitle,ytitle);
				}
				cc.updatePlot();			
			}
		});
		
		cs.getFontSize().addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				cc.setFont(cc.getFont().deriveFont((float)cs.getFontSize().getIntValue()));
				cc.updatePlot();
			}

		});
		
		cs.getExperimentLabelSkip().addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				cc.updatePlot();
			}
		});	
		
		cs.getColors().addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				cc.getArea().setForeground(cs.getColorForeground().getColorValue());
				cc.getArea().setBackground(cs.getColorBackground().getColorValue());
				cc.getArea().getGrid().setColor(cs.getColorGrid().getColorValue());
				cc.updatePlot();
			}
		});	
	}
	
}
