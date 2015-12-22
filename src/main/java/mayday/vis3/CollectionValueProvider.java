package mayday.vis3;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;

public class CollectionValueProvider extends ValueProvider {
	
	protected Collection<Double> values = new LinkedList<Double>();
	
	public CollectionValueProvider() {
		super(null, "");
	}
	
	public double getValue(Probe pb) {
		throw new RuntimeException("not implemented");
	}

	public double getMaximum() {
		if (values.size()==0)
			return Double.NaN;
		return Collections.max(values);
	}
	
	public double getMinimum() {
		if (values.size()==0)
			return Double.NaN;
		return Collections.min(values);
	}
	
	public Collection<Double> getValues() {
		return values;
	}
	
	public Map<Probe, Double> getValuesMap() {
		MasterTable theMT = new MasterTable(new DataSet(true));
		// create unreal probe for each double
		int nameInt=0;
		HashMap<Probe, Double> hdp = new HashMap<Probe, Double>();
		for (Double d : values) {
			Probe pb = new Probe(theMT);
			pb.setName(Integer.toString(++nameInt));
			hdp.put(pb, d);
		}
		return hdp;
	}
	
	public void setValues(Collection<Double> newValues) {
		values = newValues;
		fireChanged();
	}
	
	public String getSourceName() {
		return "";
	}
}