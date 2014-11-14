package mayday.vis3;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.NumericMIO;
import mayday.core.settings.Setting;
import mayday.vis3.model.ManipulationMethod;
import mayday.vis3.model.ManipulationMethodSingleValue;
import mayday.vis3.model.ViewModel;


public class ValueProvider {

	public interface Provider {
		public double getValue(Probe pb);
		public String getName();
	}
	
	public class ExperimentProvider implements Provider {
		protected int index=0;
		public double getValue(Probe pb) {
			double[] r = viewModel.getProbeValues(pb);
			if (index<r.length)
				return r[index];
			return Double.NaN;
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
			return dataName+manip;
		}		
		public int getExperiment() {
			return index;
		}
		public void setExperiment(int i) {
			index=i;
		}
	}
	
	public class MIOProvider implements Provider {
		protected MIGroup mg=null;
		protected ManipulationMethodSingleValue manip;
		@SuppressWarnings("unchecked")
		public double getValue(Probe pb) {
			double value;
			if (mg==null)
				value = Double.NaN;
			else {
				NumericMIO<Number> nm = (NumericMIO<Number>)mg.getMIO(pb);
				if (nm==null) 
					value = Double.NaN;
				else {
					value = ((Number)nm.getValue()).doubleValue();
					//check for infinity and replace with NaN for visualization
					if(Double.isInfinite(value)) {
						value = Double.NaN;
					}
				}
			}
			return manip.manipulate(value); 
		}
		public MIOProvider(MIGroup m, ManipulationMethodSingleValue manip) {
			mg=m;
			this.manip=manip;
		}
		public String getName() {
			if (mg==null)
				return "";
			String dataName = mg.getPath()+"/"+mg.getName();
			String manipu = ((ManipulationMethod)manip).getDataDescription();
			if (manipu.length()>0)
				manipu = ", "+manipu;
			return dataName+manipu;
		}				
		public MIGroup getMIGroup() {
			return mg;
		}
		public ManipulationMethodSingleValue getManipulator() {
			return manip;
		}
	}
	
	protected ViewModel viewModel;
	protected String title;
	protected Provider provider = new ExperimentProvider(0);
	protected ValueProviderSetting setting;
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
	
	public int getSourceType() {
		if (provider instanceof ExperimentProvider)
			return ValueProviderSetting.EXPERIMENT_VALUE;
		else
			return ValueProviderSetting.MIO_VALUE;
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
		if (viewModel==null)
			return Collections.emptyList();
		LinkedList<Double> values = new LinkedList<Double>();
		for (Probe pb : viewModel.getProbes())
			values.add(getValue(pb));
		return values;
	}
	
