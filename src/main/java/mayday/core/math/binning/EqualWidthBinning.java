package mayday.core.math.binning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.vis3.model.ViewModel;

public class EqualWidthBinning extends AbstractBinningStrategy implements BinningStrategy 
{
	
	private IntSetting numberOfBins = new IntSetting("Number of bins",null,3,1,null,false,true);
	
	private List<Double> thresholds;
	
	public Setting getSetting() {
		return numberOfBins;
	}
	
//	public int[][] execute(ViewModel vm) 
//	{
//		computeThresholds(vm);
//		
//		int noe = vm.getDataSet().getMasterTable().getNumberOfExperiments();
//		
//		int[][] result=new int[thresholds.size()+1][noe];		
//		for(int i=0; i!= noe; ++i)
//		{
//
//			for(Probe p:vm.getProbes())
//			{
//				double[] vals = vm.getProbeValues(p);
//				
//				boolean a=false;
//				for(int j=0; j!= thresholds.size(); ++j )
//				{
//					if(vals[i] <= thresholds.get(j))
//					{
//						result[j][i]++;
//						a=true;
//						break;
//					}
//				}
//				if(!a) result[thresholds.size()][i]++;
//			}			
//		}		
//		return result;		
//	}

	@Override
	public void init() {		
	}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.visualization.profilelogo.binning.equalwidth",
				new String[0],
				BinningStrategy.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Equal-Width Binning",
				"Equal-Width Binning"
		);		
		return pli;
	}

	public List<Double> getThresholds(ViewModel viewModel) {
		if (thresholds==null || thresholds.size()!=numberOfBins.getIntValue()-1)
			initThresholds(viewModel.asMatrix());
		return thresholds;
	}
	
	protected void initThresholds(AbstractMatrix am) {
		List<Double> ret = new LinkedList<Double>();
		
		double min=am.getMinValue(false);
		double max=am.getMaxValue(false);
			
		double range= max-min;
		double binWidth=range/ numberOfBins.getIntValue();
		
		double sofar=min;
		for(int i=0; i!=numberOfBins.getIntValue()-1; ++i )
		{
			sofar+=binWidth;
			ret.add(sofar);

		}
		thresholds = ret;
	}

	@Override
	protected double[] getThresholdValues() {
		List<Double> threshs = thresholds;		
		return Algebra.createNativeArray(threshs.toArray(new Double[0]));
	}

}
