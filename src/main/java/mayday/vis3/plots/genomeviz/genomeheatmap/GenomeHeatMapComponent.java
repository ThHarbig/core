package mayday.vis3.plots.genomeviz.genomeheatmap;


import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.scrollpane.ChromosomeScrollPane;
 

public class GenomeHeatMapComponent extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8592739586900614201L;
	protected String name = "";
	
	protected GenomeHeatMapTable theTable;
	
	public GenomeHeatMapComponent() {

		super(new BorderLayout());

		MasterManager master = new MasterManager();

		final GenomeHeatMapTableModel tableModel = new GenomeHeatMapTableModel(
				master);

		Controller c = new Controller(master, tableModel);

		master.setController(c); // set controller in master

		theTable = new GenomeHeatMapTable(master, this);

		final ChromosomeScrollPane scrollPaneForHeatMap = new ChromosomeScrollPane(
				tableModel, theTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, c);

		c.init(scrollPaneForHeatMap, theTable, scrollPaneForHeatMap
				.getHeaderPanel());

		// set table, scrollPane, component and tableModel in the tableManager
		// for later use
		setupMaster(master, theTable, scrollPaneForHeatMap, tableModel);
		tableModel.setScrollPane(scrollPaneForHeatMap);
		// set Model for table
		theTable.setModel(tableModel);
		tableModel.addTableModelListener(c);
		// add listener to component
		addComponentListener(c);
		add(scrollPaneForHeatMap, BorderLayout.CENTER);
	}

	public GenomeHeatMapTable getTable() {
		return theTable;
	}
	
	/**
	 * paints some error message if data not setted or if no locus MIO
	 * available.
	 * 
	 * @param Graphics
	 */
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	private void setupMaster(MasterManager master,GenomeHeatMapTable heatMapTable,
			JScrollPane scrollPaneForHeatMap, GenomeHeatMapTableModel tableModel) {
		master.setScrollPane(scrollPaneForHeatMap);
		master.setChromeHeatMapTable(heatMapTable);
		master.setTableModel(tableModel);
	}
	
	public String getName(){
		return name;
	}
}
