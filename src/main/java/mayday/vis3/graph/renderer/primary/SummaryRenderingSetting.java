package mayday.vis3.graph.renderer.primary;

import mayday.core.settings.typed.RestrictedStringSetting;

public class SummaryRenderingSetting extends RestrictedStringSetting
{
	public static final String[] methods={"mean","median","min","max","q1","q3"};
	
	public static final int MEAN=0;
	public static final int MEDIAN=1;
	public static final int MIN=2;
	public static final int MAX=3;
	public static final int Q1=4;
	public static final int Q3=5;
	
	public SummaryRenderingSetting() 
	{
		super("Summarization method","The avaraging / summarization method to be used",0,methods);
	}
	
	@Override
	public SummaryRenderingSetting clone() 
	{
		SummaryRenderingSetting s=new SummaryRenderingSetting();
		s.setSelectedIndex(getSelectedIndex());
		return s;
	}
	
	
	
}
