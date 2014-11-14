package mayday.core.settings.typed;

import java.util.List;

import mayday.core.math.average.AbstractAverage;
import mayday.core.math.average.AverageType;
import mayday.core.math.average.IAverage;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;

public class AveragingSetting extends SelectableHierarchicalSetting {	

	public final static String MAX = "Maximum";
	public final static String MIN = "Minimum";
	public final static String AVG = "Average";


	protected ObjectSelectionSetting<AverageType> averageType;

	@SuppressWarnings("unchecked")
	public AveragingSetting() {
		super("Summarization method","Select how multiple values should be summarized",
				2, new Object[]{
				MAX,
				MIN,
				new ObjectSelectionSetting<AverageType>("Average", null, 0, AverageType.values())
		}
		);
		averageType = (ObjectSelectionSetting<AverageType>)getChild("Average");
	}

	public AveragingSetting clone() {
		return (AveragingSetting)reflectiveClone();
	}

	public IAverage getSummaryFunction() {
		Object sel = getObjectValue();
		if (sel==MAX)
			return new AbstractAverage() {
			public double getAverage(double[] x, boolean ignoreNA) {
				double m = Double.NEGATIVE_INFINITY;
				for (double d : x)
					if (!Double.isNaN(d) && d>m)
						m=d;
				return m;
			}

			public double getAverage(List<Double> x, boolean ignoreNA) {
				double m = Double.NEGATIVE_INFINITY;
				for (double d : x)
					if (!Double.isNaN(d) && d>m)
						m=d;
				return m;
			}
			public String toString() {
				return "Maximum";
			}
		};

		if (sel==MIN)
			return new AbstractAverage() {
			public double getAverage(double[] x, boolean ignoreNA) {
				double m = Double.POSITIVE_INFINITY;
				for (double d : x)
					if (!Double.isNaN(d) && d<m)
						m=d;
				return m;
			}

			public double getAverage(List<Double> x, boolean ignoreNA) {
				double m = Double.POSITIVE_INFINITY;
				for (double d : x)
					if (!Double.isNaN(d) && d<m)
						m=d;
				return m;
			}
			public String toString() {
				return "Minimum";
			}
		};
		
		return averageType.getObjectValue().createInstance();
	}


}
