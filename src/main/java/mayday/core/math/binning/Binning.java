package mayday.core.math.binning;

import java.util.HashSet;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.vis3.model.ViewModel;


public class Binning {

	public PluginInstanceSetting<AbstractBinningStrategy> strategy = 
		new PluginInstanceSetting<AbstractBinningStrategy>("Binning",null,new EqualWidthBinning(), getStrategies());
	
	
	protected static Set<AbstractBinningStrategy> getStrategies() {
		Set<AbstractBinningStrategy> s = new HashSet<AbstractBinningStrategy>();
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(BinningStrategy.MC))
			s.add((AbstractBinningStrategy)pli.newInstance());
		return s;
	}
	
	public int getNumberOfBins(ViewModel vm) {
		return strategy.getInstance().getThresholds(vm).size()+1;
	}

	public AbstractBinningStrategy getStrategy() {
		return strategy.getInstance();
	}
	
	
}
