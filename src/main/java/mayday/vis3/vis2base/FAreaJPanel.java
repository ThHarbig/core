package mayday.vis3.vis2base;

import java.awt.BorderLayout;
import java.awt.Graphics2D;

import mayday.vis3.components.AntiAliasPlotPanel;
import wsi.ra.plotting.FunctionArea;

@SuppressWarnings("serial")
public abstract class FAreaJPanel extends AntiAliasPlotPanel {

	private FunctionArea farea;
	
	public FAreaJPanel(FunctionArea fa) {
		setLayout(new BorderLayout());
		farea=fa;
		add(farea);
	}
	
	@Override
	public void paintPlot(Graphics2D g) {
		farea.paint(g);
	}
	
}