	public Map<Probe, Double> getValuesMap() {
		if (viewModel==null)
			return Collections.emptyMap();
		HashMap<Probe, Double> values = new HashMap<Probe, Double>();
		for (Probe pb : viewModel.getProbes())
			values.put(pb, getValue(pb));
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
	
	public ValueProvider(ViewModel vm, String menuTitle) {
		viewModel = vm;
		title=menuTitle;
		makeSetting();
	}
	
	protected void makeSetting() {
		setting = new ValueProviderSetting(title, null, this, viewModel);
	}
	
	public String getMenuTitle() {
		return title;
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	protected ListSelectionModel selectionModel;	// needed for JList's to sync selected entries
	protected DefaultListModel dataModel = new DefaultListModel();	// data model for the JList's
	
//	public JMenu getMenu() {
//		JMenu menu = new JMenu( title );
//		menu.setMnemonic( title.charAt(0) );
//
//		menu.add( new ValueFromMIOAction() );
//
//		JMenu experimentMenu = new JMenu("Experiment");
//
//		ExperimentList expList = new ExperimentList();
//		
//		MasterTable mt = viewModel.getDataSet().getMasterTable();
//		for ( int i = 0; i < mt.getNumberOfExperiments(); ++i ) 
//			dataModel.addElement(mt.getExperimentName(i));
//		expList.setModel(dataModel);
//		
//		selectionModel = expList.getSelectionModel();	// get selectionModel for the second JList
//		selectionModel.addListSelectionListener(new ListSelectionManager());
//		
//		JScrollPane listScrollPane = new JScrollPane(expList);		
//		experimentMenu.add(listScrollPane);		
//		
//		menu.add( experimentMenu );
//		
//		menu.add(new JSeparator());
//		
//		menu.add(new ExperimentWindow("Detach this menu"));
//
//		return ( menu );
//	}
	
//	protected class ListSelectionManager implements ListSelectionListener{
//		private int experiment;
//
//		public void valueChanged(ListSelectionEvent event) {
//			ListSelectionModel listSelectionModel = (ListSelectionModel)event.getSource();
//			
//			if(!listSelectionModel.isSelectionEmpty()){
//				this.experiment = listSelectionModel.getMinSelectionIndex();
//				provider = new ExperimentProvider(experiment);
//				fireChanged();
//			}
//		}
//	}

//	protected class ExperimentWindow extends AbstractAction{
//
//		public ExperimentWindow(String text){
//			super(text);
//		}
//		
//		public void actionPerformed(ActionEvent arg0) {
//			if (expListFrame==null) {
//				expListFrame = new MaydayFrame(title);
//				ExperimentList expList = new ExperimentList();	
//				expList.setModel(dataModel);		// Set entries in JList
//				expList.setSelectionModel(selectionModel);	// set the selectionModel for the JList
//
//				JScrollPane listScrollPane = new JScrollPane(expList);	
//				//listScrollPane.getHorizontalScrollBar().setSize(new Dimension(100,listScrollPane.getMinimumSize().height));
//				expListFrame.setLayout(new GridBagLayout());
//				GridBagConstraints gbc = new GridBagConstraints( 0,0,1,1,1.0,0, GridBagConstraints.CENTER ,GridBagConstants.HORIZONTAL,new Insets(0,0,0,0),0,0); 
//
//				expListFrame.add( new JButton(new ValueFromMIOAction() ), gbc);
//				gbc.gridy++;
//				expListFrame.add(new JSeparator(), gbc );
//				gbc.gridy++;
//				expListFrame.add( new JLabel( "by Experiment:") , gbc);
//				gbc.gridy++; 
//				gbc.weighty=1.0;
//				expListFrame.add( listScrollPane , gbc);
//
//				expListFrame.pack();	// choose size of frame that children of frame fit window
//				MaydayDefaults.centerWindowOnScreen(expListFrame);
//			}
//			expListFrame.setVisible(true);
//		}	
//	}
	
//	// create JList with experiments as selections
//	protected class ExperimentList extends JList{
//
//		public ExperimentList(){
//
//			setVisibleRowCount(12);
////			setMinimumSize(new Dimension(100,getMinimumSize().height));
////			setPreferredSize(getMinimumSize());			
//			setSelectedIndex(0);	
//			setForeground(Color.BLACK);
//			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		}
//	}
	
	public void setProvider(Provider p) {
		provider=p;
		fireChanged();
	}
	
	// no more, displaced by ListSelectionListener
//	protected class ValueFromExperimentAction extends AbstractAction
//	{
//		private int experiment;
//		public ValueFromExperimentAction( String text, int experiment )	{
//			super( text );
//			this.experiment = experiment;
//		}
//
//		public void actionPerformed( ActionEvent event ) {
//			provider = new ExperimentProvider(experiment);
//			fireChanged();
//		}
//	}


//	protected class ValueFromMIOAction extends FromMIOAction {
//		public ValueFromMIOAction() {
//			super("Meta information", NumericMIO.class);
//		}
//	}
//	
//	protected class FromMIOAction extends AbstractAction
//	{
//		private Class<? extends MIType> mioclass;
//
//		public FromMIOAction( String text, Class<? extends MIType> acceptableMIO )
//		{
//			super( text );
//			mioclass = acceptableMIO;
//		}
//
//		public void actionPerformed( ActionEvent event )
//		{
//			MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(viewModel.getDataSet().getMIManager(), mioclass);
//			if (mgsd.getSelectableCount()==0)
//				JOptionPane.showMessageDialog(null, 
//						"No Meta Information Groups found that contain plottable values.",
//						"No MIOs found", JOptionPane.OK_OPTION);
//			else {
//				mgsd.setVisible(true);
//				if (mgsd.getSelection().size()>0) {
//					provider = new MIOProvider(mgsd.getSelection().get(0));
//					fireChanged();
//				}
//			}
//
//		}
//	}
	
//	public void removeNotify() {
//		if (expListFrame!=null)
//			expListFrame.dispose();
//	}
	

}
