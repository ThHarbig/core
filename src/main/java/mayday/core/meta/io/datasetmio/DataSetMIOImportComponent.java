/*
 * Created on 02.12.2005
 */
package mayday.core.meta.io.datasetmio;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import mayday.core.DataSet;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginInfo;

/**
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 02.12.2005
 *
 */
@SuppressWarnings("serial")
public class DataSetMIOImportComponent 
extends AbstractCSVMIOImportComponent
{

	public DataSetMIOImportComponent(DataSet dataSet, TableModel model) {
		super(dataSet, model);
	}


	public AbstractAction getProcessingAction() {
		return new AbstractAction() {
			public void actionPerformed(ActionEvent e)
			{

				/*
				 * initialize groups
				 */
				final ArrayList<MIGroup> groups = new ArrayList<MIGroup>(table.getColumnCount());
				groups.addAll(Arrays.asList(new MIGroup[table.getColumnCount()]));

				for(Enumeration<TableColumn> cols = table.getColumnModel().getColumns();cols.hasMoreElements();)
				{
					TableColumn0 c0 = (TableColumn0)cols.nextElement();
					if(c0.type!=null && c0.type instanceof PluginInfo)
					{
						groups.set(
								c0.getModelIndex(),                            
								dataSet.getMIManager().newGroup(((PluginInfo)c0.type).getIdentifier(),model.getColumnName(c0.getModelIndex()))
						);
					}
				}

				ReplaceInfo ri;

				for(Enumeration<TableColumn> cols=table.getColumnModel().getColumns();cols.hasMoreElements();)
				{
					TableColumn0 c0 = (TableColumn0)cols.nextElement();

					if(c0.type==null || c0.type==STANDARD_ENTRIES[KEY_KEY] || c0.type==STANDARD_ENTRIES[IGNORE_KEY]) continue;

					//String t = (String)c0.type;

					String v = (String)model.getValueAt(0, c0.getModelIndex());
					if(v==null) continue;

					if((ri=c0.repl)!=null && ri.regexp.length()!=0)
						v = v.replaceAll(ri.regexp, ri.replacement);

					MIGroup g = groups.get(c0.getModelIndex());
					MIType x = g.add(dataSet);
					if (!x.deSerialize(MIType.SERIAL_TEXT, v))
						g.remove(dataSet);
				}
			}
		};
	}
}
