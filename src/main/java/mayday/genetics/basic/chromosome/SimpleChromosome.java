package mayday.genetics.basic.chromosome;

import mayday.genetics.basic.ChromosomeFactory;
import mayday.genetics.basic.Species;


/**
 * The representation of a Chromosome:
 * A Chromosome has a name and a Species it belongs to
 * A Chromosome is usually part of a ChromosomeSet
 * 
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 13.07.2005
 *
 */
public class SimpleChromosome implements Chromosome
{
    private Species species;
    private String id;
    private long length = -1;
    
    /**
     * @param species
     * @param id
     * @param width
     */
    protected SimpleChromosome(Species organism, String id, long length)
    {
        this.species = organism;
        this.id = id;
        this.length = length;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
    
    /**
     * Update the length of the chromosome if the given length is larger,
     * i.e. the length can only increase over time.
     * 
     * @param length
     */
    public void updateLength(long length) {
        if(length>this.length) this.length=length;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public int compareTo(Chromosome cmp)
    {
        int result = 0;
        if(this.species!=null && cmp.getSpecies()!=null) {
            result= this.species.compareTo(cmp.getSpecies());
        }
        if(result!=0) return result;
        
        return this.id.compareToIgnoreCase(cmp.getId());
    }
    
    public int hashCode() {
    	int hc =  species.getName().hashCode()*31 + id.hashCode();
    	return hc;
    }
    
    public boolean equals(Object o)
    {
        return (o instanceof Chromosome) && this.compareTo((Chromosome)o)==0;
    }
    
    public String toString()
    {
        return species.toString() +": Chr. "+ id; 
    }
    
    
    public static class Factory implements ChromosomeFactory {
		public Chromosome createChromosome(Species s, String id, long length) {
			return new SimpleChromosome(s,id,length);
		}

		public Class<? extends Chromosome> getChromosomeClass() {
			return SimpleChromosome.class;
		}    	
    }
    
    public int compareToBySize(Chromosome cmp) {
    	int result = 0;
        if(this.species!=null && cmp.getSpecies()!=null) {
            result= this.species.compareTo(cmp.getSpecies());
        }
        if(result!=0) return result;
        
        if (cmp.getLength()==-1 || getLength()==-1)
        	return this.id.compareToIgnoreCase(cmp.getId());
        
        return Long.valueOf(getLength()).compareTo(cmp.getLength());
    }
    
}
