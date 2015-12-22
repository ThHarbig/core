package mayday.genetics.basic.chromosome;

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
public interface Chromosome extends Comparable<Chromosome>
{
	public String getId();
    void setId(String id);
    
    public long getLength();
    void setLength(long length);
    
    /**
     * Update the length of the chromosome if the given length is larger,
     * i.e. the length can only increase over time.
     * 
     * @param length
     */
    public void updateLength(long length);

    public Species getSpecies();
    void setSpecies(Species species);

    public int compareToBySize(Chromosome cmp);
}
