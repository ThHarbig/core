package mayday.core.meta.types;

import java.util.HashMap;
import java.util.LinkedList;

import mayday.core.DataSet;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class TimeseriesMIO extends DoubleListMIO {
	
	public final static String myType = "PAS.MIO.TimeSeries";
	
	public void init() {
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Represents the time points associated with each experiment in a timeseries dataset",
				"TimeSeries MIO"
				);
	}

	public TimeseriesMIO clone() {
		TimeseriesMIO slm = new TimeseriesMIO();
		slm.deSerialize(MIType.SERIAL_TEXT, this.serialize(SERIAL_TEXT));
		return slm;
	}

	public String getType() {
		return myType;
	}

	public boolean applicableTo(DataSet ds) {
		return ds.getMasterTable().getNumberOfExperiments()==Value.size();
	}
	
	
	public static TimeseriesMIO addToDataSet(DataSet ds, boolean infer) {
		MIGroup mg = getGroupInstance(ds.getMIManager());
		TimeseriesMIO tsm = new TimeseriesMIO();
		LinkedList<Double> value = parseExperimentNames(ds);
		if (infer) {
			while(value.size()<ds.getMasterTable().getNumberOfExperiments())
				value.add((double)value.size());
		}		
		tsm.setValue(value);
		if (tsm.applicableTo(ds))			
			mg.add(ds, tsm);
		else 
			tsm = null;
		return tsm;
	}
	
	public static TimeseriesMIO getForDataSet(DataSet ds) {
		return getForDataSet(ds, false, false);
	}
	
	public static TimeseriesMIO getForDataSet(DataSet ds, boolean create, boolean infer) {
		MIGroup mg = getGroupInstance(ds.getMIManager());
		MIType mt = mg.getMIO(ds);
		if (mt==null && create) 
			mt = addToDataSet(ds, infer);
		return (TimeseriesMIO)mt;		
	}
	
	public static MIGroup getGroupInstance(MIManager mim) {
		MIGroupSelection<MIType> mgs = mim.getGroupsForType(TimeseriesMIO.myType);
		MIGroup mg;
		if (mgs.size()==0) {
			mg = mim.newGroup(TimeseriesMIO.myType, "Timepoints");
		} else {
			mg = mgs.get(0);
		}
		return mg;
	}
	
	protected static LinkedList<Double> parseExperimentNames( DataSet ds ) {
		// check for common prefixes
		String prefix = (ds.getMasterTable().getNumberOfExperiments()>0?ds.getMasterTable().getExperimentDisplayName(0):"");
		for (String name : ds.getMasterTable().getExperimentDisplayNames()) {
			int lastIndex = 0; 
			while (lastIndex<name.length() 
					&& lastIndex<prefix.length() 
					&& name.charAt(lastIndex)==prefix.charAt(lastIndex))
				++ lastIndex;
			prefix = prefix.substring(0,lastIndex);
		}
		if (prefix.length()>0)
			System.out.println("Timepoint Parser: Removing common prefix \""+prefix+"\"");
		
		LinkedList<Double> ret = new LinkedList<Double>();
		for (String name : ds.getMasterTable().getExperimentDisplayNames()) {
			Double tp = null;
			try {
				tp = Double.parseDouble(name.substring(prefix.length()));
				ret.add(tp);
			} catch (NumberFormatException nfe) {
				return new LinkedList<Double>();
			}
				
		}
		return ret;
	}
	
}
