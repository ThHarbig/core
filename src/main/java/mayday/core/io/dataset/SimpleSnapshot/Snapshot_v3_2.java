package mayday.core.io.dataset.SimpleSnapshot;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.MasterTable;
import mayday.core.meta.MIGroup;

// adds experiment display names

public class Snapshot_v3_2 extends Snapshot_v3_1 {
	
	public Snapshot_v3_2() {
	}
	
	public Snapshot_v3_2(DataSet dataSet) {
		super(dataSet);
	}
	
	@Override
	public Snapshot_v2_0 olderVersion() {
		return new Snapshot_v3_1();
	}

	@Override
	protected String supportedVersion() {
		return "$$ Mayday-Snapshot 3.2 $$";
	}
	
	protected void write_closingStatements(BufferedWriter bw) throws Exception  {
		bw.write("N\t");
		bw.write(id(ds.getProbeDisplayNames()));
		bw.write("\t");
		bw.write(id(ds.getExperimentDisplayNames()));
		bw.write("\n");
	}
	
	
	protected void read_closingStatements(BufferedReader br) throws Exception {
		if (br.ready()) {
			String line = br.readLine();
			if (line.startsWith("N")) {
				String[] split = splitter.split(line,0);
				String probeDisplayNameGroupID = split[1];
				Object pmio = objectMapREAD.get(probeDisplayNameGroupID);
				if (pmio!=null)
					ds.setProbeDisplayNames((MIGroup)pmio);
				String experimentDisplayNameGroupID = split[2];
				Object emio = objectMapREAD.get(experimentDisplayNameGroupID);
				if (emio!=null)
					ds.setExperimentDisplayNames((MIGroup)emio);
				
			}
		}
	}
	
	protected void write_Experiments(BufferedWriter bw) throws Exception {
		// Add Experiments
		// E <id> <Name>
		for (Experiment e : ds.getMasterTable().getExperiments()) {
			bw.write("E");
			bw.write("\t");
			bw.write(id(e));
			bw.write("\t");
			String s = e.getName();
			if (s==null)
				s = "*";
			bw.write(wrap(s));
			bw.write("\n");
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
			} else {
				break;
			}
		}
	}
	
	protected Experiment read_Experiment(BufferedReader br, MasterTable mt) throws Exception {
		String line = br.readLine();
		if (!line.startsWith("E"))
			throw new RuntimeException("Expecting Experiment line, but found: "+line);

		String[] parts = splitter.split(line,0);
		String name = parts[2];
		if (name.equals("*"))
			name = null;
		else 
			name = unwrap(name);		
		Experiment e = new Experiment(mt, name);
		objectMapREAD.put(parts[1], e);
		return e;
	}
	
	
}
