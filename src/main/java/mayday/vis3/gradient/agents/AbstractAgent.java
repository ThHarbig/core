package mayday.vis3.gradient.agents;

import java.awt.Color;

import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.gui.GradientSetupComponent;

public interface AbstractAgent {
	
	public void setColorGradient(ColorGradient parent);
	
	public Color getColor(int index);
	
	public void updateColors();
	
	public boolean needsUpdating();
	
	public AbstractAgent clone();
	
	public GradientSetupComponent getSetupComponent();
	
	public boolean equals(AbstractAgent otherAgent);

	public String serialize();
	public void deserialize(String s);
}
