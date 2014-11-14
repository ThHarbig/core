package mayday.genetics.sequences;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import mayday.genetics.ChromosomeBasedContainer;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.chromosome.Chromosome;

public class SequenceContainer extends ChromosomeBasedContainer<ChromosomeSequence> {
	
	public final static ChromosomeSequence UNKNOWN_SEQUENCE = new ChromosomeSequence() {
		
		@Override
		public CharSequence subSequence(int start, int end) {
			return this; 
		}
		
		@Override
		public int length() {
			return Integer.MAX_VALUE;
		}
		
		@Override
		public char charAt(int index) {
			return '\u2666';
		}
		
		public String toString() {
			return "-- no sequence data --";
		}

		@Override
		public String getChromosomeName() {
			return "-- unknown chromosome --";
		}

		@Override
		public String getSpeciesName() {
			return "-- unknown species --";
		}
	};
	
	private static WeakHashMap<SequenceContainer,Object> allSCs = new WeakHashMap<SequenceContainer, Object>();
	private static SequenceContainer defaultContainer = new SequenceContainer(ChromosomeSetContainer.getDefault());

	public static SequenceContainer getDefault() {
		return defaultContainer;
	}
	
	protected ChromosomeSequence getUnknown() {
		return UNKNOWN_SEQUENCE;
	}
	
	public static Collection<SequenceContainer> getAll() {
		System.gc(); // purge old links from Weak hash map
		List<SequenceContainer> ret = new LinkedList<SequenceContainer>();
		ret.addAll(allSCs.keySet());
		ret.remove(getDefault());
		ret.add(0, getDefault());
		return ret;
	}
	
	public SequenceContainer(ChromosomeSetContainer csc) {
		super(csc);
		allSCs.put(this, null);
	}

	public ChromosomeSequence getSequence(Chromosome c) {
		return super.get(c);
	}
	
	public ChromosomeSequence getSequence(String species, String chrome) {
		return super.get(species, chrome);
	}
	
	public void addSequence(Chromosome c, ChromosomeSequence s) {		
		c.updateLength(s.length());
		super.add(c,s);
	}
	
	public void addSequence(ChromosomeSequence s) {
		Chromosome c = csc.getChromosome(SpeciesContainer.getSpecies(s.getSpeciesName()), s.getChromosomeName());
		addSequence(c, s);
	}
	
	
	
}
