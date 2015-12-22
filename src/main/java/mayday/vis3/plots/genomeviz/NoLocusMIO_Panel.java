package mayday.vis3.plots.genomeviz;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.vis3.components.CenteredMiddleLayout;

@SuppressWarnings("serial")
public class NoLocusMIO_Panel extends JPanel  {

	
	public NoLocusMIO_Panel(JComponent GenomeOverviewLayeredPane){
		super(new CenteredMiddleLayout());
		add(new JLabel("<html>No LocusInfo found in this Visualizer. <br>Add a ProbeList with a Locus MIO."),"Middle");
	}
}
