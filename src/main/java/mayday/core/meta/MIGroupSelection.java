/*
 * Created on Feb 11, 2005
 *
 */
package mayday.core.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A selection of MIO groups. Basically, this is a List<MIGroup> with some additional filtering methods. 
 * @author gehlenbo, battke
 *
 */
@SuppressWarnings("serial")
public class MIGroupSelection< T extends MIType > extends ArrayList<MIGroup>{


	/** Returns a Map mapping mioExtendables (Objects) to MIOs. For each object, only one object is contained.
	 * This function "merges" the MIOGroups in this object, i.e. if Group #1 contains MIOs for Objects A and C and 
	 * Group #2 contains MIOs for Object A, B and D, the resulting map will contain the following entries:
	 * <A,MIO grom group 1>  <B, MIO from group 2>  <C, MIO from group 1>  <D, MIO grfom group 2>
	 * @return The map of objects to mios.
	 */
	@SuppressWarnings("unchecked")
	public Map< Object, T > computeUniqueSelection()  {
		HashMap<Object, T> l_selection = new HashMap<Object,T>();

		for ( MIGroup l_group: this ) {

			for (Entry<Object, MIType> e : l_group.getMIOs()) {
				if (!l_selection.containsKey(e.getKey())) {
					l_selection.put(e.getKey(),(T)e.getValue());
				}
			}
		}
		return l_selection;
	}


	/** returns a new MIGroupSelection with only those groups that contain MIOs of a certain mio type
	 * @param mioType the Pluma ID of the MIO type to filter for 
	 * @return a new MIGroupSelection object containing only matching groups
	 */
	public MIGroupSelection<T> filterByType(String mioType) {
		MIGroupSelection<T> result = new MIGroupSelection<T>();
		for (MIGroup mg: this)
			if (mg.getMIOType().equals(mioType))			  
				result.add(mg);
		return result;
	}

	/** returns a new MIGroupSelection with only those groups that have a given name
	 * @param groupName the name ot the groups that should be kept  
	 * @return a new MIGroupSelection object containing only matching groups
	 */
	public MIGroupSelection<T> filterByName(String groupName) {
		MIGroupSelection<T> result = new MIGroupSelection<T>();
		for (MIGroup mg: this)
			if (mg.getName().equals(groupName))			  
				result.add(mg);
		return result;
	}

	/** returns a new MIGroupSelection with only those groups that contain a given object
	 * @param mioExtendable the object that has to be contained in the groups
	 * @return a new MIGroupSelection object containing only matching groups
	 */
	public MIGroupSelection<T> filterByObject(Object mioExtendable) {
		MIGroupSelection<T> result = new MIGroupSelection<T>();
		for (MIGroup mg: this)
			if (mg.contains(mioExtendable))			  
				result.add(mg);
		return result;
	}

	/** returns a new MIGroupSelection with only those groups that contain MIOs implementing a given interface
	 * or extending a given superclass, e.g. NumericMIO
	 * @param theInterface the interface resp. superclass that MIOs should implement/extend.
	 * @return a new MIGroupSelection object containing only matching groups
	 */
	public MIGroupSelection<T> filterByInterface(Class<T> theInterface) {
		MIGroupSelection<T> result = new MIGroupSelection<T>();
		for (MIGroup mg: this)
			if (theInterface.isAssignableFrom(mg.getMIOClass()))			  
				result.add(mg);
		return result;
	}



}
