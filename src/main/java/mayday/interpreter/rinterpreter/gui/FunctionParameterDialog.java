/*
 * Created on 19.02.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import mayday.core.gui.MaydayDialog;
import mayday.core.meta.MIManager;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RSettings;
import mayday.interpreter.rinterpreter.core.RSourceParam;
import mayday.interpreter.rinterpreter.core.mi.MIOGroupRequirement;

/**
 * In this dialog the user can choose the required
 * parameters for the applicable function.
 * 
 * @author Matthias Zschunke
 *
 */
@SuppressWarnings("serial")
public class FunctionParameterDialog extends MaydayDialog
{
    private RSettings settings;
    private JTable table;
    private RunAction runAction;
    
    public FunctionParameterDialog(RSettings settings)
    {
        super(RDefaults.MAYDAY_FRAME());
        
        this.settings=settings;
        setTitle(
            RDefaults.Titles.FUNCTIONPARAMCHOOSER+
            ": "+
            settings.getSource().getDescriptor()    
        );
        
        initialize();
    }
    
    protected void initialize()
    {
        Box labelBox=Box.createHorizontalBox();
        labelBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        labelBox.add(new JLabel("Set the parameters for the function '"+settings.getSource().getDescriptor()+"':"));
        labelBox.add(Box.createHorizontalGlue());

        //creating the Box that holds the table
        Box tableBox=Box.createHorizontalBox();
        tableBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        //creating the table
        String[] colNames=new String[]
        {
            RDefaults.Titles.PARAMCHOOSERCOLUMN1,
            RDefaults.Titles.PARAMCHOOSERCOLUMN2
        };
        table = new JTable(new ParamTableModel(this.settings.getSource(),colNames));
        table.setColumnSelectionAllowed(false);
        table.setDragEnabled(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //table.getColumnModel().getColumn(1).
        //create and set the cell renderer for columns, such that
        //a tooltip will be displayed
        TableColumn col1=table.getColumnModel().getColumn(0);
        col1.setCellRenderer(new TooltipCellRenderer());
        
        TableColumn col2=table.getColumnModel().getColumn(1);
        col2.setCellRenderer(new TooltipCellRenderer());
        
        //col2.setCellEditor(new DefaultCellEditor(new JComboBox(new String[] {"1","2"})));
        col2.setCellEditor(new TypedParamCellEditor());
        table.setRowHeight((int)(table.getRowHeight()*1.33));
        table.validate();
        
        
        //embed the table into a scroll pane
        JScrollPane scroll=new JScrollPane(table);
        Dimension sz=scroll.getPreferredSize();
        sz.setSize(
            sz.getWidth(),
            table.getPreferredSize().getHeight()+table.getTableHeader().getPreferredSize().height);
        scroll.setPreferredSize(sz);
        tableBox.add(scroll);
        
        //creating the buttons        
        Box buttonBox=Box.createHorizontalBox();
        buttonBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(new JButton(new CancelAction()));
        buttonBox.add(Box.createHorizontalStrut(3));
        buttonBox.add(new JButton(runAction=new RunAction()));
        
        if(settings.getSource().requiresMIOGroups())
        {
            //The select-button is shown iff MIOGroups are required!
            //The run-action will be enabled from the select-action.
            JButton b;
            buttonBox.add(Box.createHorizontalStrut(3));
            buttonBox.add(b=new JButton(new SelectAction()));
            b.setToolTipText("Select the required MIO Groups");
        }else
        {
            //Otherwise we must enable the run-action.
            runAction.setEnabled(true);
        }
        
                
        Box globalBox=Box.createVerticalBox();
        globalBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));      
        globalBox.add(labelBox);
        globalBox.add(tableBox);
        globalBox.add(buttonBox);
        
