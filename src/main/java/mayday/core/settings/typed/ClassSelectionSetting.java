package mayday.core.settings.typed;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.Preferences;
import mayday.core.gui.classes.ClassSelectionDialog;
import mayday.core.gui.classes.ClassSelectionPanel;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;

public class ClassSelectionSetting extends StringSetting {

	public enum LayoutStyle {
		FULL,
		BUTTON
	};
	
	protected Integer minClasses, maxClasses;
	protected LayoutStyle style = LayoutStyle.BUTTON;
	
	protected DataSet ds;
	
	public ClassSelectionSetting(String Name, String Description, ClassSelectionModel Model, Integer minclasses, Integer maxclasses, DataSet ds) {
		super(Name, Description, Model.serialize());
		minClasses = minclasses;
		maxClasses = maxclasses;
		this.ds = ds;
	}
	
	public ClassSelectionSetting(String Name, String Description, ClassSelectionModel Model, Integer minclasses, Integer maxclasses) {
		this(Name, Description, Model, minclasses, maxclasses, null);
	}
	
	public ClassSelectionModel getModel() {
		return ClassSelectionModel.deserialize(getStringValue());
	}
	
	public ClassSelectionSetting setLayoutStyle(LayoutStyle layout) {
		style = layout;
		return this;
	}
		
	public String getValidityHint() {
		if (minClasses!=null && maxClasses!=null)
			if (minClasses!=maxClasses)
				return "Define "+minClasses+" to "+maxClasses+" classes";
			else
				return "Define exactly "+minClasses+" classes";
		if (minClasses!=null && maxClasses==null)
			return "Define at least "+minClasses+" classes";
		if (minClasses==null && maxClasses!=null)
			return "Define at most "+maxClasses+" classes";
		return "";
	}
	
	public boolean isValidValue(String value) {
		try {
			int classesFound = (ClassSelectionModel.deserialize(value)).getNumClasses();
			return ((minClasses==null || classesFound>=minClasses) && (maxClasses==null || classesFound <= maxClasses));
		} catch (Exception e) {}
		return false;
	}
		
	public void setModel(ClassSelectionModel m) {
		setValueString(m.serialize());
	}
	
	public boolean fromPrefNode(Preferences prefNode) {
		// not doing anything
		return true;
	}

	public Preferences toPrefNode() {
		// not serializing anything
		Preferences myNode = Preferences.createUnconnectedPrefTree(getName(), "-not-serialized-");
		return myNode;
	}
	
	public ClassSelectionSetting clone() {
		return new ClassSelectionSetting(getName(),getDescription(),getModel(),minClasses,maxClasses,ds);
	}
	
	public Component getMenuItem( final Window parent ) {
		final JMenuItem jmi = new JMenuItem(modelString(getModel()));
		jmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClassSelectionModel model = getModel();
				showEditingDialog(model, getDataSet());
				setModel(model);				
			}
		});		
		addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				jmi.setText(modelString(getModel()));
			}
		});
		return jmi;
	}

	
	public SettingComponent getGUIElement() {
		switch (style) {
		case BUTTON: return new ClassSelectionSettingComponent(this);
		case FULL: return new ClassSelectionSettingPanelComponent(this);
		}
		return null;
	}
	
	protected void showEditingDialog(ClassSelectionModel csm, DataSet ds) {
		ClassSelectionDialog csd = new ClassSelectionDialog(csm, ds);
		csd.setModal(true);
		csd.setVisible(true);

	}
	
	protected DataSet getDataSet() {
		return ds;
	}
	
	protected String modelString(ClassSelectionModel model) {
		if (isValidValue(model.serialize()))
				return model.getNumClasses()+" classes on "+model.getNumObjects()+" objects";
		if (minClasses!=null && maxClasses!=null)
			if (minClasses!=maxClasses)
				return "Define "+minClasses+" to "+maxClasses+" classes";
			else
				return "Define exactly "+minClasses+" classes";
		if (minClasses!=null && maxClasses==null)
			return "Define at least "+minClasses+" classes";
		if (minClasses==null && maxClasses!=null)
			return "Define at most "+maxClasses+" classes";
		return "Please define classes";
	}
	
	public class ClassSelectionSettingComponent extends AbstractSettingComponent<ClassSelectionSetting> {

		protected JButton tf;
		protected ClassSelectionModel model;
		protected String hiddenString;
		
		public ClassSelectionSettingComponent(ClassSelectionSetting setting) {
			super(setting);
			hiddenString = mySetting.getModel().serialize();
		}
		
		public void stateChanged(SettingChangeEvent e) {
			if (tf!=null) {
				model = mySetting.getModel();
				tf.setText(modelString(model));
				hiddenString = model.serialize();
			}
		}

		protected String getCurrentValueFromGUI() {
			if (tf!=null)
				return hiddenString;
			return null;
		}

		protected Component getSettingComponent() {
			if (tf==null) {
				model = mySetting.getModel();			
				tf = new JButton(modelString(model));	
				tf.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						showEditingDialog(model, mySetting.getDataSet());
						tf.setText(modelString(model));
						hiddenString = model.serialize();
					}
					
				});
			}
			return tf;
		}
		
	}
	
	public class ClassSelectionSettingPanelComponent extends AbstractSettingComponent<ClassSelectionSetting> {

		protected ClassSelectionPanel panel;
		protected ClassSelectionModel model;
		
		public ClassSelectionSettingPanelComponent(ClassSelectionSetting setting) {
			super(setting);
		}
		
		public void stateChanged(SettingChangeEvent e) {
			if (panel!=null) {
				panel.setModel(mySetting.getModel());
			}
		}

		protected String getCurrentValueFromGUI() {
			if (panel!=null)
				return panel.getModel().serialize();
			return null;
		}

		protected Component getSettingComponent() {
			if (panel==null) {
				panel = new ClassSelectionPanel(mySetting.getModel(), mySetting.getDataSet(), mySetting.minClasses, mySetting.maxClasses);
			}
			return panel;
		}
		
	}

}
