package mayday.core.math.binning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.linalg.Algebra;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.vis3.model.ViewModel;

public class AverageBinning extends AbstractBinningStrategy
{
	private String[] AVERAGING_METHODS={"Mean", "Mid Range"};

	private RestrictedStringSetting averageSetting=new RestrictedStringSetting("Averaging Method", null, 0, AVERAGING_METHODS);
	private BooleanSetting useSD=new BooleanSetting("Use standard deviation", "Ternary Binning", false);
	private HierarchicalSetting setting=new HierarchicalSetting("Average Binning");

	private List<Double> thresholds;

	public AverageBinning() 
	{
		setting.addSetting(averageSetting).addSetting(useSD);
	}


	@Override
	public void init() {}

	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.visualization.profilelogo.binning.average",
				new String[0],
				BinningStrategy.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Use different average values as cutoffs for discretization",
				"Average Binning"
		);		
		return pli;
	}

	public List<Double> getThresholds(ViewModel viewModel) {
		initThresholds(viewModel.asMatrix());
		return thresholds;
	}

	protected void initThresholds(AbstractMatrix am) {
		List<Double> ret = new LinkedList<Double>();

		double avg=0;
		
		if(averageSetting.getSelectedIndex()==0){
			avg=am.getMeanValue();
		}
				
		if(averageSetting.getSelectedIndex()==1){
			double min=am.getMinValue(false);
			double max=am.getMaxValue(false);

			double range= max-min;
			double binWidth=range/ 2.0;
			
			avg=max-binWidth;
		}
		
		if(useSD.getBooleanValue()){
			double sd=am.getStdDev();
			ret.add(avg-sd);
			ret.add(avg+sd);
		}else
			ret.add(avg);
		
		thresholds = ret;
	}

	@Override
	protected double[] getThresholdValues() {
		List<Double> threshs = thresholds;		
		return Algebra.createNativeArray(threshs.toArray(new Double[0]));
	}
	
	public Setting getSetting() {
		return setting;
	}

}
