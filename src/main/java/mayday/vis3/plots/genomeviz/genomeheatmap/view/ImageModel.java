package mayday.vis3.plots.genomeviz.genomeheatmap.view;

import java.util.HashMap;

import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.ScaleImageModel;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.BackwardPanel;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.ForwardPanel;
import mayday.vis3.plots.genomeviz.genomeheatmap.view.images.ScalaBufferedImage;

public class ImageModel {
 
	protected GenomeHeatMapTableModel tbl_model;
	
	protected ForwardPanel forwardPanel;
	protected BackwardPanel backwardPanel;
	protected HashMap<Integer,ScalaBufferedImage> allImages;	// contains all scala images for later use
	protected ScalaBufferedImage image;
	protected int smaller;
	protected ScaleImageModel si_model;
	
	
	public ImageModel (GenomeHeatMapTableModel Model){
		this.tbl_model = Model;
		this.forwardPanel = new ForwardPanel();
		this.backwardPanel = new BackwardPanel();
		allImages = new HashMap<Integer,ScalaBufferedImage>();
		si_model = new ScaleImageModel(tbl_model);
	}

	public void setSizes(){
		int x = tbl_model.getTableSettings().getBoxSizeX();
		int y = tbl_model.getTableSettings().getBoxSizeY();
		smaller = -1;
		
		if(x < y){
			smaller = x;
		} else if(x > y){
			smaller = y;
		} else{
			smaller = x;
		}
	}
	/**
	 * returns depending on rowNumber the right ScalaBufferedImage.
	 * @param row
	 * @return
	 */
	public ScalaBufferedImage getBufferedImage(int row){

		if(allImages.containsKey(row)){
			return allImages.get(row);
		}
		else return null;
	}
	
	/**
	 * set the scala image for placeholder row.
	 * @param row
	 * @param scalaImage
	 */
	public void setBufferedImage(int row, ScalaBufferedImage scalaImage){
		allImages.put(row, scalaImage);
	}
	
	public void setBufferedImage(ScalaBufferedImage scalaImage) {
		image = scalaImage;
	}

	/**
	 * clear all old bufferedImages.
	 */
	public void clearBufferedImages(){

		allImages.clear();
	}

	//#############################################################
	/*
	 * Getting panel for forward and Backward strand
	 * 
	 */
	//#############################################################
	public ForwardPanel getForwardPanel(){
		return forwardPanel;
	}
	
	public BackwardPanel getBackwardPanel(){
		return backwardPanel;
	}

	public int getSmaller() {
		return smaller;
	}

	public ScaleImageModel getSi_model() {
		return si_model;
	}

	public ScalaBufferedImage getBufferedImage() {
		return this.image;
	}

	public void updateScalaTicks(){
		si_model.updateScalaTicks();
	}
}
