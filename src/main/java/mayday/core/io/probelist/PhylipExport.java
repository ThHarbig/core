/*
 * Created on Jun 17, 2004
 *
 */
package mayday.core.io.probelist;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.ProbeList;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.matrix.DoubleMatrix;

/**
 * Phylip Export Plugin
 * <p>
 * 
 * Last Changes: Performance & refactoring
 * 
 * @author Markus Riester
 * @version 0.4
 *  
 */
public class PhylipExport extends FileExportPlugin {
	
	
    private static final String INVALID_BINNING = "The data is not valid binned. Phylip accepts only values 0/1 or A/G/C/T.\n Therefore, allowed values for Phylip-export are: \n - range 0 - 1 \n - range 0 - 2 \n - range 0 - 3.";
    private static final String PLUGINNAME = "Phylip Export";
    private static final String VERSION_MAJOR = "0";
    private static final String VERSION_MINOR = "4";
    private static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR;
    private static final String FULLNAME = PLUGINNAME + " " + VERSION;
    
    @SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.phylip.export",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Markus Riester",
				"riester@informatik.uni-tuebingen.de",
				"Export the data to Joe Felsenstein's Phylip-Format.<br><br>" +
				"Because this format only allows the discrete characters 0,1 or A,G,C,T, " +
				"the data must be binned \"0,1\", \"0,1,2\" or \"0,1,2,3\"",
				FULLNAME
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION,"phy");
//		pli.getProperties().put(GUDEConstants.EXPORTER_DESCRIPTION,"Phylip Export");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION,"Joe Felsenstein's Phylip-Format");		
		return pli;
    }

    @SuppressWarnings("unchecked")
	public ArrayList generatePhylipFile(DoubleMatrix l_matrix) {
        ArrayList<String> lines = new ArrayList<String>();

        /*
         * abort if data is not 0-1,0-2 or 0-3 binned
         */
        try {
            checkBinning(l_matrix, 0, 3, INVALID_BINNING);
        } catch (IllegalArgumentException e) {
            return lines;
        }

        lines.add("   " + l_matrix.nrow() + "   " + l_matrix.ncol());
        HashMap<String,String> replace = new HashMap<String,String>();
        /*
         * if we need more than 2 characters, switch to DNA-alphabet
         */
        if (((int) l_matrix.getMaxValue(false) - l_matrix.getMinValue(false)) > 1) {
            replace.put("0", "A");
            replace.put("1", "G");
            replace.put("2", "C");
            replace.put("3", "T");
        }
        for (int j = 0; j != l_matrix.nrow(); ++j) {
            String taxa_name = l_matrix.getRowName(j);

            /*
             * phylip allows only taxanames with size 10, so bring it to that
             * length
             */
            if (taxa_name.length() < 10) {
                while (taxa_name.length() != 10) {
                    taxa_name += " ";
                }
            } else {
                taxa_name = taxa_name.substring(0, 10);
            }

            /*
             * now produce the characters
             */
            String chars = "";
            for (int k = 0; k != l_matrix.ncol(); ++k) {
                String c = String.valueOf(Math.round((l_matrix.getValue(j,k))));
                if (replace.containsKey(c)) {
                    c = (String) replace.get(c);
                }
                chars += c;
            }
            lines.add(taxa_name + chars);
        }
        return lines;
    }

	@SuppressWarnings("unchecked")
	public void exportTo(List<ProbeList> l_probeLists, String file) {
		// get selected probe lists
		// if none are selected, take them all, if some are selected, ask what to do
        
        ArrayList lines = generatePhylipFile(getData(l_probeLists, l_probeLists.get(0).getDataSet().getMasterTable()));
               
        if (!lines.isEmpty()) {
            saveFileTo(lines, file);
        }    

		
	}

	public void init() {
	}

}
