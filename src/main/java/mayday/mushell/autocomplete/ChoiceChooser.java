package mayday.mushell.autocomplete;

import java.awt.Component;
import java.util.List;

public interface ChoiceChooser 
{
	
	public void setChoice(List<Completion> choice);
	
	public void requestFocus();

	public void show(Component invoker, int x, int y);
}
