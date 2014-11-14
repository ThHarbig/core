package mayday.core.settings.typed;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIManagerEvent;
import mayday.core.meta.MIManagerListener;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;

public class MIGroupSetting extends StringSetting {

	protected MIManager mimanager;
	protected Class<? extends MIType>[] acceptableClasses;
	protected boolean strict = false;
	
	public MIGroupSetting(String Name, String Description, MIGroup Default, MIManager mimanager, boolean AllowEmpty) {
		super(Name, Description, Default!=null?(Default.getPath()+"/"+Default.getName()):"", AllowEmpty);
		this.mimanager = mimanager;
	}
	
	@SuppressWarnings("unchecked")
	public MIGroupSetting setAcceptableClass(Class... clazz) {		
		acceptableClasses=clazz;
		if (getMIGroup()==null && acceptableClasses!=null && acceptableClasses.length>0) { // try auto filling
			MIGroupSelection<MIType> mgs = mimanager.getGroupsForInterfaces((Class[])acceptableClasses, strict);
			if (mgs.size()>0)
				setMIGroup(mgs.get(0));			
		}
		return this;
	}
	
	/** 
	 * @param s if set to false, inheritance is allowed in acceptable class checking. if false, it is not.
	 * @return
	 */
	public void setStrictAcceptability(boolean s) {
		strict = s;		
	}
	
	public MIGroupSetting clone() {
		return new MIGroupSetting(getName(),getDescription(),getMIGroup(),mimanager,allowEmpty).setAcceptableClass(acceptableClasses);
	}
	
	public String getValidityHint() {
		String typeString ="";
		if (acceptableClasses!=null && acceptableClasses.length>0) {
			typeString = acceptableClasses[0].getCanonicalName();
			for (int i=1; i!=acceptableClasses.length; ++i) {
				typeString += " or "+acceptableClasses[i].getCanonicalName();
			}
		}
		return 
			getName()
			 	+(!allowEmpty?
			 			(" must be set to an existing Meta Information Group" +
			 					(acceptableClasses!=null?" of type "+typeString:"") +
			 					"."
			 			)
			 	:"");
	}
	
	public boolean isValidValue(String value) {
		if (!super.isValidValue(value))
			return false;
		MIGroup mg = mimanager.getGroupByPath(value);
		if (!(allowEmpty || mg!=null))
			return false;
		if (mg!=null && acceptableClasses!=null) {
			for (Class<? extends MIType> c : acceptableClasses)
				if (c.isAssignableFrom(mg.getMIOClass()))
					return true;
			return false;
		}
		return true;
	}
	
	public MIGroup getMIGroup() {		
		String s = getStringValue();
		return mimanager.getGroupByPath(s);
	}
	
	public void setMIGroup(MIGroup mg) {
		if (mg!=null)
			setValueString(mg.getPath()+"/"+mg.getName());
	}
	
	public SettingComponent getGUIElement() {
		return new MIGroupSettingComponent(this);
	}
	
	public class MIGroupSettingComponent extends AbstractSettingComponent<MIGroupSetting> {

		protected JButton btn;
		protected String path = null;
		
		public MIGroupSettingComponent(MIGroupSetting s) {
			super(s);
			path = mySetting.getStringValue();
		}

		@SuppressWarnings("serial")
		protected Component getSettingComponent() {
			if (btn==null) {
				btn = new JButton(new BrowseAction()) {
					public String getText() {
						if (path==null || path.length()==0)
							return "Select...";
						setToolTipText(path);
						return path;
					}
				};
				Dimension d = btn.getPreferredSize();
				d.width = 200;
				btn.setPreferredSize(d);
				btn.setMinimumSize(d);
				btn.setSize(d);
			}
			return btn;
		}
		
	    @SuppressWarnings("serial")
		protected class BrowseAction extends AbstractAction  
	    {
	        public BrowseAction() {
	            super( "Select ..." );
	        }            
	        
	        public BrowseAction( String text ) {
	            super( text );
	        }
	                    
	        public void actionPerformed( ActionEvent event ) {
	        	MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(mimanager, acceptableClasses);
	        	mgsd.setModal(true);
	        	mgsd.setDialogDescription("Select one MI Group");
	        	if (mgsd.getSelectableCount()==0)
	        		JOptionPane.showMessageDialog(null, "No MIO Group found that could be selected here.", "No matching MIO Group", JOptionPane.INFORMATION_MESSAGE);
	        	mgsd.setVisible(true);	        	
	        	MIGroupSelection<MIType> mgs = mgsd.getSelection();
	        	if (mgs.size()>0)
	        		path=mgs.get(0).getPath()+"/"+mgs.get(0).getName();
	        }
	    }

		protected String getCurrentValueFromGUI() {
			return path;
		}

		public void stateChanged(SettingChangeEvent e) {
			path = this.mySetting.getStringValue();
			if (btn!=null)
				btn.repaint();
		}

	}
	
	public Component getMenuItem( final Window parent ) {
		JMenu mnu = new JMenu(getName());
		final MenuList ml = new MenuList();
		addChangeListener(new SettingChangeListener() {
			public void stateChanged(SettingChangeEvent e) {
				ml.fill();				
			}
		});
		ml.setBackground(mnu.getBackground());
		mnu.add(ml);
		return mnu;
	}
	
	@SuppressWarnings("serial")
	protected class MenuList extends JList implements MIManagerListener {

		public MenuList(){
			super(new DefaultListModel());
			setVisibleRowCount(12);
			setSelectedIndex(0);	
			setForeground(Color.BLACK);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			addListSelectionListener(new ListSelectionListener() {					
				public void valueChanged(ListSelectionEvent event) {
					if (getSelectedIndex()>-1) {
						setStringValue((String)getSelectedValue());
					}
						
				}
			});
			fill();
		}
		
		public DefaultListModel getModel() {
			return (DefaultListModel)super.getModel();
		}
		
		@SuppressWarnings("unchecked")
		public void fill() {
			getModel().clear();
			int seli=-1;
			int i=0;
			MIGroupSelection<? extends MIType> mgs ;
			if (acceptableClasses!=null)
				mgs = mimanager.getGroupsForInterfaces((Class[])acceptableClasses);
			else 
				mgs = mimanager.getGroups();
			for (MIGroup mg : mgs) {
				String s = mg.getPath()+"/"+mg.getName();
				getModel().addElement(s);
				if (s.equals(getStringValue()))
					seli=i;
				++i;
			}
			setModel(getModel());
			setSelectedIndex(seli);
		}
		
		public void removeNotify() {
			super.removeNotify();
			mimanager.removeMIManagerListener(this);
		}
		
		public void addNotify() {
			super.addNotify();
			mimanager.addMIManagerListener(this);
		}

		public void miManagerChanged(MIManagerEvent event) {
			fill();			
		}
		
	}


}
