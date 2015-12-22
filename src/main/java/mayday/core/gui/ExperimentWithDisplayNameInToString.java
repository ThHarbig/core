package mayday.core.gui;

import java.util.ArrayList;

import java.util.List;

import mayday.core.Experiment;


public class ExperimentWithDisplayNameInToString {

	public Experiment e;
	
	public ExperimentWithDisplayNameInToString(Experiment ex) {
		e=ex;
	}
	
	public String toString() {
		return e.getDisplayName();
	}
	
	public static List<ExperimentWithDisplayNameInToString> convert(List<Experiment> lex) {
		List<ExperimentWithDisplayNameInToString> le = new ArrayList<ExperimentWithDisplayNameInToString>(lex.size());
		for (Experiment e: lex) {
			le.add(new ExperimentWithDisplayNameInToString(e));
		}
		return le;
	}

	public static List<Experiment> convertBack(List<ExperimentWithDisplayNameInToString> le) {
		List<Experiment> lex = new ArrayList<Experiment>(le.size());
		for (ExperimentWithDisplayNameInToString edn : le)
			lex.add(edn.e);
		return lex;
	}
	
}

