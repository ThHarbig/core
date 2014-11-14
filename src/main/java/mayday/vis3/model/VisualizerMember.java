package mayday.vis3.model;

import javax.swing.JMenu;

public interface VisualizerMember {

	public void closePlot();
	public JMenu getVisualizerMenu();
	public String getPreferredTitle();
	public void setTitle(String title);
	public String getTitle();
	public void requestFocus();
	public void toFront();
	
}
