package mayday.vis3.plots.profilelogo;

public class ProfileLogoData 
{
	private double[][] data;
	private double maxValue;
	
	public ProfileLogoData(double[][] data, double maxValue) 
	{
		this.data = data;
		this.maxValue = maxValue;
	}
	
	public int getBinCount()
	{
		return data.length;
	}
	
	public double at(int i, int j)
	{
		return data[i][j];
	}
	
	public int getExperimentCount()
	{
		return data[0].length;
	}
	
	public double[][] getData() {
		return data;
	}
	public void setData(double[][] data) {
		this.data = data;
	}
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	
	
}
