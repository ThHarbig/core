package mayday.vis3.plots.genomeviz.genomeheatmap.view.cellrenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import mayday.core.Probe;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.StrandInformation;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.ScaleImageModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.GetWholeChromePosition_Delegate;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.CellObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.MyColorProvider;

@SuppressWarnings("serial")
public abstract class AbstractBoxRenderer extends JPanel implements TableCellRenderer{

	protected MyColorProvider coloring;
	protected boolean hasFocus;
	protected MasterManager master;
	protected GenomeHeatMapTableModel model;
	protected ViewModel viewModel;
	protected List<Probe> selectedProbes;

	protected StrandInformation strand = null;
	protected boolean placeholderCell = false;
	protected boolean plusCell = false;
	protected boolean minusCell = false;
	protected boolean endOfChrome = false;
	protected int placeHolderColumn = -1;
	protected int placeHolderRow = -1;
	protected BufferedImage scalaImage;
	protected int cellnumber = -1;
	
	// for painting by probelist
	protected boolean paintMe = false;

	protected HashMap<Color,Integer> colordistribution = null;
	protected CellObject probesInf = null;
	protected ScaleImageModel simodel;
	
	public AbstractBoxRenderer(MyColorProvider Coloring, MasterManager Master, GenomeHeatMapTableModel Model){
		coloring = Coloring;
		master = Master;
		model = Model;
		viewModel = master.getViewModel();
		simodel = model.getSi_model();
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int col) {

		// for painting by Probelist reset flag and colors in cell
		paintMe = false;
		colordistribution = new HashMap<Color, Integer>();
		
		if (col == 0) {
			return this;
		}
		
		switch(model.getStyle()){
		case CLASSIC:
			setBackground(Color.BLACK);
			break;
		case MODERN:
			setBackground(Color.WHITE);
			break;
		}

			probesInf = (CellObject)value;
			
			strand = probesInf.getStrand();
			switch (strand) {
			case PLACEHOLDER:
				placeholderCell = true;
				minusCell = false;
				plusCell = false;
				placeHolderColumn = col;
				placeHolderRow = row;
				break;
			case PLUS:
				placeholderCell = false;
				minusCell = false;
				plusCell = true;
				this.scalaImage = null;
				break;
			case MINUS:
				placeholderCell = false;
				minusCell = true;
				plusCell = false;
				this.scalaImage = null;
				break;
			default:
				placeholderCell = false;
				minusCell = false;
				plusCell = false;
				this.scalaImage = null;
			}
			this.cellnumber = probesInf.getCellnumber();
			if(cellnumber > model.getTableSettings().getNumberOfNecessaryCells()){
				endOfChrome = true;
			} else {
				endOfChrome = false;
			}
			
			cellTypeSelection(row, col, isSelected);	
		
		return this;	
	}
	
	/**
	 * Choose methods to color probes.
	 * @param strand: strand of cell
	 * @param row: row of cell
	 * @param col: column of cell
	 * @param probe: probe contained in cell (null if cell is empty, or if mean split is selected)
	 * @param cellNumber: number of actual cell
	 * @param isSelected: true if cell is selected
	 */
	protected void cellTypeSelection(int row, int col, boolean isSelected){

		
		switch (strand) {
		case PLACEHOLDER:
			setPlaceholderCell(cellnumber, row, col);
			break;
		case BORDER:
			setBorderCell(row, col);
			break;
		default:
			if (endOfChrome) {
				if(!probesInf.getProbes().isEmpty()){
					System.err.println("ChromeBoxRenderer : probes contained in endofchrome cell");
				}
				setEndOfChromeCell();

			} else {

				if (!probesInf.getProbes().isEmpty())
					setProbeCell(probesInf, row, col, isSelected);
				else
					setGapCell();
			}
		}
	}
	
