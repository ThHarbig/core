/*
 * Created on 31.01.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Component;

import javax.swing.JTable;

import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.ParameterType;
import mayday.interpreter.rinterpreter.core.RSourceParam;

/**
 * @author Matthias Zschunke
 *
 *  TableCellEditor to use with the FunctionParamChooserDialog.
 */
@SuppressWarnings("serial")
public class TypedParamCellEditor
extends AbstractTypeCellEditor
{
  
    public TypedParamCellEditor()
    {
        super();
    }
    
    
    public Component getTableCellEditorComponent(
        JTable table, 
        Object value, 
        boolean isSelected, 
        int row, int column)
    {       
        RSourceParam p=((ParamTableModel)table.getModel()).
        getParameter(row);
        
        //values column
        if(table.getColumnModel().getColumn(column).
                getHeaderValue().equals(RDefaults.Titles.PARAMCHOOSERCOLUMN2))
        {
            try
            {
                ParameterType t=p.getType();
                
                if(t==null || t.values().length==0)
                {
                    this.textField.setText(value.toString());
                    this.current=this.textField;
                    return current;
                }else if(t.editor()==ParameterType.COMBOBOX)
                {                
                    this.comboBox.removeAllItems();
                    String[] entries=t.values();
                    boolean selected=false;
                    if(entries!=null && entries.length!=0)
                    {
                        for(int i=0; i!=entries.length; ++i)
                        {
                            this.comboBox.addItem(entries[i]);
                            if(entries[i].equals((String)value))
                            {
                                this.comboBox.setSelectedItem(value);
                                selected=true;
                            }
                        }
                        if(!selected 
                                && value!=null 
                                && !((String)value).equals(""))
                        {
                            this.comboBox.addItem(value);
                            this.comboBox.setSelectedItem(value);
                        }
                    }    
                    current=comboBox;
                    return current;
                }
            }catch(Exception e)
            {
                ; //ignore for default behaviour
            }
        }        
        this.textField.setText((String)value);
        this.current=this.textField;
        return current;
    }
}
