package mayday.clustering.extras.comparepartitions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import mayday.core.ClassSelectionModel;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.classes.ClassSelectionDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.tasks.AbstractTask;

public class ComparePartitionsPL
extends AbstractPlugin implements ProbelistPlugin
{
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.clustering.comparePartitions2",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Compares two partitions such as obtained by clustering the same data twice. " +
				"This plugin tries to infer the clusterings from the ProbeList names via the longest common name prefix.",
				"Compare Partitions");
		pli.addCategory(MaydayDefaults.Plugins.CATEGORY_CLUSTERING+"/"+MaydayDefaults.Plugins.SUBCATEGORY_CLUSTERINGEXTRAS);
		return pli;
	}

	@Override
	public void init() {
		
	}
	
	public List<ProbeList> run(final List<ProbeList> probeLists, final MasterTable masterTable) {
		
		new AbstractTask("Compare Partitions") {

			protected void doWork() throws Exception {
				String prefix1=null;
				String prefix2=null;
				
				for (ProbeList pl : probeLists) {
					String prefix = pl.getName();
					int i=0;
					for (i=0; i!=prefix.length(); ++i)
						if (Character.isDigit(prefix.charAt(i)))
							break;
					prefix = prefix.substring(0, i);
					if (prefix1 == null) {
						prefix1 = prefix;
						continue;
					}
					if (prefix1.equals(prefix)) {
						continue;
					}
					if (prefix2 == null) {
						prefix2 = prefix;
						continue;
					}
					if (!prefix2.equals(prefix)) {
						System.err.println("More than two clusterings selected:\n" +
								"\""+prefix1+"\", \""+prefix2+"\", and \""+prefix+"\"");
						prefix2=null; prefix1=null;
					}
				}
				
				LinkedList<ProbeList> part1 = new LinkedList<ProbeList>();
				LinkedList<ProbeList> part2 = new LinkedList<ProbeList>();
				
				LinkedList<String> names = new LinkedList<String>();
				for (ProbeList pl : probeLists)
					names.add(pl.getName());
				LinkedList<String> classes = new LinkedList<String>();

				ClassSelectionModel csm;
				
				if (prefix1!=null || prefix2!=null) {
					String firstPrefix = prefix1;
					if (prefix1==null)
						firstPrefix = prefix2;
					else if (prefix2!=null) {
						if (prefix1.length()<prefix2.length()) {
							firstPrefix = prefix2;
						}
					}
					
					for (ProbeList pl : probeLists) {
						if (pl.getName().startsWith(firstPrefix)) {
							classes.add("Partition 1");
						} else { 
							classes.add("Partition 2");
						}
					}
					csm = new ClassSelectionModel(names, classes);
				} else {
					csm = new ClassSelectionModel(names);
				}
				
				ClassSelectionDialog csd = new ClassSelectionDialog(csm, masterTable.getDataSet());
				csd.setModal(true);
				csd.setVisible(true);
				if (csm.getClassNames().size()!=2)
					throw new RuntimeException("Please create exactly two classes");
				Map<String, String> m = csm.convertToMap();					
				for (int i=0; i!=names.size(); ++i)
					if (m.get(names.get(i)).equals(csm.getClassNames().get(0)))
						part1.add(probeLists.get(i));
					else
						part2.add(probeLists.get(i));
				
				Partition p1 = new Partition( part1 );
				Partition p2 = new Partition( part2 );

				// make sure all probes are present in each of the partitions
				TreeSet<Probe> probes = new TreeSet<Probe>();
				for (ProbeList pl : probeLists)
					probes.addAll(pl.getAllProbes());
				p1.addUnclustered(probes);
				p2.addUnclustered(probes);
				
				// compute the confusing matrix
				ConfusingMatrix cfm = new ConfusingMatrix(p1,p2);
				
				new ResultFrame(cfm).setVisible(true);		
			}

			protected void initialize() {
			}
			
		}.start();
		
		return null;
	}

	
}
