package mayday.vis3;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.ExperimentSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.model.ViewModel;

public class ColorProviderSetting extends HierarchicalSetting implements ChangeListener {
	
	public static final int COLOR_BY_TOP_PRIORITY_PROBELIST=0;
	public static final int COLOR_BY_EXPERIMENT_VALUE=1;
	public static final int COLOR_BY_MIO_VALUE=2;
	
	protected ExperimentSetting experiment;
	protected MIGroupSetting mioGroupPath;
	protected ColorGradientSetting gradient;
	protected SelectableHierarchicalSetting source;
	protected final static String PROBELIST = "top-priority probe list";
	protected ViewModel viewModel;
	protected ColorProvider target;
	
	public ColorProviderSetting(String Name, String Description, ColorProvider Target, ViewModel vm) {
		super(Name);
		description = Description;
		viewModel = vm;
		
		DataSet ds = vm.getDataSet();
		MIManager mim = ds.getMIManager();
		MIGroup mg = mim.getGroup(0);
		MasterTable mt = ds.getMasterTable();

		addSetting(source = new SelectableHierarchicalSetting("Source",null,0,new Object[]{
			PROBELIST,
			experiment = new ExperimentSetting("experiment",null,mt),
			mioGroupPath = new MIGroupSetting("meta information",null, mg, mim, false)
		}));
		addSetting(gradient = new ColorGradientSetting("Gradient",null,Target.getGradient())
				.setLayoutStyle(mayday.vis3.gradient.ColorGradientSetting.LayoutStyle.FULL)
		);
		target=Target;
		setChildrenAsSubmenus(true);
		Target.addChangeListener(this);
		this.setLayoutStyle(LayoutStyle.TABBED);
	}


	protected int getMode() {
		if (source.getObjectValue()==experiment)
			return COLOR_BY_EXPERIMENT_VALUE;
		if (source.getObjectValue()==mioGroupPath)
			return COLOR_BY_MIO_VALUE;
		return COLOR_BY_TOP_PRIORITY_PROBELIST;
	}
	
	protected int getExperimentIndex() {
		return experiment.getExperiment().getIndex();
	}
	
	protected MIGroup getMIGroup() {
		return mioGroupPath.getMIGroup();
	}
	
	protected ColorGradient getGradient() {
		return gradient.getColorGradient();
	}
	
	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==target) {
			if (target.experiment>=0)
				experiment.setExperiment(viewModel.getDataSet().getMasterTable().getExperiment(target.experiment));
			switch(target.colorMode) {
			case COLOR_BY_TOP_PRIORITY_PROBELIST:
				source.setObjectValue(PROBELIST);
				break;
			case COLOR_BY_EXPERIMENT_VALUE:
				source.setObjectValue(experiment);
				break;
			case COLOR_BY_MIO_VALUE:
				source.setObjectValue(mioGroupPath);
				break;
			}
			gradient.setColorGradient(target.getGradient()); // update the gradient?
			mioGroupPath.setMIGroup(target.mg);
		} else if (e.getSource()==target.getGradient()) {
			gradient.setColorGradient(target.getGradient());
		} 
	}
	
	public void stateChanged(SettingChangeEvent e) {
		if (e.getSource()==experiment || e.getSource()==source)
			target.setExperiment(getExperimentIndex());
		if (e.getSource()==mioGroupPath || e.getSource()==source) {
			MIGroupSelection<MIType> mgs = new MIGroupSelection<MIType>();
			MIGroup mg = getMIGroup();
			if (mg!=null) {
				mgs.add(getMIGroup());
				target.setMISelection(mgs);
			}
		}
		if (e.getSource()==source)
			target.setMode(getMode());	
		fireChanged(new SettingChangeEvent(this));	
	}

	public ColorProviderSetting clone() {
		ColorProviderSetting cp = new ColorProviderSetting(getName(),getDescription(), new ColorProvider(viewModel), viewModel);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}
	

	
	public Component getMenuItem( Window parent ) {
		JMenu subMenu = new JMenu(getName());
		JMenu sourceM = (JMenu)source.getMenuItem(parent);
		for (Component cm : sourceM.getMenuComponents())
			subMenu.add(cm);
 		subMenu.add(new JSeparator());
		subMenu.add(gradient.getMenuItem( parent ));
		return subMenu;
	}	

}