        getContentPane().add(globalBox);
        this.setModal(true);
        Dimension dim=getContentPane().getPreferredSize();
        this.setSize(dim.width, dim.height+50);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);               
    }
    
    private class CancelAction extends AbstractAction
    {
        public CancelAction()
        {
            super(RDefaults.ActionNames.CANCEL);
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0)
        {
            setVisible(false);
            settings.inactivate();
        }        
    }
    
    public class RunAction extends AbstractAction
    {
        public RunAction()
        {
            super( RDefaults.ActionNames.RUN );            
            this.setEnabled(false);
        }
        
        public void actionPerformed(ActionEvent event)
        {           
            if(table.getCellEditor()!=null)
                table.getCellEditor().stopCellEditing();            
            
            int rows=table.getRowCount();
            ArrayList<RSourceParam> parlist=settings.getSource().getParameters();
            
            for(int i=1; i!=rows; ++i)
            {               
                RSourceParam par=(RSourceParam)parlist.get(i);
                String val=(String)table.getValueAt(i,1);
                if(val==null || val.equals(""))
                {
                    par.setValue(null); 
                } else
                {
                    par.setValue(val);
                }
            }       
               
            setVisible(false);
            settings.activate();
        }   
    }

    public class SelectAction extends AbstractAction
    {
        public SelectAction()
        {
            super("MIO Groups");
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            //open a MIOGroupSelectionDialog for each Type
            settings.getMIOSelection().clear();
            for(MIOGroupRequirement r:settings.getSource().getRequiredMIOGroups())
            {
                if(!r.isIN()) continue;
                
                
                try
                {
                    String s = r.getMIOType();
                    
                    if (!MIManager.getAvailableTypes().contains(s)) 
                    	throw new ClassNotFoundException();  
                    
                    MIGroupSelectionDialog dlg=new MIGroupSelectionDialog(
                        settings.getMasterTable().getDataSet().getMIManager(),
                        s
                    );
                    dlg.setDialogDescription("MIO Groups for type "+s+" (id="+r.getId()+")");
                    
                    while(true)
                    {
                        dlg.setVisible(true);
                        if( dlg.getSelection()!=null && 
                            dlg.getSelection().size()!=0) break;
                        
                        
                        int result=JOptionPane.showConfirmDialog(
                                dlg,
                                "You must select a MIO group! \n" +
                                "If there is no group, or you want to cancel the MIO group \n" +
                                "selection press <CANCEL>. Otherwise select a group and press <OK>.",
                                "Warning",
                                JOptionPane.OK_CANCEL_OPTION
                            );
                        
                        if(result==JOptionPane.CANCEL_OPTION)
                        {
                            return;
                        }
                    }
                    settings.getMIOSelection().add(dlg.getSelection());
                    
                    
                }catch(ClassNotFoundException ex)
                {
                    RDefaults.messageGUI(
                        "Could not find MIO Type with id '"+r.getMIOType()+"'!\n" +
                        "Please check class name spelling.",
                        RDefaults.Messages.Type.ERROR
                    );
                    return;
                }catch(Exception ex)
                {
                    ex.printStackTrace();
                    
                    RDefaults.messageGUI(
                        "Could not create MIO selection!\n"+
                        "Exception message: "+
                        ex.getMessage(),
                        RDefaults.Messages.Type.ERROR
                    );
                    return;
                }
            }
                        
            //if all is ok after checking, we can enable the run-action
            runAction.setEnabled(true);
        }
        
    }
    
    /**
     * CellRenderer for the table. Used for setting the
     * tooltip of the cells.
     * 
     * @author Matthias
     *
     */
    public class TooltipCellRenderer 
    extends DefaultTableCellRenderer
    {
        public Component getTableCellRendererComponent(
                JTable t, 
                Object v,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int col 
        )
        {   
            super.getTableCellRendererComponent(t,v,isSelected,hasFocus,row,col);
            
            setText((String)v);
            
            setToolTipText(((RSourceParam)settings.getSource().getParameters().get(row)).getDescription());           
            return this;
        }
    }

    
    
}
