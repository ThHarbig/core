package mayday.vis3.plots.ma;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.vis3.model.ViewModel;

/** A copy of the value provider that can only use Experiment values, no metainfo */
public class MAValueProvider {

	public interface Provider {
		public double getValue(Probe pb);
		public String getName();
	}
	
	public class ExperimentProvider implements Provider {
		protected int index=0;
		public double getValue(Probe pb) {
			return viewModel.getProbeValues(pb)[index];
		}
		public ExperimentProvider(int i) {
			index=i;
		}
		public String getName() {
			if (index>=viewModel.getDataSet().getMasterTable().getNumberOfExperiments())
				return "No input data!";
			String dataName = (viewModel.getDataSet().getMasterTable().getExperimentDisplayName(index));
			String manip = viewModel.getDataManipulator().getManipulation().getDataDescription();
			if (manip.length()>0)
				manip = ", "+manip;
			return "Experiment: "+dataName+manip;
		}		
		public int getExperiment() {
			return index;
		}
	}
	
	private ViewModel viewModel;
	private String title;
	private Provider provider = new ExperimentProvider(0);
	protected MAValueProviderSetting setting;
//	private MaydayFrame expListFrame;
	
	private EventListenerList eventListenerList = new EventListenerList();
	
	public void addChangeListener(ChangeListener cl) {
		eventListenerList.add(ChangeListener.class, cl);		
	}
	
	public void removeChangeListener(ChangeListener cl) {
		eventListenerList.remove(ChangeListener.class, cl);
	}

	public double getValue(Probe pb) {
		return provider.getValue(pb);
	}
	
	public String getSourceName() {
		return provider.getName();
	}
	
	public double getMaximum() {
		double max=Double.NaN;
		for (Probe pb : viewModel.getProbes()) {
			double d = getValue(pb);
			if (!Double.isNaN(d) && (d>max || Double.isNaN(max)))
				max=d;
		}
		return max;	
	}
	
	public double getMinimum() {
		double min=Double.NaN;
		for (Probe pb : viewModel.getProbes()) {
			double d = getValue(pb);
			if (!Double.isNaN(d) && (d<min || Double.isNaN(min)))
				min=d;
		}
		return min;	
	}
	
	public Provider getProvider() {
		return provider;
	}
	
	public ViewModel getViewModel() {
		return viewModel;
	}
	
	public Collection<Double> getValues() {
		LinkedList<Double> values = new LinkedList<Double>();
		for (Probe pb : viewModel.getProbes())
			values.add(getValue(pb));
		return values;
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
	
	public MAValueProvider(ViewModel vm, String menuTitle) {
		viewModel = vm;
		title=menuTitle;
		setting = new MAValueProviderSetting(title, null, this, vm);
	}
	
	public String getMenuTitle() {
		return title;
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	protected ListSelectionModel selectionModel;	// needed for JList's to sync selected entries
	protected DefaultListModel dataModel = new DefaultListModel();	// data model for the JList's
	
	public void setProvider(Provider p) {
		provider=p;
		fireChanged();
	}

}
