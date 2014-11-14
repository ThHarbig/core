package mayday.genetics.advanced.chromosome;

import mayday.core.structures.natives.LinkedDoubleArray;
import mayday.genetics.basic.ChromosomeFactory;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBNode;

public class LocusChromosomeDouble extends AbstractLocusChromosome<LocusGeneticCoordinateDouble> {

	protected LinkedDoubleArray values;

	LocusChromosomeDouble(Species organism, String id, long length) {
		super(organism, id, length);
		values = new LinkedDoubleArray(100);
	}

	public long addLocus(long startposition, long endposition, Strand strand, double o) {		
		long key = super.addLocus(startposition, endposition, strand);
		addObject(o, key);
		return key;
	}	
	
	protected void addObject(double o, long key) {
		values.add(o);
		while (values.size()<strands.size())
			values.add(Double.NaN);
	}

	public double getValue(long i) {
		return values.get(i);
	}

	public static class Factory implements ChromosomeFactory {
		public Chromosome createChromosome(Species s, String id, long length) {
			return new LocusChromosomeDouble(s,id,length);
		}  
		public Class<? extends Chromosome> getChromosomeClass() {
			return LocusChromosomeDouble.class;
		}   
	}

	protected LocusGeneticCoordinateDouble makeCoordinate(long index) {
		return new LocusGeneticCoordinateDouble(this, index);
	}

	public long addLocus(GBNode model, double o) {
		long key = super.addLocus(model);
		addObject(o, key);
		return key;
	}

	@Override
	public long addLocus(GBNode model, LocusGeneticCoordinateDouble coord) {
		return addLocus(model, coord.getValue());
	}
	
	public long addLocus(long startposition, long endposition, Strand strand,
			LocusGeneticCoordinateDouble coord) {
		return addLocus(startposition, endposition, strand, coord.getValue());
	}

}
