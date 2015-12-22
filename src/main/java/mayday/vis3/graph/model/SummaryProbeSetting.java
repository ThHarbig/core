package mayday.vis3.graph.model;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.graph.model.SummaryProbe.SummaryMode;
import mayday.vis3.graph.model.SummaryProbe.WeightMode;

public class SummaryProbeSetting extends HierarchicalSetting
{
	private RestrictedStringSetting summaryModeSetting;
	private RestrictedStringSetting weightModeSetting; 
	
	public SummaryProbeSetting()	
	{
		super("Summary Settings");
		
		summaryModeSetting=new RestrictedStringSetting("Summary method","How to summarize the reaction",0,SummaryMode.SUMMARY_OPTIONS);
		weightModeSetting=new RestrictedStringSetting("Weight method","How the reaction participants should be weighted.",0,WeightMode.WEIGHT_OPTIONS);
		
		addSetting(summaryModeSetting).addSetting(weightModeSetting);
	}
	
	public SummaryMode getSummaryMode()
	{
		if(summaryModeSetting.getStringValue().equals(SummaryMode.SUMMARY_OPTIONS[0]))
			return SummaryMode.MEAN;
		if(summaryModeSetting.getStringValue().equals(SummaryMode.SUMMARY_OPTIONS[1]))
			return SummaryMode.MEDIAN;
		if(summaryModeSetting.getStringValue().equals(SummaryMode.SUMMARY_OPTIONS[2]))
			return SummaryMode.MIN;
		if(summaryModeSetting.getStringValue().equals(SummaryMode.SUMMARY_OPTIONS[3]))
			return SummaryMode.MAX;
		if(summaryModeSetting.getStringValue().equals(SummaryMode.SUMMARY_OPTIONS[4]))
			return SummaryMode.SUM;
		if(summaryModeSetting.getStringValue().equals(SummaryMode.SUMMARY_OPTIONS[5]))
			return SummaryMode.PRODUCT;
		
		return SummaryMode.MEAN;
	}
	
	public WeightMode getWeightMode()
	{
		if(weightModeSetting.getStringValue().equals(WeightMode.WEIGHT_OPTIONS[0]))
			return WeightMode.ALL_POSITIVE;
		if(weightModeSetting.getStringValue().equals(WeightMode.WEIGHT_OPTIONS[1]))
			return WeightMode.IN_POSITIVE_OUT_NEGATIVE;	
		if(weightModeSetting.getStringValue().equals(WeightMode.WEIGHT_OPTIONS[2]))
			return WeightMode.IN_POSITIVE_OUT_ZERO;
		if(weightModeSetting.getStringValue().equals(WeightMode.WEIGHT_OPTIONS[3]))
			return WeightMode.IN_NEGATIVE_OUT_POSITIVE;	
		if(weightModeSetting.getStringValue().equals(WeightMode.WEIGHT_OPTIONS[4]))			
			return WeightMode.IN_NEGATIVE_OUT_ZERO;	
		
		return WeightMode.ALL_POSITIVE;	
	}
	
	public SummaryProbeSetting clone() 
	{
		 return (SummaryProbeSetting)reflectiveClone();
	}

	
	
}
