package mayday.interpreter.rinterpreter.gui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import mayday.interpreter.rinterpreter.core.RSource;
import mayday.interpreter.rinterpreter.core.RSourceParam;

/**
 * The table model for R function parameter tables as
 * applied in the <tt>FunctionChooserDialog</tt> and in the
 * <tt>FunctionParamChooserDialog</tt>.
 * 
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class ParamTableModel extends AbstractTableModel 
{
	private String[] columnNames;
	private Object[][] data;
	private RSource source;
    private ArrayList<RSourceParam> params;
		
	/**
	 * Constructor.
	 * 
	 * @param src
	 * @param columnNames, the titles of the specific columns
	 */
	public ParamTableModel(RSource src, String[] columnNames)
	{
		this.source=src;
		this.columnNames=columnNames;
		params = this.source.getParameters();
        data=new Object[params.size()][columnNames.length];
		for(int i=0; i!=params.size(); ++i)
		{
			RSourceParam par=((RSourceParam)params.get(i));
			for(int j=0; j!=columnNames.length;++j)
			{
				if(j==0) 
                    data[i][0]=par.getName();
                else if(j==1) 
                    data[i][1]=par.getDefault();
                else if(j==2)
                    data[i][2]=par.getType();
                else if(j==3) 
                    data[i][3]=par.getDescription();
			}
		}
	}
		
	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	public boolean isCellEditable(int row, int col) {
		return col>=1 && row > 0; //the first parameter cannot be edited
	}

	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}
    
    public RSourceParam getParameter(int i)
    {
        return (RSourceParam)this.params.get(i);
    }
}
