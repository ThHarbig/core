package mayday.genetics.advanced.chromosome;

import mayday.genetics.basic.ChromosomeFactory;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBNode;

public class LocusChromosome extends AbstractLocusChromosome<LocusGeneticCoordinate> {

	public LocusChromosome(Species organism, String id, long length) {
		super(organism, id, length);
    }

	protected LocusGeneticCoordinate makeCoordinate(long index) {
		return new LocusGeneticCoordinate(this, index);
	}

    public static class Factory implements ChromosomeFactory {
		public Chromosome createChromosome(Species s, String id, long length) {
			return new LocusChromosome(s,id,length);
		}   
		public Class<? extends Chromosome> getChromosomeClass() {
			return LocusChromosome.class;
		}   
    }
    
	@Override
	public long addLocus(GBNode model, LocusGeneticCoordinate coord) {
		return super.addLocus(model);
	}

	@Override
	public long addLocus(long startposition, long endposition, Strand strand,
			LocusGeneticCoordinate coord) {
		return addLocus(startposition, endposition, strand);
	}
	
	public long addLocus(long startposition, long endposition, Strand strand) {
		return super.addLocus(startposition, endposition, strand);
	}
	
	public long addLocus(GBNode model) {
		return super.addLocus(model);
	}

  
}
