package mayday.mpf.options;

import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.distance.measures.EuclideanDistance;
import mayday.core.math.distance.measures.MinkowskiDistance;
import mayday.mpf.FilterOptions;

/* * @author Florian Battke
 */
public class OptDistanceMeasure extends OptPagedDropDown implements java.awt.event.ItemListener{
	
	// Find all implemented distance measures
	protected OptInteger MinkowskiParameter;
	
	public OptDistanceMeasure(FilterOptions container) {
		super("Distance metric",
				"Select what method should be used to compute distances",
				new String[]{"No distance measures found"}, 0);
		container.add(this);
		MinkowskiParameter = new OptInteger("Minkowski parameter",
				"Set a value for p in  d(x,y) = (sum | x_i - y_i |^p)^(1/p)\n" +
				"p=2 is equivalent to Euclidean distance." ,2);
		container.add(MinkowskiParameter);
	}

	public DistanceMeasurePlugin getDistanceMeasure() {
		DistanceMeasurePlugin dmt = (DistanceMeasurePlugin)getObject();  
		if (dmt instanceof MinkowskiDistance) {
			((MinkowskiDistance) dmt).getSetting().setValueString(""+MinkowskiParameter.Value);
		}
		return dmt;		
	}
	
	protected void createEditArea() {
		int i=0; int euclidean=0; int minkowski=0;
		Options = DistanceMeasureManager.values().toArray();
		for(Object o : Options) {
			if (o instanceof EuclideanDistance)
				euclidean = i;
			if (o instanceof MinkowskiDistance)
				minkowski = i;
			++i;
		}		
		if (Options.length>0) {
			Value = euclidean;
			setPageCount(Options.length);
			this.addOption(minkowski,MinkowskiParameter);			
		}
			
		super.createEditArea();
	}

}
