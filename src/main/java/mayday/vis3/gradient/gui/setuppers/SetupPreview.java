package mayday.vis3.gradient.gui.setuppers;

import javax.swing.JComponent;

import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.gui.AbstractGradientSetupComponent;
import mayday.vis3.gradient.gui.GradientPreviewPanel;

public class SetupPreview extends AbstractGradientSetupComponent {
	
	public enum ROTATION {
		HORIZONTAL,
		VERTICAL;
	}
	
	protected GradientPreviewPanel preview;
	
	public SetupPreview() {
		preview = new GradientPreviewPanel(ColorGradient.createDefaultGradient(0, 16));
	}

	public JComponent getJComponent(ROTATION rotation) {
		preview.setRotation(rotation);
		return preview;
	}
	
	public JComponent getJComponent() {
		return this.getJComponent(ROTATION.HORIZONTAL);
	}

	public void modifyGradient(ColorGradient c) {
		//void
	}


	public void updateFromGradient(ColorGradient c, boolean overrideEverything) {
		preview.setGradient(c);
		
	}

}
