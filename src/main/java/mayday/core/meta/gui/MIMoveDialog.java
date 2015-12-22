package mayday.core.meta.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mayday.core.gui.MaydayDialog;
import mayday.core.meta.MIManager;

@SuppressWarnings("serial")
public class MIMoveDialog extends MaydayDialog {

	protected JTextField targetPath = new JTextField();
	private boolean canceled=true;
	private MIManager mimanager;
	
	public MIMoveDialog(MIManager miManager) {
		super();
		mimanager=miManager;
		setTitle("Move MI Groups");
		init();
	}
	
	protected void init() {
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(new JLabel("Specify the new path for the selected MI Groups"), BorderLayout.NORTH);
		Box selBox = Box.createHorizontalBox();
		selBox.add(targetPath);
		selBox.add(Box.createHorizontalStrut(5));
		selBox.add(new JButton(new AbstractAction("Browse") {

			public void actionPerformed(ActionEvent e) {
				MIGroupSelectionDialog mgsd = new MIGroupSelectionDialog(mimanager);
				mgsd.setDialogDescription("Select the target of the move operation");
				mgsd.setModal(true);
				mgsd.setVisible(true);
				if (!mgsd.isCanceled() && mgsd.getSelection().size()>0) {
					targetPath.setText(mimanager.getTreeRoot().getPathFor(mgsd.getSelection().get(0)));
				}
			}
			
		}));
		selBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		content.add(selBox, BorderLayout.CENTER);
		content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());		
		buttonBox.add(new JButton(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}			
		}));
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(new JButton(new AbstractAction("OK") {
			public void actionPerformed(ActionEvent e) {
				canceled=false;
				dispose();
			}			
		}));		
		buttonBox.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		content.add(buttonBox, BorderLayout.SOUTH);
		
		add(content);
		pack();
		setSize(getPreferredSize());
		setResizable(false);
	
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public String getTarget() {
		return targetPath.getText();
	}
	
}
