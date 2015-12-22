package mayday.vis3;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mayday.core.Probe;

public class MapValueProvider extends ValueProvider {
	
	protected Map<Probe,Double> values = new HashMap<Probe, Double>();
	protected String sourceName;
	
	public MapValueProvider(String sourceName) {
		super(null, sourceName);
		this.sourceName=sourceName;
	}
	
	public double getValue(Probe pb) {
		return values.get(pb);
	}

	public double getMaximum() {
		if (values.size()==0)
			return Double.NaN;
		return Collections.max(values.values());
	}
	
	public double getMinimum() {
		if (values.size()==0)
			return Double.NaN;
		return Collections.min(values.values());
	}
	
	public Collection<Double> getValues() {
		return values.values();
	}
	
	public Map<Probe, Double> getValuesMap() {
		return values;
	}
	
	public void setValues(Map<Probe,Double> newValues) {
		values = newValues;
		fireChanged();
	}
	
	public String getSourceName() {
		return sourceName;
	}
}