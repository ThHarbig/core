package mayday.core.io.dataset.SimpleSnapshot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.probelistmanager.MasterTableProbeList;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.UnionProbeList;

public class Snapshot_v3_0 extends Snapshot_v2_2 {

	protected long totalObjects;
	protected long currentObject;
	protected Pattern splitter = Pattern.compile("\t");
	
	public Snapshot_v3_0() {
	}
	
	public Snapshot_v3_0(DataSet dataSet) {
		this();
		setDataSet(dataSet);
	}
	
	@Override
	public Snapshot_v2_0 olderVersion() {
		return new Snapshot_v2_2();
	}

	@Override
	protected String supportedVersion() {
		return "$$ Mayday-Snapshot 3.0 $$";
	}
	
	protected void SaveToStream(BufferedWriter bw) throws Exception {
		long l1 = System.currentTimeMillis();
		prepare();
		bw.write(supportedVersion()+"\n");
		write_preamble(bw);
		write_DataSet(bw);
		if (processingTask.hasBeenCancelled()) return;
		write_Experiments(bw);
		if (processingTask.hasBeenCancelled()) return;
		write_MasterTable(bw);
		if (processingTask.hasBeenCancelled()) return;
		write_ProbeLists(bw);
		if (processingTask.hasBeenCancelled()) return;
		write_MIOs(bw);
		if (processingTask.hasBeenCancelled()) return;
		write_MIGroups(bw);
		if (processingTask.hasBeenCancelled()) return;
		write_closingStatements(bw);
		long l2 = System.currentTimeMillis();
		System.out.println("Wrote Snapshot in "+(l2-l1)+" ms");
	}


	protected void LoadFromStream(BufferedReader br) throws OldFormatException, Exception {
		// Version string has already been checked for us
		long l1 = System.currentTimeMillis();
		prepare();		
		read_preamble(br);
		read_DataSet(br);
		if (processingTask.hasBeenCancelled()) return;
		read_Experiments(br);
		if (processingTask.hasBeenCancelled()) return;
		read_MasterTable(br);
		if (processingTask.hasBeenCancelled()) return;
		read_ProbeLists(br);
		if (processingTask.hasBeenCancelled()) return;
		read_MIOs(br);
		if (processingTask.hasBeenCancelled()) return;
		read_MIGroups(br);
		if (processingTask.hasBeenCancelled()) return;
		read_closingStatements(br);
		long l2 = System.currentTimeMillis();
		System.out.println("Parsed Snapshot in "+(l2-l1)+" ms");
	}
	
	
	
	protected void prepare() {
		super.prepare();
		if (ds!=null) {
			MIGroupSelection<MIType> mgs = ds.getMIManager().getGroups();
			totalObjects = 1
					   	+ds.getMasterTable().getNumberOfProbes()*ds.getMasterTable().getNumberOfExperiments()
					   	+mgs.size();
			for (MIGroup mg : mgs)
				totalObjects+=(2*mg.getMIOs().size()); // once for the mio, once for the link
		}
		currentObject = 0;
	}

	protected String id(Object o) {
		Integer i = objectMapWRITE.get(o);
		if (i==null) {
			objectMapWRITE.put(o, i=uniqueID);
			uniqueID++;
		} 
		return i.toString();
	}
	
	private static String replaceAll(String in, String[] from, String[] to, int direction) {		
		String out = in;
		if (out==null) {
			return out;
		}
		if (direction==1)
			for (int i=0; i!=from.length; ++i) 
				out = out.replace(from[i],to[i]);
		else if (direction==-1) //unescape must be done backwards! 
			for (int i=from.length-1; i>=0; --i) 
				out = out.replace(from[i],to[i]);
		return out;
	}
	
	private static final String[] WRAPPED = new String[]{"~","\"","\t","\n"};
	private static final String[] UNWRAPPED = new String[]{"~tl~","~bs~","~tb~","~nl~"};
	
