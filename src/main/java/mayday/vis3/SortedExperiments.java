package mayday.vis3;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.clustering.hierarchical.TreeInfo;
import mayday.core.EventFirer;
import mayday.core.Experiment;
import mayday.core.meta.MIGroup;
import mayday.core.settings.Setting;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.ConstantIndexVector;
import mayday.core.structures.trees.tree.Node;
import mayday.genemining2.cng.Bipartition;
import mayday.vis3.model.ViewModel;

public class SortedExperiments implements Iterable<Experiment> {

	boolean requireResort = false;

	private int sortMode = SortedExperimentsSetting.SORT_BY_INDEX;
	private int sortOrder = SortedExperimentsSetting.SORT_ASCENDING;
	private MIGroup sortMIOSelection;
	
	protected ViewModel vm;
	protected TreeInfo treeInfo;
	protected JMenu treeMenu;	
	protected AbstractVector defaultOrder, sorting;
	
	protected SortedExperimentsSetting setting;

	protected EventFirer<ChangeEvent, ChangeListener> firer = new EventFirer<ChangeEvent, ChangeListener>() {

		protected void dispatchEvent(ChangeEvent event, ChangeListener listener) {
			listener.stateChanged(event);
		}

	};

	public SortedExperiments(ViewModel viewModel) {
		vm=viewModel;
		defaultOrder= new ConstantIndexVector(viewModel.getDataSet().getMasterTable().getNumberOfExperiments(),0);
		sorting = defaultOrder;
		setting = makeSetting();
	}
	
	protected SortedExperimentsSetting makeSetting() {
		return new SortedExperimentsSetting("Sort Experiments", "Select how to sort experiments", this, vm);
	}
	
	public Setting getSetting() {
		return setting;
	}


	public int mapColumn(int column) {
		if (doesRequireResort())
			updateSorting();
		return (int)sorting.get(column);
	}

	public TreeInfo getTreeInfo() {
		return treeInfo;
	}

	protected void updateSorting() {		
		if (requireResort) {
			requireResort=false; // do this here or we'll have endless recursion	
			// sort by probe identifier (this is default and the second sort criterion for all other sortings)
			sorting = defaultOrder.clone();
			requireResort=false; // do this here or we'll have endless recursion
			switch ( sortMode ) {
			case SortedExperimentsSetting.SORT_BY_INDEX:
				if (sortOrder == SortedExperimentsSetting.SORT_DESCENDING)
					sorting.reverse();
				break; 
			case SortedExperimentsSetting.SORT_BY_NAME:
				sortByName( sorting );
				if (sortOrder == SortedExperimentsSetting.SORT_DESCENDING)
					sorting.reverse();
				break;
			case SortedProbeListSetting.SORT_BY_DISPLAYNAME:
				sortByDisplayName(sorting);
				if (sortOrder == SortedExperimentsSetting.SORT_DESCENDING)
					sorting.reverse();
				break;
			case SortedProbeListSetting.SORT_BY_MIO_GROUP:
				sortByMIOSelection(sorting, sortMIOSelection);
				if (sortOrder == SortedExperimentsSetting.SORT_DESCENDING)
					sorting.reverse();
				break;
			case SortedExperimentsSetting.SORT_BY_TREE:
				sortByClusteringTree( sorting, treeInfo );
				break;
			}
		}
	}

	public void setTreeInfo(TreeInfo tree) {
		if (treeInfo!=tree) {
			treeInfo=tree;
			requireResort=true;
		}
	}
	
	public void setOrder(int order) {
		if (order!=sortOrder) {
			sortOrder=order;
			requireResort=true;
		}
	}

	
	public void setMISelection(MIGroup mg) {
		if (mg!=sortMIOSelection) {
			sortMIOSelection=mg;
			requireResort=true;
		}
	}
	
	protected void fireChanged() {
		firer.fireEvent(new ChangeEvent(this));
	}

