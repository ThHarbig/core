/*
 * Created on Oct 12, 2005
 *
 */
package wsi.ra.tool;

import java.util.Arrays;

/**
 * @author froehlic
 *
 * Sort an array of comparable objects and return the new index of each object.
 */
@SuppressWarnings("unchecked")

public class ArraySorter {
	class EntryWithIndex implements Comparable{
		Comparable value;
		int index;
		boolean sortAsc = true;
		
		public int compareTo(Object o){
			EntryWithIndex other = (EntryWithIndex) o;
			if(value.compareTo(other.value) == 0)
				return 0;
			if(sortAsc){
				if(value.compareTo(other.value) > 0)
					return 1;
				else
					return -1;
			}
			else{
				if(value.compareTo(other.value) < 0)
					return 1;
				else
					return -1;
			}
		}
	}
	
	public static int[] sort(Comparable[] a, boolean sortAsc){
		ArraySorter AS = new ArraySorter();
		EntryWithIndex[] e = new EntryWithIndex[a.length];		
		for(int i = 0; i < e.length; i++){
			e[i] = AS.new EntryWithIndex();
			e[i].sortAsc = sortAsc;
			e[i].value = a[i];
			e[i].index = i;
		}
		Arrays.sort(e);
		int[] index = new int[e.length];
		for(int i = 0; i < e.length; i++){
			index[i] = e[i].index; 
			a[i] = e[i].value;
		}
		return index;
	}

	public static int[] sort(double[] a, boolean sortAsc){
		ArraySorter AS = new ArraySorter();
		EntryWithIndex[] e = new EntryWithIndex[a.length];		
		for(int i = 0; i < e.length; i++){
			e[i] = AS.new EntryWithIndex();
			e[i].sortAsc = sortAsc;
			e[i].value = new Double(a[i]);
			e[i].index = i;
		}
		Arrays.sort(e);
		int[] index = new int[e.length];
		for(int i = 0; i < e.length; i++){
			index[i] = e[i].index; 
			a[i] = ((Double)e[i].value).doubleValue();
		}
		return index;
	}
}
