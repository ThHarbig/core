/*
 * Created on 24.05.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.core.gui.MaydayDialog;
import mayday.core.gui.abstractdialogs.AbstractStandardDialogComponent;
import mayday.core.meta.MIManager;
import mayday.core.pluma.PluginInfo;
import mayday.core.tasks.AbstractTask;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.ParameterType;
import mayday.interpreter.rinterpreter.core.RSource;
import mayday.interpreter.rinterpreter.core.RSourceDescriptionParser;
import mayday.interpreter.rinterpreter.core.RSourceParam;
import mayday.interpreter.rinterpreter.core.mi.MIOGroupRequirement;

/**
 * @author Matthias Zschunke
 *
 */
@SuppressWarnings("serial")
public class DescriptionEditorDialog extends MaydayDialog
{
    private RSource source;
    private ArrayList<AbstractStandardDialogComponent> tabs;
    
    //single dialog components
    private JTextField idField;
    private JTextField descField;
    private JTable paramTable;
    private JTable mioTable;
    private JTextArea qiArea;
    
    //value to return with showDialog()
    private int returnValue=RDefaults.Actions.CANCEL;
    
    
    private static String[] paramCols=new String[]{
        RDefaults.RSDesc.PARAMATTRIB_NAME,
        RDefaults.RSDesc.PARAMATTRIB_DEFAULT,
        RDefaults.RSDesc.TYPE_ELEM,
        RDefaults.RSDesc.PARAMATTRIB_DESC,
    };
    
    private static String[] mioCols=new String[] {
        RDefaults.RSDesc.MIOATTRIB_id,
        RDefaults.RSDesc.MIOATTRIB_classname,
        RDefaults.RSDesc.MIOATTRIB_direction
    };

    /**
     * @param owner
     * @throws java.awt.HeadlessException
     */
    public DescriptionEditorDialog(Window owner, RSource source) 
    throws HeadlessException
    {
        super(owner);
        
        File f=new File(source.getRSDescFileName());
        if(!f.canRead() && !f.canWrite())
        {
            throw new RuntimeException(
                "Editing of the description file of \n"+
                source.getDescriptor()+ 
                "\nis not supported. Maybe you have not the permission to" +
                "read or write this file."
            );
        }
                
        setTitle(RDefaults.Titles.DESCRIPTIONEDITORDIALOG+" - "+f.getName());
        setModal(true);
       
        this.source=source;
        this.tabs=new ArrayList<AbstractStandardDialogComponent>();
        
        compose();
    }
    
    protected void compose()
    {
        JTabbedPane tabbedPane=new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        
        Box globalBox=Box.createVerticalBox();
        Box buttonBox=Box.createHorizontalBox();
        
        //Functionname and descriptor
        AbstractStandardDialogComponent comp=new FunctionNameComponent();
        tabbedPane.add(comp.toString(),comp);
        tabs.add(comp);
       
        //Parameter table
        comp=new ParameterComponent();
        tabbedPane.add(comp.toString(),comp);
        tabs.add(comp);
        
        //Requires table (MIOs)
        comp=new RequiresComponent();
        tabbedPane.add(comp.toString(),comp);
        tabs.add(comp);
        
        //Quickinfo
        comp=new QuickinfoComponent();
        tabbedPane.add(comp.toString(),comp);
        tabs.add(comp);
        
        tabbedPane.addChangeListener(new FunctionChangeListener());        
        tabbedPane.addFocusListener(new FocusListener()
            {

                public void focusGained(FocusEvent e)
                {}

                public void focusLost(FocusEvent e)
                {
                    int sel = paramTable.getSelectedColumn();
                    if(sel>=0)
                    {
                        paramTable.getColumnModel().
                        getColumn(sel).getCellEditor().stopCellEditing();
                    }
                    
                    sel = mioTable.getSelectedColumn();
                    if(sel>=0)
                    {
                        mioTable.getColumnModel().
                        getColumn(sel).getCellEditor().stopCellEditing();
                    }
                }
                
            }
        );
        
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(new JButton(new CancelAction()));
        buttonBox.add(Box.createHorizontalStrut(3));
        buttonBox.add(new JButton(new SaveAction()));
        buttonBox.add(Box.createHorizontalStrut(3));
        buttonBox.add(new JButton(new OkAction()));
        
        globalBox.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        globalBox.add(tabbedPane);
        globalBox.add(Box.createVerticalStrut(5));
        globalBox.add(buttonBox);
        globalBox.add(Box.createVerticalGlue());
        
        getContentPane().add(globalBox);
        
        pack();
        setSize(new Dimension(
            getContentPane().getPreferredSize().width,
            getContentPane().getPreferredSize().height
            + getInsets().top
        ));
        setModal(true);
    }
    
