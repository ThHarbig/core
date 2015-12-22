package mayday.core.probelistmanager.search;

import javax.swing.ListModel;

import mayday.core.probelistmanager.gui.ProbeListManagerView;

/**
 * Default Probe List search. Iterates over probeLists and checks if the query is contained in the probe list name. 
 * @author Stephan Symons
 * @version 1.0
 * @since 2.9
 */
public class ProbeListSearch implements ProbeListSearchInterface
{
	
	protected int find0(ProbeListManagerView view, String query, int from, int to, int direction) {
		if (query.length() == 0) 
			return -1;
        
		ListModel model=view.getModel();
		query = query.toLowerCase();
		
        for(int i=from; i!=to; i+=direction) {        	
        	try {
        		String plName = model.getElementAt(i).toString().toLowerCase();
        		if (plName.indexOf(query)>=0)
	        		return i;  		
        	} catch (Exception e) {//do nothing and die
        		e.printStackTrace();
        	}        	
        }
		return -1;
	}
	
	public int find(ProbeListManagerView view, String query, int index) {
		return find0(view, query, index+1, view.getModel().getSize(), 1);
	}

	
	
	@Override
	public int findPrevious(ProbeListManagerView view, String query, int index) {
		return find0(view, query, index-1, -1, -1);	
	}
}
