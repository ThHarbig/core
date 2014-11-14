/**
 * 
 */
package mayday.vis3.vis2base;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.StringSetting;

public class VisibleRectSetting extends HierarchicalSetting {

	public DoubleSetting xmax = new DoubleSetting("Maximum",null,1.0);
	public DoubleSetting xmin = new DoubleSetting("Minimum",null,1.0);
	public DoubleSetting ymax = new DoubleSetting("Maximum",null,1.0);
	public DoubleSetting ymin = new DoubleSetting("Minimum",null,0.2);
	public StringSetting xtitle = new StringSetting("Title",null, "Experiment");
	public StringSetting ytitle = new StringSetting("Title",null, "Expression value");
	public BooleanHierarchicalSetting usersetXtitle = new BooleanHierarchicalSetting("Replace title", "Replace automatically defined axis title", false).addSetting(xtitle);
	public BooleanHierarchicalSetting usersetYtitle = new BooleanHierarchicalSetting("Replace title", "Replace automatically defined axis title", false).addSetting(ytitle);
	
	protected boolean silent=false;
	protected ChartComponent c;
	
	@SuppressWarnings("serial")
	public VisibleRectSetting(ChartComponent cc) {			
		super("Visible Area");
		c=cc;
		addSetting(new HierarchicalSetting("X axis")
		.addSetting(xmin).addSetting(xmax).addSetting(usersetXtitle)
		);
		addSetting(new HierarchicalSetting("Y axis")
		.addSetting(ymin).addSetting(ymax).addSetting(usersetYtitle)
		);
		addSetting(new ComponentPlaceHolderSetting("Fit content",new JButton(new AbstractAction("Fit content") {
			public void actionPerformed(ActionEvent e) {
				c.getArea().setAutoFocus(true);
				c.updatePlot();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						initSettings();
					}					
				});
			}
		})));
		initSettings();
	}
	
	public SettingComponent getGUIElement() {
		initSettings();
		return super.getGUIElement();
	}
	
	protected void initSettings() {
		silent=true;
		xmin.setDoubleValue(c.getArea().getDRectangle().x);
		xmax.setDoubleValue(c.getArea().getDRectangle().width + c.getArea().getDRectangle().x);
		ymin.setDoubleValue(c.getArea().getDRectangle().y);
		ymax.setDoubleValue(c.getArea().getDRectangle().height + c.getArea().getDRectangle().y);
		xtitle.setStringValue(c.getArea().getAxisTitleX());
		ytitle.setStringValue(c.getArea().getAxisTitleY());
		silent=false;
	}
	
	public void fireChanged(SettingChangeEvent evt) {
		if (!silent)
			super.fireChanged(evt);
	}

	public DoubleSetting getXmax() {
		return xmax;
	}

	public DoubleSetting getXmin() {
		return xmin;
	}

	public DoubleSetting getYmax() {
		return ymax;
	}

	public DoubleSetting getYmin() {
		return ymin;
	}

	public StringSetting getXtitle() {
		return xtitle;
	}

	public StringSetting getYtitle() {
		return ytitle;
	}

	public ChartComponent getC() {
		return c;
	}
	
	public VisibleRectSetting clone() {
		VisibleRectSetting cs = new VisibleRectSetting(c);
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
}