package mayday.vis3.categorical;

import java.awt.Color;
import java.util.Map.Entry;
import java.util.Set;

import mayday.vis3.model.ViewModel;

public interface CategoricalColoring {

	public void replaceCategoricalColor(Object category, Color c);
	
	public int getNumberOfCategories();
	
	public Set<Entry<Object, Color>> getCategoricalColoring();

	public ViewModel getViewModel();

	public String getSourceName();
	
}
