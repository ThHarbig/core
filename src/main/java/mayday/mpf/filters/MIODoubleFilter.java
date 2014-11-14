package mayday.mpf.filters;

import mayday.core.meta.types.DoubleMIO;
import mayday.mpf.options.OptDouble;
import mayday.mpf.options.OptPagedDropDown;

/**
 * This filter removes probes based on MIO content. During execution it opens a popup window where
 * the user can select which MIO group to use. In batchmode, the user can decide to use the same MIO group
 * for all jobs.
 * @author Florian Battke
 */
public class MIODoubleFilter extends MIOAbstractFilter<DoubleMIO> {

	private OptPagedDropDown filtermode = new OptPagedDropDown("Match double numbers that...",
			"Select how to filter",
			new String[]{"are exactly equal to a given value",
						 "are > a given value",
						 "are < a given value",
						 "fall into a given interval"},0);
	private OptDouble cmpval = new OptDouble("Value","The value to compare against",0.0);
	private OptDouble intlow = new OptDouble("Lower bound", "The lowest value still in the corridor",-10.0);
	private OptDouble inthig = new OptDouble("Upper bound","The highest value still in the corridor",10.0);
	
	public MIODoubleFilter() {
		super("Numeric meta information (double values)",
			    "Removes probes based on meta information objects containing double values.\n" +
				"May require interaction during execution (selection of a MIO Group).",
				DoubleMIO.class, MIODoubleFilter.class);
		filtermode.addOption(0,cmpval);
		filtermode.addOption(1,cmpval);
		filtermode.addOption(2,cmpval);
		filtermode.addOption(3,intlow);
		filtermode.addOption(3,inthig);
		filtermode.setVisiblePage(0);
		Options.add(filtermode);
		Options.add(cmpval);
		Options.add(intlow);
		Options.add(inthig);
		addCommonOptions();
	}

	protected boolean checkMatch(DoubleMIO d) {
		switch(filtermode.Value) {
		case 0: return d.getValue()==cmpval.Value;
		case 1: return d.getValue()>cmpval.Value;
		case 2: return d.getValue()<cmpval.Value;
		case 3: return (d.getValue()>=intlow.Value && d.getValue()<=inthig.Value);
		}
		return false; // should never happen
	}
	
}