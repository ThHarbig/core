/*
 * Created on 29.11.2005
 */
package mayday.core.meta.io.datasetmio;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import mayday.core.DataSet;
import mayday.core.Preferences;
import mayday.core.gui.abstractdialogs.SimpleStandardDialog;
import mayday.core.io.csv.CSVImportSettingComponent;
import mayday.core.io.csv.ParsingTableModel;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ApplicableFunction;

/**
 * Text file import plugin.
 * 
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 29.11.2005
 *
 */
public class ImportDatasetMIO
extends AbstractPlugin implements MetaInfoPlugin, ApplicableFunction
{
	@SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		//System.out.println("PL1: Register");		
		PluginInfo pli= new PluginInfo(
				(Class)this.getClass(),
				"PAS.mio.import.forDataset",
				new String[]{},
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Read meta information from text files, associate with DataSet.",
				"Import for DataSet");
		pli.addCategory("Dataset information");
		pli.setIcon("mayday/images/table16.gif");
		return pli;  
	}
     public static final String LAST_OPEN_DIR_KEY = "lastopendir";

     
    /* (non-Javadoc)
     * @see mayday.core.AbstractPlugin#run(java.util.List, mayday.core.MasterTable)
     */
    public List<DataSet> run(List<DataSet> datasets)
    {
    	DataSet ds = datasets.get(0);
    	
        Preferences prefs = PluginInfo.getPreferences("PAS.mio.import.forDataset");
        
        JFileChooser fc = new JFileChooser(
            prefs.get(LAST_OPEN_DIR_KEY, System.getProperty("user.home"))
        );
        fc.setDialogTitle("Import Meta Information from ...");
        fc.setMultiSelectionEnabled(false);
        int res = fc.showOpenDialog(null);
        if(res!=JFileChooser.APPROVE_OPTION)
        {
            return  null;
        }
        
        prefs.put(LAST_OPEN_DIR_KEY, fc.getCurrentDirectory().getAbsolutePath());
        
       
        try
        {
            CSVImportSettingComponent comp = new CSVImportSettingComponent(
            		new ParsingTableModel(fc.getSelectedFile()));            		
            SimpleStandardDialog dlg = new SimpleStandardDialog("Import Meta Information",comp,false);
            
            dlg.setVisible(true);
            
            if(!dlg.okActionsCalled())
            {
                return null;
            }
            
            TableModel model = comp.getTableModel();
            DataSetMIOImportComponent comp1 = 
                new DataSetMIOImportComponent(ds,model);
            dlg = new SimpleStandardDialog("Refine Table", comp1);
            dlg.setVisible(true);  
            
        }catch(Exception ex)
        {
            ex.printStackTrace();
            
            JOptionPane.showMessageDialog(null, 
                "An exception occured. The import will be canceled.\n" +
                "\nMessage:\n" +
                ex.getLocalizedMessage(),
                "Import Meta Information ...",
                JOptionPane.ERROR_MESSAGE
            );   
        }
        return null;
    }


	public void init() {
	}


	@SuppressWarnings("unchecked")
	public void run(MIGroupSelection input, MIManager miManager) {
		LinkedList<DataSet> ds = new LinkedList<DataSet>();
		ds.add(miManager.getDataSet());
		run(ds);		
	}

	@Override
	public boolean isApplicable(Object... o) {
		return true; //always possible
	}

}
