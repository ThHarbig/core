package mayday.vis3.model;

import java.util.EventListener;

public interface ViewModelListener extends EventListener {
	
	public void viewModelChanged(ViewModelEvent vme);

}
