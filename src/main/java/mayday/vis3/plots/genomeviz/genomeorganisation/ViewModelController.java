package mayday.vis3.plots.genomeviz.genomeorganisation;

import mayday.core.DelayedUpdateTask;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.plots.genomeviz.Organiser;

public class ViewModelController implements ViewModelListener {

	protected GenomeDAO gd;
	protected ViewModel vm; 
	protected Organiser org;
	protected DelayedUpdateTask expensiveUpdateTrigger = new DelayedUpdateTask("Updating model",100) {
	
		@Override
		protected void performUpdate() {
			gd.init();
		}
	
		@Override
		protected boolean needsUpdating() {
			return true;
		}
	};
	
	public ViewModelController(Organiser Org, GenomeDAO Gd, ViewModel viewModel){
		gd = Gd;
		vm = viewModel;
		org = Org;
	}

	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case ViewModelEvent.VIEWMODEL_CLOSED:
			gd.clear();
			break;
		case ViewModelEvent.TOTAL_PROBES_CHANGED: // fall
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED: 
			expensiveUpdateTrigger.trigger();
			break;
		case ViewModelEvent.DATA_MANIPULATION_CHANGED: //fall
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED:
			// this usually only requires a redraw, but for now it is implemented as complete re-init
			// because from here I have no idea how to call AbstractLogixVizModel.actualizeTracks()
			expensiveUpdateTrigger.trigger();
			break;
		}
	}
	
	
	public void addNotify() {
		vm.addViewModelListener(this);
	}
	
	public void removeNotify() {
		vm.removeViewModelListener(this);
	}
}
