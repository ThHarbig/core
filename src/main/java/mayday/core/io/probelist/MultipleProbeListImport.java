/*
 * Created on March 03, 2009
 *
 */

package mayday.core.io.probelist;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.ProbelistFileImportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * This plugin reads a textfile containing probe names contained in several
 * probelists and generates the respective probelists for the current data set
 * in Mayday.
 * 
 * Partially adapted from Nils Gehlenborgs SimpleProbeListImportPlugin.
 * 
 * @author Alexander Herbig
 * 
 */
public class MultipleProbeListImport extends AbstractPlugin implements ProbelistFileImportPlugin
{
  
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dataimport.MultipleProbeList",
				new String[0],
				Constants.MC_PROBELIST_IMPORT,
				new HashMap<String, Object>(),
				"Alexander Herbig",
				"herbig@informatik.uni-tuebingen.de",
				"Imports several probe lists from a text file.<br>The file format is as follows:<br><br>" +
				"<code>" +
				"# this is a comment<br>" +
				"probelist_id_1\tprobe_id_11\tprobe_id_12\t...<br>"+
				"probelist_id_2\tprobe_id_21\tprobe_id_22\t...<br>"+
				"</code>",
				"Multiple ProbeList Text import"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"*");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Multiple ProbeList parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Multiple ProbeList text file");
		return pli;
	}

  

  
 
	public void init() {
	}
	
	
	public List<ProbeList> importFrom(List<String> files, DataSet dataSet) {
		
		String l_fileName = files.get(0);
		
		LinkedList<List<String>> probeStringLists = new LinkedList<List<String>>();
		
	    //ArrayList<String> l_probeNames = new ArrayList< String >();
	      
	    try
	      {
	        BufferedReader l_reader = new BufferedReader( new FileReader( l_fileName ) );
	        
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
	          
	          probeStringLists.add(Arrays.asList(l_line.split("[\t]+")));

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
	      
	      ArrayList< ProbeList > l_probeLists = new ArrayList< ProbeList >();
	      

	      for(List<String> tmpStringList : probeStringLists)
	      {
	    	  ProbeList l_newProbeList = new ProbeList( masterTable.getDataSet(), true );
	    	  
		      for ( String name: tmpStringList.subList(1, tmpStringList.size()) )
		      {
		        Probe l_probe = masterTable.getProbe( name );
		        
		        if ( l_probe != null )
		        {
		          l_newProbeList.addProbe( l_probe );
		        }
		        else
		        {
		        	//check for displayName match
		        	MIGroup probeDisplayNames = dataSet.getProbeDisplayNames();
		        	if(probeDisplayNames != null) {
		        		Collection<Object> probes = probeDisplayNames.getObjects();
		        		for(Object o : probes) {
		        			MIType mio = probeDisplayNames.getMIO(o);
		        			if(mio.getType().equals(StringMIO.myType)) {
		        				StringMIO displayName = (StringMIO)mio;
		        				String dName = displayName.getValue();
		        				if(dName.equals(name)) {
		        					l_probe = (Probe)o;
		        					l_newProbeList.addProbe(l_probe);
		        				}
		        			}
		        		}
		        	}
		        }
		        
//		        System.err.println( "" + (l_missingCounter + l_foundCounter ) );
		      }
		      
		      l_newProbeList.setName(tmpStringList.get(0));
		      
		      
		      l_probeLists.add( l_newProbeList );
	      
	      }
	      
	      JOptionPane.showMessageDialog( null,
	          "" + 
	          l_probeLists.size()+" new probe lists added.",
	          MaydayDefaults.Messages.INFORMATION_TITLE, JOptionPane.INFORMATION_MESSAGE );
	      
	      return l_probeLists;

	}

}
