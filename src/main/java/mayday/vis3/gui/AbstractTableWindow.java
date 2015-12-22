package mayday.vis3.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import mayday.vis3.model.Visualizer;
@SuppressWarnings("serial")
public abstract class AbstractTableWindow<T extends JTable> extends AbstractVisualizerWindow {
	
	protected T tabular;
	

	public AbstractTableWindow(Visualizer pg, String title) {
		super(null, pg, title);
	}
	
	public void initContent() {
		tabular = createTableComponent();
		add(new JScrollPane(tabular));
	}
	
	protected abstract T createTableComponent();
	
	protected abstract void goToProbe(String name);
	
	public String getPreferredTitle() {
		return menuManager.getPreferredTitle();
	}
	
	protected class JumpToSelectionAction extends AbstractAction {

		public JumpToSelectionAction() {
			super("Jump to selection");
		}

		public void actionPerformed(ActionEvent e) {
			goToProbe(visualizer.getViewModel().getSelectedProbes().iterator().next().toString());
		}			
	}

}
