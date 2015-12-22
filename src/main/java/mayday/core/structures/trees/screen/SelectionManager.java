package mayday.core.structures.trees.screen;

import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.EventFirer;
import mayday.core.structures.trees.tree.ITreePart;

public abstract class SelectionManager {

	protected EventFirer<ChangeEvent, ChangeListener> firer = new EventFirer<ChangeEvent, ChangeListener>() {

		@Override
		protected void dispatchEvent(ChangeEvent event, ChangeListener listener) {
			listener.stateChanged(event);
		}
	};
	
	public void addListener(ChangeListener cl) {
		firer.addListener(cl);
	}
	
	public void removeListener(ChangeListener cl) {
		firer.removeListener(cl);
	}
	
	public abstract void clearSelection();
	
	public abstract void setSelected(ITreePart object, boolean status);
	
	public abstract boolean isSelected(ITreePart object);
	
	public abstract Set<ITreePart> getSelection();
	
}
