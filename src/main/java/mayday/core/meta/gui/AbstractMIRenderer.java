package mayday.core.meta.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupEvent;
import mayday.core.meta.MIType;
import mayday.core.meta.WrappedMIO;

@SuppressWarnings("serial")
public abstract class AbstractMIRenderer<T extends MIType>  extends DefaultTableCellRenderer
implements TableCellRenderer/*, CellEditor*/ {

	private T displayedMIO;
	private MIGroup group;
	private Object object;
	

	private JLabel lbl = new JLabel();
	
	public Component getTableCellRendererComponent(
			JTable table, 
			Object value, 
			boolean selected,
			boolean focused,
			int row,
			int col) {
		MIType theMIO=null;
		if (value instanceof WrappedMIO)
			theMIO = ((WrappedMIO)value).getMio();
		else if (value instanceof MIType)
			theMIO = (MIType)value;
		if (theMIO==null)
			return super.getTableCellRendererComponent(table, value, selected, focused, row, col);
		else {
			lbl.setText(theMIO.toString());
			lbl.setOpaque(true);
			lbl.setBackground(UIManager.getColor((selected || focused) ? "textHighlight" : "control"));
			return lbl;
		}
	}
		
	public abstract Component getEditorComponent();
	
	public void connectToMIO(T mio, Object mioExtendable, MIGroup group) {
		displayedMIO = mio;
		object = mioExtendable;
		this.group=group;
		setEditorValue(displayedMIO.serialize(MIType.SERIAL_TEXT));
		getEditorComponent().repaint();
	}
	
	public abstract void setEditorValue(String serializedValue);
	public abstract String getEditorValue(); 	
	public abstract void setEditable(boolean editable);	
	
	@SuppressWarnings("unchecked")
	public void applyChanges() {
		if (displayedMIO!=null) { 
			if (group==null || object==null) {
				// no cloning to do
			} else {
				if (JOptionPane.showConfirmDialog(null, 
						"Should changes apply only to this specific MIO instance?\n" +
						"The alternative is to change all copies of this MIO simultaneously.",
		    			"Clone MIO before editing?", 
		    			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
		    			==JOptionPane.YES_OPTION) {
					displayedMIO = (T)displayedMIO.clone();
					group.add(object, displayedMIO);
				}				
			}
			displayedMIO.deSerialize(MIType.SERIAL_TEXT, getEditorValue());
			
			// create a fake MIGroup to notify listeners of the change, if possible
			if (group!=null || object!=null) {
				group.fireMIGroupChanged(MIGroupEvent.MIO_REPLACED, object);
			}

		}			
	}

	
}
