package mayday.core.io.dataset.SimpleSnapshot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.ReadyBufferedReader;
import mayday.core.io.StorageNode;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.tasks.AbstractTask;

public class Snapshot_v2_0 extends Snapshot {

	protected static final String KEY_NAME = "DataSet";
	protected static final String KEY_EXPERIMENTNAMES = "ExperimentNames";
	protected static final String KEY_PROBES = "Probes";
	protected static final String KEY_PROBELISTS = "ProbeLists";
	protected static final String KEY_PROBELIST_COLOR = "Color";
	protected static final String KEY_MIOS = "MetaInformationObjects";
	protected static final String KEY_MIOGROUPS = "MetaInformationGroups";
	protected static final String KEY_MIOGROUP_TYPE = "Type";
	protected static final String KEY_MIOGROUP_LINKS = "Links";
	protected static final String KEY_MIOGROUP_PATH = "Path";
	
    protected AbstractTask processingTask;
	
	protected DataSet ds;
	
	public Snapshot_v2_0() {
		super();
	}
	
	public Snapshot_v2_0(DataSet dataSet) {
		this();
		setDataSet(dataSet);
	}
	
	protected void read_internal(BufferedReader br) throws Exception {
		processingTask.setProgress(0,"Parsing Snapshot...");
		LoadFromStream(br);
		processingTask.setProgress(10000);
	}
	
