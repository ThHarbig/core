/*
 * Created on 29.11.2005
 */
package mayday.core.meta.io.probemio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import mayday.core.Preferences;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.ProgressListener;

/**
 * Text file export plugin.
 * 
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 29.11.2005
 *
 */
public class CSVExportPlugin
extends AbstractPlugin implements MetaInfoPlugin
{
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.mio.export.csv",
				new String[]{},
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Save meta information to tabular text files.",
				"Export Table (CSV)");
		pli.addCategory("Probe information");
		pli.setIcon("mayday/images/table16.gif");
		return pli;  
	}
     public static final String LAST_OPEN_DIR_KEY = "lastopendir";

 	public void init() {
	}


	public void run(final MIGroupSelection<MIType> input, final MIManager miManager) {
		Preferences prefs = PluginInfo.getPreferences("PAS.mio.export.csv");
        
        final JFileChooser fc = new JFileChooser(
            prefs.get(LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
        );
        fc.setDialogTitle("Export Meta Information to ...");
        fc.setMultiSelectionEnabled(false);
        int res = fc.showSaveDialog(null);
        if(res!=JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        prefs.put(LAST_OPEN_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());
        
        if (fc.getSelectedFile().exists())
        	if (JOptionPane.showConfirmDialog(null, 
        			"Replace existing file?", "Confirm file replacement", JOptionPane.YES_NO_OPTION)
        			!=JOptionPane.YES_OPTION)
        		return;
        
        exportTo(input, fc.getSelectedFile());
	}
        	
    public AbstractTask exportTo(final Collection<MIGroup> input, final File file) {
        	
        AbstractTask task = new AbstractTask("Exporting MIOs") {

			protected void doWork() throws Exception {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				exportToWriter(input, bw, this);	           
	            bw.close();
			}

			protected void initialize() {}        	
        }; 
        task.start();
        
        return task; 
        
	}
    
    public static void exportToWriter(final Collection<MIGroup> input, final BufferedWriter bw, ProgressListener pl) throws IOException {
    	 // write header
        bw.write("ProbeID");
        for (MIGroup mg : input)
        	bw.write("\t\""+mg.getName()+"\"");
        bw.write("\n");
        
        Collection<Probe> pcoll = input.iterator().next().getMIManager().getDataSet().getMasterTable().getProbes().values();
        int total = pcoll.size();
        int cur=0;
        
        // write content lines
        StringBuilder line = new StringBuilder();
        for (Object op : pcoll) {
        	Probe p = (Probe)op;
        	line.setLength(0);
        	line.append(p.getName());
        	boolean found = false;
        	for (MIGroup mg : input) {
        		MIType mt = mg.getMIO(p);
        		line.append("\t");
        		if (mt!=null) {
        			String mtt = mt.serialize(MIType.SERIAL_TEXT);
        			if (mtt.contains("\t"))
        				mtt = "\""+mtt+"\"";
        			line.append(mtt);
        			found=true;
        		}
        	}
        	if (found)
        		bw.write(line.append("\n").toString());
        	if (pl!=null)
        		pl.setProgress((10000*++cur)/total);
        	
        }
        bw.flush();    	
    }

}
