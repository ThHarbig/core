package mayday.core.math.binning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.vis3.model.ViewModel;

public class ThresholdBinning extends AbstractBinningStrategy implements BinningStrategy 
{
	
	protected BinningThresholdSetting thresholds = new BinningThresholdSetting();
	
	public Setting getSetting() {
		return thresholds;
	}
	
//	public int[][] execute(ViewModel vm) 
//	{
//		List<Double> threshs = thresholds.getThresholds();
//		
//		if (threshs.size()==0) {
//			thresholds.setDoubleListValue(estimateThresholds(vm));
//		}
//		
//		int noe = vm.getDataSet().getMasterTable().getNumberOfExperiments();
//		
//		int[][] result=new int[threshs.size()+1][noe];		
//		for(int i=0; i!= noe; ++i)
//		{
//
//			for(Probe p:vm.getProbes())
//			{
//				double[] vals = vm.getProbeValues(p);
//				
//				boolean a=false;
//				for(int j=0; j!= threshs.size(); ++j )
//				{
//					if(vals[i] <= threshs.get(j))
//					{
//						result[j][i]++;
//						a=true;
//						break;
//					}
//				}
//				if(!a) result[threshs.size()][i]++;
//			}			
//		}		
//		return result;		
//	}
	

	public List<Double> getThresholds(ViewModel viewModel ) {
		return thresholds.getDoubleListValue();
	}


	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.visualization.profilelogo.binning.threshold",
				new String[0],
				BinningStrategy.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Threshold Binning",
				"Threshold Binning"
		);		
		return pli;
	}

	@Override
	public void init() {
	}
	
	private List<Double> estimateThresholds(AbstractMatrix am)
	{
		List<Double> res=new ArrayList<Double>();
		double mean=am.getMeanValue();
		double stdev=am.getStdDev();
		res.add(mean+stdev);
		res.add(mean-stdev);		
		return res;
	}
	
	protected void initThresholds(AbstractMatrix am) {
		List<Double> threshs = thresholds.getThresholds();
		
		if (threshs.size()==0) {
			thresholds.setDoubleListValue(estimateThresholds(am));
		}
	}
	
	@Override
	protected double[] getThresholdValues() {
		List<Double> threshs = thresholds.getThresholds();		
		return Algebra.createNativeArray(threshs.toArray(new Double[0]));
	}

}