	public static String wrap(String s) {
		return replaceAll(s, WRAPPED, UNWRAPPED, 1);
	}

	public static String unwrap(String s) {
		return replaceAll(s, UNWRAPPED, WRAPPED, -1);
	}

	protected void step(int s) {
		long last = currentObject / 100;
		currentObject+=s;
		long next = currentObject / 100;
		if (next!=last) {
			processingTask.setProgress((int)((10000l*currentObject)/totalObjects));
		}
	}
	
	protected void write_DataSet(BufferedWriter bw) throws Exception {
		// Add dataSet information
		// D <Name> <#objects to display progress for>
		bw.write("D\t");
		bw.write(wrap(ds.getName()));
		bw.write("\t"+totalObjects);
		bw.write("\n");
		id(ds);
		step(1);
	}
	
	protected void write_Experiments(BufferedWriter bw) throws Exception {
		// Add Experiments
		// E <Name1> <Name2> <Name3> ...
		bw.write("E");
		for (String s : ds.getMasterTable().getExperimentNames()) {
			bw.write("\t");
			if (s==null)
				s = "*";
			bw.write(wrap(s));
		}
		bw.write("\n");
	}
	
	protected void write_MasterTable(BufferedWriter bw) throws Exception {
		// P <id> <name> <value1> <value2> ...
		for (Probe pb : ds.getMasterTable().getProbes().values()) {
			if (processingTask.hasBeenCancelled())
				return;
			bw.write("P\t");
			bw.write(id(pb));
			bw.write("\t");
			bw.write(wrap(pb.getName()));
			for (double d : pb.getValues()) {
				bw.write("\t");
				bw.write(Double.toString(d));
			}
			bw.write("\n");
			step(pb.getNumberOfExperiments());
		}
	}
	
	protected void write_ProbeLists(BufferedWriter bw) throws Exception {
		// Probelists are written in PLM ordering, which ensures parents get read before children
		// L <id> <name> <color> <parentid> <entry1> <entry2> ...
		for (ProbeList pl : ds.getProbeListManager().getProbeLists()) {
			if (processingTask.hasBeenCancelled())
				return;
			
			bw.write("L\t");
			bw.write(id(pl));
			bw.write("\t");
			bw.write(wrap(pl.getName()));
			bw.write("\t");
			bw.write(Integer.toString(pl.getColor().getRGB()));
			bw.write("\t");
			
			String parentID = "*";
			if (pl.getParent()!=null) {
				parentID = id(pl.getParent());
			}			
			bw.write(parentID);
			
			if ((pl instanceof MasterTableProbeList) || (pl instanceof UnionProbeList)) {
				bw.write("\t");
				bw.write("*");
			} else {
				for (Probe pb : pl) {
					bw.write("\t");
					bw.write(id(pb));
				}
			}
			
			bw.write("\n");
			step(1);
		}
	}
	
	protected void write_MIOs(BufferedWriter bw) throws Exception {
		// Add all MIOs - their type is not stored here but with the containing migroup
		// M <id> <value>
		MIManager mim = ds.getMIManager();
		HashSet<MIType> seen = new HashSet<MIType>();
		for (MIGroup mg : mim.getGroups()) {
			for (Entry<Object,MIType> e : mg.getMIOs()) {
				if (processingTask.hasBeenCancelled())
					return;
				MIType mt = e.getValue();
				if (seen.add(mt)) {
					String ser = mt.serialize(MIType.SERIAL_TEXT);
					if (ser!=null) {
						bw.write("M\t");
						bw.write(id(mt));
						bw.write("\t");
						bw.write(wrap(mt.serialize(MIType.SERIAL_TEXT)));
						bw.write("\n");
					} else {
						System.err.println("Dropping \"null\" MIO value in group "+mg.getPath()+"/"+mg.getName());
					}
				}
				step(1);
			}
		}
	}
	
