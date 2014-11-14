package mayday.mushell.queue;

import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

@SuppressWarnings("serial")
public class CommandQueueIndicator extends JLabel implements ListDataListener {

	public CommandQueueIndicator(CommandQueue cq) {
		cq.addListDataListener(this);
	}
	
	protected void updateText(int size) {
		if (size>0)
			setText(size+" commands queued");
		else
			setText("");
	}
	
	public void contentsChanged(ListDataEvent e) {
		updateText(((ListModel)e.getSource()).getSize());
	}

	public void intervalAdded(ListDataEvent e) {
		updateText(((ListModel)e.getSource()).getSize());
	}

	public void intervalRemoved(ListDataEvent e) {
		updateText(((ListModel)e.getSource()).getSize());
	}

}
