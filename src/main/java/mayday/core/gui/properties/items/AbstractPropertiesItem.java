package mayday.core.gui.properties.items;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class AbstractPropertiesItem extends JPanel {

	protected JDialog parent;
	
	public abstract Object getValue();
	
	public abstract void setValue(Object value);
	
	public abstract boolean hasChanged();
	
	public AbstractPropertiesItem(String caption) {
		super(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(caption));
	}
	
	public void setParentDialog(JDialog pd) {
		parent = pd;
	}

}
