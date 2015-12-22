package mayday.core.settings;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public abstract class DialogMenuItem<T> extends JMenuItem {	
	
	public DialogMenuItem(final T s, final Window parent) {
		super();
		AbstractAction aa = new AbstractAction(getName(s)+"...") {
			public void actionPerformed(ActionEvent e) {
				final Window sdlg = createDialog(s);
				sdlg.setVisible(true);			
				if (parent!=null)
					parent.addWindowListener(new WindowAdapter() {
						public void windowClosing( WindowEvent evt ) {
							sdlg.dispose();
						}
					});
			}
		};
		aa.putValue(TOOL_TIP_TEXT_KEY, getTooltip(s));
		setAction(aa);
	}
	
	public abstract String getName(T o);
	public abstract Window createDialog(T o);
	public abstract String getTooltip(T o);
	
		
}
