package mayday.core.gui.listdialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.core.gui.MaydayFrame;
import mayday.core.gui.components.ExcellentBoxLayout;

@SuppressWarnings("serial")
public abstract class BaseFrame extends MaydayFrame {

	protected Boolean canceled = null;
	protected JLabel countLabel = new JLabel();

	public BaseFrame(String title) {
		super(title);
		getRootPane().setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	}

	protected void init() {
		if (canceled != null)
			return;
		setLayout(new BorderLayout(5,5));

		JPanel descPanel = new JPanel();
		descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.X_AXIS));
		descPanel.add(getDescription());
		descPanel.add(countLabel);
		add(descPanel, BorderLayout.NORTH);

		LinkedList<Object> listActions = new LinkedList<Object>();
		fillListActions(listActions);		
		JPanel listActionsPanel = new JPanel();
		listActionsPanel.setLayout(new ExcellentBoxLayout(true,1));
		for (Object ae : listActions) {
			if (ae==null)
				listActionsPanel.add(Box.createVerticalStrut(10));
			else if (ae instanceof AbstractAction) {
				listActionsPanel.add(new JButton((AbstractAction)ae));
			} else {
				listActionsPanel.add(new JLabel(ae.toString(), JLabel.CENTER));
			}							
		}
		
		listActionsPanel.add(Box.createVerticalGlue());		
		add(listActionsPanel, BorderLayout.EAST);


		JScrollPane centerSP = new JScrollPane();
		centerSP.setViewportView(getList());
		add(centerSP, BorderLayout.CENTER);
		centerSP.getViewport().setBackground(Color.WHITE);

		LinkedList<Object> dialogActions = new LinkedList<Object>();
		fillDialogActions(dialogActions);		
		JPanel dialogActionsPanel = new JPanel();
		dialogActionsPanel.setLayout(new BoxLayout(dialogActionsPanel, BoxLayout.X_AXIS));
		dialogActionsPanel.add(Box.createHorizontalGlue());
		for (Object ae : dialogActions) {
			if (ae==null)
				dialogActionsPanel.add(Box.createHorizontalStrut(10));
			else if (ae instanceof AbstractAction) {
				dialogActionsPanel.add(new JButton((AbstractAction)ae));
			} else if (ae instanceof Component) {
				dialogActionsPanel.add(((Component)ae));
			} else {
				dialogActionsPanel.add(new JLabel(ae.toString()));
			}							
		}
		
		dialogActionsPanel.setBorder(new TopOnlyBorder());
		
		add(dialogActionsPanel, BorderLayout.SOUTH);

		update();
		pack();
	}

	public void setVisible(boolean vis) {
		init();
		canceled = true;
		super.setVisible(vis);
	}


	public abstract JComponent getDescription();
	public abstract void fillListActions(List<Object> actions);
	public abstract JComponent getList();
	public abstract int getListSize(); 

	public void update() {
		countLabel.setText(""+getListSize());
	}

	public void fillDialogActions(List<Object> actions) {
		actions.add(new CancelAction());
		actions.add(new OKAction());
	}

	public boolean closedWithOK() {
		return !canceled;
	}


	protected class OKAction extends AbstractAction {
		public OKAction() {
			super("OK");
		}

		public void actionPerformed(ActionEvent e) {
			canceled = false;
			dispose();
		}
	}

	protected class CancelAction extends AbstractAction {
		public CancelAction() {
			super("Cancel");
		}
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}



}
