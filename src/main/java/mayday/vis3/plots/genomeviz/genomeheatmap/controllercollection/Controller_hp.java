package mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfData;
import mayday.vis3.plots.genomeviz.delegates.HelperClass;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.DataMapper;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.SearchOperations;
import mayday.vis3.plots.genomeviz.genomeheatmap.delegates.TableComputations;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.ChromosomeHeader;
import mayday.vis3.plots.genomeviz.genomeoverview.FromToPosition;

public class Controller_hp implements MouseListener{

	protected Controller c;
	protected GenomeHeatMapTableModel model;
//	protected Get_Data_ForMousePos positioner;
	
	public Controller_hp (Controller C, GenomeHeatMapTableModel Model){
		c = C;
		model = Model;
//		positioner = new Get_Data_ForMousePos();
	}

	public void mouseClicked(MouseEvent e) {
		FromToPosition ftp = new FromToPosition();
			Controller_hp.this.getClickedPosition(e,ftp);
			long pos = -1;
			int cellnumber = 0;
			// get the middle position where clicked
			if(ftp.getFrom() >=0. || ftp.getTo() >=0.){
				pos = Math.round((ftp.getTo()-ftp.getFrom())/2) +ftp.getFrom();
				
				if(model.getKindOfData().equals(KindOfData.STANDARD)){
					cellnumber = TableComputations.computeCellnumberOfChromePosition((int) pos, model.getZoomMultiplikator(), (int)model.getSkipValue());
					
					SearchOperations.searchCellnumber(cellnumber, model,this.c.table);
				} else {
					this.c.jumpToData(pos);
				}
			}
	}

	public void mouseEntered(MouseEvent arg0) {

	}

	public void mouseExited(MouseEvent arg0) {

	}

	public void mousePressed(MouseEvent arg0) {

	}

	public void mouseReleased(MouseEvent arg0) {

	}
	
	protected void getClickedPosition(MouseEvent e, FromToPosition ftp){
		
		ChromosomeHeader chp = (ChromosomeHeader)e.getSource();
		int width_headerpanel = chp.getWidth();
		int width_headerpanel_reduced = HelperClass.getWidthReduced(width_headerpanel, chp.getLeft_margin(), chp.getRight_margin());

		double mousePos_x_inPaintingArea = HelperClass.getTranslatedX(width_headerpanel, chp.getLeft_margin(), chp.getRight_margin(), e.getX());
//			new Mapper().translateXPosition(e.getX(), chp.getLeft_margin(), chp.getRight_margin(), width_headerpanel);
		if(mousePos_x_inPaintingArea>=0)DataMapper.getDataForMousePos(width_headerpanel_reduced, model, mousePos_x_inPaintingArea,ftp);
	}
}
