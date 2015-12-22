package mayday.vis3;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mayday.clustering.hierarchical.TreeInfo;
import mayday.core.Probe;
import mayday.core.structures.trees.tree.Node;
import mayday.genemining2.cng.Bipartition;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class HierarchicalSortedProbeList extends SortedProbeList {
	
	protected TreeInfo treeInfo;

	public HierarchicalSortedProbeList(ViewModel viewModel, Collection<Probe> probes) {
		super(viewModel, probes);
	}
	
	protected HierarchicalSortedProbeListSetting makeSetting() {
		return new HierarchicalSortedProbeListSetting("Sort Probes", "Select how to sort probes", this, vm);
	}
	
	protected void updateSorting() {
		if (doesRequireResort()) {
			switch ( getMode() ) {
			case HierarchicalSortedProbeListSetting.SORT_BY_TREE:
				requireResort=false; // do this here or we'll have endless recursion	
				sortByClusteringTree( this, treeInfo );
				break;
			default:
				super.updateSorting();
			}
		}
	}
	

	
	public void setMode(int mode) {
		if (mode!=HierarchicalSortedProbeListSetting.SORT_BY_TREE)
			treeInfo = null;
		super.setMode(mode);
	}
	
	public void setTreeInfo(TreeInfo tree) {
		if (treeInfo!=tree) {
			treeInfo=tree;
			requireResort=true;
		}
	}
	
	public TreeInfo getTreeInfo() {
		return treeInfo;
	}

	protected static void sortByClusteringTree( List<Probe> list, TreeInfo treeInfo ) {
		Node t = treeInfo.getTree();
		Bipartition bp = new Bipartition();
		bp.set(t);		
		
		final List<String> orderedProbeNameList = bp.getLeafList1();
		
		if ( list != null ) {
			Collections.sort( list , new Comparator<Probe>(){

				public int compare(Probe o1, Probe o2) {
					Integer i1 = orderedProbeNameList.indexOf(o1.getName());
					Integer i2 = orderedProbeNameList.indexOf(o2.getName());
					return i1.compareTo(i2); 
				}
			});
		}
	}
	
	public void dispose() {
		((HierarchicalSortedProbeListSetting)this.setting).dispose();
	}


}
