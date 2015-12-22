package mayday.core.math.binning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.typed.DoubleListSetting;

public class BinningThresholdSetting extends DoubleListSetting
{

	public BinningThresholdSetting() {
		super("Thresholds",null,null);
	}
	
	public BinningThresholdSetting(int bins) {
		this();
		ArrayList<Double> thresholds=new ArrayList<Double>();
		for(int i=0; i!=bins-1; ++i)
			thresholds.add(0.0);
		setThresholds(thresholds);
	}
	
	public BinningThresholdSetting(List<Double> threshList)	{
		this();
		setThresholds(threshList);
	}

	public List<Double> getThresholds() {
		return getDoubleListValue();
	}

	public void setThresholds(List<Double> thresholds) {
		Collections.sort(thresholds);
		setDoubleListValue(thresholds);
	}
	
	public double getThreshold() {
		return getThresholds().get(0);
	}
	
	public double getThreshold(int i) {
		return  getThresholds().get(i);
	}
	
	public BinningThresholdSetting clone() {
		return (BinningThresholdSetting)reflectiveClone();
	};
	
	protected String fixString(String v) {
		StringListMIO slm = new StringListMIO();
		slm.deSerialize(MIType.SERIAL_TEXT, v);
		List<Double> ret = convertToDouble(slm.getValue());
		Collections.sort(ret);
		slm.setValue(convertFromDouble(ret));
		return slm.serialize(MIType.SERIAL_TEXT);
	}
	
	public void setValueString(String v) {
		super.setValueString(fixString(v));
	}
	
}
