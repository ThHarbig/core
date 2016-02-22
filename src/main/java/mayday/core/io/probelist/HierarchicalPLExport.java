package mayday.core.io.probelist;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot_v3_0;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.ProbelistFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.probelistmanager.MasterTableProbeList;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.UnionProbeList;

public class HierarchicalPLExport extends AbstractPlugin implements ProbelistFileExportPlugin {
	

	
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.gudi.ExportProbelistHierarchy",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Alexander Herbig, Florian Battke",
				"herbig@informatik.uni-tuebingen.de",
				"Saves a hierarchy of selected probelists",
				"Hierarchical ProbeList export"
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION,"pl");
//		pli.getProperties().put(GUDEConstants.EXPORTER_DESCRIPTION,"Multiple ProbeList Probe Name Exporter");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION,"Hierarchical ProbeList File");
		return pli;
	}
	
	protected String id(Object o) {
		Integer i = objectMapWRITE.get(o);
		if (i==null) {
			objectMapWRITE.put(o, i=uniqueID);
			uniqueID++;
		} 
		return i.toString();
	}
	
	protected HashMap<Object, Integer> objectMapWRITE;
	protected int uniqueID;
	
	protected void write_ProbeLists(BufferedWriter bw, List<ProbeList> pls) throws Exception {
		// Probelists are written in PLM ordering, which ensures parents get read before children
		// L <id> <name> <color> <parentid> <entry1> <entry2> ...
		for (ProbeList pl : pls) {
			
			bw.write("L\t");
			bw.write(id(pl));
			bw.write("\t");
			bw.write(Snapshot_v3_0.wrap(pl.getName()));
			bw.write("\t");
			bw.write(Integer.toString(pl.getColor().getRGB()));
			bw.write("\t");
			
			String parentID = "*";
			if (pl.getParent()!=null && !(pl.getParent() instanceof MasterTableProbeList)) {
				parentID = id(pl.getParent());
			}			
			bw.write(parentID);
			
			if ((pl instanceof MasterTableProbeList) || (pl instanceof UnionProbeList)) {
				bw.write("\t");
				bw.write("*");
			} else {
				for (Probe pb : pl) {
					bw.write("\t");
					bw.write(Snapshot_v3_0.wrap(pb.getName()));
				}
			}
			
			bw.write("\n");
		}
	}

	public void exportTo(List<ProbeList> probelists, String file) {
		
		if (probelists.size()==0)
			return;
		
		ProbeListManager plm = probelists.get(0).getDataSet().getProbeListManager();
		
		objectMapWRITE = new HashMap<Object, Integer>();
		uniqueID = 0;
		
		ProbeList lca = plm.getSharedAncestor(probelists);
		
		HashSet<UnionProbeList> noConversionNeeded = new HashSet<UnionProbeList>();
		// find all groups that have no leaf-descendants in the selection
		for (ProbeList pl : probelists) {
			if (! (pl instanceof UnionProbeList)) {				
				while (pl.getParent()!=null) {
					pl = pl.getParent();
					noConversionNeeded.add((UnionProbeList)pl);					
				}
			}
		}
		
		// get all paths to LCA and add path components also
		HashSet<ProbeList> doneAlready = new HashSet<ProbeList>();
		LinkedList<ProbeList> inCorrectOrder = new LinkedList<ProbeList>();
		for (ProbeList pl : probelists) {
			int insertionPoint = inCorrectOrder.size();
			// if a selected pl is not a leaf in the tree, and none of its leaf-descendants is selected,
			// it must be converted into a leavf or it loses its their content
			if (pl instanceof UnionProbeList && !noConversionNeeded.contains(pl)) {
				ProbeList pl2 = new ProbeList(pl.getDataSet(), false);
				for (Probe pb : pl.getAllProbes())
					pl2.addProbe(pb);
				pl2.setColor(pl.getColor());
				pl2.setName(pl.getName());
				pl2.setParent(pl.getParent());
				pl = pl2;
			}
			// now add path to LCA
			do {
				inCorrectOrder.add(insertionPoint, pl);
				doneAlready.add(pl);
				pl = pl.getParent();				
			} while (pl!=lca && !doneAlready.contains(pl));
		}
		
		
        try
          {
        	BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    		write_ProbeLists(bw, inCorrectOrder);
        	bw.flush();
        	bw.close();
          }            
          catch ( FileNotFoundException exception )
          {
              String l_message = MaydayDefaults.Messages.FILE_NOT_FOUND;
              l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, file );
              
              JOptionPane.showMessageDialog( null,
                  l_message,
                  MaydayDefaults.Messages.ERROR_TITLE,
                  JOptionPane.ERROR_MESSAGE ); 
          }
          catch ( Exception exception )
          {
              JOptionPane.showMessageDialog( null,
                  exception.toString(),
                  MaydayDefaults.Messages.ERROR_TITLE,
                  JOptionPane.ERROR_MESSAGE ); 
          }
          
      }      
	
}
