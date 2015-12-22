package mayday.core.settings.typed;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.meta.types.StringMIO;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.GenericSettingComponent;

public class PathSetting extends StringSetting {

    public static final String LAST_OPEN_DIR_KEY = "lastopendir";

    protected boolean Dir,Existing;

	public PathSetting(String Name, String Description, String Default, boolean directory, boolean existing, boolean allowEmpty) {
		super(Name, Description, Default, allowEmpty);
		Dir = directory;
		Existing = existing;
	}

	public String getValidityHint() {
		return 
		getName()+" must be "
		+(Existing?"an existing":"a")
		+(Dir?" directory":" file")
		+(!allowEmpty?" and may not be empty":"")
		+".";
	}

	public boolean isValidValue(String value) {

		if(allowEmpty &&  value.trim().isEmpty())
			return super.isValidValue(value);

		return (super.isValidValue(value) 
				&& (allowEmpty || value.trim().length()>0) 
				&& (!Dir || new File(value).isDirectory())
				&& (!Existing || new File(value).exists()));
	}

	public void setStringValue(String nv) {
		setValueString(nv);
	}

	public SettingComponent getGUIElement() {
		return new PathSettingComponent(this);
	}

	public PathSetting clone() {
		return new PathSetting(getName(),getDescription(),getStringValue(),Dir,Existing,allowEmpty);
	}

	public static class PathSettingComponent extends GenericSettingComponent<StringMIO> {

		protected JPanel pnl;

		public PathSettingComponent(PathSetting s) {
			super(s);
		}

		protected Component getSettingComponent() {
			if (miRenderer==null) {
				super.getSettingComponent();
				pnl = new JPanel(new BorderLayout());
				pnl.add(miRenderer.getEditorComponent(), BorderLayout.CENTER);
				pnl.add(new JButton(new BrowseAction()), BorderLayout.EAST);
				pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE,miRenderer.getEditorComponent().getPreferredSize().height));
			}
			return pnl;
		}

		@SuppressWarnings("serial")
		protected class BrowseAction extends AbstractAction  
		{
			public BrowseAction() {
				super( "Browse ..." );
			}            

			public BrowseAction( String text ) {
				super( text );
			}

			public void actionPerformed( ActionEvent event ) {
				
				Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( this.getClass().getName() );
				JFileChooser chooser = new JFileChooser(
			            prefs.get(LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
				);
				
				if (((PathSetting)mySetting).Dir)
					chooser.setFileSelectionMode(  JFileChooser.DIRECTORIES_ONLY );
				else 
					chooser.setFileSelectionMode(  JFileChooser.FILES_ONLY );

				File f=new File( getCurrentValueFromGUI());
				chooser.setCurrentDirectory(f);

				int l_option = chooser.showOpenDialog( (Component)event.getSource() );
				if ( l_option  == JFileChooser.APPROVE_OPTION ) {
					String fileName = chooser.getSelectedFile().getAbsolutePath();
		            prefs.put(LAST_OPEN_DIR_KEY, chooser.getCurrentDirectory().getAbsolutePath());      
					// if the user presses cancel, then quit
					if ( fileName == null ) {
						return;
					}
					miRenderer.setEditorValue(fileName);
				}
			}
		}

	}



}
