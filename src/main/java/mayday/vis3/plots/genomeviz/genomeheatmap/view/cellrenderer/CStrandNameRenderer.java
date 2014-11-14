package mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.GHMStyle;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.ScaleImageModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;


public class CStrandNameRenderer extends JLabel implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4124371321001267180L;
	protected int FontHEIGHT;
	protected MasterManager master;
	protected StrandInformation strand;
	protected GHMStyle style;
	protected GenomeHeatMapTableModel model;
	protected ScaleImageModel simodel;
	protected BufferedImage scalaImage;
	protected int row = -1;
	protected int col = -1;
	protected Font displayFont = null;
	protected int width = 0;
	protected int height = 0;
	  
	public CStrandNameRenderer(MasterManager Master,GenomeHeatMapTableModel Model){
		master = Master;
		model = Model;
		simodel = model.getSi_model();
		style = Model.getStyle();
		
		switch(style){
		case CLASSIC:
			setBackground(Color.BLACK);
			break;
		case MODERN:
			setBackground(Color.white);
			break;
		}
		
		setOpaque(true);
		setHorizontalAlignment(RIGHT);
		FontHEIGHT = getFontMetrics(getFont()).getHeight();
		width = model.getTableSettings().getBoxSizeX();
		height = model.getTableSettings().getBoxSizeY();
		int val = height-5;

		displayFont = new Font("Sans", Font.PLAIN, val); 
		
	}

	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int Row, int Col) {
		row = Row;
		col = Col;
		
		switch(style){
			case CLASSIC:
				setBackground(Color.BLACK);
				setForeground(Color.BLACK);
				break;
			case MODERN:
				setBackground(Color.WHITE);
				setForeground(Color.WHITE);
				break;
		}
	
		
		if(model.getTableSettings().getBoxSizeX() >= 10 && model.getTableSettings().getBoxSizeY() > 4){
			strand = ((CellObject)value).getStrand();
		} 

		return this;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2D = (Graphics2D)g;
		g2D.setFont(displayFont);
		g2D.setColor(Color.RED);
		g2D.setStroke(new BasicStroke(13f / this.getWidth(), BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		if(strand!=null){
			if(strand.equals(StrandInformation.MINUS)){
				this.scalaImage = null;
				String tick = "-";
				g2D.drawString(tick,
						(int)(Math.floor((double)width/2.) - (Math.min(g2D.getFontMetrics().stringWidth(tick) >> 1, 
						g2D.getFontMetrics().stringWidth(tick)+ Math.abs(Math.floor((double)width/2.)) -1))),
						(int)(height-(Math.floor((double)height/3.))));
			} else if(strand.equals(StrandInformation.PLUS)){
				this.scalaImage = null;
				String tick = "+";
				g2D.drawString(tick,
						(int) ((double)width/2. - (Math.min(g2D.getFontMetrics().stringWidth(tick) >> 1, 
						g2D.getFontMetrics().stringWidth(tick)+ Math.abs((double)width/2.) -1))),
						(int)(height-(Math.floor((double)height/3.))));
			} else if(strand.equals(StrandInformation.PLACEHOLDER)){
				if(model.getKindOfChromeView() == KindOfChromeView.WHOLE){
					
					if (model.getBufferedImage(row) == null) {
						model.setBufferedImage(row, simodel.paintScalaImage(row, model, strand));
					}
					
					if (model.getBufferedImage(row) != null) {
						this.scalaImage = model.getBufferedImage(row).getScalaImage();
					}

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
