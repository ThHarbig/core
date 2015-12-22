package mayday.vis3.plots.genomeviz.genomeoverview.delegates;

import java.awt.Rectangle;
import java.util.Set;

import javax.swing.SwingUtilities;

import mayday.core.Probe;
import mayday.genetics.basic.Strand;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.Track;


public abstract class SearchProbe {
	
	protected static Probe findInSet(Set<Probe> set, String identifier) {
		Probe searchedProbe = null;
		for(Probe pb: set){
			if(pb.getName().equals(identifier) || pb.getDisplayName().equals(identifier)){
				searchedProbe = pb;
				break;
			}
		}
		return searchedProbe;
	}
	
	public static Probe getSearchedProbe(String identifier, GenomeOverviewModel model){
		Probe pb = findInSet(model.getData().getForwardProbesetChromosome(), identifier);
		if (pb==null) {
			pb = findInSet(model.getData().getBackwardProbesetChromosome(), identifier);
		}
		return pb;		
	}
	 
	public static boolean searchTracksAndScroll(final GenomeOverviewModel model, Controller c){
		
		boolean found = false;
		if(model.getSelectionModel().getFoundProbe()!=null && model.getSelectionModel().getPosOfFoundProbe() != null){
			Strand strand = model.getStrandOfProbe(model.getSelectionModel().getFoundProbe());
			if(strand!=null){
				for(Integer key : model.getPanelPositioner().getTracks().keySet()){
					if(model.getPanelPositioner().getTracks().get(key) instanceof Track){
						Track tp = (Track) model.getPanelPositioner().getTracks().get(key);
						if(!Track.isScaleTrack(tp)){
							if(tp.getTrackPlugin().getTrackSettings().getStrand().equals(strand)
									|| tp.getTrackPlugin().getTrackSettings().getStrand().equals(Strand.BOTH)){
								found = true;
								tp.paintRange(model.getSelectionModel().getPosOfFoundProbe());
							}
						}
					}
				}
			}
		}
		
		if(found){
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					Rectangle rect = model.getScrollPane().getVisibleRect();
					double width = rect.getWidth();

					int[] list = model.getSelectionModel().getPosOfFoundProbe();
					int first = list[0] + model.getLocation_paintingpanel_X();
					
					Rectangle aRect	= new Rectangle();
					aRect.x = first - (int)Math.round(width/2.);
					aRect.width = (int)width;
					model.scrollViewToRect(aRect);
				}
			});
		}
		return found;
	}
}
