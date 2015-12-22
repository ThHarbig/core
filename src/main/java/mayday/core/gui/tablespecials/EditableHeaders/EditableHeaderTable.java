package mayday.core.gui.tablespecials.EditableHeaders;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class EditableHeaderTable extends JTable {

	private EditableHeader eh;	
	
	public EditableHeaderTable(TableHeaderPanel headerPanelPrototype, int rows, int columns) {
		super(rows, columns);
		init(headerPanelPrototype);
	}
	
	public EditableHeaderTable(TableHeaderPanel headerPanelPrototype, TableModel tableModel) {
		super(tableModel);
		init(headerPanelPrototype);
	}
	
	private void init(TableHeaderPanel headerPanelPrototype) {

		TableColumnModel columnModel = getColumnModel();
		eh = new EditableHeader(columnModel);
		setTableHeader(eh);
		
		TableHeaderPanelRenderer renderer = new TableHeaderPanelRenderer(headerPanelPrototype);
		TableHeaderPanelEditor editor = new TableHeaderPanelEditor(headerPanelPrototype.clone());

		for (int i=0; i!=getColumnCount(); ++i) {
		
			EditableHeaderTableColumn col = (EditableHeaderTableColumn)getColumnModel().getColumn(i);
			col.setHeaderValue(headerPanelPrototype.getValue());
			//TableHeaderPanel thp = headerPanelPrototype.clone();			
			col.setHeaderRenderer(renderer);   
			col.setHeaderEditor(editor);			
		}
	}
	
	public void finish() {
		eh.editingStopped(new ChangeEvent(this));
	}
	
	public TableHeaderPanel getHeaderPanel(int column) {
		EditableHeaderTableColumn col = (EditableHeaderTableColumn)getColumnModel().getColumn(column);
		TableHeaderPanelRenderer renderer = (TableHeaderPanelRenderer)col.getHeaderRenderer();
		return renderer.getTableHeaderPanel();
	}
	
}