	protected void write_MIGroups(BufferedWriter bw) throws Exception {
		MIManager mim = ds.getMIManager();
		MIGroup annotationMIGroup = null;
		
		for (MIGroup mg : mim.getGroups()) {
			if (mg.getMIOType().equals("PAS.MIO.Annotation")) // defer annotation group to the end
				annotationMIGroup = mg;
			else
				write_MIGroup(bw, mg);
		}
		write_MIGroup(bw, annotationMIGroup);
	}
	
	protected void write_MIGroup(BufferedWriter bw, MIGroup mg) throws Exception {
		// G <id> <Name> <Type> <Path> 
		// Links are stored in extra lines:
		// C <object_id> <mitype_id>
		MIManager mim = ds.getMIManager();
		String path = mim.getTreeRoot().getPathFor(mg);
		path = path.substring(0, path.lastIndexOf("/"));

		bw.write("G\t");
		bw.write(id(mg));
		bw.write("\t");
		bw.write(wrap(mg.getName()));
		bw.write("\t");
		bw.write(mg.getMIOType());
		bw.write("\t");
		bw.write(wrap(path));
		bw.write("\n");
		step(1);
		
		for (Entry<Object,MIType> e : mg.getMIOs()) {
			Integer sourceID = objectMapWRITE.get(e.getKey());
			Integer targetID = objectMapWRITE.get(e.getValue());
			if (sourceID==null || targetID==null) {
				System.err.println(
						"NOT STORED: Orphan object: \""+e.getKey()+"\" of "+e.getKey().getClass()+" referenced by a MIO in the Group \""+mg.getName()+"\"");
			} else {
				bw.write("C\t");
				bw.write(sourceID.toString());
				bw.write("\t");
				bw.write(targetID.toString());
				bw.write("\n");
			}
			step(1);
		}
	}
	
	protected void write_closingStatements(BufferedWriter bw) throws Exception  {
		bw.write("N\t");
		bw.write(id(ds.getProbeDisplayNames()));
		bw.write("\n");
	}
	
	
	protected void read_DataSet(BufferedReader br) throws Exception {
		//Dataset Properties
		String line = br.readLine();
		if (!line.startsWith("D"))
			throw new RuntimeException("Expecting DataSet line, but found: "+line);
		String[] parts = splitter.split(line,0);
		String dataSetName = unwrap(parts[1]);
		ds = new DataSet(dataSetName);
		// Dataset is always object with ID = 0
		totalObjects = Integer.parseInt(parts[2]);
		objectMapREAD.put("0",ds);		
		step(1);
	}
	
	protected void read_Experiments(BufferedReader br) throws Exception {
		String line = br.readLine();
		if (!line.startsWith("E"))
			throw new RuntimeException("Expecting Experiment line, but found: "+line);

		MasterTable mt = ds.getMasterTable();
		String[] parts = splitter.split(line,0);

		int numberOfExperiments = parts.length-1;
		mt.setNumberOfExperiments(numberOfExperiments);
		for (int i=0; i!=numberOfExperiments; ++i) {
			if (parts[i+1].equals("*"))
				parts[i+1] = null;
			else 
				parts[i+1] = unwrap(parts[i+1]);
			mt.setExperimentName(i, parts[i+1]);			
		}			
	}
	
	protected void read_MasterTable(BufferedReader br) throws Exception {
		MasterTable mt = ds.getMasterTable();
		int step = mt.getNumberOfExperiments();
		char c;
		while (br.ready()) {
			br.mark(1);
			c = (char)br.read();
			br.reset();
			if (c=='P') {
				Probe pb = read_Probe(br, mt);
				mt.addProbe(pb);
				step(step);
			} else {
				break;
			}
		} 
	}
	
	protected Probe read_Probe(BufferedReader br, MasterTable mt) throws Exception {
		String line = br.readLine();
		String[] parts = splitter.split(line,0);		
		Probe pb = new Probe(mt);
		pb.setName(unwrap(parts[2]));
		objectMapREAD.put(parts[1], pb);
		for (int j=0; j!=mt.getNumberOfExperiments(); ++j) {
			String v = parts[j+3];
			pb.addExperiment(Double.parseDouble(v));
		}
		return pb;
	}
	
