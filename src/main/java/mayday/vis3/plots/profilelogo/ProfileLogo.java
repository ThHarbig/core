package mayday.vis3.plots.profilelogo;


public class ProfileLogo 
{
	//int[][] result=new int[parameters.getNumberOfBins()][masterTable.getNumberOfExperiments()];
	public static double[][] produceLogo(int[][] data)
	{
		// calculate the correction value 
		int n=0;
		for(int i=0; i!= data.length; ++i){
			
			n+=data[i][1];
		}
		double en= (data.length-1) / (2.0*Math.log(2)*n);
		double[][] height=new double[data.length][data[0].length];
		for(int i=0; i!= data[0].length; ++i)
		{
			double freq[]=new double[data.length];
			
			double count=0;
			for(int j=0; j!= data.length; ++j)
			{
				count+=data[j][i];				
			}
			for(int j=0; j!= data.length; ++j)
			{
				freq[j]=(data[j][i]*1.0/count*1.0);						
			}
			double h=0;
			for(int j=0; j!= data.length; ++j)
			{
				h+= freq[j]==0?0:(freq[j]*log2(freq[j]));				
			}
			h*=-1;
			double r=log2(data.length)-(h+en);
			for(int j=0; j!= data.length; ++j)
			{
				height[j][i]= freq[j]*r;				
			}			
		}		
		return height;
	}

	
	public static double log2(double x)
	{
		return Math.log(x) / Math.log(2);
	}
}
