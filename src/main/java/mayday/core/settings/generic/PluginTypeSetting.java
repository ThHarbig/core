package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

/** Holds a Plugin Info object, instantiating a new Plugin on every call to getInstnace().
 * Probably you should use PluginInstanceSetting instead. */ 
public class PluginTypeSetting<T extends AbstractPlugin> extends StringSetting implements SettingChangeListener {

	protected Set<PluginInfo> predef;

	protected Setting childSetting; 

	public PluginTypeSetting(String Name, String Description, T Default, Set<PluginInfo> plis) {
		super(Name,Description,"");
		predef = plis;
		setInstance(Default);
	}

	public PluginTypeSetting(String Name, String Description, T Default, Collection<PluginInfo> plis) {
		this(Name, Description, Default, new HashSet<PluginInfo>(plis));
	}

	public PluginTypeSetting(String Name, String Description, T Default, String... MC) {
		this(Name, Description, Default, PluginManager.getInstance().getPluginsFor(MC));
	}
	
	
	protected PluginInfo pliFromID(String id) {
		return PluginManager.getInstance().getPluginFromID(id);
	}
	
	protected PluginInfo pliFromInstance(AbstractPlugin instance) {
		return PluginManager.getInstance().getPluginFromClass(instance.getClass());
	}
	
	
	public boolean isValidValue(String newVal) {
		return predef.contains(pliFromID(newVal));
	}

	public SettingComponent getGUIElement() {
		return new PluginTypeSettingComponent(this);
	}

	@SuppressWarnings("unchecked")
	public T getInstance() {
		T instance = (T)pliFromID(getStringValue()).newInstance();
		if (childSetting!=null)
			instance.getSetting().fromPrefNode(childSetting.toPrefNode());
		return instance;
	}
	
	public PluginInfo getPluginInfo() {
		PluginInfo info = pliFromID(getStringValue());
		return info;
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
	
	@SuppressWarnings("unchecked")
	public void setValueString(String newValue) {
		PluginInfo pli = pliFromID(newValue);
		if (pli!=null)
			setInstance( (T)pli.newInstance() );
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
	
	public PluginTypeSetting<T> clone() {
		PluginTypeSetting<T> pts = new PluginTypeSetting<T>(getName(),getDescription(),getInstance(),predef);
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
	public class PluginTypeSettingComponent extends AbstractSettingComponent<PluginTypeSetting>  {

		protected JComboBox cb;
		protected JPanel pnl;
		protected Setting clonedChildSetting;
		protected SettingComponent clonedChildSC;
		protected PluginInfo lastSelected;
		protected Dimension minDim = new Dimension();
		protected Setting lastListenedToChildSetting;

		public PluginTypeSettingComponent(PluginTypeSetting setting) {
			super(setting);
			clonedChildSetting = setting.childSetting!=null?setting.childSetting.clone():null;
		}

		public void stateChanged(SettingChangeEvent e) {
			if (e.hasSource(mySetting.childSetting)) {
				if (lastSelected.getIdentifier().equals(mySetting.getValueString()))
					clonedChildSetting.fromPrefNode(mySetting.childSetting.toPrefNode());
			} else 
				setSelected();
		}

		public void setSelected() {
			String newVal = mySetting.getStringValue();
			int i;
			for (i=0; i!=cb.getItemCount(); ++i) {
				if (((PluginInfo)cb.getItemAt(i)).getIdentifier().equals(newVal)) {
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
				lastSelected = (PluginInfo)cb.getSelectedItem();				
				// clone the child's setting, if necessary
				if (!lastSelected.getIdentifier().equals(mySetting.getValueString()))
					clonedChildSetting = lastSelected.newInstance().getSetting();
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
				mySetting.setInstance( pliFromID(newVal).newInstance() );
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
			return ((PluginInfo)cb.getSelectedItem()).getIdentifier();
		}

		@SuppressWarnings("serial")
		protected Component getSettingComponent() {
			if (cb==null) {
				cb = new JComboBox(mySetting.predef.toArray());
				cb.setRenderer(new DefaultListCellRenderer() {
					 public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
						 this.setToolTipText(((PluginInfo)value).getAbout());
						 return super.getListCellRendererComponent(list, ((PluginInfo)value).getName(), index, isSelected, cellHasFocus);
					 }
				});
				pnl = new JPanel(new BorderLayout());
				
				for (PluginInfo pli : (Set<PluginInfo>)mySetting.predef) {
					Setting subs = pli.getInstance().getSetting();
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
