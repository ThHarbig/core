package mayday.core.gui.properties.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.gui.properties.PropertiesDialogFactory;


@SuppressWarnings("serial")
public class MultiplePropertiesDialog extends AbstractPropertiesDialog {

	protected Object[] dialogObjects;
	protected int curIndex;
	protected JButton NextButton;
	protected JButton PrevButton;
	protected JLabel PositionLabel;
	protected AbstractPropertiesDialog currentContent;
	protected JLabel placeholder;

	public MultiplePropertiesDialog(Object[] objects) {
		setLayout(new BorderLayout());
		dialogObjects=objects;
		NextButton = new JButton(new AbstractAction(">>") {
			public void actionPerformed(ActionEvent e) {
				init(curIndex+1);								
			}			
		});
		PrevButton = new JButton(new AbstractAction("<<") {
			public void actionPerformed(ActionEvent e) {
				init(curIndex-1);								
			}			
		});		
		JPanel topButtonPanel = new JPanel();
		topButtonPanel.setLayout(new BoxLayout(topButtonPanel, BoxLayout.LINE_AXIS));
		PositionLabel = new JLabel();
		topButtonPanel.add(PrevButton);
		topButtonPanel.add(Box.createHorizontalGlue());
		topButtonPanel.add(PositionLabel);
		topButtonPanel.add(Box.createHorizontalGlue());
		topButtonPanel.add(NextButton);
		add(topButtonPanel, BorderLayout.NORTH);
		
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.LINE_AXIS));
		JButton SaveButton = new JButton(new AbstractAction("Apply changes") {
			public void actionPerformed(ActionEvent e) {
				if (currentContent!=null)
					currentContent.doOKAction();				
			}
		}); 
		bottomButtonPanel.add(Box.createHorizontalGlue());
		bottomButtonPanel.add(SaveButton);
		add(bottomButtonPanel, BorderLayout.SOUTH);
		
		init(0);
	}

	public void init(int index) {
		curIndex=index;
		NextButton.setEnabled((curIndex+1)<dialogObjects.length);
		PrevButton.setEnabled((curIndex)>0);
		PositionLabel.setText((curIndex+1)+"/"+dialogObjects.length);
		// make room
		if (currentContent!=null)
			getContentPane().remove(currentContent.getContentPane());
		else if (placeholder!=null)
			getContentPane().remove(placeholder);
		// fill room
		Component add;
		try {
			AbstractPropertiesDialog innerDialog = PropertiesDialogFactory.createDialog(dialogObjects[curIndex]);
			add = innerDialog.getContentPane();
			currentContent = innerDialog;
			setTitle(PositionLabel.getText()+": "+innerDialog.getTitle());
		} catch (Exception e) {
			add = placeholder = new JLabel(e.getMessage());
			currentContent = null;
			setTitle(PositionLabel.getText()+": Object of wrong kind");
		}
		add(add, BorderLayout.CENTER);
	}
	
	public void setVisible(boolean vis) {
		if (dialogObjects.length==0)
			return; 
		super.setVisible(vis);
	}
	
	public void assignObject(Object o) {
	}

	protected void doOKAction() {
	}	
	
	protected void compose() {
	}
	
}
