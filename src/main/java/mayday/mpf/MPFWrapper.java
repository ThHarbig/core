package mayday.mpf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.ProbeList;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;

/** 
 * MPFWrapper provides the link between the Mayday Processing Framework and 
 * Mayday's plugin scanner. It also hands over data from Mayday to the Applicator
 * and integrates processing results into Mayday's data structure.  
 * @author Florian Battke
 */
@SuppressWarnings("deprecation")
public class MPFWrapper extends mayday.core.pluma.AbstractPlugin implements ProbelistPlugin {
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.mpf",
				new String[0],
				Constants.MC_PROBELIST,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Manages and applies processing pipelines built from modules.",
				"Mayday Processing Framework"
				);
//		pli.addCategory("Data Processing");
		return pli;
	}
	
	
	private Applicator app;
	
	public List<ProbeList> run(List<ProbeList> probeLists, MasterTable masterTable) {
		
		FilterClassList.getInstance();
		
		this.app = new Applicator(masterTable);
		
		List<ProbeList> returnedLists = new ArrayList<ProbeList>();
		
		// Create Objects for input Probelists
		for (Object o : probeLists) {
			app.InputDataSets.addElement(new MaydayDataObject((ProbeList)o));			
		}
		
		JFrame modalParent = Mayday.sharedInstance;
		
		app.showModal(modalParent); 
		
		// Collect output
		for (MaydayDataObject mdo : app.OutputDataSets) {
			returnedLists.add(mdo.getProbeList());			
		}
		return returnedLists;
	}

	public void init() {
	}

	
}
