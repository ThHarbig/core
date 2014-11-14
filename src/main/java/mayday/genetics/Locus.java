/*
 * Created on 10.06.2005
 */
package mayday.genetics;

import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;






/**

 * @author Matthias Zschunke
 * @version 0.1
 * Created on 10.06.2005
 * Changed 30.06.2009 Florian Battke
 *
 */
@SuppressWarnings("unchecked")
public class Locus
implements Comparable
{

	/**
	 *  the location of this locus 
	 */
	private AbstractGeneticCoordinate coordinate;

	/**
	 *  Constructs an empty locus
	 */
	public Locus()
	{   
		this(null);
	}

	/**
	 * @param coordinate
	 */
	public Locus(AbstractGeneticCoordinate coordinate)
	{
		this.coordinate = coordinate;
	}

	public AbstractGeneticCoordinate getCoordinate()
	{
		return coordinate;
	}

	public void setCoordinate(AbstractGeneticCoordinate coordinate)
	{
		this.coordinate = coordinate;
	}

	public int compareTo(Object o)
	{
		if(!(o instanceof Locus))
			throw new ClassCastException("Cannot compare to an instance of "+o.getClass().getName());

		Locus cmp = (Locus)o;

		int result = this.coordinate.compareTo(cmp.coordinate);
		return result;

	}

	public boolean equals(Object o)
	{
		return this.compareTo(o)==0;
	}


	public String toString()
	{
		return coordinate.toString();
	}
}
