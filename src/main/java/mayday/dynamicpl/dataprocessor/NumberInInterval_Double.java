package mayday.dynamicpl.dataprocessor;

public class NumberInInterval_Double extends NumberInInterval_Generic<Double> {

	@Override
	protected Boolean convert(Double value) {
		double v = value;
		return v>=lower && v<=upper;
	}

	@Override
	public void initNumbers() {
		lower = 0.0d;
		upper = 0.0d;
	}

	@Override
	public Double parseNumber(String s) {
		return Double.parseDouble(s);
	}

}
