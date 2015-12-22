package mayday.core.math.binning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.vis3.model.ViewModel;

public class TSDBinning extends AbstractBinningStrategy {

	private BooleanHierarchicalSetting toleranceSetting=new BooleanHierarchicalSetting("Use tolerance", "Ternary Binning", false);
	private DoubleSetting toleranceValue=new DoubleSetting("Tolerance", null, 0.5);

	public TSDBinning() {
		toleranceSetting.addSetting(toleranceValue);
	}

	@Override
	public Setting getSetting() {
		return toleranceSetting;
	}

	@Override
	protected double[] getThresholdValues() {
		return new double[]{0};
	}

	@Override
	protected void initThresholds(AbstractMatrix am) {}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.visualization.profilelogo.binning.TSD",
				new String[0],
				BinningStrategy.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Transitional state discrimination",
				"TSD Binning"
		);		
		return pli;
	}

	@Override
	public List<Double> getThresholds(ViewModel viewModel) {
		List<Double> ret=new ArrayList<Double>();
		if(toleranceSetting.getBooleanValue()){
			ret.add(-toleranceValue.getDoubleValue());
			ret.add(toleranceValue.getDoubleValue());
		}else{
			ret.add(0.0d);
		}
		return ret;
	}


	public int[][] execute(AbstractMatrix matrix) {

		initThresholds(matrix);
		DoubleMatrix m=new DoubleMatrix(matrix);
		m.normalizeRowWise();
		int noe = m.ncol();
		int[][] result=null;
		if(!toleranceSetting.getBooleanValue()){
			int not=1;
			result=new int[not+1][noe];	
			for(int i=1; i!= noe; ++i){
				for(int j=0; j!= m.nrow(); ++j){
					if( (m.getValue(j, i) - m.getValue(j, i-1)) >= 0)
						result[1][i]=result[1][i]+1;
					else
						result[0][i]=result[0][i]+1;
				}
			}
		}else{
			double t=toleranceValue.getDoubleValue();
			int not=2;
			result=new int[not+1][noe];	
			for(int i=1; i!= noe; ++i){
				for(int j=0; j!= m.nrow(); ++j){
					double d= (m.getValue(j, i) - m.getValue(j, i-1));
					if(d >= t)
						result[2][i]=result[2][i]+1;
					else
						if(d < -t)
							result[0][i]=result[0][i]+1;
						else
							result[1][i]=result[1][i]+1;



				}
			}

		}
		return result;		
	}
}
