package mayday.vis3.plots.genomeviz.genomeorganisation;

import java.util.Iterator;
import java.util.List;

import mayday.core.Probe;
import mayday.core.structures.natives.LinkedLongArray;
import mayday.core.structures.natives.LinkedObjectArray;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeFactory;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;

/**
 * Chrome stores probes aligned to the genome. Three access methods are given
 * 1) Access by index
 * 2) Access by genomic position
 * 3) Access to all probes (unsorted)
 * @author battke
 *
 */
public class Chrome extends LocusChromosomeObject<Probe> {

	protected LinkedLongArray coveredPos;
	
	Chrome(Species organism, String id, long length) {
		super(organism, id, length);		
	}

	public List<LocusGeneticCoordinateObject<Probe>> get(long position, Strand strand) {
		return getOverlappingLoci(position, strand);
	}
	
	public List<LocusGeneticCoordinateObject<Probe>> get(long start, long end, Strand strand) {
		return getOverlappingLoci(start, end, strand);
	}
	
	public LinkedObjectArray<Probe> getAllProbes() {
		return objects;
	}
	
	public LinkedLongArray getCoveredPositions() {
		if (coveredPos==null) {
			coveredPos = new LinkedLongArray(1000);
			Iterator<Long> gki = this.iterateAllCoveredPositions();
			while(gki.hasNext()){
				coveredPos.add(gki.next());
			}
		}			
		return coveredPos;
	}

	public static class Factory implements ChromosomeFactory {
		public Chromosome createChromosome(Species s, String id, long length) {
			return new Chrome(s,id,length);
		}    	
		public Class<? extends Chromosome> getChromosomeClass() {
			return Chrome.class;
		}   
	}
	
}
