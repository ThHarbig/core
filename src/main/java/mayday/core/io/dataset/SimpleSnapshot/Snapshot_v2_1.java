package mayday.core.io.dataset.SimpleSnapshot;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import mayday.core.DataSet;
import mayday.core.meta.MIGroup;

/**
 * Like Snapshot v2.0, but also saves DisplayName MIO group
 * @author battke
 *
 */

public class Snapshot_v2_1 extends Snapshot_v2_0 {

	protected static final String KEY_DISPLAYNAMES = "DisplayNames";
    
	public Snapshot_v2_1() {
		super();
	}
	
	public Snapshot_v2_1(DataSet dataSet) {
		super(dataSet);
	}
	
	protected String supportedVersion() {
		return "$$ Mayday-Snapshot 2.1 $$";
	}

	public Snapshot_v2_0 olderVersion() {
		return new Snapshot_v2_0(getDataSet());
	}

	protected void write_closingStatements(BufferedWriter bw) throws Exception  {
		if (ds.getProbeDisplayNames()==null)
			bw.write(buildLine(KEY_DISPLAYNAMES, "none",0));
		else
			bw.write(buildLine(KEY_DISPLAYNAMES, ""+objectMapWRITE.get(ds.getProbeDisplayNames()),0));
		write_closingStatements1(bw);
	}
	
	protected void write_closingStatements1(BufferedWriter bw)  throws Exception {
		// for derived classes
	}
	
	protected void read_closingStatements(BufferedReader br) throws Exception {
		String displayNameGroupID = checkAndGet(br.readLine(),0); 
		Object o = objectMapREAD.get(displayNameGroupID);
		if (o instanceof MIGroup)
			ds.setProbeDisplayNames((MIGroup)o);
	}
	
	protected void read_closingStatements1(BufferedWriter bw)  throws Exception {
		// for derived classes
	}
	
}