    /**
     * Returns a value indicating whether the dialog was
     * canceled or ok-ed.
     * @return one out of {@linkplain RDefaults.Actions.OK}, 
     * {@linkplain RDefaults.Action.Cancel}
     */
    public int showDialog()
    {
        setVisible(true);
        return returnValue;
    }
    
    class CancelAction
    extends AbstractAction
    {
        public CancelAction()
        {
            super(RDefaults.ActionNames.CANCEL);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            paramTable.editingStopped(null);
            returnValue=RDefaults.Actions.CANCEL;
            setVisible(false);
        }
    }
    

    class OkAction
    extends AbstractAction
    {
        public OkAction()
        {
            super(RDefaults.ActionNames.OK);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            new SaveAction().actionPerformed(e);
            setVisible(false);
        }
    }
    
    private class SaveAction
    extends AbstractAction
    {
        public SaveAction()
        {
            super(RDefaults.ActionNames.SAVE);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            for(AbstractStandardDialogComponent c:tabs)
            {
                for(Action a:c.getOkActions())
                {
                    a.actionPerformed(e);
                }
            }
            
            //write to descFile
            try
            {
                RSourceDescriptionParser.writeXML(new File(source.getRSDescFileName()),source);

                //set returnValue
                returnValue=RDefaults.Actions.OK;       
            }catch(IOException ex)
            {
                RDefaults.messageGUI(ex.getMessage(),RDefaults.Messages.Type.ERROR);
                returnValue=RDefaults.Actions.CANCEL;
            }
        }
    }
    
    
    private class FunctionNameComponent
    extends AbstractStandardDialogComponent
    implements ChangeListener
    {

        public FunctionNameComponent()
        {
            super(BoxLayout.Y_AXIS);
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            
            init();
        }
        
        protected void init()
        {
            idField=new JTextField();
            idField.setText(source.getName());
            idField.setEditable(false);
            idField.setEnabled(false);
            idField.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                idField.getPreferredSize().height
            ));
            
            Box innerBox=Box.createVerticalBox();
            Box upperBox=Box.createHorizontalBox();
            upperBox.add(new JLabel(RDefaults.RSDesc.FUNCATTRIB_ID));
            upperBox.add(Box.createHorizontalGlue());
            innerBox.add(upperBox);
            innerBox.add(idField);
            
            add(innerBox);
            add(Box.createVerticalStrut(5));
            
