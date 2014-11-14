package mayday.core.io.dataset.SimpleSnapshot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.TreeMap;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.probelistmanager.MasterTableProbeList;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.UnionProbeList;

/***
 * Like Snapshot v2.1 but also saves ProbeList hierarchy including the new type UnionProbeList  
 * @author battke
 *
 */
public class Snapshot_v2_2 extends Snapshot_v2_1 {

	protected static final String KEY_PROBELIST_PARENT = "ProbeListParent";
	protected static final String KEY_PROBELIST_TYPE = "ProbeListType";
	protected static final String PROBELIST_TYPE_MT = "MasterTable";
	protected static final String PROBELIST_TYPE_UNION = "Union";
	protected static final String PROBELIST_TYPE_GENERIC = "Default";
    
	public Snapshot_v2_2() {
		super();
	}
	
	public Snapshot_v2_2(DataSet dataSet) {
		super(dataSet);
	}
	
	protected String supportedVersion() {
		return "$$ Mayday-Snapshot 2.2 $$";
	}

	public Snapshot_v2_0 olderVersion() {
		return new Snapshot_v2_1(getDataSet());
	}
	
	
	protected void write_ProbeList(BufferedWriter bw, ProbeList pl) throws Exception {
		objectMapWRITE.put(pl, uniqueID);
		bw.write(buildLine(""+uniqueID, pl.getName(), 1));
		// Type
		String pltype;
		if (pl instanceof MasterTableProbeList)
			pltype = PROBELIST_TYPE_MT;
		else if (pl instanceof UnionProbeList)
			pltype = PROBELIST_TYPE_UNION;
		else
			pltype = PROBELIST_TYPE_GENERIC;
		bw.write(buildLine(KEY_PROBELIST_TYPE, pltype,2));
		// Color
		bw.write(buildLine(KEY_PROBELIST_COLOR, pl.getColor().getRGB(), 2));			
		// Parent
		bw.write(buildLine(KEY_PROBELIST_PARENT, (pl.getParent()==null?"**null**":pl.getParent().getName()), 2));
		// Probes
		StringBuilder plcontent = new StringBuilder();
		if (pltype==PROBELIST_TYPE_GENERIC) {
			for (Probe pb : pl.getAllProbes())
				plcontent.append(objectMapWRITE.get(pb)+",");
		}
		bw.write(buildLine(KEY_PROBES, plcontent.toString(), 2));
		++uniqueID;
	}	
	
	public void addProbeList(ProbeList pl, ProbeListManager plm) {
		if ((pl instanceof MasterTableProbeList) || plm.contains(pl))
			return;
		if (pl.getParent()==null) {
			plm.addObjectAtBottom(pl);
		} else {			
			// parent needs to be present
			if (!plm.contains(pl.getParent()))
				addProbeList(pl.getParent(), plm);
			plm.addObjectAtBottom(pl);
		}
	}

	
	protected void read_ProbeLists(BufferedReader br) throws Exception {
		ArrayList<ProbeList> probeLists = new ArrayList<ProbeList>();		
		ArrayList<String> probeList_parent_names = new ArrayList<String>();

		int numberOfProbeLists = Integer.parseInt(
				checkAndGet(br.readLine(), KEY_PROBELISTS));
		
		for (int i=0; i!=numberOfProbeLists; ++i) {
			ChildProbeList pl = read_ProbeList2(br);
			probeLists.add(pl.pl);
			probeList_parent_names.add(pl.parentName);
			if (processingTask.hasBeenCancelled())
				return;
		}
		
		// add parent information
		TreeMap<String, ProbeList> probeLists_by_name = new TreeMap<String, ProbeList>();		
		for (ProbeList pl : probeLists)
			probeLists_by_name.put(pl.getName(), pl);
		
		for (int i=0; i!=probeList_parent_names.size(); ++i) {			
			UnionProbeList plgparent = (UnionProbeList)probeLists_by_name.get(probeList_parent_names.get(i));
			if (!(plgparent instanceof MasterTableProbeList))
				probeLists.get(i).setParent(plgparent);
		}
		
		// add probelists to manager
		ProbeListManager plm = ds.getProbeListManager();
		for (ProbeList pl : probeLists)
			addProbeList(pl, plm);
		
	}
	
	class ChildProbeList {
		ProbeList pl;
		String parentName;
		public ChildProbeList(ProbeList _pl, String _parentName) {
			pl=_pl;
			parentName=_parentName;
		}		
	}
	
	protected ProbeList read_ProbeList(BufferedReader br) throws Exception {
		throw new RuntimeException("Java should not call this method");
	}
	
	protected ChildProbeList read_ProbeList2(BufferedReader br) throws Exception {
		String[] ProbeListName = checkAndGetTwo(br.readLine(), 1);
		ProbeList pl;
		// Type
		String pltype = checkAndGet(br.readLine(), 2);
		if (pltype.equals(PROBELIST_TYPE_MT)) 
			pl = new MasterTableProbeList(ds);
		else if (pltype.equals(PROBELIST_TYPE_UNION))
			pl = new UnionProbeList(ds, null);
		else
			pl = new ProbeList(ds, true);
		// Color			
		Color col = new Color(Integer.parseInt(checkAndGet(br.readLine(), KEY_PROBELIST_COLOR)));			
		if (!(pl instanceof MasterTableProbeList)) {
			pl.setName(ProbeListName[1]);
			pl.setColor(col);
		}
		// add 
		String uniqueID = ProbeListName[0];
		objectMapREAD.put(uniqueID, pl);
		// Parent
		String parentName = (checkAndGet(br.readLine(), KEY_PROBELIST_PARENT));
		ChildProbeList cpl = new ChildProbeList(pl, parentName);
		// Probes
		String probeIDString = checkAndGet(br.readLine(), KEY_PROBES);
		String[] probeIDs = probeIDString.split(",");
		for (String probeID : probeIDs) {
			Probe pb = (Probe)objectMapREAD.get(probeID);
			if (pb!=null) 
				pl.addProbe(pb);
		}
		return cpl;
	}
	
}
