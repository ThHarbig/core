package mayday.core.io.dataset.SimpleSnapshot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.meta.HugeMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginManager;
import mayday.core.tasks.AbstractTask;

//adds huge mios

public class Snapshot_v3_1 extends Snapshot_v3_0 {
	
	protected HashMap<String, MIType> hugeMIOs;

	public Snapshot_v3_1() {
	}
	
	public Snapshot_v3_1(DataSet dataSet) {
		this();
		setDataSet(dataSet);
	}
	
	@Override
	public Snapshot_v2_0 olderVersion() {
		return new Snapshot_v3_0();
	}

	@Override
	protected String supportedVersion() {
		return "$$ Mayday-Snapshot 3.1 $$";
	}
	
	protected void SaveToStream(BufferedWriter bw) throws Exception {
		super.SaveToStream(bw);
		bw.flush();
		if (hugeMIOs.size()>0)
			write_HugeMIOs();
	}


	protected void LoadFromStream(BufferedReader br) throws OldFormatException, Exception {
		super.LoadFromStream(br);
		if (hugeMIOs.size()>0)
			read_HugeMIOs();
	}
	
	protected void prepare() {
		super.prepare();
		if (ds!=null) {
			MIGroupSelection<MIType> mgs = ds.getMIManager().getGroups();
			totalObjects = 1
					   	+ds.getMasterTable().getNumberOfProbes()*ds.getMasterTable().getNumberOfExperiments()
					   	+ds.getMasterTable().getNumberOfExperiments()
					   	+mgs.size();
			for (MIGroup mg : mgs)
				totalObjects+=(2*mg.getMIOs().size()); // once for the mio, once for the link
		}
		currentObject = 0;
		hugeMIOs = new HashMap<String,MIType>();
	}
	
	protected void write_Experiments(BufferedWriter bw) throws Exception {
		// Add Experiments
		// E <id> <Name>
		for (Experiment e : ds.getMasterTable().getExperiments()) {
			bw.write("E\t");
			bw.write(id(e));
			bw.write("\t");
			String s = e.getName();
			if (s==null)
				s = "*";
			bw.write(wrap(s));
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
					String ser;
					if (mt instanceof HugeMIO) {
						ser = ds.getName()+".MIO."+id(mt);
						hugeMIOs.put(ser, mt);
					} else {
						ser = mt.serialize(MIType.SERIAL_TEXT);
					}
					if (ser!=null) {
						bw.write("M\t");
						bw.write(id(mt));
						bw.write("\t");
						bw.write(wrap(ser));
						bw.write("\n");
					} else {
						System.err.println("Dropping \"null\" MIO value in group "+mg.getPath()+"/"+mg.getName());
					}
				}
				step(1);
			}
		}
	}

	
	protected void read_Experiments(BufferedReader br) throws Exception {
		MasterTable mt = ds.getMasterTable();
		char c;
		while (br.ready()) {
			br.mark(1);
			c = (char)br.read();
			br.reset();
			if (c=='E') {
				Experiment e = read_Experiment(br, mt);
				mt.addExperiment(e);
				step(1);
			} else {
				break;
			}
		}
	}
	
	protected Experiment read_Experiment(BufferedReader br, MasterTable mt) throws Exception {
		String[] parts = splitter.split(br.readLine(),0);
		Experiment e = new Experiment(mt, unwrap(parts[2]));
		objectMapREAD.put(parts[1], e);
		return e;
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
					if (mt instanceof HugeMIO) {
						hugeMIOs.put((String)mio, mt);
					} else {
						mt.deSerialize(MIType.SERIAL_TEXT, (String)mio);						
					}
					objectMapREAD.put(parts[2], mt); // replace string with actual instance here
				} else {
					mg.add(source, (MIType)mio);
				}
				step(1);
			} else {
				break;
			}
		} 	
	}
	
	protected void write_HugeMIOs() {
		long before=System.currentTimeMillis();
		AbstractTask at = new AbstractTask("Writing large metadata") {
			
			@Override
			protected void initialize() {}
			
			@Override
			protected void doWork() throws Exception {
				int c=0;
				for (Entry<String, MIType> entry : hugeMIOs.entrySet()) {
					setProgress(10000*c/hugeMIOs.size(), 
							"Writing "+PluginManager.getInstance().getPluginFromID(entry.getValue().getType()).getName());
					OutputStream os = streamprovider.getOutputStream(entry.getKey());
					os = new BufferedOutputStream(os);
					HugeMIO hm = (HugeMIO)entry.getValue();
					hm.serializeHuge(os);
					os.flush();
					++c;
				}
				setProgress(10000);
			}
		};
		
		at.start();
		at.waitFor();
		long after=System.currentTimeMillis();
		System.out.println("Wrote large mios in "+(after-before)+" ms");

	}
	
	protected void read_HugeMIOs() {
		long before=System.currentTimeMillis();
		AbstractTask at = new AbstractTask("Reading large metadata") {
			
			@Override
			protected void initialize() {}
			
			@Override
			protected void doWork() throws Exception {
				int c=0;
				for (Entry<String, MIType> entry : hugeMIOs.entrySet()) {
					setProgress(10000*c/hugeMIOs.size(), 
							"Reading "+PluginManager.getInstance().getPluginFromID(entry.getValue().getType()).getName());
					InputStream is = streamprovider.getInputStream(entry.getKey());
					is = new BufferedInputStream(is);
					HugeMIO hm = (HugeMIO)entry.getValue();
					hm.deserializeHuge(is);
					++c;
				}
				setProgress(10000);
			}
		};
		
		at.start();
		at.waitFor();
		long after=System.currentTimeMillis();
		System.out.println("Read large mios in "+(after-before)+" ms");
	}
	
}
