/*
 * Created on 16.12.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mayday.core.pluma.PluginInfo;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RSettings;

/**
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 16.12.2005
 *
 */
@SuppressWarnings("serial")
public class FileSettingsComponent
extends AbstractProblemDialogComponent
{
    //components
    private JTextField rbinaryText;
    private JTextField workDirText;
    private JTextField logFileText;
    
    private JRadioButton ask_input;
    private JRadioButton ask_output;
    private JRadioButton yes_input;
    private JRadioButton yes_output;
    private JRadioButton no_input;
    private JRadioButton no_output;
    
    private JComboBox plotsComboBox;
    private JCheckBox showPlotsCheckBox;
    
    Set<FileDoesNotExistProblemListener> problems=new HashSet<FileDoesNotExistProblemListener>();
    private ArrayList<Action> actions;
    
    public FileSettingsComponent()
    {
        super(BoxLayout.X_AXIS);
        setName("File Settings");
        
        compose();
    }
    
    protected void compose()
    {
        //box with textfields
        rbinaryText=new JTextField("",30);
        rbinaryText.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            rbinaryText.getPreferredSize().height
        ));
        JLabel binaryLabel=new JLabel(RDefaults.Labels.RBINARY);
        rbinaryText.getDocument().addDocumentListener(
            new FileDoesNotExistProblemListener(rbinaryText,binaryLabel)
            {
                protected boolean accept(File file)
                {
                    return file.exists() && file.isFile();
                } 
            }
        );
        rbinaryText.setText(RDefaults.getPrefs().get(
                RDefaults.Prefs.BINARY_KEY,
                RDefaults.Prefs.BINARY_DEFAULT
        ));
        
        workDirText=new JTextField("",30);
        workDirText.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            workDirText.getPreferredSize().height
        ));
        JLabel workDirLabel=new JLabel(RDefaults.Labels.RWORKING);
        workDirText.getDocument().addDocumentListener(
            new FileDoesNotExistProblemListener(workDirText,workDirLabel)
            {                
                protected boolean accept(File file)
                {
                    return file.exists() && file.isDirectory();
                }    
            }
        );
        workDirText.setText(RDefaults.getPrefs().get(
                RDefaults.Prefs.WORKINGDIR_KEY ,
                RDefaults.Prefs.WORKINGDIR_DEFAULT        
       ));
        
        
        logFileText=new JTextField("",30); 
        logFileText.setMaximumSize(new Dimension(
            Integer.MAX_VALUE,
            logFileText.getPreferredSize().height
        ));
        JLabel logFileLabel=new JLabel(RDefaults.Labels.RLOGFILE);
        logFileText.getDocument().addDocumentListener(
            new FileDoesNotExistProblemListener(logFileText,logFileLabel)
            {
                protected String getProblemText()
                {
                    return "The directory does not exist!";
                }

                protected boolean accept(File file)
                {
                    File parent=file.getParentFile();
                    return parent==null || parent.exists();
                }               
            }
        );
        logFileText.setText(RDefaults.getPrefs().get(
                RDefaults.Prefs.LOGFILE_KEY ,
                RDefaults.Prefs.LOGFILE_DEFAULT        
        ));
        
        
        Box box1=Box.createVerticalBox();
        box1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        //create binary field
        Box innerBox=Box.createVerticalBox();
        Box horzBox=Box.createHorizontalBox();
        horzBox.add(binaryLabel);
        horzBox.add(Box.createHorizontalGlue());
        innerBox.add(horzBox);
        innerBox.add(rbinaryText);
        horzBox=Box.createHorizontalBox();
        horzBox.add(Box.createHorizontalGlue());
        horzBox.add(new JButton(new BrowseAction(
            rbinaryText,
            RDefaults.Prefs.BINARY_KEY,
            false
        )));
        innerBox.add(horzBox);
        box1.add(innerBox);
        box1.add(Box.createVerticalStrut(5));
        
        //create workingdir field
        innerBox=Box.createVerticalBox();
        horzBox=Box.createHorizontalBox();
        horzBox.add(workDirLabel);
        horzBox.add(Box.createHorizontalGlue());
        innerBox.add(horzBox);
        innerBox.add(workDirText);
        horzBox=Box.createHorizontalBox();
        horzBox.add(Box.createHorizontalGlue());
        horzBox.add(new JButton(new BrowseAction(
            workDirText,
            RDefaults.Prefs.WORKINGDIR_KEY,
            true
        )));
        innerBox.add(horzBox);
        box1.add(innerBox);
        box1.add(Box.createVerticalStrut(5));
            
        //create logfile field
        innerBox=Box.createVerticalBox();
        horzBox=Box.createHorizontalBox();
        horzBox.add(logFileLabel);
        horzBox.add(Box.createHorizontalGlue());
        innerBox.add(horzBox);
        innerBox.add(logFileText);
        horzBox=Box.createHorizontalBox();
        horzBox.add(Box.createHorizontalGlue());
        horzBox.add(new JButton(new ClearLogAction()));
        horzBox.add(new JLabel(" "));
        horzBox.add(new JButton(new BrowseAction(
            logFileText,
            RDefaults.Prefs.LOGFILE_KEY,
            false
        )));
        innerBox.add(horzBox);
        box1.add(innerBox);
        box1.add(Box.createVerticalGlue());
        
        
        //box with tempfile settings
        Box box2=Box.createVerticalBox();
        box2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        Box inputFileBox=Box.createHorizontalBox();
        inputFileBox.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(RDefaults.Labels.INPUT_FILES),
                BorderFactory.createEmptyBorder(5,5,5,5)
            )
        );        
        ButtonGroup inputGroup=new ButtonGroup();
        inputGroup.add(this.ask_input=new RadioButton(RDefaults.Labels.ASK_FOR));
        inputGroup.add(this.yes_input=new RadioButton(RDefaults.Labels.ALWAYS));
        inputGroup.add(this.no_input=new RadioButton(RDefaults.Labels.NEVER));
        inputFileBox.add(this.ask_input);
        inputFileBox.add(this.yes_input);
        inputFileBox.add(this.no_input);
        
        
