package mayday.genetics.advanced.chromosome;

import mayday.core.structures.natives.LinkedObjectArray;
import mayday.genetics.basic.ChromosomeFactory;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBNode;

/**
 * Contains a chromosome on which loci are located. Each locus can be connected to an object
 * @author battke
 * In addition to the Species, ID and length, 
 * LocusChromosome holds a number of loci via the ChromosomeArray class 
 */
public class LocusChromosomeObject<ObjectType> 
extends AbstractLocusChromosome<LocusGeneticCoordinateObject<ObjectType>> {

	protected LinkedObjectArray<ObjectType> objects;

	protected LocusChromosomeObject(Species organism, String id, long length) {
		super(organism, id, length);
		objects = new LinkedObjectArray<ObjectType>(100);
	}

	public long addLocus(long startposition, long endposition, Strand strand, ObjectType o) {		
		long key = super.addLocus(startposition, endposition, strand);		
		addObject(o, key);
		return key;
	}
	
	protected void addObject(ObjectType o, long key) {
		objects.add(o);
		while (objects.size()<strands.size())
			objects.add(null);
	}

	public ObjectType getObject(long i) {
		return objects.get(i);
	}
	
	public LinkedObjectArray<ObjectType> getObjects() {
		return objects;
	}

	public static class Factory<T> implements ChromosomeFactory {
		public Chromosome createChromosome(Species s, String id, long length) {
			return new LocusChromosomeObject<T>(s,id,length);
		}    	
		public Class<? extends Chromosome> getChromosomeClass() {
			return LocusChromosomeObject.class;
		}   
	}

	protected LocusGeneticCoordinateObject<ObjectType> makeCoordinate(long index) {
		return new LocusGeneticCoordinateObject<ObjectType>(this, index);
	}
	
	public long addLocus(GBNode model, ObjectType o) {
		long key = super.addLocus(model);
		addObject(o, key);
		return key;
	}

	@Override
	public long addLocus(GBNode model, LocusGeneticCoordinateObject<ObjectType> coord) {
		return addLocus(model, coord.getObject());
	}
	
	public long addLocus(long startposition, long endposition, Strand strand,
			LocusGeneticCoordinateObject<ObjectType> coord) {
		return addLocus(startposition, endposition, strand, coord.getObject());
	}


}
