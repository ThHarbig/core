/*
 * Created on 24.05.2005
 */
package mayday.interpreter.rinterpreter.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.table.TableCellRenderer;

import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.ParameterType;


@SuppressWarnings("serial")
public class ToolTipCellRenderer
extends JLabel
implements TableCellRenderer
{        
    public JToolTip createToolTip()
    {
        JToolTip tip=new MultiLineToolTip();
        tip.setComponent(this);
        return tip;
    }
    
    public Component getTableCellRendererComponent(
            JTable t, 
            Object v,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int col 
    )
    {            
        setText(v==null?(String)v : v.toString());
        setToolTipText(null);
        
        if(t.getColumnModel().getColumn(col).getHeaderValue().equals(
            RDefaults.RSDesc.TYPE_ELEM)) //type
        {                
            ParameterType type=(ParameterType)v;
            if( v!=null
                && type.editor()==ParameterType.COMBOBOX
              )
            {
                StringBuffer buf=new StringBuffer();
                buf.append("{"+type.id()+"}");
                for(int i=0; i!=type.values().length; ++i)
                {
                    buf.append("\n"+type.values()[i]);
                }
                setToolTipText(buf.toString());
            }
            
        }else //all other cols
        {
            String text=(String)t.getValueAt(row,col);
            if(text!=null)
            {
                if(t.getFontMetrics(t.getFont()).stringWidth(text)>
                        t.getColumnModel().getColumn(col).getWidth())
                {
                    setToolTipText(text);
                }
            }
        }
        
        return this;
    }        
}