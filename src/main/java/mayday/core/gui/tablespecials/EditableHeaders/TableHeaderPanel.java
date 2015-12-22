package mayday.core.gui.tablespecials.EditableHeaders;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class TableHeaderPanel extends JPanel {

	public abstract Object getValue();

	public abstract void setValue(Object value);

	public abstract TableHeaderPanel clone();
	
	public abstract void setTitle(String Title);
	
	public abstract void setColumnIndex(int column);

}
