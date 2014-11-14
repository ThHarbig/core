package mayday.core.math.scoring;

import java.util.Comparator;

/** A scoring result for scores where either smaller is better or larger is better */
public class DefaultScoringResult extends AbstractScoringResult {

	protected boolean smallerIsBetter = false;
	
	public DefaultScoringResult(boolean smallerIsBetter) {
		setSmallerScoreIsBetter(smallerIsBetter);
	}

	public void setSmallerScoreIsBetter(boolean smallerIsBetter) {
		this.smallerIsBetter = smallerIsBetter;
	}

	@Override
	public Comparator<Double> getRawScoreComparator() {
		if (smallerIsBetter) {
			return new Comparator<Double>() {

				@Override
				public int compare(Double o1, Double o2) {
					return -1 * o1.compareTo(o2);
				}

			};
		} else {
			return new Comparator<Double>() {

				@Override
				public int compare(Double o1, Double o2) {
					return o1.compareTo(o2);
				}

			};
		}
	}
}
