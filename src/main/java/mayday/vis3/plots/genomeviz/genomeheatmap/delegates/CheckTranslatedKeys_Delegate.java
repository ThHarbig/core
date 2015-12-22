package mayday.vis3.plots.genomeviz.genomeheatmap.delegates;

public class CheckTranslatedKeys_Delegate {

	/**
	 * check if the translated key is valid.
	 * @param translatedKeyLow
	 * @param translatedKeyHigh
	 * @param originalNumberOfCell
	 * @return
	 */
	public static long execute(long translatedKeyLow, long translatedKeyHigh, int originalNumberOfCell){
		// Check if numberOfOrigCell is greater than existing cells
		
		if (translatedKeyHigh > originalNumberOfCell){
			translatedKeyHigh = originalNumberOfCell;
		}
		
		if(translatedKeyLow > originalNumberOfCell){
			System.err.println("ChromeHeatMapTableModel - translatedKeyLow is to high! " + translatedKeyLow);
		}
		if(translatedKeyHigh <=0 || translatedKeyLow <= 0){
			System.err.println("ChromeHeatMapTableModel - translatedKey is to low! " + translatedKeyLow + " " + translatedKeyHigh);
		}
		return translatedKeyHigh;
	}
}
