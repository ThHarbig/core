package mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures;


public class TranslatedKey {
 
	protected long translatedKeyFirst;
	protected long translatedKeyLast;
	
	public TranslatedKey(int maxCellNumber, long translatekKeyFirst, long translatedKeyLast){
		this.translatedKeyFirst = translatekKeyFirst;
		this.translatedKeyLast = translatedKeyLast;
		if (this.translatedKeyLast > maxCellNumber){
			this.translatedKeyLast = maxCellNumber;
		}
	}
	
	public long getTranslatedKeyFirst(){
		return this.translatedKeyFirst;
	}
	
	public long getTranslatedKeyLast(){
		return this.translatedKeyLast;
	}
}
