/*
 * Created on 05.02.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.ParameterType;

/**
 * TableCellEditor for the parameter entries in the
 * DescriptionEditorDialog. 
 * 
 * For the &quot;type&quot;s column it will always be
 * a combo box containing the known types from 
 * <code>ParameterType</code>.
 * 
 * The &quot;default&quot; value's column shows either
 * a combo box containing the possible entries from 
 * a selection, or it shows a text field for editing depending
 * on the &quot;type&quot;s entry.
 * 
 * @see mayday.interpreter.rinterpreter.core.ParameterType * 
 * 
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class DescriptionCellEditor 
extends AbstractTypeCellEditor
{   
    JTable table=null;
    
    public DescriptionCellEditor()
    {
        super();
        this.comboBox.addMouseListener(new SelectionEditorListener());
    }

    /* (non-Javadoc)
     * @see mayday.interpreter.rinterpreter.gui.AbstractTypeCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(
            JTable table, Object value, 
            boolean isSelected, int row, int column)
    {
        this.value=value;
        
        if(table.getColumnModel().getColumn(column).getHeaderValue()
                .equals(RDefaults.RSDesc.TYPE_ELEM)) //edit "type" cells
        {
            ParameterType type=(ParameterType)value;
            ParameterType list;
            if( type!=null 
                && type.id().equals(RDefaults.RSDesc.TYPENAME_LIST)
              )
            {
                list=type;         
            }else
            {
                list=ParameterType.createInstance(
                    RDefaults.RSDesc.TYPENAME_LIST
                );
                String parDefault=(String)table.getModel().getValueAt(row,1);
                if(parDefault!=null && !parDefault.trim().equals(""))
                {
                    list.setValues(new String[] {parDefault});
                }   
            }
            
            //fill comboBox
            comboBox.removeAllItems();
            for(int i=0; i!=ParameterType.KNOWN_TYPES.length; ++i)
            {
                if(i==ParameterType.LIST)
                {
                    comboBox.addItem(list);
                }else
                {
                    comboBox.addItem(ParameterType.KNOWN_TYPES[i]);
                }
            }
            
            if(value==null)
            {
                this.comboBox.setSelectedIndex(0);
            }else
            {
                this.comboBox.setSelectedItem(value);
            }
            
            this.current=this.comboBox;
            return this.current;            
        }else if(table.getColumnModel().getColumn(column).getHeaderValue()
                .equals(RDefaults.RSDesc.PARAMATTRIB_DEFAULT)) //edit "default" cells
        {
            ParameterType type=(ParameterType)table.getModel().getValueAt(row,2);
            if( type!=null && type.editor()==ParameterType.COMBOBOX)
            {
                this.comboBox.removeAllItems();
                if( value!=null 
                    && (type.values()==null || type.values().length==0)
                   )
                {
                   type.setValues(new String[] {(String)value});
                }               
                
                for(int i=0; i!=type.values().length; ++i)
                {
                    this.comboBox.addItem(
                        type.values()[i]
                    );
                }
                
                if(value==null)
                {
                    this.comboBox.setSelectedIndex(0);
                }else
                {
                    this.comboBox.setSelectedItem(value);
                }
                
                this.current=this.comboBox;
                return this.current;                
            }           
        }        
        
        this.textField.setText((String)value);
        this.current=this.textField;
        return this.current;
    }
    
    /**
     * This editor is used to change the list of 
     * possible values for parameter with parameter type
     * &quot;selection&quot;.
     * 
     *  It is invoked when a double click on the 
     *  &quot;type&quot; column of the parameter table
     *  in the description editor dialog occurs.
     *  
     *  Editing is only possible for comboboxes showing
     *  &quot;selection&quot;.
     * 
     */
    private class SelectionEditorListener
    implements MouseListener
    {

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent event)
        {          
            //event.consume();
            if( event.getClickCount()==2)
            {
                if( comboBox.getSelectedItem() instanceof ParameterType
                    && !((ParameterType)comboBox.getSelectedItem()).suppressEditing()
                  )
                {
                    Component comp=comboBox;
                    while(!(comp instanceof DescriptionEditorDialog))
                    {
                        comp=comp.getParent();
                        if(comp==null) return;
                    }
                    DescriptionEditorDialog dlg=(DescriptionEditorDialog)comp;
                    SelectionEditorDialog sDlg=
                        new SelectionEditorDialog(
                            dlg, comboBox,
                            (ParameterType)comboBox.getSelectedItem()
                        );
                    
                    sDlg.showDlg();
                }                  
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent arg0)
        {           
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent arg0)
        {
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent arg0)
        {
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent arg0)
        {
        }        
    }

}
