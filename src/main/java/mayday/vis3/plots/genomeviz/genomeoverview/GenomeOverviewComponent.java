package mayday.vis3.plots.genomeviz.genomeoverview;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import mayday.vis3.gui.PlotComponent;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.DataSetted;
import mayday.vis3.plots.genomeviz.genomeoverview.panels.ChromosomeHeaderPanel;

@SuppressWarnings("serial")
public class GenomeOverviewComponent extends JPanel{

	protected GenomeOverviewModel chromeModel;
	protected Controller c;
	protected DataSetted ds = null;
	protected MyPlotScrollPane scrollPane = null;
	protected ChromosomeHeaderPanel headerPanel = null;
	protected GenomeOverviewLayeredPane overviewPanel;
	
	public GenomeOverviewComponent(){
		super(new BorderLayout());
		ds = new DataSetted();
//		System.out.println("ChromeOverviewComponent - Constructor");

		chromeModel = new GenomeOverviewModel(this,ds);
		c = new Controller(chromeModel,ds);
		overviewPanel = new GenomeOverviewLayeredPane(chromeModel, c);
		chromeModel.setLayeredPane(overviewPanel);
		
		scrollPane = new MyPlotScrollPane(overviewPanel, chromeModel, c);

		chromeModel.setScrollPane(scrollPane);
		
		add(scrollPane,BorderLayout.CENTER);
		
		scrollPane.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				// update marker line when mouse moves over the track panel background
				chromeModel.updateMouseLinePosition(e.getXOnScreen());
			}
		});
		
		addComponentListener(c);
	}
	
	public PlotComponent getComponentForTitling() {
		return overviewPanel;
	}
}
