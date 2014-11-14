package mayday.core.io.probelist.BuiltinParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.ProbelistFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class Load extends AbstractPlugin implements ProbelistFileImportPlugin {
	

	public List<ProbeList> importFrom(List<String> files, DataSet dataSet) {
		LinkedList<ProbeList> res = new LinkedList<ProbeList>();
		loadProbeLists(res, files, dataSet);
		return res;
	}

	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.gudi.OpenProbeList",
				new String[0],
				Constants.MC_PROBELIST_IMPORT,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Loads Probe lists from simple Mayday XML files",
				"ProbeList XML import"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.MANYFILES);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"xml");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"XML ProbeList parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Mayday XML ProbeList file");
		return pli;
	}

    public void loadProbeLists(LinkedList<ProbeList> res, List<String> files, DataSet selDS) {

    	for (String l_fileName : files ) {
    		try
    		{
    			ProbeList l_probeList = new ProbeList( selDS, true );
    			new SAXHandler(l_probeList).read( l_fileName );
                l_probeList.setName( new File(l_fileName).getName() );    			
    			res.add(l_probeList);
            }
        
    		catch ( RuntimeException exception )
    		{
    			exception.printStackTrace();
    			JOptionPane.showMessageDialog( null,
    					exception.getMessage(),
    					MaydayDefaults.Messages.ERROR_TITLE,
    					JOptionPane.ERROR_MESSAGE ); 
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
    		catch ( ParserConfigurationException exception )
    		{
    			JOptionPane.showMessageDialog( null,
    					exception.getMessage(),
    					MaydayDefaults.Messages.ERROR_TITLE,
    					JOptionPane.ERROR_MESSAGE ); 
    		}
    		catch ( SAXParseException exception )
    		{
    			JOptionPane.showMessageDialog( null,
    					l_fileName + 
    					":" +
    					exception.getLineNumber() +
    					"\n" +
    					exception.getMessage(),
    					MaydayDefaults.Messages.ERROR_TITLE,
    					JOptionPane.ERROR_MESSAGE ); 
    		}
    		catch ( SAXException exception )
    		{
    			JOptionPane.showMessageDialog( null,
    					exception.getMessage(),
    					MaydayDefaults.Messages.ERROR_TITLE,
    					JOptionPane.ERROR_MESSAGE ); 
    		}
    		catch ( OutOfMemoryError exception )
    		{
    			JOptionPane.showMessageDialog( null,
    					MaydayDefaults.Messages.UNABLE_TO_OPEN_PROBE_LIST +
    					"\n" + "\n" +
    					MaydayDefaults.Messages.OUT_OF_MEMORY,
    					MaydayDefaults.Messages.ERROR_TITLE,
    					JOptionPane.ERROR_MESSAGE ); 
    		}
    	}
    }
    
}
