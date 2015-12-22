package mayday.vis3.plots.genomeviz.genomeheatmap.
scrollpane;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.ChromosomeHeader;

@SuppressWarnings("serial")
public class ChromosomeScrollPane extends JScrollPane{
 
	protected GenomeHeatMapTableModel model = null;
	protected ChromosomeHeader chromeHeader;
	protected Controller c;
	
	public ChromosomeScrollPane(GenomeHeatMapTableModel tableModel,Component heatMapTable, int vsbPolicy, int hsbPolicy, Controller C) {
		super(heatMapTable, vsbPolicy, hsbPolicy);
		model = tableModel;
		c = C;
		setName("scrollPaneForHeatmap");
		
		
		
		JPanel panel = new JPanel();
		
		switch (model.getStyle()){
		case CLASSIC:
			getViewport().setBackground(Color.BLACK);
			panel.setBackground(Color.BLACK);
			break;
		case MODERN:
			getViewport().setBackground(Color.WHITE);
			panel.setBackground(Color.WHITE);
			break;
		}
		
		setCorner(UPPER_RIGHT_CORNER,panel);
		
		
	}
	
	public void initScrollPane(){
		chromeHeader = new ChromosomeHeader(model, c, this);
		setColumnHeaderView(chromeHeader);
		getVerticalScrollBar().addAdjustmentListener(c);
	}
	
	public void repositionHeader() {
		remove(chromeHeader);
		setColumnHeaderView(chromeHeader);
	}

	public ChromosomeHeader getHeaderPanel() {
		return chromeHeader;
	}
	
}
