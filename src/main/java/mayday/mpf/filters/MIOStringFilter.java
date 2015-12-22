package mayday.mpf.filters;

import mayday.core.meta.types.StringMIO;
import mayday.mpf.options.OptPagedDropDown;
import mayday.mpf.options.OptString;

/**
 * This filter removes probes based on MIO content. During execution it opens a popup window where
 * the user can select which MIO group to use. In batchmode, the user can decide to use the same MIO group
 * for all jobs.
 * @author Florian Battke
 */
public class MIOStringFilter extends MIOAbstractFilter<StringMIO> {

	private OptPagedDropDown filtermode = new OptPagedDropDown("Match strings that...",
			"Select how to filter strings",
			new String[]{"exactly match a query string",
						 "contain a given substring",
						 "match a regular expression"},0);
	private OptString exact = new OptString("Exact match","The string that must be matched exactly","");
	private OptString substr = new OptString("Substring", "The substring that the filter looks for","");
	private OptString regex = new OptString("Regular Expression","The regular expression that is used to check the MIO string.","");
	
	public MIOStringFilter() {
		super(  "Textual meta information (string values)",
				"Removes probes based on meta information objects containing Strings.\n" +
				"May require user interaction during execution (selection of a MIO Group).",
				StringMIO.class, MIOStringFilter.class 
			);
		filtermode.addOption(0,exact);
		filtermode.addOption(1,substr);
		filtermode.addOption(2,regex);
		Options.add(filtermode);
		Options.add(exact);
		Options.add(substr);
		Options.add(regex);
		addCommonOptions(); 
	}

	protected boolean checkMatch(StringMIO s) {
		switch(filtermode.Value) {
		case 0: return s.getValue().equals(exact.Value);
		case 1: return s.getValue().contains(substr.Value);
		case 2: return s.getValue().matches(regex.Value);
		}
		return false; // should never happen
	}
	
}