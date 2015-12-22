/*
 *  Created on Aug 16, 2004
 *
 */
package mayday.core.math.distance.measures;

import java.util.HashMap;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.DoubleSetting;


/**
 * Minkowski Distance, the p norm<p>
 * 
 *   d(x,y) = (sum | x_i - y_i |^p)^(1/p) 
 * 
 * <p>p will be initialized with 2 if not else defined in 
 * constructor (equivalent to euclidean distance).
 * 
 * @author Markus Riester
 * @version 0.1
 */
public class MinkowskiDistance extends DistanceMeasurePlugin {

	protected DoubleSetting power;
	
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
					(Class)this.getClass(),
					"PAS.distance.Minkowski",
					new String[0],
					MC,
					new HashMap<String, Object>(),
					"Markus Riester",
					"riester@informatik.uni-tuebingen.de",
					"The Minkowski distance: (sum | x_i - y_i |^p)^(1/p)<br>" +
					"p=1 : The Manhattan Distance<br>" +
					"p=2 : The Euclidean Distance",
					"Minkowski"
					);
			return pli;
	}
	
	public MinkowskiDistance() {
		//empty for pluginmanager
	}
	
	public MinkowskiDistance(Double Power) {
		getSetting();
		power.setDoubleValue(Power);
	}
	
	public Setting getSetting() {
		if (power==null) {
			power = new DoubleSetting("Power",null,3);
			PluginInfo.loadDefaultSettings(power, "PAS.distance.Minkowski");
		}
		return power;
	}
	
    public double getDistance(double[] VectorOne, double[] VectorTwo) {
    	if (power==null) {
    		SettingDialog md = new SettingDialog(null, "Minkowski Distance", getSetting());
    		md.setModal(true);
    		md.setVisible(true);
    	}
        dimensionCheck(VectorOne, VectorTwo);
		double Distance = 0.0;
		double power = this.power.getDoubleValue();
		for (int x = 0; x < VectorOne.length; x++) {
			double d = Math.abs( (VectorOne[x] - VectorTwo[x]) );
			Distance += Math.pow(d, power);
		}
		Distance = Math.pow(Distance, (1.0/power));
		return Distance;
    }

    public String toString() {
    	return super.toString()+(power!=null?" Power "+power.getDoubleValue():"");
    }
}
