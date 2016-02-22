/*
 * Created on March 03, 2009
 *
 */

package mayday.core.io.probelist;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.ReadyBufferedReader;
import mayday.core.io.dataset.SimpleSnapshot.Snapshot_v3_0;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.ProbelistFileImportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.probelistmanager.UnionProbeList;

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
public class HierarchicalPLImport extends AbstractPlugin implements ProbelistFileImportPlugin
{

	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.dataimport.HierarchicalProbeList",
				new String[0],
				Constants.MC_PROBELIST_IMPORT,
				new HashMap<String, Object>(),
				"Alexander Herbig, Florian Battke",
				"herbig@informatik.uni-tuebingen.de",
				"Imports a hierarchy of probe lists from a text file",
				"Hierarchical ProbeList import"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"*");
		//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Multiple ProbeList parser");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Hierarchical ProbeList file");
		return pli;
	}


	protected HashMap<String, Object> objectMapREAD;
	protected Pattern splitter = Pattern.compile("\t");


	public void init() {
	}

	
	protected List<ProbeList> read_ProbeLists(BufferedReader br, DataSet ds) throws Exception {
		LinkedList<ProbeList> result = new LinkedList<ProbeList>();
		
		char c;
		while (br.ready()) {
			br.mark(1);
			c = (char)br.read();
			br.reset();
			if (c=='L') {
				String line = br.readLine();
				String[] parts = splitter.split(line,0);
				String name = Snapshot_v3_0.unwrap(parts[2]);
				Color col = new Color(Integer.parseInt(parts[3]));
				String parentID = parts[4];
				ProbeList pl;
				if (parts.length>5 && parts[5].equals("*")) {
					pl = new UnionProbeList(ds, null);
				} else {
					pl = new ProbeList(ds, true);
					// add probes
					for (int i=5; i<parts.length; ++i) {
						Probe pb = ds.getMasterTable().getProbe(Snapshot_v3_0.unwrap(parts[i]));
						if (pb!=null)
							pl.addProbe(pb);
					}
				}
				pl.setName(name);
				pl.setColor(col);
				if (!parentID.equals("*"))
					pl.setParent((UnionProbeList)objectMapREAD.get(parentID));
				result.add(pl);
				objectMapREAD.put(parts[1], pl);
			} else {
				break;
			}
		} 
		return result;
	}

	public List<ProbeList> importFrom(List<String> files, DataSet dataSet) {

		objectMapREAD = new HashMap<String, Object>();
		
		String l_fileName = files.get(0);

		List<ProbeList> res = null;
		
		try
		{
			BufferedReader l_reader = new ReadyBufferedReader( new FileReader( l_fileName ) );
			res = read_ProbeLists(l_reader, dataSet);
		}
		
		catch ( Exception exception )
		{

			JOptionPane.showMessageDialog( null, exception.toString(),
					MaydayDefaults.Messages.ERROR_TITLE, JOptionPane.ERROR_MESSAGE );
		}

		if (res!=null)
			JOptionPane.showMessageDialog( null,
				"" + 
				res.size()+" new probe lists added.",
				MaydayDefaults.Messages.INFORMATION_TITLE, JOptionPane.INFORMATION_MESSAGE );

		return res;

	}
	

}
