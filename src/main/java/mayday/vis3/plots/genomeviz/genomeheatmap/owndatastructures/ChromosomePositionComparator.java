package mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures;

import java.util.Comparator;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.genetics.LocusMIO;

public class ChromosomePositionComparator {
 
	protected MIGroup miGroup;
	
	ChromosomePositionComparator(MIGroup miGroup){
		this.miGroup = miGroup;
	}
	
	Comparator<Probe> persComp = new Comparator<Probe>() {

		public int compare(Probe o1, Probe o2) {

			long posFromO1 = getStartPosition(o1, miGroup);
			long posFrom02 = getStartPosition(o2, miGroup);

			// Returns a negative integer, zero, or a positive integer as
			// the first argument is
			// less than, equal to, or greater than the second.
			if (posFromO1 > posFrom02)
				return 1;
			else if (posFromO1 < posFrom02)
				return -1;
			else
				return 0;

		}
	};
	
	public long getStartPosition(Probe probe, MIGroup miGroup) {
		return ((LocusMIO) miGroup.getMIO(probe)).getValue().getCoordinate()
				.getFrom();
	}
}
