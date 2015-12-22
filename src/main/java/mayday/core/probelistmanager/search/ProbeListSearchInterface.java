package mayday.core.probelistmanager.search;

import mayday.core.probelistmanager.gui.ProbeListManagerView;

public interface ProbeListSearchInterface 
{

	/**
	 * Finds a probelist according to a query and a start index
	 * @param view the ProbeListManagerView to search through
	 * @param the query string, interpretation is up to the probelistsearchinterface implementing class
	 * @param index the start index for the search, is NOT included in the search space
	 * @return The index of the first element that matches AFTER the start index. 
	 */
	public int find(ProbeListManagerView view, String query, int index);
	
	/**
	 * Finds a probelist according to a query and a start index
	 * @param view the ProbeListManagerView to search through
	 * @param the query string, interpretation is up to the probelistsearchinterface implementing class
	 * @param index the start index for the search, is NOT included in the search space
	 * @return The index of the first element that matches BEFORE the start index. 
	 */
	public int findPrevious(ProbeListManagerView view, String query, int index);

}
