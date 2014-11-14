package mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.table.DefaultTableCellRenderer;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;

// Die ist der standard Header der Tabelle, wird auf weiss gesetzt
@SuppressWarnings("serial")
public class CEmptyHeaderRenderer extends DefaultTableCellRenderer{

	
	protected GenomeHeatMapTableModel model;
	public CEmptyHeaderRenderer(GenomeHeatMapTableModel Model){
		model = Model;
		setOpaque(true);
		setVisible(true);
		Dimension d = new Dimension(model.getTableSettings().getBoxSizeX(),model.getTableSettings().getBoxSizeY());
		setSize(d); 
		setPreferredSize(d);
	}	
	
	public void paint(Graphics g) {
		super.paint(g);
		switch(model.getStyle()){
		case CLASSIC:
			setBackground(Color.BLACK);
			setForeground(Color.BLACK);
			break;
		case MODERN:
			setBackground(Color.WHITE);
			setForeground(Color.WHITE);
			break;
		}
	}
}