	/**
	 * set placeholder cells and set tooltip for position in chromosome.
	 * @param cellNumber
	 * @param row
	 * @param col
	 */
	protected void setPlaceholderCell(int cellNumber, int row, int col){

		switch (model.getKindOfChromeView()) {
		case WHOLE:
			
			if (model.getBufferedImage(row) == null) {
				model.setBufferedImage(row, simodel.paintScalaImage(row, model, strand));
			}
			
			if (model.getBufferedImage(row) != null) {
				this.scalaImage = model.getBufferedImage(row).getScalaImage();
			}

			int chromePosition = new GetWholeChromePosition_Delegate().execFirstPos(cellNumber, model);

			if (chromePosition <= 0) {
				this.setToolTipText(null);
			} else {
				if (!endOfChrome) {
					String toolBoxText = "";
					int cellnumber = probesInf.getCellnumber();
				
					int chromePosition_from = new GetWholeChromePosition_Delegate()
							.execFirstPos(cellnumber, model);
					int chromePosition_to = new GetWholeChromePosition_Delegate().execLastPos(cellnumber, model);
					
					if(chromePosition_from==chromePosition_to){
						toolBoxText = "<html><body>loc: " + chromePosition_from
						+ " (1 bp)<br>"
						+ "Chromosome size: " + master.getSelectedChrome().getLength() +" bp"
						+ "</body></htlm>";
					} else{
						toolBoxText = "<html><body>loc: " + chromePosition_from + "-"
						+ chromePosition_to + " ("
						+ (chromePosition_to - chromePosition_from + 1)
						+ "bp)<br>"
						+ "Chromosome size: " + master.getSelectedChrome().getLength() +" bp"
						+ "</body></htlm>";
					}
					this.setToolTipText(toolBoxText);
				} else{
					this.setToolTipText(null);
				}
				
			}
			break;
		default:
			this.setToolTipText("<html><body>Chromosome size "
					+ Long.toString(master.getSelectedChrome().getLength())
					+ "</body></htlm>");
		}
	}

	protected void setBorderCell(int row, int col){
		String name = master.getSpeciesAndChromeName();
		this.setToolTipText(name);
	}
	
	// set end of forward/backward strand
	protected void setEndOfChromeCell() {
		switch (model.getStyle()) {
		case CLASSIC:
			setBackground(Color.BLACK);
			setForeground(Color.BLACK);
			break;
		case MODERN:
			setForeground(Color.WHITE);
			break;
		}
		this.setToolTipText("End Of Chrome");
	}
	
	// set cell within a forward/backward strand where no probe is located
	protected void setGapCell(){
		this.setToolTipText("Gap");
		switch(model.getStyle()){
		case CLASSIC:
			setBackground(Color.DARK_GRAY);
			break;
		case MODERN:
			setBackground(Color.WHITE);
			break;
		}
	}
	
	protected void setProbeCell(CellObject cellObject, int row, int col,
			boolean isSelected) {

	}
	
	public String getWholeToolTipText(CellObject probesInf) {
		String toolBoxText = "";
		if(model.getKindOfChromeView().equals(KindOfChromeView.WHOLE)){
			int cellnumber = probesInf.getCellnumber();
			//int zoommult = this.model.getZoomMultiplikator();
			int chromePosition_from = new GetWholeChromePosition_Delegate().execFirstPos(cellnumber, model);
			int chromePosition_to = new GetWholeChromePosition_Delegate().execFirstPos(cellnumber+1, model) -1;
			
			toolBoxText = "<html><body>Probes: ("+ probesInf.getProbes().size() +") loc: " + chromePosition_from + "-" + chromePosition_to +
			" (" + (chromePosition_to-chromePosition_from+1)+"bp)<br>";
		} else{
			toolBoxText = "<html><body>Probes: ("+ probesInf.getProbes().size() +")<br>";
		}
		
		
		if(probesInf.getProbes().size() > 10){
			Probe pb =  probesInf.getProbes().get(0);
			toolBoxText = toolBoxText + pb.getDisplayName() + " val: " + pb.getValue(model.getActualExperimentNumber()) + "<br>";
			toolBoxText = toolBoxText + " ... <br>";
			int size = probesInf.getProbes().size();
			pb =  probesInf.getProbes().get(size-1);
			toolBoxText = toolBoxText + pb.getDisplayName() + " val: " + pb.getValue(model.getActualExperimentNumber()) + "<br>";
		} else {
			for(Probe p: probesInf.getProbes()){
				toolBoxText = toolBoxText + p.getDisplayName() + " val: " +  p.getValue(model.getActualExperimentNumber()) + "<br>";
			}
		}

		toolBoxText = toolBoxText + "</body></htlm>";
		return toolBoxText;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2_main = (Graphics2D)g.create();
		
		if (paintMe) {
			drawCellcolorByProbelist(g2_main);
		}
		
		if (placeholderCell){
			drawScala(g2_main);
		} else {
			// paint cross onto selected cell if only one of the selected probes
			// is contained in cell
			if (selectedProbes != null) {
				
				drawCellAsSelected(g2_main, Color.WHITE);

				// For searched Probe
				if (model.searchForProbe()) {
					
					drawFoundProbe(g2_main, Color.YELLOW);
				}
				
				selectedProbes = null;
			}
		}
		
		switch (model.getStyle()) {
		case MODERN:
			if(!endOfChrome){
				g2_main.setColor(Color.DARK_GRAY);
				if(minusCell){
					g2_main.drawLine(0, getHeight()-1, this.getWidth(),getHeight()-1);
				} else if(plusCell){
					g2_main.drawLine(0, 0, this.getWidth(),0);
				}
			}
			break;
		default:
			break;
		}
		
		if(model.chromePositionSearched()){
			drawSearchedChromosomePosition(g2_main, Color.YELLOW);
		}
	}
	