//        int sel=RDefaults.getPrefs().getInt(
//            RDefaults.Prefs.DELETEINPUTFILES_KEY,
//            RDefaults.Prefs.DELETEINPUTFILES
//        );
        switch(RDefaults.getPrefs().getInt(
            RDefaults.Prefs.DELETEINPUTFILES_KEY,
            RDefaults.Prefs.DELETEINPUTFILES)
        )
        {
            case RDefaults.TempFiles.DEL_ASK:
                ask_input.setSelected(true);
                break;
            case RDefaults.TempFiles.DEL_YES:
                yes_input.setSelected(true);
                break;
            default:
                no_input.setSelected(true);
        }
        box2.add(inputFileBox);
        box2.add(Box.createVerticalStrut(5));
        
        Box outputFileBox=Box.createHorizontalBox();
        outputFileBox.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(RDefaults.Labels.OUTPUT_FILES),
                BorderFactory.createEmptyBorder(5,5,5,5)
            )
        );
        ButtonGroup outputGroup=new ButtonGroup();
        outputGroup.add(this.ask_output=new RadioButton(RDefaults.Labels.ASK_FOR));
        outputGroup.add(this.yes_output=new RadioButton(RDefaults.Labels.ALWAYS));
        outputGroup.add(this.no_output=new RadioButton(RDefaults.Labels.NEVER));
        outputFileBox.add(this.ask_output);
        outputFileBox.add(this.yes_output);
        outputFileBox.add(this.no_output);
        
