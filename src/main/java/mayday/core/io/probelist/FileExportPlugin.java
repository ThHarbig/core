/*
 *  Created on Sep 5, 2004
 *
 */
package mayday.core.io.probelist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import mayday.core.LastDirListener;
import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.io.gude.prototypes.ProbelistFileExportPlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.structures.linalg.matrix.DoubleMatrix;

/**
 * Superclass for Plugins that export the expression matrix to a textfile
 * 
 * Last Change: 2004/09/29 Refactoring
 * 
 * @author Markus Riester
 * @version 0.4
 */
public abstract class FileExportPlugin extends AbstractPlugin
implements ProbelistFileExportPlugin
 {

    private boolean gui = true;

 
    /**
     * Performs a simple check for binning
     * 
     * @param matrix The matrix to be checked
     * @param min    The smallest value allowed in this matrix   
     * @param max    The highest value allowed in this matrix
     * @param message  The error message to be displayed if values exceed allowed range
     * @throws IllegalArgumentException
     */
    protected void checkBinning(DoubleMatrix matrix, int min, int max, String message)
            throws IllegalArgumentException {
        int m_min = ((int) matrix.getMinValue(false));
        int m_max = (int) matrix.getMaxValue(false);
        if ( m_min < min
                ||  m_max > max) {
            if (isGui()) {
                JOptionPane.showMessageDialog(null, message + "\n",
                        MaydayDefaults.Messages.ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println(message);
                System.err.println("Smallest value in matrix: " + m_min + " must be >= " + min);
                System.err.println("Highest  value in matrix: " + m_max + " must be <= " + max);
            }
            throw new IllegalArgumentException(message);
        }

    }
    
    /**
     * Returns a Matrix with the Data. Unique Probes
     * 
     * @param probeLists
     * @param masterTable
     * @return @throws
     *         RuntimeException
     */
    protected DoubleMatrix getData(java.util.List<ProbeList> probeLists, MasterTable masterTable)
            throws RuntimeException {
        ProbeList l_uniqueProbeList = new ProbeList(masterTable.getDataSet(),
                false);

        // this list is sorted according to the sorting of the layers, top layer
        // at the top of the list
        java.util.List<Probe> l_uniqueProbes = new ArrayList<Probe>();

        // create a unique subset of the input probe lists
        for (int i = 0; i < probeLists.size(); ++i) {
            ProbeList l_newProbeList = new ProbeList(masterTable.getDataSet(),
                    false);
            ProbeList l_probeList = (ProbeList) probeLists.get(i);

            // extract new probes
            l_newProbeList.setOperation(l_uniqueProbeList.invert(false),
                    l_probeList, ProbeList.AND_MODE);

            // store new probes
            l_uniqueProbeList.setOperation(l_uniqueProbeList, l_probeList,
                    ProbeList.OR_MODE);

            l_uniqueProbes.addAll(0, l_newProbeList.toCollection());
        }

        // extract expression matrix from unique probe list

        // get array of probes from probe list
        Object[] l_probes = l_uniqueProbeList.toCollection().toArray();

        // new expression matrix
        DoubleMatrix l_matrix = new DoubleMatrix(l_probes.length, masterTable
                .getNumberOfExperiments());

        for (int i = 0; i < l_probes.length; ++i) {
            l_matrix.setRowName(i, ((Probe) l_probes[i]).getName());
            for (int j = 0; j < masterTable.getNumberOfExperiments(); ++j) {
                l_matrix.setColumnName(j, masterTable.getExperimentName(j));
                if (((Probe) l_probes[i]).getValue(j) != null) {
                    l_matrix.setValue(i, j, ((Probe) l_probes[i]).getValue(j)
                            .doubleValue());
                } else {
                    throw (new RuntimeException(
                            "Unable to cluster probes with missing expression values."));
                }
            }
        }
        //    l_matrix.setName(
        // masterTable.getDataSet().getAnnotation().getName());
        l_matrix.setName(((ProbeList) probeLists.get(0)).getName());
        return l_matrix;
    }


    /**
     * This function takes an ArrayList, opens an Save-File-Dialog, and tries to
     * write the file. Gives a lot of feedback when something went wrong
     * 
     * 
     * @param lines
     *            The content of a file in an ArrayList
	 */
    @SuppressWarnings("unchecked")
    protected void saveFile(ArrayList lines, String defaultFilename) {

        JFileChooser l_chooser = new JFileChooser();
        l_chooser.addActionListener(new LastDirListener());
        String lastOpenPath = MaydayDefaults.Prefs.NODE_PREFS.get(
                MaydayDefaults.Prefs.KEY_LASTOPENDIR,
                MaydayDefaults.Prefs.DEFAULT_LASTOPENDIR);

        if (!lastOpenPath.equals("")) {
            l_chooser.setCurrentDirectory(new File(lastOpenPath));
        }
        l_chooser.setSelectedFile(new File(defaultFilename));
        int l_option = l_chooser.showSaveDialog(null);

        if (l_option == JFileChooser.APPROVE_OPTION) {
            //       Cursor l_originalCursor = getCursor();
            //setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
            String l_fileName = l_chooser.getSelectedFile().getAbsolutePath();
            lastOpenPath = l_chooser.getCurrentDirectory().getAbsolutePath();
            File tmp = new File(l_fileName);
            if (tmp.exists()) {
                String l_message = MaydayDefaults.Messages.FILE_NAME_NOT_UNIQUE;
                l_message = l_message.replaceAll(
                        MaydayDefaults.Messages.REPLACEMENT, l_fileName);
                int ret = JOptionPane.showConfirmDialog(null, l_message,
                        MaydayDefaults.Messages.WARNING_TITLE,
                        JOptionPane.OK_CANCEL_OPTION);
                if (ret == 2)
                    return;
            }
            saveFileTo(lines, l_fileName);
        }

    }
    
    @SuppressWarnings("unchecked")
	public void saveFileTo(ArrayList lines, String l_fileName) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(
                    l_fileName));

            for (int i = 0; i != lines.size(); ++i) {
                br.write((String) lines.get(i));
                br.newLine();
            }
            br.close();
        } catch (FileNotFoundException exception) {
            String l_message = MaydayDefaults.Messages.FILE_NOT_FOUND;
            l_message = l_message.replaceAll(
                    MaydayDefaults.Messages.REPLACEMENT, l_fileName);

            JOptionPane.showMessageDialog(null, l_message,
                    MaydayDefaults.Messages.ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException exception) {

            JOptionPane.showMessageDialog(null, exception.getMessage(),
                    MaydayDefaults.Messages.ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException exception) {
            String l_message = MaydayDefaults.Messages.WRONG_FILE_FORMAT;
            l_message = l_message.replaceAll(
                    MaydayDefaults.Messages.REPLACEMENT, l_fileName);
            l_message += "\n" + exception.getMessage();

            JOptionPane.showMessageDialog(null, l_message,
                    MaydayDefaults.Messages.ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        } catch (OutOfMemoryError exception) {
            JOptionPane.showMessageDialog(null,
                    MaydayDefaults.Messages.UNABLE_TO_OPEN_DATA_SET + "\n"
                            + "\n" + MaydayDefaults.Messages.OUT_OF_MEMORY,
                    MaydayDefaults.Messages.ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }

    }


    
    /**
     * @return Returns true if this class should display graphical dialogs
     *         (MAYDAY); false otherwise.
     */
    public boolean isGui() {
        return gui;
    }

    /**
     * Switch graphical messages on or off
     * 
     * @param gui
     */
    public void setGui(boolean gui) {
        this.gui = gui;
    }
}