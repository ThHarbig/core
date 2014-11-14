package mayday.vis3.gradient.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JPanel;

import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.gui.setuppers.SetupAgent;
import mayday.vis3.gradient.gui.setuppers.SetupExtraOptions;
import mayday.vis3.gradient.gui.setuppers.SetupMidpoint;
import mayday.vis3.gradient.gui.setuppers.SetupPreview;
import mayday.vis3.gradient.gui.setuppers.SetupResolution;

@SuppressWarnings("serial")
public class GradientEditorPanel extends JPanel {
	
	protected ColorGradient base;
	protected ColorGradient state;
	
	protected LinkedList<GradientSetupComponent> setupComponents = new LinkedList<GradientSetupComponent>(); 
	
	public GradientEditorPanel(ColorGradient base) {
		super(new ExcellentBoxLayout(true, 5));
		this.base = base;
		state = new ColorGradient(base);
		init();
	}
	
	protected void addSetupComponent(final GradientSetupComponent gsc) {		
		gsc.addListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gsc.modifyGradient(state);
				for (GradientSetupComponent sc : setupComponents)
					if (sc!=gsc)
						sc.updateFromGradient(state, false);
			}
		});
		setupComponents.add(gsc);
		add(gsc.getJComponent());
	}
	
	protected void init() {

		addSetupComponent(new SetupResolution());
		addSetupComponent(new SetupAgent());
		addSetupComponent(new SetupExtraOptions());
		addSetupComponent(new SetupMidpoint());
		addSetupComponent(new SetupPreview());
		
		setGradient(base);
		
	}
	
	public void setGradient(ColorGradient base) {
		state = new ColorGradient(base);
		for (GradientSetupComponent sc : setupComponents)
			sc.updateFromGradient(state, true);
	}

	public void apply() {		
		base.copySettings(state);
	}


}
