package mayday.core.io.probelist;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.io.gude.prototypes.ProbelistFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class ExportMultipleProbeListProbeNamesToTextFile extends AbstractPlugin implements ProbelistFileExportPlugin {
	

	
	public void init() {
	}

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.gudi.ExportProbelistsToTextFile",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Alexander Herbig",
				"herbig@informatik.uni-tuebingen.de",
				"Saves probe names of selected probe lists to a simple text file.<br>"+
				"Each row contains the data for a single probe list (tab separated):<br>"+
				"probelist_id1\tprobe_id11\tprobe_id12\t...<br>"+
				"probelist_id2\tprobe_id21\tprobe_id22\t...<br>",
				"Multiple ProbeList export"
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION,"txt");
//		pli.getProperties().put(GUDEConstants.EXPORTER_DESCRIPTION,"Multiple ProbeList Probe Name Exporter");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION,"Multiple ProbeList Probe Names");
		return pli;
	}
	

	public void exportTo(List<ProbeList> probelists, String file) {
		
        try
          {
        	BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        	for(ProbeList l_probeList : probelists)
        	{
        		bw.write(l_probeList.getName());
	        	for (Probe pb : l_probeList.getAllProbes())
	        		bw.write("\t"+pb.getName());
	        	bw.newLine();
        	}
        	bw.flush();
        	bw.close();
          }            
          catch ( FileNotFoundException exception )
          {
              String l_message = MaydayDefaults.Messages.FILE_NOT_FOUND;
              l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, file );
              
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
