package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

import java.util.Collections;
import java.util.List;

import mayday.core.Probe;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.KindOfChromeView;
import mayday.vis3.plots.genomeviz.genomeheatmap.GenomeHeatMapTableModel;
import mayday.vis3.plots.genomeviz.genomeorganisation.ChromosomeDataSet;

public class FindNearestProbe_Delegate {
	
	public static Probe findNearestProbe(long chromePosition, GenomeHeatMapTableModel model){
		ChromosomeDataSet cds = model.getActualChromeData();
		List<LocusGeneticCoordinateObject<Probe>> lolgcp = Collections.emptyList();
		long len = cds.getViewLength(KindOfChromeView.WHOLE);

		for (long dist = 0;; ++dist) {
			if (chromePosition-dist>0)
				lolgcp = cds.getProbes(chromePosition+dist);
			if (lolgcp.size()==0 && chromePosition+dist < len)
				lolgcp = cds.getProbes(chromePosition-dist);
			if (lolgcp.size()>0) {
				return lolgcp.get(0).getObject();
			}
			if (chromePosition-dist<1 && chromePosition+dist>len)
				break;
		}
		
		return null;
	}
}
