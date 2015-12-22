package mayday.core.io.probelist.BuiltinParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.ProbelistFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class Store extends AbstractPlugin implements ProbelistFileExportPlugin {



	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.gudi.SaveProbeList",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Stores Probe lists to simple Mayday XML files",
				"ProbeList XML export"
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION,"xml");
//		pli.getProperties().put(GUDEConstants.EXPORTER_DESCRIPTION,"XML ProbeList parser");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION,"Mayday XML ProbeList file");
		return pli;
	}


	public void exportTo(List<ProbeList> probelists, String file) {

		int seq=0;
		File t = new File(file);		
		String ext = t.getName().contains(".") ? t.getName().substring(t.getName().lastIndexOf(".")) : "";
		String pre = file.substring(0,file.length()-ext.length());
		for (ProbeList l_probeList : probelists) {
			String l_fileName = pre + (seq>0 ? "-"+seq : "") +ext;
			++seq;
			try
			{
				new SAXHandler(l_probeList).write( l_fileName );
			}
			catch ( FileNotFoundException exception )
			{
				String l_message = MaydayDefaults.Messages.FILE_NOT_FOUND;
				l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, l_fileName );

				JOptionPane.showMessageDialog( null,
						l_message,
						MaydayDefaults.Messages.ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE ); 
			}
			catch ( IOException exception )
			{
				JOptionPane.showMessageDialog( null,
						exception.getMessage(),
						MaydayDefaults.Messages.ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE ); 
			}
		}
	}      

    
  


}
