package mayday.dynamicpl.dataprocessor;

public class NumberInInterval_Long extends NumberInInterval_Generic<Long> {

	@Override
	protected Boolean convert(Long value) {
		double v = value;
		return v>=lower && v<=upper;
	}

	@Override
	public void initNumbers() {
		lower = 0l;
		upper = 0l;
	}

	@Override
	public Long parseNumber(String s) {
		return Long.parseLong(s);
	}

}
