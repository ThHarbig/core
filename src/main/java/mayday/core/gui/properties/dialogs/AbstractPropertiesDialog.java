package mayday.core.gui.properties.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;

import mayday.core.gui.MaydayDialog;
import mayday.core.gui.properties.items.AbstractPropertiesItem;

@SuppressWarnings("serial")
public abstract class AbstractPropertiesDialog extends MaydayDialog {

	private boolean composed=false;
	protected GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,0.0,
			GridBagConstraints.PAGE_START,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
	private boolean cancelled=true;
	
	// derived classes need a default constructor!
	public AbstractPropertiesDialog() {
		super();
		this.setMinimumSize(new Dimension(400,700));
		this.setPreferredSize(new Dimension(600,700));
		setLayout(new GridBagLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	protected void addDialogItem(AbstractPropertiesItem ali) {
		if (composed)
			throw new RuntimeException("Can't add elements to a dialog that has already been composed");
		add(ali,gbc);
		gbc.gridy++;
	}
	
	protected void addDialogItem(AbstractPropertiesItem ali, double weighty) {
		if (composed)
			throw new RuntimeException("Can't add elements to a dialog that has already been composed");
		gbc.weighty=weighty;
		gbc.fill=GridBagConstraints.BOTH;
		addDialogItem(ali);
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weighty=0.0;
		ali.setParentDialog(this);
	}
	
	public abstract void assignObject(Object o);
	
	protected void compose() {
		//add buttons
		gbc.gridx=0;
		gbc.gridy++;
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());
		JButton cancelButton =new JButton(new CancelAction()); 
		b.add(cancelButton);
		b.add(Box.createHorizontalStrut(5));
		JButton okButton=new JButton(new OKAction());
		b.add(okButton);
		add(b,gbc);
		getRootPane().setDefaultButton(okButton);
		pack();

		composed=true;
	}
	
	public void setVisible(boolean vis) {
		if (!composed)
			compose();
		super.setVisible(vis);
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	protected abstract void doOKAction();
	
	protected class OKAction extends AbstractAction {
		public OKAction() {
			super("OK");
		}
		public void actionPerformed(ActionEvent arg0) {
			cancelled=false;
			doOKAction();
			dispose();
		}
	}

	
	protected class CancelAction extends AbstractAction {
		public CancelAction() {
			super("Cancel");
		}
		public void actionPerformed(ActionEvent arg0) {
			dispose();
		}
	}
	
}
