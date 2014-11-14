/*
 * Created on 10.06.2005
 */
package mayday.genetics.basic;

public class Species 
implements Comparable<Species>
{
    private String name;
    
    /**
     * A species identifier is normally a
     * family name and an species name like in
     * <i>Homo sapiens</i>.
     * 
     * @param name
     */
    Species(String name) //must be package private
    {
        this.name = name;
    }

    /**
     * 
     */
    Species() //must be package private
    {
        this(null);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean equals(Object o)
    {
        if(!(o instanceof Species))
            throw new ClassCastException("Cannot compare to an instance of "+o.getClass().getName());

        return this.compareTo((Species)o)==0;
    }

    public String toString()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(Species o)
    {
        return this.name.compareToIgnoreCase(o.name);
    }
}
