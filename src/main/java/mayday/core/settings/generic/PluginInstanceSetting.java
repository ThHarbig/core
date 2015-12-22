package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.StringSetting;

/** Holds a plugin instance complete with subsettings. Changes to the subsettings are not lost */
public class PluginInstanceSetting<T extends AbstractPlugin> extends StringSetting implements SettingChangeListener {

	protected Set<T> predef;
	protected TreeMap<String, T> asMap = new TreeMap<String, T>();
	protected Setting childSetting; 

	public PluginInstanceSetting(String Name, String Description, T Default, Set<T> apls) {
		super(Name,Description,"");
		predef = apls;
		for (T apl : apls)
			asMap.put(pliFromInstance(apl).getIdentifier(), apl);
		setInstance(Default);
	}
	
	@SuppressWarnings("unchecked")
	public PluginInstanceSetting(String Name, String Description, String... MCs) {
		super(Name,Description,"");
		Set<T> apls = new TreeSet<T>(new AbstractPlugin.AbstractPluginInstanceComparator());
		for (PluginInfo pli : PluginManager.getInstance().getPluginsFor(MCs))
			apls.add((T)pli.newInstance());
		predef = apls;
		for (T apl : apls)
			asMap.put(pliFromInstance(apl).getIdentifier(), apl);
		if (apls.size()>0)
			setInstance(apls.iterator().next());
	}
	
	public PluginInstanceSetting(String Name, String Description, T Default, String... MCs) {
		this(Name, Description, MCs);
		if (Default!=null)
			if (asMap.containsKey(pliFromInstance(Default).getIdentifier()))
				setInstance(Default);
	}

	protected PluginInfo pliFromInstance(AbstractPlugin instance) {
		return instance==null?null:PluginManager.getInstance().getPluginFromClass(instance.getClass());
	}

	public boolean isValidValue(String newVal) {
		return asMap.containsKey(newVal);
	}

	public SettingComponent getGUIElement() {
		return new PluginInstanceSettingComponent(this);
	}

	public T getInstance() {
		T instance = asMap.get(getStringValue());
		return instance;
	}

	public void setInstance(T instance) {
		if (childSetting!=null)
			childSetting.removeChangeListener(this);
		String oldVal = getStringValue();
		String newValue = pliFromInstance(instance).getIdentifier();
		if (!representative.deSerialize(MIType.SERIAL_TEXT, newValue))
			throw new RuntimeException("Invalid value \""+newValue+"\" for Setting of type "+getType());
		childSetting=instance.getSetting();
		if (childSetting!=null)
			childSetting.addChangeListener(this);
		if (!oldVal.equals(getStringValue()))
			fireChanged();		
	}

	public boolean fromPrefNode(Preferences prefNode) {
		super.fromPrefNode(prefNode);
		if (prefNode.getChild("CHILD")!=null && childSetting!=null) {
			childSetting.fromPrefNode(prefNode.node("CHILD"));
		}
		return true;
	}
	
	public void setValueString(String newValue) {
		if (asMap!=null) {
			T apl = asMap.get(newValue);
			if (apl!=null)
				setInstance( apl );
			else
				super.setValueString(newValue);
		}
		else
			super.setValueString(newValue);
	}
	
	public Preferences toPrefNode() {
		Preferences pn = super.toPrefNode();
		if (childSetting!=null) {
			Preferences pnc = childSetting.toPrefNode();
			pnc.Name="CHILD";
			pn.connectSubtree(pnc);
		}
		return pn;
	}
	
	public PluginInstanceSetting<T> clone() {
		PluginInstanceSetting<T> pts = new PluginInstanceSetting<T>(getName(),getDescription(),getInstance(),predef);
		if (childSetting!=null)
			pts.childSetting.fromPrefNode(childSetting.toPrefNode());
		return pts;
	}
	
	public void stateChanged(SettingChangeEvent e) {
		// pass on
		e.addSource(this);
		fireChanged(e);		
	}
	

	@SuppressWarnings("unchecked")
	public class PluginInstanceSettingComponent extends AbstractSettingComponent<PluginInstanceSetting<T>>  {

		protected JComboBox cb;
		protected JPanel pnl;
		protected Setting clonedChildSetting;
		protected SettingComponent clonedChildSC;
		protected T lastSelected;
		protected Dimension minDim = new Dimension();
		protected Setting lastListenedToChildSetting;

