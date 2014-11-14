/*
 * Created on 05.02.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractTypeCellEditor 
extends AbstractCellEditor
implements TableCellEditor
{
    protected Object value;
    
    protected final JComboBox comboBox=new JComboBox();
    protected final JTextField textField=new JTextField();
    protected JComponent current=null;
    
    public AbstractTypeCellEditor()
    {
        super();
    }
    
    
    public abstract Component getTableCellEditorComponent(
        JTable table, Object value, boolean isSelected, int row, int column);
    
    public Component getComponent() 
    {
        return current;
    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
     */
    public boolean isCellEditable(EventObject arg0)
    {
        return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
     */
    public boolean shouldSelectCell(EventObject arg0)
    {
        return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing()
    {
        if(current instanceof JTextField)
        {
            this.value=((JTextField)current).getText();
        }else if(current instanceof JComboBox)
        {
            this.value=((JComboBox)current).getSelectedItem();
        }else
        {
            return false;
        }
        fireEditingStopped();
        this.value=null;
        return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing()
    {
        fireEditingCanceled();
        this.value=null;
    } 
}
