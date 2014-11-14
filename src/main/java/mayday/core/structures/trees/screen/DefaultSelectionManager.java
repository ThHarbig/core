package mayday.core.structures.trees.screen;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.ChangeEvent;

import mayday.core.structures.trees.tree.ITreePart;

public class DefaultSelectionManager extends SelectionManager {

	protected HashSet<ITreePart> s = new HashSet<ITreePart>();
	
	@Override
	public void clearSelection() {
		s.clear();
		firer.fireEvent(new ChangeEvent(this));
	}

	@Override
	public Set<ITreePart> getSelection() {
		return Collections.unmodifiableSet(s);
	}

	@Override
	public boolean isSelected(ITreePart object) {
		return s.contains(object);
	}

	@Override
	public void setSelected(ITreePart object, boolean status) {
		if (!status && s.contains(object)) {
			s.remove(object);
			firer.fireEvent(new ChangeEvent(this));
		};
		if (status && !s.contains(object)) {
			s.add(object);
			firer.fireEvent(new ChangeEvent(this));
		}
	}

}
