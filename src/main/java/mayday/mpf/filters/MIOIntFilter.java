package mayday.mpf.filters;

import mayday.core.meta.types.IntegerMIO;
import mayday.mpf.options.OptInteger;
import mayday.mpf.options.OptPagedDropDown;

/**
 * This filter removes probes based on MIO content. During execution it opens a popup window where
 * the user can select which MIO group to use. In batchmode, the user can decide to use the same MIO group
 * for all jobs.
 * @author Florian Battke
 */
public class MIOIntFilter extends MIOAbstractFilter<IntegerMIO> {

	private OptPagedDropDown filtermode = new OptPagedDropDown("Match integers that...",
			"Select how to filter",
			new String[]{"are exactly equal to a given value",
						 "are > a given value",
						 "are < a given value",
						 "fall into a given interval"},0);
	private OptInteger cmpval = new OptInteger("Value","The value to compare against",0);
	private OptInteger intlow = new OptInteger("Lower bound", "The lowest value still in the corridor",-10);
	private OptInteger inthig = new OptInteger("Upper bound","The highest value still in the corridor",10);
	
	public MIOIntFilter() {
		super("Numeric meta information (integer values)",
				"Removes probes based on meta information objects containing integer values.\n" +
				"May require user interaction during execution (selection of a MIO Group).",
				IntegerMIO.class, MIOIntFilter.class);
		filtermode.addOption(0,cmpval);
		filtermode.addOption(1,cmpval);
		filtermode.addOption(2,cmpval);
		filtermode.addOption(3,intlow);
		filtermode.addOption(3,inthig);
		Options.add(filtermode);
		Options.add(cmpval);
		Options.add(intlow);
		Options.add(inthig);
		addCommonOptions();
	}

	protected boolean checkMatch(IntegerMIO d) {
		switch(filtermode.Value) {
		case 0: return d.getValue()==cmpval.Value;
		case 1: return d.getValue()>cmpval.Value;
		case 2: return d.getValue()<cmpval.Value;
		case 3: return (d.getValue()>=intlow.Value && d.getValue()<=inthig.Value);
		}
		return false; // should never happen
	}
	
}