package mayday.vis3.graph.renderer.primary;

import mayday.core.Probe;
import mayday.core.ProbeList;

public class ProbeSummary 
{
	private SummaryRenderingSetting setting;
	
	public ProbeSummary(SummaryRenderingSetting setting)
	{
		this.setting=setting;
	}
	
	public double[] summarize(Iterable<Probe> probes)
	{
	
		
		return summarizeByProbeList(probes);
	}
	
	private double[] summarizeByProbeList(Iterable<Probe> probes)
	{
		if(probes.iterator().hasNext()==false)
			return new double[]{};
		ProbeList pl=new ProbeList(probes.iterator().next().getMasterTable().getDataSet(),false);
		for(Probe p:probes)
			pl.addProbe(p);
		
		if(setting.getSelectedIndex()==SummaryRenderingSetting.MEAN)
			return pl.getStatistics().getMean().getValues();
		if(setting.getSelectedIndex()==SummaryRenderingSetting.MEDIAN)
			return pl.getStatistics().getMedian().getValues();
		if(setting.getSelectedIndex()==SummaryRenderingSetting.Q1)
			return pl.getStatistics().getQ1().getValues();
		if(setting.getSelectedIndex()==SummaryRenderingSetting.Q3)
			return pl.getStatistics().getQ3().getValues();
		if(setting.getSelectedIndex()==SummaryRenderingSetting.MIN)
		{
			double[] res=new double[pl.getDataSet().getMasterTable().getNumberOfExperiments()];
			for(int i=0; i!=res.length; ++i)
			{
				res[i]=pl.getMinValue(i);
			}
			return res;
		}
		if(setting.getSelectedIndex()==SummaryRenderingSetting.MAX)
		{
			double[] res=new double[pl.getDataSet().getMasterTable().getNumberOfExperiments()];
			for(int i=0; i!=res.length; ++i)
			{
				res[i]=pl.getMaxValue(i);
			}
			return res;
		}
		
		return null;
	}
}