            descField=new JTextField();
            descField.setText(source.getDescriptor());
            descField.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                descField.getPreferredSize().height
            ));
            innerBox=Box.createVerticalBox();
            upperBox=Box.createHorizontalBox();
            upperBox.add(new JLabel(RDefaults.RSDesc.FUNCATTRIB_DESC));
            upperBox.add(Box.createHorizontalGlue());
            innerBox.add(upperBox);
            innerBox.add(descField);
            
            add(innerBox);
            add(Box.createVerticalStrut(5));
            
            innerBox=Box.createHorizontalBox();
            innerBox.add(Box.createHorizontalGlue());
            innerBox.add(new JButton(
                new AbstractAction(RDefaults.ActionNames.SELECTFUN)
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        source.chooseRFunction();
                        paramTable.setModel(new ParamTableModel(source,paramCols));          
                    }
                }
            ));
            
            add(innerBox);
            add(Box.createVerticalGlue());
        }
        
        /* (non-Javadoc)
         * @see mayday.core.gui.AbstractStandardDialogComponent#getOkActions()
         */
        public ArrayList<Action> getOkActions()
        {
            return new ArrayList<Action>(Arrays.asList(
                new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        source.setDescriptor(descField.getText());
                    }
                }
            ));
        }
        
        public String toString()
        {
            return RDefaults.RSDesc.FUNCNAME_ELEM;
        }

        /* (non-Javadoc)
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        public void stateChanged(ChangeEvent e)
        {
            System.out.println(e);
        }
    }

    private class ParameterComponent
    extends AbstractStandardDialogComponent
    {

        public ParameterComponent()
        {
            super(BoxLayout.Y_AXIS);
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            
            init();
        }
        
        protected void init()
        {
            //creating the Table
            paramTable=new JTable(new ParamTableModel(source,paramCols))
            {    
                public JToolTip createToolTip()
                {
                    JToolTip tip=new MultiLineToolTip();
                    tip.setComponent(this);
                    return tip;
                }
            };
            
            paramTable.setColumnSelectionAllowed(false);
            paramTable.setDragEnabled(false);
            paramTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            paramTable.setRowHeight((int)(paramTable.getRowHeight()*1.25));
            paramTable.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                paramTable.getPreferredSize().height
            ));
            paramTable.setPreferredScrollableViewportSize(new Dimension(
                paramTable.getPreferredScrollableViewportSize().width,
                paramTable.getRowHeight()*7
            ));
                    
            DescriptionCellEditor edit=new DescriptionCellEditor();
            ToolTipCellRenderer render=new ToolTipCellRenderer();
            for(int i=0; i!=paramTable.getColumnCount(); ++i)
            {
                paramTable.getColumnModel().getColumn(i).setCellRenderer(
                    render    
                );
                paramTable.getColumnModel().getColumn(i).setCellEditor(
                    edit
                );
            }            
                       
            JScrollPane scroll=new JScrollPane(
                paramTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            );            
            add(scroll);
            add(Box.createVerticalStrut(5));
            
            Box box=Box.createHorizontalBox();
            box.add(Box.createHorizontalGlue());
            box.add(new JButton(new RestoreAction()));
            
            add(box);
            add(Box.createVerticalGlue());
        }
        
        /* (non-Javadoc)
         * @see mayday.core.gui.AbstractStandardDialogComponent#getOkActions()
         */
        public ArrayList<Action> getOkActions()
        {
            return new ArrayList<Action>(Arrays.asList(
                new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        int sel = paramTable.getSelectedColumn();
                        if(sel>=0)
                        {
                            paramTable.getColumnModel().
                            getColumn(sel).getCellEditor().stopCellEditing();
                        }
                        
                        //apply the changes of the function table
                        int rows=paramTable.getRowCount();
                        ArrayList<RSourceParam> parlist=source.getParameters();
                        for(int i=1; i!=rows; ++i)
                        {
                            RSourceParam par=(RSourceParam)parlist.get(i);
                            String def=(String)paramTable.getValueAt(i,1);
                            ParameterType type=(ParameterType)paramTable.getValueAt(i,2);
                            String desc=(String)paramTable.getValueAt(i,3);
                            
                            par.setDefault(def);
                            par.setType(type);
                            par.setDescription(desc);
                        }
                        
                    }
                }
            ));
        }
        
        public String toString()
        {
            return RDefaults.RSDesc.PARAM_ELEM;
        }
        
    }
    
    private class QuickinfoComponent
    extends AbstractStandardDialogComponent
    {
        public QuickinfoComponent()
        {
            super(BoxLayout.Y_AXIS);
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            
            init();
        }
        
        private void init()
        {
            qiArea=new JTextArea(12,70);
            qiArea.setText(source.getInfo());
            qiArea.setCaretPosition(0);
            
            Box box=Box.createHorizontalBox();
            box.add(Box.createHorizontalGlue());
            box.add(
                new JButton(
                    new AbstractAction(RDefaults.ActionNames.REFRESH)
                    {
                        public void actionPerformed(ActionEvent arg0)
                        {
                            source.readInfo();
                            qiArea.setText(source.getInfo());
                            qiArea.setCaretPosition(0);
                        }
                        
                    }));
            JScrollPane scroll=new JScrollPane(
                qiArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS                
            );
            scroll.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                qiArea.getPreferredSize().height+20
            ));
            add(scroll);
            add(Box.createVerticalStrut(5));
            add(box);            
        }
        
        /* (non-Javadoc)
         * @see mayday.core.gui.AbstractStandardDialogComponent#getOkActions()
         */
        public ArrayList<Action> getOkActions()
        {
            return new ArrayList<Action>(Arrays.asList(
                new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        source.setInfo( qiArea.getText() );                        
                    }
                }
            ));
        }   
        
        public String toString()
        {
            return RDefaults.RSDesc.QUICKINFO_ELEM;
        }
    }

    private class RequiresComponent
    extends AbstractStandardDialogComponent
    {
        private MIOTableModel mioTableModel=new MIOTableModel(source.getRequiredMIOGroups());
        private ClassNameCellEditor classNameCellEditor=new ClassNameCellEditor();
        
        public RequiresComponent()
        {
            super(BoxLayout.Y_AXIS);
            setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            
            init();
        }
        
        protected void init()
        {
            mioTable=new JTable(mioTableModel);
            
            mioTable.setColumnSelectionAllowed(false);
            mioTable.setDragEnabled(false);
            mioTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            mioTable.setRowHeight((int)(mioTable.getRowHeight()*1.25));
            mioTable.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                mioTable.getPreferredSize().height
            ));
            mioTable.setPreferredScrollableViewportSize(new Dimension(
                mioTable.getPreferredScrollableViewportSize().width,
                mioTable.getRowHeight()*7
            ));
                    
            mioTable.getColumnModel().getColumn(2).setCellEditor(
                new DefaultCellEditor(new JComboBox(new String[] {"IN","OUT"}))
            );
            
            mioTable.getColumnModel().getColumn(2).setCellRenderer(
                new MIOLastColumnToolTipCellRenderer()
            );
            
            mioTable.getColumnModel().getColumn(1).setCellEditor(
                classNameCellEditor
            );
            
            mioTable.getColumnModel().getColumn(0).setCellEditor(
                new DefaultCellEditor(new JTextField())
            );
            
                       
            JScrollPane scroll=new JScrollPane(
                mioTable,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            );       
            
            Box borderBox=Box.createVerticalBox();
            borderBox.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Meta Information Objects (MIO)"),
                    BorderFactory.createEmptyBorder(5,5,5,5)
            ));
            
            borderBox.add(scroll);
            borderBox.add(Box.createVerticalStrut(5));
            
            Box box=Box.createHorizontalBox();
            box.add(Box.createHorizontalGlue());
            JButton button=new JButton(new AbstractAction(RDefaults.ActionNames.REFRESH)
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        classNameCellEditor.refresh();
                    }                
                }
            );
            button.setToolTipText("Refresh the list of known MIO types.");
            box.add(button);
            box.add(Box.createHorizontalStrut(13)); 
            
            button=new JButton(new AbstractAction(RDefaults.ActionNames.ADD_SHORT)
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        mioTableModel.append();
                    }
                }
            );
            button.setToolTipText("Add a new row to the table.");
            box.add(button);
            box.add(Box.createHorizontalStrut(3));
            
            button=new JButton(new AbstractAction(RDefaults.ActionNames.REMOVE)
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        mioTableModel.remove(
                            mioTable.getSelectedRow()
                        );
                    }
                }
            );
            button.setToolTipText("Remove the selected row");
            box.add(button);
            box.add(Box.createHorizontalStrut(3));
            
            borderBox.add(box);
            
            
            box=Box.createHorizontalBox();
            box.add(new ProblemLabel());
            box.add(Box.createHorizontalGlue());
            borderBox.add(box);
            
            add(borderBox);
            add(Box.createVerticalGlue());
        }
        
        /* (non-Javadoc)
         * @see mayday.core.gui.AbstractStandardDialogComponent#getOkActions()
         */
        public ArrayList<Action> getOkActions()
        {
            return new ArrayList<Action>(Arrays.asList(
                new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        int sel = mioTable.getSelectedColumn();
                        if(sel>=0)
                        {
                            mioTable.getColumnModel().
                            getColumn(sel).getCellEditor().stopCellEditing();
                        }
                        source.setRequiredMIOGroups(mioTableModel.getData());
                    }
                }
            ));
            
        }
        
        public String toString()
        {
            return RDefaults.RSDesc.REQUIRES_ELEM;
        }
        
        private class ProblemLabel
        extends JLabel
        implements TableModelListener
        {
            public ProblemLabel()
            {
                super(" ",null,SwingConstants.LEFT);
                mioTableModel.addTableModelListener(this);                  
            }
            
            @SuppressWarnings("unchecked")
			public void tableChanged(TableModelEvent e)
            {
                //if(e.getColumn()==0 || e.getColumn()==2)
                {
                    //test for IN-mio ids (there should be some)
                    for(int row=0;row!=mioTableModel.getRowCount(); ++row)
                    {
                        if(mioTableModel.getValueAt(row,2).equals("IN") &&
                          (mioTableModel.getValueAt(row,0)==null ||
                           mioTableModel.getValueAt(row,0).equals("")))
                        {
                            setText("An MIO group of type IN should have an identifier (id).");
                            setIcon(PluginInfo.getIcon(RDefaults.ICON_WARNING));
                            return;
                        }
                    }
                    
                    //test for abstract mio classes on OUT MIOGroups
                    for(int row=0; row!=mioTableModel.getRowCount(); ++row)
                    {
                        if(mioTableModel.getValueAt(row,2).equals("OUT"))
                        {
                            try
                            {
                                Class c=Class.forName((String)mioTableModel.getValueAt(row,1));
                                if(Modifier.isAbstract(c.getModifiers()) || Modifier.isInterface(c.getModifiers()))
                                {
                                    setText("An MIO group of type OUT can neither be a interface nor an abstract class.");
                                    setIcon(PluginInfo.getIcon(RDefaults.ICON_WARNING));
                                    return;                                    
                                }
                            }catch(Exception ex)
                            {;}
                            
                        }
                    }
                    
                    //test for duplicate mio ids
                    Set<String> ids=new HashSet<String>();
                    int emptyCnt=0;
                    for(int row=0; row!=mioTableModel.getRowCount(); ++row)
                    {
                        if(mioTableModel.getValueAt(row,0)==null ||
                           ((String)mioTableModel.getValueAt(row,0)).equals(""))
                        {
                            ++emptyCnt;
                        }else
                        {
                            ids.add((String)mioTableModel.getValueAt(row,0));
                        }
                    }
                    
                    if(emptyCnt+ids.size() < mioTableModel.getRowCount())
                    {
                        setText("The MIO group identifiers (id) should be unique.");
                        setIcon(PluginInfo.getIcon(RDefaults.ICON_WARNING));
                        return;
                    }
                                
                    setText(" ");
                    setIcon(null);
                }
            }        
        }
        
        private class MIOTableModel extends AbstractTableModel
        {
            List<String[]> data=new ArrayList<String[]>();
            
            public MIOTableModel(List<MIOGroupRequirement> list)
            {
                if(list!=null)
                {
                    for(MIOGroupRequirement r:list)
                    {
                        data.add(new String[] {
                                r.getId(),
                                r.getMIOType(),
                                r.getDir()                            
                        });
                    }
                }
            }
            
            /* (non-Javadoc)
             * @see javax.swing.table.TableModel#getRowCount()
             */
            public int getRowCount()
            {
                return data.size();
            }

            /* (non-Javadoc)
             * @see javax.swing.table.TableModel#getColumnCount()
             */
            public int getColumnCount()
            {
                return 3;
            }

            /* (non-Javadoc)
             * @see javax.swing.table.TableModel#getValueAt(int, int)
             */
            public Object getValueAt(int rowIndex, int columnIndex)
            {
                if(data.get(rowIndex)[columnIndex]==null) return "";
                return ((String)data.get(rowIndex)[columnIndex]).trim();
            }
            public String getColumnName(int col) 
            {
                return mioCols[col];
            }

            
            public boolean isCellEditable(int row, int col) 
            {
                return true;                
            }

            public void setValueAt(Object value, int row, int col) 
            {
                if(value==null || ((String)value).trim().equals("")) 
                {
                    value="";
                }
                
                data.get(row)[col] = (String)value;
                fireTableCellUpdated(row, col);
            }
            
            public void append()
            {
                data.add(new String[] {"","","IN"});
                fireTableRowsInserted(data.size()-1,data.size()-1);
            }
            
            public void remove(int row)
            {
                data.remove(row);
                fireTableRowsDeleted(row,row);
            }
            
            public List<MIOGroupRequirement> getData()
            {
                List<MIOGroupRequirement> result=new ArrayList<MIOGroupRequirement>();
                for(String[] s:data)
                {
                    result.add(
                        new MIOGroupRequirement(
                            s[0],s[1],s[2]
                    ));
                }                
                
                return result;
            }
            
        }
    }

    private class RestoreAction 
    extends AbstractAction
    {

        public RestoreAction()
        {
            super(RDefaults.ActionNames.RESTORE);
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0)
        {
            paramTable.editingCanceled(null);
            ParamTableModel m=(ParamTableModel)paramTable.getModel();
            int rows=m.getRowCount();
            for(int i=0; i!=rows; ++i)
            {
                RSourceParam p=m.getParameter(i);
                m.setValueAt(p.getName(),i,0);
                m.setValueAt(p.getDefault(),i,1);
                m.setValueAt(p.getType(),i,2);
                m.setValueAt(p.getDescription(),i,3);
            }
        }        
    }
    
    private class FunctionChangeListener implements ChangeListener
    {
        public void stateChanged(ChangeEvent event)
        {
            idField.setText(source.getName());
            descField.setText(source.getDescriptor());
            qiArea.setText(source.getInfo());
            qiArea.setCaretPosition(0);          
        }
    }
    
    private static class MIOLastColumnToolTip extends MultiLineToolTip
    {
        private static MIOLastColumnToolTip sharedInstance;
        
        protected MIOLastColumnToolTip()
        {
            super();
            setTipText(
              "{Help}\nIN:  Direction from Java to R\nOUT: Direction from R to Java"    
            );
        }
        
        public static synchronized MIOLastColumnToolTip getInstance()
        {
            if(sharedInstance==null)
            {
                sharedInstance=new MIOLastColumnToolTip();
            }
            
            return sharedInstance;
        }
    }
    
    private static class MIOLastColumnToolTipCellRenderer
    extends DefaultTableCellRenderer
    {   
        public MIOLastColumnToolTipCellRenderer()
        {
            super();
            setToolTipText(createToolTip().getTipText());
        }
        
        
        
        public JToolTip createToolTip()
        {
            JToolTip tip=MIOLastColumnToolTip.getInstance();
            tip.setComponent(this);
            return tip;
        }
    }

    private static class ClassNameCellEditor extends DefaultCellEditor
    {
        private Set<String> data=new TreeSet<String>();
        private JComboBox comboBox=null;
        
        public ClassNameCellEditor()
        {
            super(new JComboBox());
            this.comboBox=(JComboBox)this.getComponent();
            
            refresh();            
        }
        
        public void refresh()
        {
            AbstractTask task=new AbstractTask("Refresh MIOType class names.")
            {
                protected void initialize()
                {
                }

                protected void doWork()
                {
                    data.clear();
                    DefaultComboBoxModel model=(DefaultComboBoxModel)comboBox.getModel();
                    model.removeAllElements();

                    for (String typename : MIManager.getAvailableTypes()) {
                    	data.add(typename);
                    }
                                        
                    for(String s:data) {
                        model.addElement(s);
                    }
                }                
            };
            
            task.start();
            //task.waitFor();            
        }
    }
    
}
