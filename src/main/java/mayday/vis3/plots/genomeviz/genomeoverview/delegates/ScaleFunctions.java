package mayday.vis3.plots.genomeviz.genomeoverview.delegates;

import mayday.vis3.plots.genomeviz.genomeoverview.delegates.ChromosomeMarker;


public abstract class ScaleFunctions {
	
	/**
	 * returns index of tickmarks which is necessary to paint.
	 * @param val_range
	 * @param tickmarks
	 * @param lb
	 * @param rb
	 */
//	public static void computeScaleValues(int[] range,
//			double[] tickmarks, Double lb, Double rb) {
//		if (lb == Double.NaN && rb == Double.NaN){
//			range[0]=0;
//			range[1]=tickmarks.length - 1;
//		}
//		else{
//			int index=0;
//			while(index<=tickmarks.length - 1 && tickmarks[index]<lb){
//				range[0]=index;
//				index++;
//			}
//				
//			while(index<=tickmarks.length - 1 && tickmarks[index]<=rb){
//				range[1]=index;
//				index++;
//			}
//		}
//	}

	public static void computeScaleValues(int[] range,
			Double lb, Double rb, double min, double max, int n) {

		if (lb == Double.NaN && rb == Double.NaN){
			range[0]=0;
			range[1]=n - 1;
		} else {
//			int index=0;
			
			range[0] = ChromosomeMarker.tickToIndex(lb, min, max, n);
			range[1] = ChromosomeMarker.tickToIndex(rb, min, max, n);
			
//			System.out.print(range[0]+" "+range[1]);
//			
//			while(index < n && ChromosomeMarker.tickmark_power_unit(index, min, max, n).tick <lb){
//				range[0]=index;
//				index++;
//			}
//				
//			while(index < n && ChromosomeMarker.tickmark_power_unit(index, min, max, n).tick <= rb){
//				range[1]=index;
//				index++;
//			}
//			System.out.println("\t"+range[0]+" "+range[1]);
		}
	}
	
	public static int computeDigits(int[] range, double min, double max, int n) {
		double minAbsDifference = computeMinAbsDifference(range,min,max,n);
		double log = Math.log10(minAbsDifference);
		return (int) Math.floor(log);
	}

	private static double computeMinAbsDifference(int[] range, double min, double max, int n) {
		double minAbsDifference = Double.MAX_VALUE;
		Double prev = null;
		for (int i= range[0]; i <= range[1]; i++) {
			if (prev != null) {
				minAbsDifference = Math.min(minAbsDifference, Math.abs(ChromosomeMarker.tickmark_power_unit(i, min, max, n).tick- prev));
			} else {
				prev = ChromosomeMarker.tickmark_power_unit(i, min, max, n).tick;
			}
		}
		return minAbsDifference;
	}

	
	/**
	 * 
	 * @param data
	 * @return
	 */
//	public static int computeDigits(int[] range, double[] tickmarks) {
//		double minAbsDifference = computeMinAbsDifference(range,tickmarks);
//		double log = Math.log10(minAbsDifference);
//		return (int) Math.floor(log);
//	}
//
//	
//	
//	/**
//	 * 
//	 * @param data
//	 * @return
//	 */
//	private static double computeMinAbsDifference(int[] range, double[] tickmarks) {
//		double minAbsDifference = Double.MAX_VALUE;
//		Double prev = null;
//		for (int i= range[0]; i <= range[1]; i++) {
//			if (prev != null) {
//				minAbsDifference = Math.min(minAbsDifference, Math.abs(tickmarks[i]- prev));
//			} else {
//				prev = tickmarks[i];
//			}
//		}
//		return minAbsDifference;
//	}

	/**
	 * 
	 * @return
	 */
	public static int computeMarkerDiff() {
		int markerdiff=70;
		return markerdiff;
	}
}
