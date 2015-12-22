/*
 * Created on 15.11.2005
 */
package mayday.genetics.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;

import mayday.genetics.basic.chromosome.Chromosome;

/**
 * @author Matthias Zschunke
 * @version 0.2
 * Created on 15.11.2005

 * Changed 30.06.2009 Florian Battke
 * 
 * A ChromosomeSet holds a number of Chromosomes, i.e. all Chromosomes of one Species.
 *
 */
@SuppressWarnings("serial")
public class ChromosomeSet extends TreeMap<String, Chromosome> implements Comparable<ChromosomeSet>
{

	private static Comparator<String> ignoreCaseComparator = new Comparator<String>(){
		public int compare(String o1, String o2) {
			return o1.compareToIgnoreCase(o2);
		}

	};

	protected Species species;

	protected ChromosomeSet(Species s) {
		super(ignoreCaseComparator);
		this.species = s;
	}

	@Override
	public Chromosome get(Object name) {
		Chromosome chrome = super.get(name);
    	// check if hashmap is out of date
    	if (chrome!=null && !chrome.getId().equals(name)) {
    		rehash();
    		chrome = super.get(name);
    	}
    	
		return chrome;
	}
	
    
    protected void rehash() {
    	ArrayList<Chromosome> tmp = new ArrayList<Chromosome>();
    	tmp.addAll(values());
    	clear();
    	for (Chromosome c: tmp)
    		put(c.getId(), c);
    }

	public Collection<Chromosome> getAllChromosomes() {
		return values();
	}

	public Set<String> getAllChromosomeNames() {
        return keySet();
    }
    
    public String getFirstChromosomeName(){
    	return firstKey();
    }
    
    public Chromosome getFirstChromosome(){
    	return firstEntry().getValue();
    }
	
	public int compareTo(ChromosomeSet o)	{		 
		 return this.species.compareTo(o.species);
	}
	
    public static class Factory implements ChromosomeSetFactory {
		public ChromosomeSet createChromosomeSet(Species s) {
			return new ChromosomeSet(s);
		}    	
    }
}