//        sel=RDefaults.getPrefs().getInt(
//            RDefaults.Prefs.DELETEOUTPUTFILES_KEY,
//            RDefaults.Prefs.DELETEOUTPUTFILES
//        );
        switch(RDefaults.getPrefs().getInt(
            RDefaults.Prefs.DELETEOUTPUTFILES_KEY,
            RDefaults.Prefs.DELETEOUTPUTFILES)
        )
        {
            case RDefaults.TempFiles.DEL_ASK:
                ask_output.setSelected(true);
                break;
            case RDefaults.TempFiles.DEL_YES:
                yes_output.setSelected(true);
                break;
            default:
                no_output.setSelected(true);
        }
        box2.add(outputFileBox);
        box2.add(Box.createVerticalStrut(5));
        
        
        this.plotsComboBox=new JComboBox(
            RDefaults.TempFiles.GRAPHICS_EXTENTIONS
        );
        this.plotsComboBox.setToolTipText(
            RDefaults.ToolTips.PLOT_TYPE_COMBOBOX
        );
        this.plotsComboBox.setMaximumSize(this.plotsComboBox.getPreferredSize());
        this.showPlotsCheckBox=new JCheckBox();
        this.showPlotsCheckBox.setOpaque(false);
        this.showPlotsCheckBox.setToolTipText(
            RDefaults.ToolTips.SHOW_PLOTS_CHECKBOX
        );
        this.showPlotsCheckBox.setText(
            RDefaults.Labels.SHOW_PLOTS_CHECKBOX
        );
        
        this.plotsComboBox.setSelectedIndex(
            RDefaults.getPrefs().getInt(
                RDefaults.Prefs.PLOT_TYPE_KEY,
                RDefaults.Prefs.PLOT_TYPE_DEFAULT
            )
        );
        this.showPlotsCheckBox.setSelected(
            RDefaults.getPrefs().getBoolean(
                RDefaults.Prefs.SHOW_PLOTS_KEY,
                RDefaults.Prefs.SHOW_PLOTS_DEFAULT
            )
        );
        Box plotBox=Box.createHorizontalBox();
        plotBox.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    RDefaults.Titles.RPLOTS
                ),
                BorderFactory.createEmptyBorder(5,5,5,5)
            )           
        );
        plotBox.add(this.plotsComboBox);
        plotBox.add(Box.createVerticalStrut(5));
        plotBox.add(this.showPlotsCheckBox);
        plotBox.add(Box.createHorizontalGlue());
        box2.add(plotBox);
        box2.add(Box.createVerticalGlue());
        
        add(box1);
        add(box2);
        
        actions = new ArrayList<Action>();
        actions.add(new SaveAction());
    }

    /* (non-Javadoc)
     * @see mayday.core.gui.AbstractStandardDialogComponent#getOkActions()
     */
    public ArrayList<Action> getOkActions()
    {
        return new ArrayList<Action>(actions);
    }
    
    public void applyValues( RSettings settings)
    {
          //apply changes to the RSettings object
          
          //textFields
          settings.setBinary(rbinaryText.getText());
          settings.setWorkingDir(workDirText.getText());
          settings.setLogFilename(logFileText.getText());
          
          //tempfiles
          settings.setDeleteInputFiles(
              ask_input.isSelected() ? RDefaults.TempFiles.DEL_ASK :
              yes_input.isSelected() ? RDefaults.TempFiles.DEL_YES :
                                       RDefaults.TempFiles.DEL_NO
              
          );
          settings.setDeleteOutputFiles(
              ask_output.isSelected() ? RDefaults.TempFiles.DEL_ASK :
              yes_output.isSelected() ? RDefaults.TempFiles.DEL_YES :
                                        RDefaults.TempFiles.DEL_NO
          );
          
          //plots
          settings.setPlotType(plotsComboBox.getSelectedIndex());
          settings.setShowPlots(showPlotsCheckBox.isSelected());
    }
    
    public String toString()
    {
        return RDefaults.Titles.FILE_SETTINGS;
    }
    
    private class SaveAction extends AbstractAction
    {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0)
        {
            if(problems.size()!=0) return;
            
            
            //save binary, working dir, log file
            RDefaults.getPrefs().put(
                RDefaults.Prefs.BINARY_KEY,
                rbinaryText.getText());
            RDefaults.getPrefs().put(
                RDefaults.Prefs.WORKINGDIR_KEY,
                workDirText.getText());
            RDefaults.getPrefs().put(
                RDefaults.Prefs.LOGFILE_KEY,
                logFileText.getText());
                
            RDefaults.getPrefs().putInt(
                RDefaults.Prefs.DELETEINPUTFILES_KEY,
                (ask_input.isSelected()? RDefaults.TempFiles.DEL_ASK :
                 yes_input.isSelected()? RDefaults.TempFiles.DEL_YES :
                 RDefaults.TempFiles.DEL_NO)
            );
            
            RDefaults.getPrefs().putInt(
                RDefaults.Prefs.DELETEOUTPUTFILES_KEY,
                (ask_output.isSelected()? RDefaults.TempFiles.DEL_ASK :
                 yes_output.isSelected()? RDefaults.TempFiles.DEL_YES :
                 RDefaults.TempFiles.DEL_NO)
            );
            
            //Save PlotType in Preferences
            RDefaults.getPrefs().putInt(
                    RDefaults.Prefs.PLOT_TYPE_KEY,
                    plotsComboBox.getSelectedIndex()
            );
            
            //save showPlots
            RDefaults.getPrefs().putBoolean(
                    RDefaults.Prefs.SHOW_PLOTS_KEY,
                    showPlotsCheckBox.isSelected()
            );    
            
            
            try
            {
                RDefaults.getPrefs().flush();
            }catch(BackingStoreException ex)
            {
                ex.printStackTrace();
            }
        }        
    }
    
    
    
    /**
     * Open a browse dialog.
     * 
     * @author Matthias
     *
     */
    private class BrowseAction extends AbstractAction
    {
        private JTextField textField;
        private boolean dirSelection;
//        private String prefsKey;
        
        public BrowseAction(JTextField textField, String prefsKey, boolean dirSelection)
        {
            super(RDefaults.ActionNames.BROWSE);
            this.textField=textField;
//            this.prefsKey=prefsKey;
            this.dirSelection=dirSelection;
        }
        
//        public BrowseAction(JTextField textField, String prefsKey)
//        {
//            super(RDefaults.ActionNames.BROWSE);
//            this.textField=textField;
////            this.prefsKey=prefsKey;
//            this.dirSelection=false;
//        }
        
        public void actionPerformed(ActionEvent event)
        {
            JFileChooser fileDlg=new JFileChooser(
                dirSelection?
                new File(textField.getText()):
                new File(textField.getText()).getParentFile()
            );
            
            int returnVal=-1;
            if(dirSelection)
            {
                fileDlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                returnVal=fileDlg.showOpenDialog((Component)event.getSource());
                
            } else
            {
                returnVal=fileDlg.showOpenDialog((Component)event.getSource());
            }
        
            if(returnVal==JFileChooser.APPROVE_OPTION)
            {
                String fileName=fileDlg.getSelectedFile().getAbsolutePath();
                if(fileName==null) return;                              
                
                //set textField entry
                textField.setText(fileName);                
            }
        }    
    }
    
    private class ClearLogAction extends AbstractAction
    {
        public ClearLogAction()
        {
            super(RDefaults.ActionNames.CLEANLOG);
        }

        public void actionPerformed(ActionEvent arg0)
        {
            File f=new File(logFileText.getText());
                        
            if(f.exists())
            {
                try
                {
                    if(f.length()!=0)
                    {
                        //check if this is really a logfile
                        BufferedReader rd=new BufferedReader(new FileReader(f));
                        String firstLine=rd.readLine();
                        if(!firstLine.equals(RDefaults.R_LOGFILE_HEADER))
                        {
                            throw new IOException(f.getName()+" is not an RForMayday log-file.");
                        }
                    }  
                    
                    //print the logfile header to the file.
                    PrintWriter w=new PrintWriter(new FileWriter(f,false));
                    w.println(RDefaults.R_LOGFILE_HEADER);
                    w.close();
                    
                }catch(IOException ex)
                {
                    JOptionPane.showMessageDialog(
                        RDefaults.RForMaydayDialog,
                        RDefaults.Messages.FILE_COULD_NOT_WRITE_TO+f.getName()+".\n\n"+
                        ex.getMessage(),
                        RDefaults.messageTitle(RDefaults.Messages.Type.WARNING),
                        RDefaults.Messages.Type.WARNING
                    );
                }                                
            }
        }
    }
    
    
    private class RadioButton
    extends JRadioButton
    {

        /**
         * 
//         */
//        public RadioButton()
//        {
//            super();
//            setOpaque(false);
//        }
//
//        /**
//         * @param a
//         */
//        public RadioButton(Action a)
//        {
//            super(a);
//        }
//
//        /**
//         * @param icon
//         * @param selected
//         */
//        public RadioButton(Icon icon, boolean selected)
//        {
//            super(icon, selected);
//        }
//
//        /**
//         * @param icon
//         */
//        public RadioButton(Icon icon)
//        {
//            super(icon);
//        }
//
//        /**
//         * @param text
//         * @param selected
//         */
//        public RadioButton(String text, boolean selected)
//        {
//            super(text, selected);
//        }
//
//        /**
//         * @param text
//         * @param icon
//         * @param selected
//         */
//        public RadioButton(String text, Icon icon, boolean selected)
//        {
//            super(text, icon, selected);
//        }
//
//        /**
//         * @param text
//         * @param icon
//         */
//        public RadioButton(String text, Icon icon)
//        {
//            super(text, icon);
//        }

        /**
         * @param text
         */
        public RadioButton(String text)
        {
            super(text);
            setOpaque(false);
        }
                
    }
    

    private class FileDoesNotExistProblemListener
    implements DocumentListener
    {
        private JTextField text;
        private JLabel label;
        private String originalToolTipText;
        private Icon originalLabelIcon;

        public FileDoesNotExistProblemListener(JTextField text, JLabel label)
        {
            this.text=text;
            this.label=label;
            this.originalToolTipText=text.getToolTipText();
            this.originalLabelIcon=label.getIcon();
        }

        public void insertUpdate(DocumentEvent e)
        {
            checkChange();            
        }

        public void removeUpdate(DocumentEvent e)
        {
            checkChange();
            
        }

        public void changedUpdate(DocumentEvent e)
        {
            checkChange();
            
        }
        
        protected synchronized void checkChange()
        {   
            File file=new File(text.getText());
            if(accept(file))
            {
                text.setToolTipText(originalToolTipText);
                label.setIcon(originalLabelIcon);
                
                problems.remove(this);
                if(problems.size()==0) fireProblemSolved();
                
            }else //here we do something to indicate that a problem occured
            {   
                text.setToolTipText(getProblemText());
                label.setIcon(createIcon());
                
                problems.add(this);                
                fireProblemOccured();
            }
        }
        
        protected boolean accept(File file)
        {
            return file.exists();
        }
        
        protected String getProblemText()
        {
            return "The file does not exist!";
        }
        
        protected ImageIcon createIcon()
        {
        	return PluginInfo.getIcon(RDefaults.ICON_WARNING);
        }        
    }
}
