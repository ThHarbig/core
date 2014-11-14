package mayday.dynamicpl.dataprocessor;


public class NumberMatches_Long extends NumberMatches_Generic<Long> {

	@Override
	protected Boolean convert(Long v)  {
		switch(operation) {
		case 0: return v<number;
		case 1: return v<=number;
		case 2: return v==number;
		case 3: return v>=number;
		case 4: return v>number;
		}
		return null;
	}

	@Override
	protected Long parseNumber(String string) {
		return Long.parseLong(string);
	} 


	@Override
	protected void initNumbers() {
		number = 0l;
	}

}
