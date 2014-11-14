/*
 * Created on Oct 30, 2005
 *
 */
package mayday.core.io.probelist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.ProbelistFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * This plugin asks for an index file and generates a new probelist with the
 * probes that are in the selected probe and in the index-file. Comment symbol
 * is "#",
 * 
 * @author Nils Gehlenborg
 * 
 */
public class SimpleProbeListImportPlugin extends AbstractPlugin implements ProbelistFileImportPlugin
{
  private String fileName;
  
	@SuppressWarnings("unchecked")  
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dataimport.ProbeList",
				new String[0],
				Constants.MC_PROBELIST_IMPORT,
				new HashMap<String, Object>(),
				"Nils Gehlenborg",
				"neil@mangojelly.org",
				"Imports a probe list from a text file.<br>The file format is as follows:<br><br>" +
				"<code>" +
				"# this is a comment<br>" +
				"probe_id_1<br>"+
				"probe_id_2<br>"+
				"...<br>"+
				"probe_id_m<br>"+
				"</code>",
				"ProbeList Text import"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"*");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Text ProbeList parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"ProbeList text file");
		return pli;
	}

  

  
 
	public void init() {
	}
	
	
	public List<ProbeList> importFrom(List<String> files, DataSet dataSet) {
		
		String l_fileName = files.get(0);
		
	    ArrayList<String> l_probeNames = new ArrayList< String >();
	      
	    try
	      {
	        BufferedReader l_reader = new BufferedReader( new FileReader( l_fileName ) );
	        this.fileName = l_fileName;
	        String l_line;
	        
	        while ( ( l_line = l_reader.readLine() ) != null )
	        {
	          if ( l_line.trim().startsWith( "#" ) )
	          {
	            continue;
	          }
	          
	          if ( l_line.trim().length() == 0 )
	          {
	            continue;
	          }

	          l_probeNames.add( l_line.trim() );
	        }
	        
	        l_reader.close();
	      }
	      catch ( FileNotFoundException exception )
	      {
	        String l_message = MaydayDefaults.Messages.FILE_NOT_FOUND;
	        l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT,
	            l_fileName );

	        JOptionPane.showMessageDialog( null, l_message,
	            MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE );
	      }
	      catch ( IOException exception )
	      {

	        JOptionPane.showMessageDialog( null, exception.getMessage(),
	            MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE );
	      }
	      catch ( RuntimeException exception )
	      {
	        String l_message = MaydayDefaults.Messages.WRONG_FILE_FORMAT;
	        l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT,
	            l_fileName );
	        l_message += "\n" + exception.getMessage();

	        JOptionPane.showMessageDialog( null, l_message,
	            MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE );
	      }
	      catch ( OutOfMemoryError exception )
	      {
	        JOptionPane.showMessageDialog( null,
	            MaydayDefaults.Messages.UNABLE_TO_OPEN_DATA_SET + "\n" + "\n"
	                + MaydayDefaults.Messages.OUT_OF_MEMORY,
	            MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE );
	      }

	      
	      MasterTable masterTable = dataSet.getMasterTable();
	      
	      ProbeList l_newProbeList = new ProbeList( masterTable.getDataSet(), true );
	      for ( String name: l_probeNames )
	      {
	        Probe l_probe = masterTable.getProbe( name );
	        
	        if ( l_probe != null )
	        {
	          l_newProbeList.addProbe( l_probe );
	        }
	        else
	        {
	          System.out.println("Probe \""+name+"\" is not in MasterTable.");
	        }
	        
//	        System.err.println( "" + (l_missingCounter + l_foundCounter ) );
	      }
	      
	      l_newProbeList.setName( new File(this.fileName).getName() );
	      
	      ArrayList< ProbeList > l_probeLists = new ArrayList< ProbeList >();
	      l_probeLists.add( l_newProbeList );
	      
	      JOptionPane.showMessageDialog( null,
	          "" + 
	          l_newProbeList.getNumberOfProbes() + 
	          " probes out of " +
	          l_probeNames.size() +
	          " added to probe list " +
	          l_newProbeList.getName(),
	          MaydayDefaults.Messages.INFORMATION_TITLE, JOptionPane.INFORMATION_MESSAGE );
	      
	      return l_probeLists;

	}

}
