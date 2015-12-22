package mayday.dynamicpl.dataprocessor;


public class NumberMatches_Int extends NumberMatches_Generic<Integer> {

	@Override
	protected Boolean convert(Integer v)  {
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
	protected Integer parseNumber(String string) {
		return Integer.parseInt(string);
	} 


	@Override
	protected void initNumbers() {
		number = 0;
	}

}
