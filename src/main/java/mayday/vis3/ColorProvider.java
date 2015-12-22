package mayday.vis3;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.EventFirer;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.NumericMIO;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.categorical.CategoricalColoring;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

public class ColorProvider implements ViewModelListener, CategoricalColoring {

	protected ViewModel viewModel;
	protected int colorMode=ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST;
	protected int experiment=-1;
	protected double expmax, expmin;
	protected double miomax, miomin;
	protected MIGroup mg;
	protected boolean categoricalMIO;	
	protected boolean coloringChanged = false;

	protected ColorProviderSetting setting;

	protected ListSelectionModel selectionModel;	// needed for JList's to sync selected entries
	protected DefaultListModel dataModel = new DefaultListModel();	// data model for the JList's

	protected ColorGradient colorGradient = ColorGradient.createDefaultGradient(0, 16);
	
	protected HashMap<Object, Color> categoricalColors = new HashMap<Object, Color>();
	protected ColorProviderCategoricalAssignmentComponent colorProviderCategoricalAssignmentComponent;

	protected EventFirer<ChangeEvent, ChangeListener> eventFirer = new EventFirer<ChangeEvent, ChangeListener>() {
		protected void dispatchEvent(ChangeEvent event, ChangeListener listener) {
			listener.stateChanged(event);
		}
	};


