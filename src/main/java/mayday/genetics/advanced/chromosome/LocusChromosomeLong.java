package mayday.genetics.advanced.chromosome;

import java.util.LinkedList;

import mayday.core.structures.natives.mmap.MMLongArray;
import mayday.genetics.basic.ChromosomeFactory;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBNode;

public class LocusChromosomeLong extends AbstractLocusChromosome<LocusGeneticCoordinateLong> {

	protected MMLongArray values;
	protected long maximalValue;
	protected long minimalValue;

	LocusChromosomeLong(Species organism, String id, long length) {
		super(organism, id, length);
		values = new MMLongArray(100);
//		System.out.println("LCL values: "+values.id);
	}

	public long addLocus(long startposition, long endposition, Strand strand, long o) {		
		long key = super.addLocus(startposition, endposition, strand);
		addObject(o, key);
		return key;
	}
	
	protected void addObject(long o, long key) {
		values.add(o);
		maximalValue = maximalValue>o?maximalValue:o;
		minimalValue = minimalValue<o?minimalValue:o;
		while (values.size()<strands.size())
			values.add(0); // fill to same size
	}

	public long getValue(long i) {
		return values.get(i);
	}

	public static class Factory implements ChromosomeFactory {
		public Chromosome createChromosome(Species s, String id, long length) {
			return new LocusChromosomeLong(s,id,length);
		}  
		public Class<? extends Chromosome> getChromosomeClass() {
			return LocusChromosomeLong.class;
		}   
	}

	protected LocusGeneticCoordinateLong makeCoordinate(long index) {
		return new LocusGeneticCoordinateLong(this, index);
	}

	public long addLocus(GBNode model, long o) {
		long key = super.addLocus(model);
		addObject(o, key);
		return key;
	}

	@Override
	public long addLocus(GBNode model, LocusGeneticCoordinateLong coord) {
		return addLocus(model, coord.getValue());
	}
	
	public long addLocus(long startposition, long endposition, Strand strand,
			LocusGeneticCoordinateLong coord) {
		return addLocus(startposition, endposition, strand, coord.getValue());
	}

	/** reduce the size of this chromosome by choosing a representation of coordinates with fewer bytes if possible */
	public void compact() {		
		super.compact();
		if (minimalValue>=0)
			values = values.changeStorageRange(maximalValue,true);
	}
	
//	@Override  DumpableStructure is implemented by AbstractLocusChromosome
//	public void readDump(DataInputStream dis) throws IOException {
//		maximalValue = dis.readLong();
//		minimalValue = dis.readLong();
//		values.readDump(dis);
//		super.readDump(dis);		
//	}
//
//	@Override
//	public void writeDump(DataOutputStream dos) throws IOException {
//		dos.writeLong(maximalValue);
//		dos.writeLong(minimalValue);
//		values.writeDump(dos);		
//		super.writeDump(dos);
//	}
	
	@Override
	public String getCompactionInitializer() {
		return minimalValue+"\t"+maximalValue+"\t"+super.getCompactionInitializer();
	}

	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		minimalValue = Long.parseLong(compactionInitializer.removeFirst());
		maximalValue = Long.parseLong(compactionInitializer.removeFirst());
		super.setCompaction(compactionInitializer);
		compact();
	}
	

}
