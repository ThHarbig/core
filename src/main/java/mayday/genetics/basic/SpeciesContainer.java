/*
 * Created on 14.07.2005
 */
package mayday.genetics.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 14.07.2005
 *
 */
//would be nice: map different names towards the same species
//      e.g. H.sapiens --> Homo sapiens <-- Human
public class SpeciesContainer
{
    private static HashMap<String, Species> species = 
        new HashMap<String, Species>();
    
    /**
     * This class cannot be instanciated!
     */
    private SpeciesContainer()
    {}
    
    public static boolean containsKey(String speciesName)
    {
        return species.containsKey(speciesName);
    }
    
    public static Species getSpecies(String name)
    {
    	Species spec = species.get(name);
    	// check if hashmap is out of date
    	if (spec!=null && !spec.getName().equals(name)) {
    		rehash();
    		spec = species.get(name);
    	}
    	
        if(!species.containsKey(name)) {
            species.put(name, new Species(name));
        }
        return species.get(name);
    }
    
    protected static void rehash() {
    	ArrayList<Species> tmp = new ArrayList<Species>();
    	tmp.addAll(species.values());
    	species.clear();
    	for (Species s: tmp)
    		species.put(s.getName(), s);
    }
    
    public static Collection<Species> getAllSpecies() {
        return species.values();
    }
}
