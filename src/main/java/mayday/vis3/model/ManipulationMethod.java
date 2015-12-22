/**
 * 
 */
package mayday.vis3.model;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;


public abstract class ManipulationMethod extends AbstractPlugin {
	
	public final static String MC = "Math/Data Manipulators"; 

	/** a description of the resulting data, e.g. "centerED". for use in plot axis titles, for example */
    public abstract String getDataDescription();    
	
    /** the name of the method, without parameters, e.g. "centerING" */
    public abstract String getName();
	
	public abstract double[] manipulate(double[] input);
		
    public void init() {}
    
    /** the description of the method complete with parameters, e.g. "log base 2" - overload this if needed */
    public String toString() {
    	return PluginManager.getInstance().getPluginFromClass(getClass()).getName();
    }
    
    public boolean equals(Object o) {
    	return toString().equals(o.toString()); 
    }
    
}