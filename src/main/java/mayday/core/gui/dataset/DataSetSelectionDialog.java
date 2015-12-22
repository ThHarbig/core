package mayday.core.gui.dataset;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.core.DataSet;
import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class DataSetSelectionDialog extends MaydayDialog implements WindowListener{
		
	//private DataSetManager plmanager;
	private JPanel additionalDialogElements = new JPanel();
	private boolean canceled=true; //default true to handle windowClosing events correctly
	private JEditorPane dialogDescription = new JEditorPane();
	private DataSetSelectionPanel selPanel;
	
	private ArrayList<AbstractAction> additionalOKActions = new ArrayList<AbstractAction>();
	
	public DataSetSelectionDialog() {
		setModal(true);
		selPanel = new DataSetSelectionPanel();
		init();
	}
	
	public void setFilter(DataSetSelectionFilter plf) {
		selPanel.setFilter(plf);
	}
	
	public JPanel getAdditionalDialogElementsPanel() {
		return additionalDialogElements;
	}
	
	public void setDialogDescription(String description) {
		dialogDescription.setText(description);
	}
	
	private void init() {
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		
		dialogDescription.setOpaque(false);
		dialogDescription.setEditable(false);
		dialogDescription.setContentType("text/html");		
		dialogDescription.setText("Please select one or more datasets.");
		
		JScrollPane additionalElementsScrollPane = new JScrollPane(additionalDialogElements);
		additionalDialogElements.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		additionalElementsScrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(selPanel, BorderLayout.CENTER);
		centerPanel.add(additionalElementsScrollPane, BorderLayout.SOUTH);
	
		// Buttons at the bottom
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(new JButton(new CancelAction()));
		buttonBox.add(Box .createHorizontalStrut(5));
		JButton okButton = new JButton(new OKAction());
		buttonBox.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		content.add(dialogDescription, BorderLayout.NORTH);
		content.add(centerPanel, BorderLayout.CENTER);
		content.add(buttonBox, BorderLayout.SOUTH);
			
		this.add(content);
		this.addWindowListener(this);
		
		this.setTitle("DataSet Selection");
		setMinimumSize(new Dimension(600,500));
		pack();
	}
	
	public void setVisible(boolean vis) {
		if (selPanel.getSelectableCount()>0 || !vis)  //hide always, show only when helpful
			super.setVisible(vis);
	}
	
	private void doAdditionalOKActions() {
		for (AbstractAction act : additionalOKActions)
			act.actionPerformed(null);
	}
	
	public void addAdditionalOKAction(AbstractAction act) {
		additionalOKActions.add(act);
	}
	
	protected class OKAction extends AbstractAction {

		public OKAction() {
			super("OK");
		}
		
		public void actionPerformed(ActionEvent arg0) {
			canceled=false;			
			doAdditionalOKActions();
			dispose();
		}		
	}
	
	protected class CancelAction extends AbstractAction {

		public CancelAction() {
			super("Cancel");
		}
		
		public void actionPerformed(ActionEvent arg0) {
			canceled=true;
			dispose();
		}		
	}

	public boolean isCanceled() {
		return canceled;
	}
	
	public List<DataSet> getSelection() {
		if (canceled)
			return new LinkedList<DataSet>();
		else 
			return selPanel.getSelection();
	}

	public void windowActivated(WindowEvent arg0) {		
	}

	public void windowClosed(WindowEvent arg0) {
		selPanel.removeNotify();
	}

	public void windowClosing(WindowEvent arg0) {
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}
	
}
