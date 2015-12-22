package mayday.vis3.plots.genomeviz.genomeoverview;

import java.awt.Color;
import java.util.Collections;
import java.util.Set;

import javax.swing.BorderFactory;

import mayday.core.Probe;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.EnumManagerGO.Zoom;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.ITrack;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;

public class SelectionsModel {

	protected Set<Probe> selectedProbes = Collections.emptySet();
	protected ITrack selectedTrack = null;
	protected FromToPosition ftp = null;

	
	protected long needed_bp = 0;
	
	protected Double visiblePos_low_new = null;
	protected Double visiblePos_high_new = null;
	protected Zoom zoom = null;
	
	protected GenomeOverviewModel chromeOverviewModel = null;
	private double selectedXPos_first = -1;
	private double selectedXPos_last = -1;
	
	private boolean drawFoundProbe = false;
	private Probe foundProbe = null;
	private int[] posOfFoundProbe = null;
	private Strand foundProbe_strand = null;

	public SelectionsModel(GenomeOverviewModel chromeOverviewModel){
		this.chromeOverviewModel = chromeOverviewModel;
	}

	public Set<Probe> getSelectedProbes() {
		return selectedProbes;
	}

	public void setSelectedProbes(Set<Probe> selectedSet) {
		if(this.selectedProbes.isEmpty()){
			this.selectedProbes = selectedSet;
		} else {
			selectedProbes.addAll(selectedSet);
		}
	}
	
	public void clearSelectedProbes() {
		selectedProbes = Collections.emptySet();
	}

	public ITrack getSelectedTrack() {
		return selectedTrack;
	}

	public void setSelectedTrack(ITrack selectedTrack) {
		if(this.selectedTrack!= null){
			this.selectedTrack.setBorder(null);
			this.selectedTrack = null;
		}
		
		if(selectedTrack!=null){
			this.selectedTrack = selectedTrack;
			this.selectedTrack.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
		}
	}

	
	//###################################################
	//
	//	visible range in chromosome header
	//
	//###################################################
	
	public void setWantedVisiblePositions(double visLowPos_new,
			double visHighPos_new) {
		
		if(visLowPos_new <= 0){
			visiblePos_low_new = 1.;
			System.err.println("SelectionsModel: New visible setted positions not valid");
		} else{
			visiblePos_low_new = visLowPos_new;
		}
		
		long endPosOfChrome = chromeOverviewModel.getChromosomeEnd();
		
		if(visHighPos_new > endPosOfChrome){
			visiblePos_high_new = (double)endPosOfChrome;
			System.err.println("SelectionsModel: New visible setted positions not valid");
		}else{
			visiblePos_high_new = visHighPos_new;
		}
	}

	public double getWantedVisiblePosition_low() {
		return this.visiblePos_low_new;
	}
	
	public double getWantedVisiblePosition_high() {
		return this.visiblePos_high_new;
	}
	
	public void setZooming(Zoom Zoom) {
		zoom = Zoom;
	}

	public long getNeeded_bp() {
		return needed_bp;
	}

	public void setNeeded_bp(long needed_bp) {
		this.needed_bp = needed_bp;
	}

	public void setNewLabel(String text, boolean addInfo) {

		String label = "";
		if(selectedTrack != null && selectedTrack instanceof Track){
			Track tp = (Track)selectedTrack;
			if(addInfo){
				label= "test " + ": "+ text;
				//label= tp.getColorProvider().getStringForLabel() + ": "+ text;
				tp.setNewLabel(label);
			} else {
				tp.setNewLabel(text);
			}
		}
	}
	
	public void setNewLabel() {

		String label = "";
		if (selectedTrack != null
				&& selectedTrack instanceof Track) {
			Track tp = (Track) selectedTrack;
			label = tp.getTrackPlugin().getTrackSettings().getTrackLabel();
			tp.setNewLabel(label);
		}
	}

	public void setSelectedPositon_first(double posX) {
		selectedXPos_first  = posX;
	}
	
	public void setSelectedPositon_last(double posX) {
		selectedXPos_last  = posX;
	}

	public double getSelectedXPos_first() {
		return selectedXPos_first;
	}

	public double getSelectedXPos_last() {
		return selectedXPos_last;
	}

	public void setFoundProbe(Probe pb_searched, int[] positions_x) {
		drawFoundProbe = true;
		foundProbe = pb_searched;
		if(positions_x == null){
			posOfFoundProbe = null;
		} else {
			posOfFoundProbe = positions_x;
		}
		foundProbe_strand  = chromeOverviewModel.getStrandOfProbe(foundProbe);
	}

	public Probe getFoundProbe() {
		return foundProbe;
	}

	public int[] getPosOfFoundProbe() {
		return posOfFoundProbe;
	}

	public boolean isDrawFoundProbe() {
		return drawFoundProbe;
	}

	public void setDrawFoundProbe(boolean drawFoundProbe) {
		this.drawFoundProbe = drawFoundProbe;
	}

	public Strand getFoundProbe_strand() {
		return foundProbe_strand;
	}

	public void setFoundProbe_strand(Strand foundProbe_strand) {
		this.foundProbe_strand = foundProbe_strand;
	}

	public void setFoundProbe(Probe foundProbe) {
		this.foundProbe = foundProbe;
	}
}
