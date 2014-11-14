package mayday.core.math.scoring;

import java.util.Comparator;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;

public interface ScoringResult {
	
	/** Statistics can return additional information for each element. 
	 * These are returned as meta information objects*/
	public MIGroupSelection<MIType> getAdditionalValues();
	
	/** Provide access to raw statistics result, e.g. F values for F test */
	public MIGroup getRawScore();
	
	/** Provide access to raw statistics result, e.g. F values for F test */
	public boolean hasRawScore();
	
	/** A comparator to tell which value is "better" (sometimes smaller is better, sometimes it isn't)*/
	public Comparator<Double> getRawScoreComparator();

}
