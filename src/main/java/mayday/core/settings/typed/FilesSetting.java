package mayday.core.settings.typed;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.GenericSetting;
import mayday.core.settings.generic.GenericSettingComponent;

public class FilesSetting extends GenericSetting {

	protected boolean allowEmpty = true;
	protected String fileFilter;

    public static final String LAST_OPEN_DIR_KEY = "lastopendir";

	public FilesSetting(String Name, String Description, List<String> Default) {
		super(Name, StringListMIO.class, Description);
		if (Default!=null)
			setFileNames(Default);
		else
			setFileNames(Collections.<String>emptyList());
	}

	public FilesSetting(String Name, String Description, List<String> Default, boolean AllowEmpty, String FileFilter) {
		this(Name, Description, Default);
		allowEmpty = AllowEmpty;
		fileFilter = FileFilter;
	}
	
	public FilesSetting clone() {
		return new FilesSetting(getName(),getDescription(),getFileNames());
	}

	public List<String> getFileNames() {
		return ((StringListMIO)representative).getValue();
	}

	public void setFileNames(List<String> nv) {
		((StringListMIO)representative).getValue().clear();
		((StringListMIO)representative).getValue().addAll(nv);
	}

	public String getValidityHint() {
		return getName()+" must not be empty";
	}

	public boolean isValidValue(String value) {
		return (super.isValidValue(value) && (allowEmpty || value.trim().length()>0));
	}

	public SettingComponent getGUIElement() {
		return new FileListSettingComponent(this);
	}


	public static class FileListSettingComponent extends GenericSettingComponent<StringListMIO> {

		public FileListSettingComponent(FilesSetting s) {
			super(s);
		}

		@SuppressWarnings("unchecked")
		protected Component getSettingComponent() {
			if (miRenderer==null) {
				miRenderer = new FileListMIORenderer((FilesSetting)mySetting);
				miRenderer.setEditable(true);
				miRenderer.connectToMIO((StringListMIO)mySetting.getValue(), (Object)null, (MIGroup)null);
			}
			return miRenderer.getEditorComponent();
		}		
	}

	@SuppressWarnings("serial")
	protected static class FileListMIORenderer extends StringListMIO.StringListMIORenderer {

		protected FilesSetting fs;

		public FileListMIORenderer(FilesSetting s) {			
			super();
			fs = s;
			tableField.getColumnModel().getColumn(0).setHeaderValue("File name");
			addRowButton.setAction(new AddFileAction());
		}


		protected class AddFileAction extends AbstractAction {
			public AddFileAction() {
				super("Add");
			}
			public void actionPerformed(ActionEvent arg0) {
				File[] files;
				// build dialog;
				
				Preferences prefs = MaydayDefaults.Prefs.getPluginPrefs().node( this.getClass().getName() );
				JFileChooser fileDialog = new JFileChooser(
			            prefs.get(LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
				);
				
				fileDialog.setMultiSelectionEnabled(true);
				if (fs.fileFilter!=null) {
					fileDialog.setFileFilter( new FileFilter() {
						public boolean accept( File f ) {
							return f.isDirectory() || f.getName().toLowerCase().endsWith(fs.fileFilter);

						}
						public String getDescription() {
							return fs.getName();
						}
					} );
				}
				int fdr = fileDialog.showOpenDialog(null) ;
				if(fdr==JFileChooser.APPROVE_OPTION) {
					files=fileDialog.getSelectedFiles();
					prefs.put(LAST_OPEN_DIR_KEY, fileDialog.getCurrentDirectory().getAbsolutePath());
					for(int i=0; i < files.length; ++i) {	
						String s = files[i].getPath();
						s = s.replace('\\', '/');
						tableModel.setRowCount(tableModel.getRowCount()+1);
						tableModel.setValueAt(s, tableModel.getRowCount()-1, 0);
					}
				}
			}		
		}

	}


}
