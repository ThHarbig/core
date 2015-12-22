package mayday.genetics.locusmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.meta.MIGroup;
import mayday.genetics.LocusMIO;

@SuppressWarnings("serial")
public class LocusMapContainer extends HashMap<String, LocusMap>{

	public static LocusMapContainer INSTANCE = new LocusMapContainer();
	
	private LocusMapContainer() {		
	}
	
	public List<LocusMap> list() {
		return new LinkedList<LocusMap>(values());
	}
	
	public void add(LocusMap lm) {
		// names should be unique!
		int i=0;
		String name = lm.getName();
		while (get(name)!=null) {
			name = lm.getName()+" ("+(++i)+")";
		}
		lm.name = name;		
		put(name, lm);
	}
	
	public void updateFromMIOs() {
		for (DataSet ds : DataSetManager.singleInstance.getDataSets())
			for (MIGroup mg : ds.getMIManager().getGroupsForType(LocusMIO.myType)) 
				if (!containsKey(mg.getMIManager().getDataSet().getName()+": "+mg.getName()))
					add(new LocusMap(mg));
	}
	
	public void writeToStream(BufferedWriter bw) throws IOException {
		for (LocusMap lm : values())
			lm.writeToStream(bw);		
	}
	
	public void readFromStream(BufferedReader br) throws IOException {		
		while(br.ready()) {
			LocusMap lm = new LocusMap(br);
			add(lm);
		}
	}
	
}
