/*
 * Created on 14.07.2005
 */
package mayday.genetics.basic;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

import mayday.core.structures.CompactableStructure;
import mayday.genetics.advanced.chromosome.AbstractLocusChromosome;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.chromosome.SimpleChromosome;

/**
 * @author Matthias Zschunke
 * @version 0.2
 * Created on 14.07.2005
 * Changed on 30.06.2009 Florian Battke
 * 
 * A ChromosomeSetContainer maps Species to their ChromosomeSets. This is the top-level class of the mayday.genetics structures 
 *
 */
public class ChromosomeSetContainer implements CompactableStructure {
	
	private static WeakHashMap<ChromosomeSetContainer,Object> allCSCs = new WeakHashMap<ChromosomeSetContainer, Object>();

	private TreeMap<Species, ChromosomeSet> map = new TreeMap<Species, ChromosomeSet>();
	private ChromosomeFactory chromosomeFactory;
	private ChromosomeSetFactory chromosomeSetFactory;

	private static ChromosomeSetContainer defaultContainer = new ChromosomeSetContainer();

	public static ChromosomeSetContainer getDefault() {
		return defaultContainer;
	}
	
	public static Collection<ChromosomeSetContainer> getAll() {
		System.gc(); // purge old links from Weak hash map
		List<ChromosomeSetContainer> ret = new LinkedList<ChromosomeSetContainer>();
		ret.addAll(allCSCs.keySet());
		ret.remove(getDefault());
		ret.add(0, getDefault());
		return ret;
	}
	
	public ChromosomeSetContainer(ChromosomeFactory factory, ChromosomeSetFactory setfactory) {
		this.chromosomeFactory=factory;
		this.chromosomeSetFactory=setfactory;
		allCSCs.put(this, null);
	}
	
	public ChromosomeSetContainer(ChromosomeFactory factory) {
		this(factory, new ChromosomeSet.Factory());
	}
	
	public ChromosomeSetContainer(ChromosomeSetFactory setFactory) {
		this(new SimpleChromosome.Factory(), setFactory);
	}
	
	public ChromosomeSetContainer() {
		this(new SimpleChromosome.Factory(), new ChromosomeSet.Factory());
	}
	
	/** Creates a new CSC with the same factories as the template */
	public ChromosomeSetContainer(ChromosomeSetContainer template) {
		this(template.getChromosomeFactory(), template.getChromosomeSetFactory());		
	}
	
	
	public ChromosomeFactory getChromosomeFactory() {
		return chromosomeFactory;
	}
	
	public ChromosomeSetFactory getChromosomeSetFactory() {
		return chromosomeSetFactory;
	}
	
	
	public Chromosome getChromosome(Species s, String name) {
		return getChromosome(s, name, -1);
	}
	
	public Chromosome getChromosomeOrNull(Species s, String name) {
		ChromosomeSet chromeset = getChromosomes(s);		
		Chromosome chrome = chromeset.get(name);
		return chrome;
	}
	
	/**
	 * Get a specific Chromosome by species and name. If such a
	 * Chromosome does not exist, it will be created. Note that
	 * the initial length of a chromosome is -1.
	 *  
	 * @param s
	 * @param name
	 * @param len the length for the chromosome in case it needs to be created
	 * @return
	 */
	public Chromosome getChromosome( Species s, String name, long length )
	{
		ChromosomeSet chromeset = getChromosomes(s);		

		Chromosome chrome = chromeset.get(name);
		if(chrome==null)  {
			chromeset.put(name, chrome = chromosomeFactory.createChromosome(s,name,length));
		}

		return chrome;
	}
	
	/**
	 * Get a specific Chromosome with the same species and name as the input chromosome
	 *  
	 * @param chrome
	 * @return
	 */
	public Chromosome getChromosome( Chromosome chrome )
	{
		ChromosomeSet chromeset = getChromosomes(chrome.getSpecies());		

		Chromosome c = chromeset.get(chrome.getId());
		if(c==null) 
			chromeset.put(
					chrome.getId(), 
					c = chromosomeFactory.createChromosome(chrome.getSpecies(),chrome.getId(),chrome.getLength()));

		return c;
	}

	/**
	 * Get all chromosomes of a given species.
	 * 
	 * @param s
	 * @return an array of Chromosomes sorted w.r.t to the natural order of the Chromosomes.
	 * 
	 * @see SimpleChromosome#compareTo(Object)
	 */
	public ChromosomeSet getChromosomes(Species s)
	{
		ChromosomeSet cs = map.get(s);
		if (cs==null)
			map.put(s,cs = chromosomeSetFactory.createChromosomeSet(s));
		return cs;
	}
	
	public Collection<Chromosome> getAllChromosomes() {
		LinkedList<Chromosome> ret = new LinkedList<Chromosome>();
		for (ChromosomeSet cs : map.values())
			ret.addAll(cs.getAllChromosomes());
		return ret;
	}

	public boolean containsKey(Species s) {
		return map.containsKey(s);
	}

	public boolean containsChromosome(Species s, String chromosome) {
		return map.containsKey(s) && map.get(s).containsKey(chromosome);
	}

	public Set<Species> keySet() {
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getCompactionInitializer() {
		// we need a list of chromosomes as well as their compaction statistics
		Collection<Chromosome> cc = getAllChromosomes();
		String res = Integer.toString(cc.size());
		for (Chromosome lcl : cc) {
			if (lcl instanceof AbstractLocusChromosome)
				res+="\t"+lcl.getSpecies().getName()+"\t"+lcl.getId()+"\t"+((AbstractLocusChromosome)lcl).getCompactionInitializer();
			else 
				throw new RuntimeException("Can only compact structures based on AbstractLocusChromosome");
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setCompaction(LinkedList<String> compactionInitializer) {
		int chromeCount = Integer.parseInt(compactionInitializer.removeFirst());
		for (int i=0; i!=chromeCount; ++i) {
			String spec = compactionInitializer.removeFirst();
			String chrid = compactionInitializer.removeFirst();
			Chromosome cc = getChromosome(SpeciesContainer.getSpecies(spec), chrid);
			if (cc instanceof AbstractLocusChromosome)
				((AbstractLocusChromosome)cc).setCompaction(compactionInitializer);
			else 
				throw new RuntimeException("Can only compact structures based on AbstractLocusChromosome");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void compact() {
		for (Chromosome cc : getAllChromosomes())
			if (cc instanceof AbstractLocusChromosome)
				((AbstractLocusChromosome)cc).compact();
			else 
				throw new RuntimeException("Can only compact structures based on AbstractLocusChromosome");
	}

}
