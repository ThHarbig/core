/*
 * Created on Jun 17, 2004
 *
 */
package mayday.core.io.probelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.io.gude.GUDEConstants;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.linalg.matrix.DoubleMatrix;

;

/**
 * Nexus Export Plugin
 * <p>
 * 
 * Last changes: 2004/9/29 Refactoring
 * 
 * @author Markus Riester
 * @version 0.4
 */
public class NexusExport extends FileExportPlugin {
    /*
     * a very small plugin, so define the few strings here
     */
    private static final String INVALID_BINNING = "The data is not valid binned.";
    private static final String TOO_MANY_TAXA = "Number of taxa exceeds maximum permitted in version 4.0b10 and earlier of PAUP* (16384)";
    private static final String PLUGINNAME = "Nexus Export";
    private static final String VERSION_MAJOR = "0";
    private static final String VERSION_MINOR = "4";
    private static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR;
    private static final String FULLNAME = PLUGINNAME + " " + VERSION;

    @SuppressWarnings("unused")
	private static final String[] number_alphabet = { "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9" };

    private static final String[] hex_alphabet = { "0", "1", "2", "3", "4",
            "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

    @SuppressWarnings("unused")
	private static final String[] latin_alphabet = { "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z" };

    private NexusSettings settings;

    @SuppressWarnings("unchecked")
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.nexus.export",
				new String[0],
				Constants.MC_PROBELIST_EXPORT,
				new HashMap<String, Object>(),
				"Markus Riester",
				"riester@informatik.uni-tuebingen.de",
				"Export to Nexus-Format.<br><br>"
				+ "Generates a NEXUS file, designed for PAUP (David L. Swofford). "
                + "In the default settings, a paup-block is added to this file. "
                + "This means you can run a basic phylogenetic analyis of the "
                + "data just in running PAUP with this file.<br><br>"
                + "Example for the UNIX-version of PAUP:<br><code> &gt; paup file.nxs </code>",
				FULLNAME
		);
		pli.getProperties().put(GUDEConstants.EXPORTER_TYPE, GUDEConstants.EXPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDEConstants.FILE_EXTENSION,"nxs");
//		pli.getProperties().put(GUDEConstants.EXPORTER_DESCRIPTION,"Creates Nexus files for use with PAUP (David L. Swofford)");
		pli.getProperties().put(GUDEConstants.TYPE_DESCRIPTION,"Nexus files for use with PAUP");		
		return pli;
    }
    
    
	@SuppressWarnings("unchecked")
	public void exportTo(List<ProbeList> l_probeLists, String file) {
        settings = new NexusSettings();
        settings.setAlphabet(hex_alphabet);
        
        DataSet dataSet = l_probeLists.get(0).getDataSet();

        ArrayList lines = generateNexusFile(getData(l_probeLists, dataSet.getMasterTable()));
        if (!lines.isEmpty()) {
        	saveFileTo(lines, file);
        }        	
    }

    @SuppressWarnings("unchecked")
	public ArrayList generateNexusFile(DoubleMatrix l_matrix) {
        ArrayList<String> lines = new ArrayList<String>();

        /*
         * abort if data is not 0-1,0-2 or 0-3 binned
         */
        try {
            checkBinning(l_matrix, 0, settings.getAlphabet().length, INVALID_BINNING);
        } catch (IllegalArgumentException e) {
            return lines;
        }

        lines.add("#NEXUS\n\n");

        lines.add("begin taxa;\n\tdimensions ntax=" + l_matrix.nrow()
                + ";\n\ttaxlabels");
        if (l_matrix.nrow() > 16384) {
            if (isGui()) {
                JOptionPane.showMessageDialog(null, TOO_MANY_TAXA + "\n",
                        MaydayDefaults.Messages.WARNING_TITLE,
                        JOptionPane.WARNING_MESSAGE);
            } else {
                System.out.println(MaydayDefaults.Messages.WARNING_TITLE + " "
                        + TOO_MANY_TAXA);
            }
        }
        String[] taxas = new String[l_matrix.nrow()];
        String[] characters = new String[l_matrix.nrow()];
        int max_taxa_length = 0;
        int max_values_length = l_matrix.ncol();

        for (int j = 0; j != l_matrix.nrow(); ++j) {
            taxas[j] = l_matrix.getRowName(j);

            /*
             * fix identifier names
             */
            taxas[j] = taxas[j].replaceAll("[^a-zA-Z_0-9]", "_");

            if (taxas[j].length() > max_taxa_length) {
                max_taxa_length = taxas[j].length();
            }

            /*
             * now produce the characters
             */
            String chars = "";
            for (int k = 0; k != l_matrix.ncol(); ++k) {
                int c = Math.round((float) (l_matrix.getValue(j,k)));

                chars += String.valueOf(settings.getAlphabet()[c]);
            }
            characters[j] = chars;
        }

        /*
         * make sure that everything has the correct size
         */
        for (int j = 0; j != l_matrix.nrow(); ++j) {
            if (taxas[j].length() < max_taxa_length) {
                while (taxas[j].length() != max_taxa_length) {
                    taxas[j] += " ";
                }
            }
            lines.add(taxas[j]);
            /*
             * this shouldnt happen, but just to easy to implement
             */
            if (characters[j].length() < max_values_length) {
                while (characters[j].length() != max_values_length) {
                    characters[j] += " ";
                }
            }
        }

        lines.add("\t;\nend;\n");

        lines.add("begin characters;\n\tdimensions nchar="
                + +l_matrix.ncol() + ";\n\tformat interleave symbols = \""
                + settings.getAlphabetString() + "\";" + "\n\tmatrix");
        int blocksize = 50;
        int l = 0;
        while (l < max_values_length) {

            for (int k = 0; k != l_matrix.nrow(); ++k) {
                int end = l + blocksize;
                if (end >= max_values_length) {
                    end = max_values_length;
                }
                lines.add(taxas[k] + "   " + characters[k].substring(l, end));
            }
            l += blocksize;
            if (l < max_values_length) {
                lines.add("");
            }
        }

        /*
         * Footer of this matrix
         */
        lines.add("\t;\nend;");

        if (settings.getAddAssumptionsBlock()) {
            /*
             * the header of this block
             */
            lines.add("\nbegin assumptions;\n\tusertype mymatrix (stepmatrix)="
                    + settings.getAlphabet().length);
            /*
             * col_size is just for formatting this matrix: the number of digits
             * of the largest distance. for example in the hex alphabet, 0 and f
             * have distance 15, so best looking col_size is 2.
             * 
             * Use the function rightJustify to get the values to this colsize
             */
            int col_size = 2;

            /*
             * the header of this matrix
             */
            String tmp = "\t " + rightJustify(" ", col_size);
            for (int j = 0; j != settings.getAlphabet().length; ++j) {
                tmp += " " + rightJustify(settings.getAlphabet()[j], col_size);
            }
            lines.add(tmp);

            /*
             * the values of this matrix
             */
            for (int j = 0; j != settings.getAlphabet().length; ++j) {
                tmp = "\t[" + settings.getAlphabet()[j] + "]";
                for (int k = 0; k != settings.getAlphabet().length; ++k) {
                    if (j == k) {
                        tmp += " " + rightJustify(".", col_size);
                    } else {
                        tmp += " "
                                + rightJustify(String.valueOf(Math.abs(j - k)),
                                        col_size);
                    }
                }
                lines.add(tmp);
            }
            /*
             * the footer of this matrix
             */
            lines.add("\t;");

            /*
             * the footer of this block
             */
            lines.add("end;\n");
        }

        if (settings.getAddPaupBlock()) {
            String filename = "default";

            /*
             * the header of this block
             */
            lines.add("\nbegin paup;");
            /*
             * the body of this block
             */

            /*
             * log everything
             */
            lines.add("\tlog start file=" + filename + ".log replace;");
            /*
             * use our matrix
             */
            lines.add("\tctype mymatrix: all;");

            if (settings.getPB_BandB() > 0) {
                if (settings.getPB_BandB() == 1
                        || taxas.length <= settings
                                .getPB_BandB_auto_taxalimit()) {
                    lines.add("\tSet criterion=parsimony; BandB;");
                    if (settings.getCreatePhylipTree()) {
                        lines.add("\tsavetrees format=Phylip file=" + filename
                                + "_bb.phylip BrLens=yes replace;");
                    }
                    lines.add("\tsavetrees file=" + filename
                            + "_bb.tre BrLens=yes replace;");
                }
            }

            if (settings.getPB_NJ()) {
                lines.add("\tSet criterion=distance; NJ;");
                if (settings.getCreatePhylipTree()) {
                    lines.add("\tsavetrees format=Phylip file=" + filename
                            + "_nj.phylip BrLens=yes replace;");
                }
                lines.add("\tsavetrees file=" + filename
                        + "_nj.tre BrLens=yes replace;");
            }

            /*
             * quit paup
             */
            lines.add("\tquit;");

            /*
             * the footer of this block
             */
            lines.add("end;\n");
        }

        return lines;
    }

    /**
     * 
     * @param s
     * @param length
     * @return the right justified string of the specified length
     */
    private String rightJustify(String s, int length) {
        String tmp = s;
        while (tmp.length() < length) {
            tmp = " " + tmp;
        }
        return tmp;
    }


	public void init() {
	}

}
