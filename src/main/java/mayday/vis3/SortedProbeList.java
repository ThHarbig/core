package mayday.vis3;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.settings.Setting;
import mayday.vis3.model.ViewModel;

@SuppressWarnings({ "unchecked", "serial" })
public class SortedProbeList extends LinkedList<Probe> {
	
	protected boolean requireResort = true;	

	private int experiment=0;
	private int sortMode = SortedProbeListSetting.SORT_BY_TOP_PRIORITY_PROBE_LIST;
	private int sortOrder = SortedProbeListSetting.SORT_DESCENDING;
	private MIGroup sortMIOSelection;
	
	protected ViewModel vm;
	
	protected SortedProbeListSetting setting;
	
	private EventListenerList eventListenerList = new EventListenerList();
	
	public SortedProbeList(ViewModel viewModel, Collection<Probe> probes) {
		vm=viewModel;
		setting = makeSetting();
		if (probes!=null)
			addAll(probes);
	};

	protected SortedProbeListSetting makeSetting() {
		return new SortedProbeListSetting("Sort Probes", "Select how to sort probes", this, vm);
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	@Override
	public void add(int index, Probe e) {
		super.add(index,e);
		requireResort=true;
	}
	
	@Override
	public boolean addAll(Collection<? extends Probe> coll) {
		boolean ret = super.addAll(coll);
		requireResort=true;
		updateSorting();
		return ret;
	}
	
	protected void updateSorting() {		
		if (requireResort) {
			requireResort=false; // do this here or we'll have endless recursion	
			// sort by probe identifier (this is default and the second sort criterion for all other sortings)
			sortByProbeIdentifier( this, sortOrder );
			switch ( sortMode ) {
			case SortedProbeListSetting.SORT_BY_EXPERIMENT:
				sortByExperiment( this, experiment, sortOrder, vm );
				break;                                                                       
			case SortedProbeListSetting.SORT_BY_TOP_PRIORITY_PROBE_LIST:
				sortByTopPriorityProbeList( this, vm, sortOrder );
				break;                            
			case SortedProbeListSetting.SORT_BY_MIO_GROUP:
				sortByMIOSelection(this, sortMIOSelection, sortOrder);
				break;
			case SortedProbeListSetting.SORT_BY_DISPLAYNAME:
				sortByDisplayName(this, sortOrder);
				break;
			case SortedProbeListSetting.SORT_BY_PROBE_IDENTIFIER: 
				break;
			default:
				// do nothing, the probes have already been sorted by probe identifier
				break;                                                                       
			}
		}
	}
	
	public void setOrder(int order) {
		if (order!=sortOrder) {
			sortOrder=order;
			requireResort=true;
		}
	}
	
	public void setMode(int mode) {
		if (mode!=sortMode) {
			sortMode=mode;
			requireResort=true;
		}
	}
	
	public void setMISelection(MIGroup mg) {
		if (mg!=sortMIOSelection) {
			sortMIOSelection=mg;
			requireResort=true;
		}
	}
	
	public int getMode() {
		return sortMode;
	}
	
	public void setExperiment(int experiment) {
		if (this.experiment!=experiment) {
			this.experiment=experiment;
			requireResort=true;
		}
	}
	
	public boolean doesRequireResort() {
		return requireResort;
	}
	
	@Override
	public Probe get(int index) {
		if (doesRequireResort())
			updateSorting();
		return super.get(index);
	}
	
	

	public void addChangeListener(ChangeListener cl) {
		eventListenerList.add(ChangeListener.class, cl);		
	}
	
	public void removeChangeListener(ChangeListener cl) {
		eventListenerList.remove(ChangeListener.class, cl);
	}
	

	protected void fireChanged() {
		Object[] l_listeners = this.eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;
		
		ChangeEvent event = new ChangeEvent(this);

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ChangeListener.class )  {
				ChangeListener list = ((ChangeListener)l_listeners[i+1]);
				list.stateChanged(event);
			}
		}
	}
	
	@Override
	public Iterator<Probe> iterator() {
		if (doesRequireResort())
			updateSorting();
		return super.iterator();
	}
	
	
	public static void sortByExperiment( List<Probe> list, int experiment, int mode, ViewModel vm ) {
		if ( list != null && list.size()>0) {
			int NoE = list.get(0).getNumberOfExperiments();

			if ( experiment < NoE ) 
				Collections.sort( list, new ExperimentComparator( Probe.IMPLICIT_PROBE | Probe.EXPLICIT_PROBE, experiment, vm ) );

			if ( mode == SortedProbeListSetting.SORT_DESCENDING )
				Collections.reverse( list );
		}
	}


	public static void sortByProbeIdentifier( List<Probe> list, int mode )	{
		if ( list != null ) {
			Collections.sort( list );
			if ( mode == SortedProbeListSetting.SORT_DESCENDING ) 
				Collections.reverse( list );
		}
	}

	public static void sortByDisplayName( List<Probe> list, int mode )	{
		if ( list != null ) {
			Collections.sort( list , new DisplayNameComparator() );
			if ( mode == SortedProbeListSetting.SORT_DESCENDING ) 
				Collections.reverse( list );
		}
	}

	public static void sortByTopPriorityProbeList( List<Probe> list, ViewModel vm, int mode )	{
		if ( list != null ) {
			Collections.sort( list, new ProbeListComparator( vm ) );
			if ( mode == SortedProbeListSetting.SORT_ASCENDING )
				Collections.reverse( list );
		}
	}


	public static void sortByMIOSelection( List<Probe> list, MIGroup mio, int mode )
	{
		if ( list != null ) {
			Collections.sort( list, new Probe.MIOListComparator( 
					Probe.IMPLICIT_PROBE | Probe.EXPLICIT_PROBE, 
					mio) );

			if ( mode == SortedProbeListSetting.SORT_DESCENDING )
				Collections.reverse( list );
		}
	}


	public static class ProbeListComparator	implements Comparator<Probe>
	{
		ViewModel viewModel;

		public ProbeListComparator( ViewModel viewModel )
		{
			this.viewModel = viewModel;
		}    

		public int compare( Probe object1, Probe object2 )
		{
			ProbeList l_object1;
			ProbeList l_object2;

			// both user and system probes
			l_object1 = viewModel.getTopPriorityProbeList( ((Probe)object1) );
			l_object2 = viewModel.getTopPriorityProbeList( ((Probe)object2) );

			if ( l_object1 == null )
				return ( -1 );

			if ( l_object2 == null )
				return ( 1 );

			int l_object1Pos = this.viewModel.indexOf( (ProbeList)l_object1 );
			int l_object2Pos = this.viewModel.indexOf( (ProbeList)l_object2 );

			if ( l_object1Pos < l_object2Pos )
				return ( -1 );

			if ( l_object1Pos > l_object2Pos )
				return ( 1 );
			return ( 0 );      
		}
	}
	
	public static class DisplayNameComparator	implements Comparator<Probe>
	{

		public int compare( Probe object1, Probe object2 )
		{
			String name1 = object1.getDisplayName();
			String name2 = object2.getDisplayName();
			return name1.compareTo(name2);
		}
	}

	public static class ExperimentComparator
	implements Comparator
	{
		private int mode;
		private int experiment;    
		private ViewModel vm;

		public ExperimentComparator( int mode, int experiment, ViewModel viewModel )
		{
			this.mode = mode;
			this.experiment = experiment;
			this.vm = viewModel;
		}


		public int compare( Object object1, Object object2 )
		{
			Probe p1 = (Probe)object1;
			Probe p2 = (Probe)object2;
			
			Double v1 = vm.getProbeValues(p1)[experiment];
			Double v2 = vm.getProbeValues(p2)[experiment];
			
			boolean implicit = (mode & Probe.IMPLICIT_PROBE)!=0;
			boolean explicit = (mode & Probe.EXPLICIT_PROBE)!=0;
			
			// check modes
			if (implicit && !explicit) {
				if (!p1.isImplicitProbe()) 
					v1 = null;
				if (!p2.isImplicitProbe())
					v2 = null;
			} else if (!implicit && explicit) {
				if (!p1.isExplicitProbe())
					v1 = null;
				if (!p2.isExplicitProbe())
					v2 = null;
			}; 
				
			if ( v1 == null )
				return ( -1 );

			if ( v2 == null )
				return ( 1 );

			return v1.compareTo( v2 );
		}
	}

	public MIGroup getMIGroup() {
		return sortMIOSelection;
	}

	public int getExperiment() {
		return experiment;
	}

	public int getOrder() {
		return sortOrder;
	}

	public void fireIfNeeded() {
		if (doesRequireResort())
			fireChanged();
	}
}
