package mayday.core.gui.properties.items;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;

import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.meta.WrappedMIO;

@SuppressWarnings("serial")
public class EnclosingGroupItem extends AbstractPropertiesItem {

	public EnclosingGroupItem(final WrappedMIO wm) {
		super("Enclosing MIO Group for this instance");
		this.add(new JLabel(wm.getGroup().getPath()+"/"+wm.getGroup().getName()), BorderLayout.CENTER);
		this.add(new JButton(new AbstractAction("Properties...") {
		
			public void actionPerformed(ActionEvent arg0) {
				PropertiesDialogFactory.createDialog(wm.getGroup()).setVisible(true);
			}
			
		}), BorderLayout.EAST);
	}
	
	public Object getValue() {
		return null;
	}
	
	
	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void setValue(Object value) {
		
	}
	

}
