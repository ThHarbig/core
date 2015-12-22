package mayday.interpreter.rinterpreter.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import mayday.interpreter.rinterpreter.RDefaults;

/**
 * The list containing the sources.
 * 
 * @author Matthias
 *
 */
public class RSourcesList
{
	private ArrayList<RSource> sources;
	
	
	public RSourcesList()
	{
		sources=new ArrayList<RSource>();
	}
	
	/**
	 * Add the given source to the list.
	 * Give an information message if the source is
	 * already in this list, then nothing is done.
	 * <br><br>
	 * The equals method of RSource is used to decide
	 * whether two sources are equal or not.
	 * @see RSource#equals(java.lang.Object)
	 * 
	 * @param src
	 */
	public void add(RSource src)
	{
		if(!sources.contains(src))
		  sources.add(src);
		else
		  RDefaults.messageGUI(
			"The source file '"
			  +src.getFilename()
			  +"' is already in the list!", 
			RDefaults.Messages.Type.INFO);
	}
	
	public boolean contains(RSource src) {
		return sources.contains(src);
	}
	
	public void remove(RSource src)
	{
		sources.remove(src);
	}
	
	public void remove(int index)
	{
		sources.remove(index);
	}
	
	public int size()
	{
		return sources.size();
	}
	
	public RSource getSourceAt(int i)
	{
		return (RSource)sources.get(i);
	}
	
	public RSource getSource(String name)
	{
		return (RSource)sources.get(sources.indexOf(name));
	}
	
	public Iterator<RSource> iterator()
	{
		return sources.iterator();
	}
	
	public RSource[] toArray()
	{
	    return (RSource[])this.sources.toArray(new RSource[0]);
	}
	
	public void sort()
	{
	    RSource[] data=(RSource[])sources.toArray(new RSource[0]);
	    Arrays.sort(data);
	    this.sources.clear();
	    for(int i=0; i!=data.length; ++i)
	    {
	        this.sources.add(data[i]);
	    }
	}
	
	public int findFirst(String descriptor)
	{
	    for(int i=0; i!=sources.size(); ++i)
	    {
	        if(((RSource)sources.get(i)).getDescriptor().equals(descriptor))
	        {
	            return i;
	        }
	    }
	    return -1;
	}
	
	public void setSources(RSource[] data)
	{
	    this.sources.clear();
	    for(int i=0; i!=data.length; ++i)
	    {
	        this.sources.add(data[i]);
	    }
	}
}
