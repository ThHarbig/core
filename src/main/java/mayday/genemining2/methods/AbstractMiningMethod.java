package mayday.genemining2.methods;

import mayday.core.math.scoring.ScoringPlugin;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class AbstractMiningMethod extends ScoringPlugin {
		
	protected class Gene<AnnotationType> implements Comparable<Gene<?>> {
		double dataValue = 0.0;
		AnnotationType annotationValue;

		public Gene(double value, AnnotationType annotation) {
			this.dataValue = value;
			this.annotationValue = annotation;
		}

		public AnnotationType getAnnotationValue() {
			return annotationValue;			
		}
		
		public double getDataValue() {
			return dataValue;
		}
		
		@Override
		public int compareTo(Gene<?> g) {
			if (this.dataValue < g.dataValue)
				return -1;
			if (this.dataValue > g.dataValue)
				return 1;
			return 0;
		}
	}
}