	protected void read_ProbeLists(BufferedReader br) throws Exception {
		ProbeListManager plm = ds.getProbeListManager();
		
		char c;
		while (br.ready()) {
			br.mark(1);
			c = (char)br.read();
			br.reset();
			if (c=='L') {
				String line = br.readLine();
				String[] parts = splitter.split(line,0);
				String name = unwrap(parts[2]);
				Color col = new Color(Integer.parseInt(parts[3]));
				String parentID = parts[4];
				ProbeList pl;
				if (parentID.equals("*")) {
					pl = ds.getProbeListManager().getProbeLists().get(0);
				
				} else {
					if (parts.length>5 && parts[5].equals("*")) {
						pl = new UnionProbeList(ds, null);
					} else {
						pl = new ProbeList(ds, true);
						// add probes
						for (int i=5; i<parts.length; ++i) {
							Probe pb = (Probe)objectMapREAD.get(parts[i]);
							pl.addProbe(pb);
						}
					}
					pl.setName(name);
					pl.setColor(col);
					pl.setParent((UnionProbeList)objectMapREAD.get(parentID));
					plm.addObjectAtBottom(pl);
				}
				objectMapREAD.put(parts[1], pl);
				step(1);
			} else {
				break;
			}
		} 
		
	}
	
	protected void read_MIOs(BufferedReader br) throws Exception {
		// MIType objects are not parsed now because their type is unknown
		// it is only known when associating with a migroup
		char c;
		while (br.ready()) {
			br.mark(1);
			c = (char)br.read();
			br.reset();
			if (c=='M') {
				String line = br.readLine();
				String[] parts = splitter.split(line,0);
				if (parts.length<3) {
					objectMapREAD.put(parts[1], "");
				} else {
					objectMapREAD.put(parts[1], unwrap(parts[2]));
				}
				step(1);
			} else {
				break;
			}
		} 
	}
	
	protected void read_MIGroups(BufferedReader br) throws Exception {
		char c;
		while (br.ready()) {
			br.mark(1);
			c = (char)br.read();
			br.reset();
			if (c=='G') {
				read_MIGroup(br);
				step(1);
			} else {
				break;
			}
		} 		
	}
	

	protected void read_MIGroup(BufferedReader br) throws Exception {
		MIManager mim = ds.getMIManager();
		String line = br.readLine();
		String[] parts = splitter.split(line,0);
		String mioName = unwrap(parts[2]);
		String uniqueID = parts[1];
		String mioType = parts[3];
		String mioPath="";
		if (parts.length>4)
			mioPath = unwrap(parts[4]);
		
		MIGroup mg = mim.newGroup(mioType, mioName, mioPath);
		objectMapREAD.put(uniqueID, mg);
		step(1);

		// Add all contents
		char c;
		while (br.ready()) {
			br.mark(1);
			c = (char)br.read();
			br.reset();
			if (c=='C') {
				line = br.readLine();
				parts = splitter.split(line,0);
				Object source = objectMapREAD.get(parts[1]);
				Object mio = objectMapREAD.get(parts[2]);
				if (mio instanceof String) {
					MIType mt = mg.add(source);
					mt.deSerialize(MIType.SERIAL_TEXT, (String)mio);
				} else {
					mg.add(source, (MIType)mio);
				}
				step(1);
			} else {
				break;
			}
		} 	
	}

	protected void read_closingStatements(BufferedReader br) throws Exception {
		if (br.ready()) {
			String line = br.readLine();
			if (line.startsWith("N")) {
				String displayNameGroupID = splitter.split(line,0)[1];
				Object mio = objectMapREAD.get(displayNameGroupID);
				if (mio!=null)
					ds.setProbeDisplayNames((MIGroup)mio);
			}
		}
	}
	
}
