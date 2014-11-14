package mayday.vis3.gradient.gui;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

import mayday.vis3.gradient.ColorGradient;

public interface GradientSetupComponent {

	public void addListener(ActionListener l);	
	public void removeListener(ActionListener l);
	
	public JComponent getJComponent();
	
	public void modifyGradient(ColorGradient c);
	
	public void updateFromGradient(ColorGradient c, boolean overrideEverything);
		
}