		public PluginInstanceSettingComponent(PluginInstanceSetting setting) {
			super(setting);
			clonedChildSetting = setting.childSetting!=null?setting.childSetting.clone():null;
		}

		public void stateChanged(SettingChangeEvent e) {
			if (e.hasSource(mySetting.childSetting)) {
				PluginInfo pli = pliFromInstance(lastSelected);
				if (pli!=null&&pli.getIdentifier().equals(mySetting.getValueString()))
					clonedChildSetting.fromPrefNode(mySetting.childSetting.toPrefNode());
			} else 
				setSelected();
		}

		public void setSelected() {
			String newVal = mySetting.getStringValue();
			int i;
			for (i=0; i!=cb.getItemCount(); ++i) {
				if ( pliFromInstance(((T)cb.getItemAt(i))).getIdentifier().equals(newVal)) {
					cb.setSelectedIndex(i);
					break;
				}
			}
			updateChild();
		}
		
		protected void updateChild() {
			if (lastSelected==null || lastSelected!=cb.getSelectedItem()) { 				

				// remove old listener
				if (lastListenedToChildSetting!=null)
					lastListenedToChildSetting.removeChangeListener(this);
				// add new listener
				if (mySetting.childSetting!=null) {
					mySetting.childSetting.addChangeListener(this);
					lastListenedToChildSetting = mySetting.childSetting;
				}
				
				// get new selected item
				lastSelected = (T)cb.getSelectedItem();				
				// clone the child's setting, if necessary
				if (!pliFromInstance(lastSelected).equals(mySetting.getValueString()))
					clonedChildSetting = lastSelected.getSetting();
				else
					clonedChildSetting = mySetting.childSetting!=null?mySetting.childSetting.clone():null;
				
				// remove old editor component
				if (clonedChildSC!=null)
					pnl.remove(clonedChildSC.getEditorComponent());
				// add new editor component
				if (clonedChildSetting!=null) {
					pnl.add( (clonedChildSC = clonedChildSetting.getGUIElement()) .getEditorComponent(), BorderLayout.CENTER);
				}
				
				// fit GUI
				pnl.setPreferredSize(minDim);				
				pnl.invalidate();
				pnl.revalidate();
				pnl.repaint();
			}
		}

		public boolean updateSettingFromEditor(boolean failSilently) {
			String newVal = getCurrentValueFromGUI();
			if (newVal==null)
				return true; // no changes
			if (mySetting.isValidValue(newVal)) {
				mySetting.setInstance( asMap.get(newVal) );
				if (clonedChildSetting!=null && mySetting.childSetting!=null) {
					if (clonedChildSC.updateSettingFromEditor(failSilently)) {
						mySetting.childSetting.fromPrefNode(clonedChildSetting.toPrefNode());
						return true;
					}
					return false;
				}
				return true;
			} else {
				if (!failSilently) {
					JOptionPane.showMessageDialog(null, mySetting.getValidityHint()
							+ "\nPlease correct your input."
							,
							MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
				}
				return false;
			}
		}
		
		protected String getCurrentValueFromGUI() {
			if (cb==null)
				return null;
			return pliFromInstance((T)cb.getSelectedItem()).getIdentifier();
		}

		@SuppressWarnings("serial")
		protected Component getSettingComponent() {
			if (cb==null) {
				cb = new JComboBox(mySetting.predef.toArray());
				cb.setRenderer(new DefaultListCellRenderer() {
					 public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						 this.setToolTipText(pliFromInstance((T)value).getAbout());
						 return super.getListCellRendererComponent(list, pliFromInstance((T)value).getName(), index, isSelected, cellHasFocus);
					 }
				});
				pnl = new JPanel(new BorderLayout());
				
				for (AbstractPlugin apl : (Set<T>)mySetting.predef) {
					Setting subs = apl.getSetting();
					if (subs!=null) {
						Dimension d = subs.getGUIElement().getEditorComponent().getPreferredSize();
						minDim.width = Math.max(minDim.width, d.width);
						minDim.height = Math.max(minDim.height, d.height);
					}
				}
				minDim.height += cb.getPreferredSize().getHeight()+3;
				
				cb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						updateChild();
					}
				});
				cb.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
				pnl.add(cb, BorderLayout.NORTH);
			}			
			setSelected();
			return pnl;
		}

	}

}