	public void read(final ReadyBufferedReader br) throws Exception {

		if (processingTask==null) {
			processingTask = new AbstractTask("Parsing Snapshot") {

				@Override
				protected void doWork() throws Exception {
					read_internal(br);
				}

				@Override
				protected void initialize() {}
				
			};
		
			processingTask.start();			
		} else
			read_internal(br);
		
	}
	
	
	protected void write_internal(OutputStream ostr) throws Exception {
		processingTask.setProgress(0,"Writing Snapshot...");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ostr)); 
		SaveToStream(bw);
		bw.flush();
		processingTask.setProgress(10000);
	}
	
	public void write(final OutputStream ostr) throws Exception {
		if (processingTask==null) {
			processingTask = new AbstractTask("Writing Snapshot") {

				@Override
				protected void doWork() throws Exception {
					write_internal(ostr);
				}

				protected void initialize() {}
			};
			processingTask.start();
		} else 
			write_internal(ostr);				
	}
	
	public DataSet getDataSet() {
		return ds;
	}
	
	public void setDataSet(DataSet dataSet) {
		ds = dataSet;
	}
	
	
	// Each object has a unique ID
	public HashMap<String, Object> objectMapREAD;
	public HashMap<Object, Integer> objectMapWRITE;
	int uniqueID;
		
	protected void prepare() {
		uniqueID = 0;
		objectMapWRITE = new HashMap<Object, Integer>();
		objectMapREAD = new HashMap<String, Object>();
	}
	
	protected void write_DataSet(BufferedWriter bw) throws Exception {
		// Add dataSet information
		bw.write(buildLine(KEY_NAME, ds.getName(),0));
		objectMapWRITE.put(ds, uniqueID);
		uniqueID++;
	}
	
	protected void write_Experiments(BufferedWriter bw) throws Exception {
		// Add Experiments
		bw.write(buildLine(KEY_EXPERIMENTNAMES, ds.getMasterTable().getNumberOfExperiments(), 0));
		for (int i=0; i!=ds.getMasterTable().getNumberOfExperiments(); ++i)
			bw.write(buildLine(""+i, ds.getMasterTable().getExperimentName(i), 1));
	}
	
	protected void write_MasterTable(BufferedWriter bw) throws Exception {
		int numberOfProbes = ds.getMasterTable().getNumberOfProbes();
		double stepSize = (4500.0) / (double)numberOfProbes;
		bw.write(buildLine(KEY_PROBES, numberOfProbes, 0));
		int i=0;
		for (Probe pb : ds.getMasterTable().getProbes().values()) {
			if (processingTask.hasBeenCancelled())
				return;
			write_Probe(bw, pb);
			++i;
			if (i%100==0)
				processingTask.setProgress(500+(int)(stepSize*i));
		}
	}
	
	protected void write_Probe(BufferedWriter bw, Probe pb) throws Exception {
		objectMapWRITE.put(pb, uniqueID);
		bw.write(buildLine(uniqueID+"", pb.getName(), 1));
		for (int j=0; j!=ds.getMasterTable().getNumberOfExperiments(); ++j)
			bw.write(buildLine(""+j, pb.getValue(j)==null?"null":pb.getValue(j), 2));
		++uniqueID;
	}
	
	protected void write_ProbeLists(BufferedWriter bw) throws Exception {
		// Add all Probe Lists + colors
		bw.write(buildLine(KEY_PROBELISTS, ds.getProbeListManager().getNumberOfObjects(), 0));
		for (ProbeList pl : ds.getProbeListManager().getProbeLists()) {
			if (processingTask.hasBeenCancelled())
				return;
			write_ProbeList(bw, pl);
		}
	}
	
	protected void write_ProbeList(BufferedWriter bw, ProbeList pl) throws Exception {
		objectMapWRITE.put(pl, uniqueID);
		bw.write(buildLine(""+uniqueID, pl.getName(), 1));
		// Color
		bw.write(buildLine(KEY_PROBELIST_COLOR, pl.getColor().getRGB(), 2));
		// Probes
		StringBuilder plcontent = new StringBuilder();
		for (Probe pb : pl.getAllProbes())
			plcontent.append(objectMapREAD.get(pb)+",");
		bw.write(buildLine(KEY_PROBES, plcontent.toString(), 2));
		++uniqueID;
	}
	
	protected void write_MIOs(BufferedWriter bw) throws Exception {
		// Add all MIOs
		MIManager mim = ds.getMIManager();
		int numberOfTypes = mim.getTypes().size();
		bw.write(buildLine(KEY_MIOS, numberOfTypes, 0));
		// Per type
		int i=0;
		double stepSize = (9500.0-5500.0)/(double)numberOfTypes;
		for (String miotype : mim.getTypes()) {
			// Build list of all mios of this type
			HashSet<MIType> mitypes = new HashSet<MIType>();
			for (MIGroup mg : mim.getGroupsForType(miotype)) 
				for (Entry<Object,MIType> e : mg.getMIOs())
					mitypes.add(e.getValue());	
			bw.write(buildLine(miotype, mitypes.size(),1));
			for (MIType mt : mitypes) {
				objectMapWRITE.put(mt, uniqueID);
				bw.write(buildLine(""+uniqueID,mt.serialize(MIType.SERIAL_TEXT),2));
				++uniqueID;
			}
			++i;
			processingTask.setProgress(5500+(int)(stepSize*i));		
			if (processingTask.hasBeenCancelled())
				return;
		}	
	}
	
	protected void write_MIGroups(BufferedWriter bw) throws Exception {
		MIManager mim = ds.getMIManager();
		bw.write(buildLine(KEY_MIOGROUPS, mim.getGroups().size(), 0));
		for (MIGroup mg : mim.getGroups()) {
			write_MIGroup(bw, mg);
			if (processingTask.hasBeenCancelled())
				return;
		}
	}
	
	protected void write_MIGroup(BufferedWriter bw, MIGroup mg) throws Exception {
		MIManager mim = ds.getMIManager();
		objectMapWRITE.put(mg, uniqueID);
		bw.write(buildLine(""+uniqueID, mg.getName(), 1));

		String path = mim.getTreeRoot().getPathFor(mg);
		path = path.substring(0, path.lastIndexOf("/"));
		bw.write(buildLine(KEY_MIOGROUP_PATH, path, 2));
		bw.write(buildLine(KEY_MIOGROUP_TYPE, mg.getMIOType(), 2));			
		// Add all contents
		/* 081012-fb: Build the map before storing the mio so that the
		 * announced number of links is equal to the number of stored
		 * (non-orphan) links.
		 * This should fix the "Expecting <n> more lines" bug.
		 */
		Map<Integer,Integer> miomap = new HashMap<Integer,Integer>();
		for (Entry<Object,MIType> e : mg.getMIOs()) {
			Integer sourceID = objectMapWRITE.get(e.getKey());
			if (sourceID==null) {
				System.err.println(
						"NOT STORED: Orphan object: \""+e.getKey()+"\" of "+e.getKey().getClass()+" referenced by a MIO in the Group \""+mg.getName()+"\"");
			} else {
				Integer targetID = objectMapWRITE.get(e.getValue());
				miomap.put(sourceID, targetID);
			}
		}
		/* Now write the finished map to the stream */
		bw.write(buildLine(KEY_MIOGROUP_LINKS, miomap.size(), 2));
		for (Entry<Integer,Integer> link : miomap.entrySet()) {
			bw.write(buildLine(link.getKey()+"",link.getValue(), 3));				
		}
		++uniqueID;
	}
	
	protected void write_preamble(BufferedWriter bw) {
		// for derived classes
	}
	
	protected void write_closingStatements(BufferedWriter bw)  throws Exception {
		// for derived classes
	}
	
	protected void SaveToStream(BufferedWriter bw) throws Exception {
		long l1 = System.currentTimeMillis();
		prepare();
				
		// Add version string
		bw.write(supportedVersion()+"\n");
		
		write_preamble(bw);
		
		write_DataSet(bw);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(400, "Experiments");		
		write_Experiments(bw);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(500, "Probes"); // 5-50%
		write_MasterTable(bw);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(5000, "Probe Lists");
		write_ProbeLists(bw);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(5500, "Meta Information");
		write_MIOs(bw);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(9500, "Meta Information Groups");
		write_MIGroups(bw);
		if (processingTask.hasBeenCancelled())
			return;
		
		write_closingStatements(bw);
		long l2 = System.currentTimeMillis();
		System.out.println("Wrote Snapshot in "+(l2-l1)+" ms");
	}

	
	protected String checkAndGet(String line, String key) {
		String[] res = splitLine(line);
		if (!res[0].trim().equals(key))
			throw new RuntimeException("Expected key \""+key+"\" but found key \""+res[0]+"\"");
		return res[1];
	}
	
	
	protected String checkAndGet(String line, int level) {
		String[] res = splitLine(line);
		int lvl;
		for(lvl=0; lvl!=res[0].length() && res[0].charAt(lvl)==' '; ++lvl);
		if (lvl!=level)
			throw new RuntimeException("Expected nesting level \""+level+"\" but found level \""+lvl+"\"");
		return res[1];
	}
	
	
	protected String[] checkAndGetTwo(String line, int level) {
		String[] res = splitLine(line);
		int lvl;
		for(lvl=0; lvl!=res[0].length() && res[0].charAt(lvl)==' '; ++lvl);
		if (lvl!=level)
			throw new RuntimeException("Expected nesting level \""+level+"\" but found level \""+lvl+"\"");
		res[0] = res[0].substring(level);
		return new String[]{res[0],res[1]};
	}
	
	protected String[] splitLine(String line) {
		String res[] = line.split("=");
		if (res.length>2)
			throw new RuntimeException("A line contained more than one \"=\" sign: \n"+line);
		res[0] = StorageNode.unwrapString(res[0]);
		if (res.length==1)
			res = new String[]{res[0],""};
		else
		    res[1] = StorageNode.unwrapString(res[1]);
		return res;
	}
	
	protected String copyString(int length, String cpy) {
		if (length==0) return "";
		StringBuilder ret= new StringBuilder(cpy.length()*length);
		while(length-->0) 
			ret.append(cpy);
		return ret.toString();
	}
	
	protected String buildLine(String left, Object right, int level) {
		return copyString(level, " ")+StorageNode.wrapString(left)+"="+StorageNode.wrapString(right.toString())+"\n";
	}
	
	protected String supportedVersion() {
		return "$$ Mayday-Snapshot 2 $$";
	}
	
	public Snapshot_v2_0 olderVersion() {
		return null;
	}
	

	protected void read_preamble(BufferedReader br) throws Exception {
		// for derived classes
	}

	protected void read_DataSet(BufferedReader br) throws Exception {
		//Dataset Properties
		String dataSetName = checkAndGet(br.readLine(), KEY_NAME);
		ds = new DataSet(dataSetName);
		// Dataset is always object with ID = 0
		objectMapREAD.put("0", ds);
	}
	
	protected void read_Experiments(BufferedReader br) throws Exception {
		MasterTable mt = ds.getMasterTable();		
		// Add Experiments
		int numberOfExperiments = Integer.parseInt(
				checkAndGet(br.readLine(), KEY_EXPERIMENTNAMES)
				);
		mt.setNumberOfExperiments(numberOfExperiments);
		for (Integer i=0; i!=numberOfExperiments; ++i)
			mt.setExperimentName(i, checkAndGet(br.readLine(), ""+i));
	}
	
	protected void read_MasterTable(BufferedReader br) throws Exception {
		MasterTable mt = ds.getMasterTable();
		int numberOfProbes = Integer.parseInt(
				checkAndGet(br.readLine(), KEY_PROBES));
		double stepSize = 4500.0 / (double)numberOfProbes;
		for (int i=0; i!=numberOfProbes; ++i) {
			if (processingTask.hasBeenCancelled())
				return;
			Probe pb = read_Probe(br, mt);
			mt.addProbe(pb);
			processingTask.setProgress(500+(int)(stepSize*i));
		}
	}

	protected Probe read_Probe(BufferedReader br, MasterTable mt) throws Exception {
		String[] ProbeName = checkAndGetTwo(br.readLine(), 1);
		Probe pb = new Probe(mt);
		pb.setName(ProbeName[1]);
		String uniqueID = ProbeName[0];
		objectMapREAD.put(uniqueID, pb);
		for (int j=0; j!=mt.getNumberOfExperiments(); ++j) {
			String v = checkAndGet(br.readLine(), 2);
			pb.addExperiment(v.equals("null")?null:Double.parseDouble(v));
		}
		return pb;
	}
	
	protected void read_ProbeLists(BufferedReader br) throws Exception {
		LinkedList<ProbeList> probeLists = new LinkedList<ProbeList>();
		int numberOfProbeLists = Integer.parseInt(
				checkAndGet(br.readLine(), KEY_PROBELISTS));
		for (int i=0; i!=numberOfProbeLists; ++i) {
			ProbeList pl = read_ProbeList(br);
			probeLists.add(pl);
			if (processingTask.hasBeenCancelled())
				return;
		}
		ProbeListManager plm = ds.getProbeListManager();
		plm.setObjects(probeLists);
	}
	
	protected ProbeList read_ProbeList(BufferedReader br) throws Exception {
		String[] ProbeListName = checkAndGetTwo(br.readLine(), 1);
		ProbeList pl = new ProbeList(ds, true);
		pl.setName(ProbeListName[1]);
		String uniqueID = ProbeListName[0];
		objectMapREAD.put(uniqueID, pl);
		// Color			
		Color col = new Color(Integer.parseInt(checkAndGet(br.readLine(), KEY_PROBELIST_COLOR)));
		pl.setColor(col);
		// Probes
		String probeIDString = checkAndGet(br.readLine(), KEY_PROBES);
		String[] probeIDs = probeIDString.split(",");
		for (String probeID : probeIDs) {
			Probe pb = (Probe)objectMapREAD.get(probeID);
			if (pb!=null) 
				pl.addProbe(pb);
		}
		return pl;
	}
	
	protected void read_MIOs(BufferedReader br) throws Exception {
		int numberOfMIOTypes = Integer.parseInt(
				checkAndGet(br.readLine(), KEY_MIOS));
		double stepSize = (9500.0-5500.0)/(double)numberOfMIOTypes;
		// Per type
		for (int i=0; i!=numberOfMIOTypes; ++i) {
			String[] groupinfo = checkAndGetTwo(br.readLine(), 1);
			String miotype = groupinfo[0];
			int numberOfMIOs = Integer.parseInt(groupinfo[1]);
			for (int j=0; j!=numberOfMIOs; ++j) {				
				String[] parts = checkAndGetTwo(br.readLine(), 2);
				String uniqueID = parts[0];
				MIType aMio = MIManager.newMIO(miotype);
				aMio.deSerialize(MIType.SERIAL_TEXT, parts[1]);
				objectMapREAD.put(uniqueID, aMio);		
			}
			processingTask.setProgress(5500+(int)(stepSize*i));
			if (processingTask.hasBeenCancelled())
				return;
		}
	}
	
	protected void read_MIGroups(BufferedReader br) throws Exception {
		int numberOfMIGroups = Integer.parseInt(
				checkAndGet(br.readLine(), KEY_MIOGROUPS));		
		for (int i=0; i!=numberOfMIGroups; ++i) {
			read_MIGroup(br);
			if (processingTask.hasBeenCancelled())
				return;
		}
	}
	

	protected void read_MIGroup(BufferedReader br) throws Exception {
		MIManager mim = ds.getMIManager();
		String[] parts = checkAndGetTwo(br.readLine(), 1);
		String mioName = parts[1];
		String uniqueID = parts[0];
		String mioPath = checkAndGet(br.readLine(), KEY_MIOGROUP_PATH);
		String mioType = checkAndGet(br.readLine(), KEY_MIOGROUP_TYPE);
		MIGroup mg = mim.newGroup(mioType, mioName, mioPath);
		objectMapREAD.put(uniqueID, mg);
		// Add all contents
		int numberOfLinks = Integer.parseInt(
				checkAndGet(br.readLine(), KEY_MIOGROUP_LINKS));
		for (int j=0; j!=numberOfLinks; ++j) {
			String readline = br.readLine();
			String[] partners = checkAndGetTwo(readline, 3);
			Object source = objectMapREAD.get(partners[0]);
			Object mio = objectMapREAD.get(partners[1]);
			mg.add(source, (MIType)mio);
		}
	}
	
	protected void read_closingStatements(BufferedReader br)  throws Exception {
		// for derived classes
	}

	
	protected void LoadFromStream(BufferedReader br) throws OldFormatException, Exception {
		long l1 = System.currentTimeMillis();
		// Version string has already been checked for us
		
		prepare();
		
		read_preamble(br);
		
		read_DataSet(br);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(400, "Experiments");		
		read_Experiments(br);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(500, "Probes"); // 5-50%
		read_MasterTable(br);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(5000, "Probe Lists");
		read_ProbeLists(br);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(5500, "Meta Information");
		read_MIOs(br);
		if (processingTask.hasBeenCancelled())
			return;
		
		processingTask.setProgress(9500, "Meta Information Groups");
		read_MIGroups(br);
		if (processingTask.hasBeenCancelled())
			return;
		
		read_closingStatements(br);		
		long l2 = System.currentTimeMillis();
		System.out.println("Parsed Snapshot in "+(l2-l1)+" ms");
	}

	public void setProcessingTask(AbstractTask at) {
		processingTask=at;
	}
	
}