	public ColorProvider(ViewModel vm, String name) {
		viewModel = vm;
		vm.addViewModelListener(this);
		setting = new ColorProviderSetting(name,null,this,viewModel);
		setting.addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				if (coloringHasChanged())
					updateGradient();
				fireChanged();
			}			
		});
	}

	public ColorProvider(ViewModel vm) {
		this(vm,"Coloring");
	}


	protected void fireChanged() {
		eventFirer.fireEvent(new ChangeEvent(this));
	}

	public void addChangeListener(ChangeListener cl) {
		eventFirer.addListener(cl);		
	}

	public void removeChangeListener(ChangeListener cl) {
		eventFirer.removeListener(cl);
	}


	public int getColoringMode() {
		return colorMode;
	}


	@SuppressWarnings("unchecked")
	public Color getColor(Probe pb) {
		Color c = Color.black; // default to black
		switch(colorMode) {
		case ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST:
			ProbeList pl = viewModel.getTopPriorityProbeList(pb);
			if (pl!=null)
				c = pl.getColor();
			break;
		case ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE:
			double v = viewModel.getProbeValues(pb)[experiment];
			c= getColor(v);
			break;
		case ColorProviderSetting.COLOR_BY_MIO_VALUE:
			GenericMIO mt = (GenericMIO)mg.getMIO(pb);
			if (mt!=null) {
				if (categoricalMIO) {
					c = categoricalColors.get(mt.getValue());
				} else {
					v = ((NumericMIO<Number>)mt).getValue().doubleValue();
					c = getColor(v);
				}								
			}
			break;
		}
		return c;
	}

	public Color getColor(Double value) {
		return getColorFromGradient(value, colorGradient);
	}

	protected Color getColorFromGradient(double position, ColorGradient gradient) {
		Color color = gradient.mapValueToColor(position);
		return color;
	}

	public void setMode(int mode) {
		if (mode!=colorMode) {
			colorMode=mode;
			if (mode!=ColorProviderSetting.COLOR_BY_MIO_VALUE) {
				hideMIOColorAssignmentWindow();
			} else {
				if (categoricalMIO) 
					showMIOColorAssignmentWindow();
			}
			coloringChanged=true;			
		}
	}

	@SuppressWarnings("unchecked")
	public void setMISelection(MIGroupSelection<MIType> mgs) {
		MIGroup mg = null;
		if (mgs.size()>0)
			mg = mgs.get(0);
		if (mg!=null) {
			if (mg!=this.mg) {
				this.mg=mg;
				categoricalMIO = !(mg.getMIOs().iterator().next().getValue() instanceof NumericMIO );
				if (categoricalMIO) {
					categoricalColors.clear();
					// find all distinct values
					HashSet<Object> valuesFound = new HashSet<Object>();
					for (Probe pb : viewModel.getProbes()) {
						MIType mt = mg.getMIO(pb);
						if (mt!=null) {							
							Object value = ((GenericMIO)mt).getValue();
							valuesFound.add(value);
						}
					}
					ColorGradient cg = new ColorGradient(colorGradient);
					cg.setResolution(valuesFound.size());
					int c = 0;
					for (Object o :valuesFound) {
						categoricalColors.put(o, cg.getColor(c++));
					}	
					showMIOColorAssignmentWindow();
				} else {
					hideMIOColorAssignmentWindow();
					// find min, max
					updateGradient();
				}
				coloringChanged=true;
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void updateGradient() {
		switch(colorMode) {
		case ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE:
			expmax = viewModel.getMaximum(experiment, null);
			expmin = viewModel.getMinimum(experiment, null);
			colorGradient.setMax(expmax);
			colorGradient.setMin(expmin);
			break;
		case ColorProviderSetting.COLOR_BY_MIO_VALUE:
			if (categoricalMIO) {
				ColorGradient cg = new ColorGradient(colorGradient);
				cg.setResolution(categoricalColors.size());
				int c = 0;
				for (Object o : categoricalColors.keySet()) {
					categoricalColors.put(o, cg.getColor(c++));
				}	
				showMIOColorAssignmentWindow();
			} else {
				miomin=Double.MAX_VALUE;
				miomax=Double.MIN_VALUE;
				for (Entry<Object,MIType> e :mg.getMIOs()) {
					MIType mt = e.getValue();
					double value = ((NumericMIO<Number>)mt).getValue().doubleValue();
					miomin = miomin<=value?miomin:value;
					miomax = miomax>=value?miomax:value;
				}
				colorGradient.setMax(miomax);
				colorGradient.setMin(miomin);
			}
			break;
		case ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST: //nothing to do 
			break; 
		}
	}

	public void setExperiment(int experiment) {
		if (this.experiment!=experiment) {
			this.experiment=experiment;
			updateGradient();
			coloringChanged=true;
		}
	}

	// get the number of the experiment
	public int getExperiment(){
		return this.experiment;
	}

	// has coloring changed
	public boolean coloringHasChanged() {
		return coloringChanged;
	}

	public Setting getSetting() {
		return setting;
	}


	public String getSourceName() {
		switch(colorMode) {
		case ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST: 
			return "Top PL";
		case ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE: 
			return viewModel.getDataSet().getMasterTable().getExperimentDisplayName(experiment);
		case ColorProviderSetting.COLOR_BY_MIO_VALUE:
			return mg.getName();
		}
		return "";
	}


	public void removeNotify() {
		viewModel.removeViewModelListener(this);
		hideMIOColorAssignmentWindow();
	}

	public void addNotify() {
		viewModel.addViewModelListener(this);
	}


	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.DATA_MANIPULATION_CHANGED:
			updateGradient();
			coloringChanged = true;
			fireChanged();
			break;
		case ViewModelEvent.TOTAL_PROBES_CHANGED:
			updateGradient();
			coloringChanged = true;
			//this is not fired here because this event already caused a redraw of the plots
			break;
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED:
			//this is not handled here because this event already caused a redraw of the plots
			break;
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			// update mio assignmend table if visible
			if (colorProviderCategoricalAssignmentComponent!=null)
				colorProviderCategoricalAssignmentComponent.update();
			break;
		}					
	}

	public ColorGradient getGradient() {
		return colorGradient;
	}


	protected void showMIOColorAssignmentWindow() {
		if (categoricalColors.size()==0)
			return;
		if (colorProviderCategoricalAssignmentComponent==null)
			colorProviderCategoricalAssignmentComponent = new ColorProviderCategoricalAssignmentComponent(this);
		colorProviderCategoricalAssignmentComponent.showWindow(); 					
	}
	
	protected void hideMIOColorAssignmentWindow() {
		if (colorProviderCategoricalAssignmentComponent!=null)
			colorProviderCategoricalAssignmentComponent.hideWindow();
	}

	public void replaceCategoricalColor(Object key, Color lcolor) {
		if (categoricalColors!=null) {
			Color cl = categoricalColors.get(key);			
			if (cl==null || !lcolor.equals(cl)) {
				categoricalColors.put(key, lcolor);
				fireChanged();
			}
		}
		
	}

	public int getNumberOfCategories() {
		return categoricalColors.size();
	}

	public Set<Entry<Object, Color>> getCategoricalColoring() {
		return categoricalColors.entrySet();
	}

	public ViewModel getViewModel() {
		return viewModel;
	}

}
