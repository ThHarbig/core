package mayday.core.settings.typed;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.ProbeListManagerEvent;
import mayday.core.probelistmanager.ProbeListManagerListener;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;

public class ProbeListSetting extends StringSetting {

	protected ProbeListManager plmanager;
	
	public ProbeListSetting(String Name, String Description, ProbeList Default, DataSet ds, boolean AllowEmpty) {
		super(Name, Description, Default!=null?(Default.getName()):"", AllowEmpty);
		this.plmanager = ds.getProbeListManager();
	}
	
	public ProbeListSetting clone() {
		return new ProbeListSetting(getName(),getDescription(),getProbeList(),plmanager.getDataSet(),allowEmpty);
	}
	
	public String getValidityHint() {	
		return 
			getName()+(!allowEmpty?" must be set to an existing ProbeList":"");
	}
	
	public boolean isValidValue(String value) {
		if (!super.isValidValue(value))
			return false;
		ProbeList pl = plmanager.getProbeList(value);
		if (!(allowEmpty || pl!=null))
			return false;
		return true;
	}
	
	public ProbeList getProbeList() {		
		String s = getStringValue();
		return plmanager.getProbeList(s);
	}
	
	public void setProbeList(ProbeList pl) {
		if (pl!=null)
			setValueString(pl.getName());
	}
	
	public SettingComponent getGUIElement() {
		return new ProbeListSettingComponent(this);
	}
	
	public class ProbeListSettingComponent extends AbstractSettingComponent<ProbeListSetting> {

		protected JButton btn;
		protected String path = null;
		
		public ProbeListSettingComponent(ProbeListSetting s) {
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
	        	ProbeListSelectionDialog mgsd = new ProbeListSelectionDialog(plmanager);
	        	mgsd.setModal(true);
	        	mgsd.setDialogDescription("Select one Probe List");
	        	mgsd.setVisible(true);	        	
	        	List<ProbeList> mgs = mgsd.getSelection();
	        	if (mgs.size()>0)
	        		path=mgs.get(0).getName();
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
	protected class MenuList extends JList implements ProbeListManagerListener {

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
		
		public void fill() {
			getModel().clear();
			int seli=-1;
			int i=0;
			List<ProbeList> pls ;
			pls = plmanager.getProbeLists();
			for (ProbeList pl : pls) {
				String s = pl.getName();
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
			plmanager.removeProbeListManagerListener(this);
		}
		
		public void addNotify() {
			super.addNotify();
			plmanager.addProbeListManagerListener(this);
		}

		@Override
		public void probeListManagerChanged(ProbeListManagerEvent event) {
			fill();			
		}
		
	}


}