	protected void sortByName( AbstractVector list) {

		if ( list != null ) {
			Collections.sort( list.asList() , new Comparator<Double>(){

				public int compare(Double o1, Double o2) {
					String i1 = vm.getDataSet().getMasterTable().getExperimentName(o1.intValue());
					String i2 = vm.getDataSet().getMasterTable().getExperimentName(o2.intValue());
					return i1.compareTo(i2);
				}
			});
		}
	}
	
	public void sortByDisplayName(  AbstractVector list )	{
		if ( list != null ) {
			Collections.sort( list.asList() , new Comparator<Double>(){
				public int compare(Double o1, Double o2) {
					String i1 = vm.getDataSet().getMasterTable().getExperiment(o1.intValue()).getDisplayName();
					String i2 = vm.getDataSet().getMasterTable().getExperiment(o2.intValue()).getDisplayName();
					return i1.compareTo(i2);
				}
			});
		}
	}

	public void sortByMIOSelection( AbstractVector list, MIGroup mio)
	{
		if ( list != null ) {
			Collections.sort( list.asList() , new Comparator<Double>(){
				@SuppressWarnings("unchecked")
				public int compare(Double o1, Double o2) {
					Comparable i1 = (Comparable)sortMIOSelection.getMIO(vm.getDataSet().getMasterTable().getExperiment(o1.intValue()));
					Comparable i2 = (Comparable)sortMIOSelection.getMIO(vm.getDataSet().getMasterTable().getExperiment(o2.intValue()));
					if (i1==null)
						return 1;
					if (i2==null)
						return -1;
					return i1.compareTo(i2);
				}
			});
		}
	}
		
	protected void sortByIndex( AbstractVector list) {

		final List<String> orderedExperimentNameList = vm.getDataSet().getMasterTable().getExperimentNames();

		if ( list != null ) {
			Collections.sort( list.asList() , new Comparator<Double>(){

				public int compare(Double o1, Double o2) {
					Integer i1 = orderedExperimentNameList.indexOf(vm.getDataSet().getMasterTable().getExperimentName(o1.intValue()));
					Integer i2 = orderedExperimentNameList.indexOf(vm.getDataSet().getMasterTable().getExperimentName(o2.intValue()));
					return i1.compareTo(i2); 
				}
			});
		}
	}
	
	protected void sortByClusteringTree( AbstractVector list, TreeInfo treeInfo ) {
		Node t = treeInfo.getTree();
		Bipartition bp = new Bipartition();
		bp.set(t);		

		final List<String> orderedExperimentNameList = bp.getLeafList1();

		if ( list != null ) {
			Collections.sort( list.asList() , new Comparator<Double>(){

				public int compare(Double o1, Double o2) {
					Integer i1 = orderedExperimentNameList.indexOf(vm.getDataSet().getMasterTable().getExperimentName(o1.intValue()));
					Integer i2 = orderedExperimentNameList.indexOf(vm.getDataSet().getMasterTable().getExperimentName(o2.intValue()));
					return i1.compareTo(i2); 
				}
			});
		}
	}


	public void fireIfNeeded() {
		if (doesRequireResort())
			fireChanged();
	}
	
	public boolean doesRequireResort() {
		return requireResort;
	}

	public void setMode(int mode) {
		if (mode!=sortMode) {
			sortMode=mode;
			requireResort=true;
		}
	}

	public int getMode() {
		return sortMode;
	}
	
	public int getOrder() {
		return sortOrder;
	}

	public MIGroup getMIGroup() {
		return sortMIOSelection;
	}


	public void addChangeListener(ChangeListener cl) {
		firer.addListener(cl);		
	}
	
	public void removeChangeListener(ChangeListener cl) {
		firer.removeListener(cl);
	}
	
	
	public void dispose() {
		setting.dispose();
	}

	@Override
	public Iterator<Experiment> iterator() {
		return new Iterator<Experiment>() {
			protected int pos=0;
			@Override
			public boolean hasNext() {
				return pos<vm.getDataSet().getMasterTable().getNumberOfExperiments();
			}

			@Override
			public Experiment next() {
				return vm.getDataSet().getMasterTable().getExperiment(mapColumn(pos++));
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}

}
