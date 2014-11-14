package mayday.dynamicpl.dataprocessor;

public class NumberInInterval_Int extends NumberInInterval_Generic<Integer> {

	@Override
	protected Boolean convert(Integer value) {
		double v = value;
		return v>=lower && v<=upper;
	}

	@Override
	public void initNumbers() {
		lower = 0;
		upper = 0;
	}

	@Override
	public Integer parseNumber(String s) {
		return Integer.parseInt(s);
	}

}
