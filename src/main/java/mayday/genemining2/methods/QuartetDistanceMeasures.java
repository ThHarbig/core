package mayday.genemining2.methods;

import mayday.core.settings.generic.ObjectSelectionSetting;

public class QuartetDistanceMeasures {
	
	public final static String[] DISTANCEMEASURES = new String[]{"quadratic","euclidean", "canberra", "manhattan", "tanimoto"};
	
	public static ObjectSelectionSetting<String> createSetting() {
		return new ObjectSelectionSetting<String>("Distance Measure", null, 0, DISTANCEMEASURES);
	}
	
	public static double getDistance(double a, double b, int distType) {		
		switch (distType) {
		case 0 : // quadratic
			return (a-b) * (a-b);
		case 1: // euclidean
			return Math.sqrt((a-b) * (a-b));
		case 2: // canberra
			double nominator = Math.abs( a - b ); 
			double denominator = Math.abs( a + b );
			double dist = 0;
			if(denominator!=0){
				dist = nominator/denominator;
			}
			return dist;
		case 3: // manhattan
			return Math.abs(a-b);
		case 4: // tanimoto
			return (a*b) / ( (a*a) + (b*b) - (a*b) );
		}
		throw new IllegalArgumentException("Selected Distance Type : "+distType+" is not implemented");
	}

	
	

}
