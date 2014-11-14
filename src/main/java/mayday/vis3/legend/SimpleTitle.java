package mayday.vis3.legend;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;

import mayday.core.ProbeList;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class SimpleTitle extends BasicPlotPanel implements SettingChangeListener{

	protected ViewModel viewModel;
	protected JLabel legendLabel = new JLabel();
	protected PlotComponent titledComponent;
	
	protected HierarchicalSetting titleSetting;
	protected BooleanSetting showCaption;
	protected StringSetting captionText;
	protected IntSetting fontSize;
	
	/**
		@Deprecated should always use SimpleTitle(PlotComponent) instead
	**/
	public SimpleTitle() {
		add(legendLabel);
		Font f = legendLabel.getFont();
		legendLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize()+5));

		titleSetting = new HierarchicalSetting("Caption")
			.addSetting(showCaption = new BooleanSetting("Show caption",null,false))
			.addSetting(captionText = new StringSetting("Caption text",null,""))			
			.addSetting(fontSize = new IntSetting("Font size",null,legendLabel.getFont().getSize(),4,null,true,true))
			.setChildrenAsSubmenus(false);
		titleSetting.addChangeListener(this);
		setVisible(showCaption.getBooleanValue());
		setBackground(Color.WHITE);
		setOpaque(true);		
		titledComponent = this;
	}
	
	public SimpleTitle(String t) {
		this();
		captionText.setStringValue(t);
		showCaption.setBooleanValue(true);
		revalidate();
	}
	
	public SimpleTitle(String t, PlotComponent titledComponent) {
		this(t);
		setTitledComponent(titledComponent);
	}
	
	public SimpleTitle(PlotComponent titledComponent) {
		this();
		setTitledComponent(titledComponent);
	}
	
	public void setTitledComponent(PlotComponent c) {
		titledComponent = c;
	}
	
	public void setup(PlotContainer plotContainer) {
		plotContainer.addViewSetting(titleSetting, titledComponent); // pretend to be someone else
		if (captionText.getStringValue().length()==0) {
			List<ProbeList> lpl = plotContainer.getViewModel().getProbeLists(false);
			String c = "";
			for (ProbeList pl : lpl)
				c+=pl.getName()+", ";
			if (c.length()>0)
				c = c.substring(0, c.length()-2);
			captionText.setStringValue(c);
			
		}
	}

	public void updatePlot() {
	}

	public void stateChanged(SettingChangeEvent e) {
		legendLabel.setText(this.captionText.getStringValue());
		SimpleTitle.this.setVisible(showCaption.getBooleanValue());
		Font f = legendLabel.getFont();
		legendLabel.setFont(new Font(f.getName(), Font.BOLD, fontSize.getIntValue()));
		revalidate();
	}
	
	
}