	protected void drawCellcolorByProbelist(Graphics2D g2_help) {
		
	}
	
	/**
	 * draw a yellow marker at searched chromosome position.
	 * @param g2_main
	 * @param color 
	 */
	private void drawSearchedChromosomePosition(Graphics2D g2_main, Color color) {
		if(this.cellnumber == model.getSearchedCellnumber()){ 
			g2_main.setColor(color);
			g2_main.setStroke(new BasicStroke(3f));
			
			if(minusCell){
				g2_main.drawLine(0, 0,0, getHeight());
				g2_main.drawLine(0, 0,getWidth()/2, 0);
			} else if(plusCell){
				g2_main.drawLine(0, 0,0, getHeight());
				g2_main.drawLine(0, getHeight()-1,getWidth()/2,getHeight()-1);
			}
		}
	}

	/**
	 * color found probe with color.
	 * @param g2_main
	 */
	private void drawFoundProbe(Graphics2D g2_main, Color col) {
		for (Probe pb : selectedProbes) {
			if (pb.equals(model.getFoundProbe())) {
				g2_main.setColor(col);
				g2_main.fillRect(0, 0, getWidth(), getHeight());
			}
		}
	}

	private void drawCellAsSelected(Graphics2D g2_main, Color color) {
		g2_main.setColor(color);
		for (Probe pb : selectedProbes) {
			if (viewModel.getSelectedProbes().contains(pb)) {
				g2_main.drawLine(0, 0, getWidth(), getHeight());
				g2_main.drawLine(0, getHeight(), getWidth(), 0);
				break;
			}

		}
	}

	private void drawScala(Graphics2D g2_main) {
		if(model.getKindOfChromeView() == KindOfChromeView.WHOLE){
			if (scalaImage != null) {
				int boxSizeX = model.getTableSettings().getBoxSizeX();
				int boxSizeY = model.getTableSettings().getBoxSizeY();
				
				g2_main.drawImage(scalaImage, 0, 0, boxSizeX, boxSizeY,
						(placeHolderColumn) * boxSizeX, 0,
						((placeHolderColumn) * boxSizeX + boxSizeX),
						boxSizeY, this);
			}
		}
	}
	
//	private void paintLine(Graphics2D g2_main){
//		switch (model.getStyle()) {
//		case MODERN:
//			g2_main.setColor(Color.BLACK);
//				if(minusCell){
//					g2_main.drawLine(0, this.getHeight()-1, this.getWidth(), this.getHeight()-1);
//				}else if(plusCell){
//					g2_main.drawLine(0, 0, this.getWidth(), 0);
//				}
//			break;
//		default:
//			break;
//		}
//	}
}
