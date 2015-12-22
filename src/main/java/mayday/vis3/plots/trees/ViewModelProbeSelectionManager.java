package mayday.vis3.plots.trees;

import java.util.Collections;
import java.util.Iterator;

import mayday.core.Probe;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.screen.DefaultSelectionManager;
import mayday.core.structures.trees.tree.ITreePart;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

public class ViewModelProbeSelectionManager extends DefaultSelectionManager implements ViewModelListener {

	protected ViewModel vm;
	protected Layout l;
	
	public ViewModelProbeSelectionManager(ViewModel vm, Layout ly) {
		vm.addViewModelListener(this);
		this.vm= vm;
		l = ly;
		// copy selection from viewmodel to tree
		copySelection();
	}
	
	@Override
	public void clearSelection() {
		vm.setProbeSelection(Collections.<Probe>emptySet());
		super.clearSelection();
	}

	@Override
	public void setSelected(ITreePart object, boolean status) {
		if (object instanceof Node) {
			Node n = (Node)object;
			Probe pb = (Probe)l.getObject(n);
			if (pb!=null) {
				if (status)
					vm.selectProbe(pb);
				else
					vm.unselectProbe(pb);
				return; // upstream processing is done via viewmodel events
			}
		}
		// now do upstream processing
		super.setSelected(object, status);
	}

	protected void clearNodes() {
		Iterator<ITreePart> itp = s.iterator();
		while (itp.hasNext())
			if (itp.next() instanceof Node)
				itp.remove();
	}

	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED) {
			copySelection();				
		}		
	}
	
	protected void copySelection() {
		clearNodes();
		for (Probe pb : vm.getSelectedProbes()) {
			Node o = l.getNode(pb);
			if (o!=null)
				super.setSelected(o, true);
		}
	}

}
