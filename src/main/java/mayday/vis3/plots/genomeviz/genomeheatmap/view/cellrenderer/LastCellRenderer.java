package mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;

@SuppressWarnings("serial")
public class LastCellRenderer extends JLabel implements TableCellRenderer  {
	
	protected GenomeHeatMapTableModel model;
	protected BufferedImage scalaImage;
	protected StrandInformation strand;
	protected int row = -1;
	protected int col = -1;
	
	public LastCellRenderer(GenomeHeatMapTableModel Model){
	
		model = Model;
		
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
		setOpaque(true);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int Row, int Col) {
		row = Row;
		col = Col;
		
		if(model.getTableSettings().getBoxSizeX() >= 10 && model.getTableSettings().getBoxSizeY() > 4){
			strand = ((CellObject)value).getStrand();
		} 
		
		return this;
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2D = (Graphics2D)g;

		if(strand!=null){
			if(strand.equals(StrandInformation.PLACEHOLDER)){
				if(model.getKindOfChromeView() == KindOfChromeView.WHOLE){
					if (model.getBufferedImage(row) != null) {
						scalaImage = model.getBufferedImage(row).getScalaImage();
						if(scalaImage!=null){
							int boxSizeX = model.getTableSettings().getBoxSizeX();
							int boxSizeY = model.getTableSettings().getBoxSizeY();
							
							g2D.drawImage(scalaImage, 0, 0, boxSizeX, boxSizeY,
									(col) * boxSizeX, 0,
									((col) * boxSizeX + boxSizeX),
									boxSizeY, this);
						}
					}
				}
			}
		}
	}
}
