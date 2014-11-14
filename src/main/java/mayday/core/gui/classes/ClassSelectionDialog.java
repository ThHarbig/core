package mayday.core.gui.classes;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class ClassSelectionDialog extends MaydayDialog 
{
	private ClassSelectionPanel panel;
	private boolean cancelled; 
	/**
	 * @param model
	 */
	public ClassSelectionDialog(ClassSelectionModel model, DataSet ds)
	{
		this(model, ds, null, null);
	}
	
	public ClassSelectionDialog(ClassSelectionModel model, DataSet ds, Integer minClasses, Integer maxClasses) {
	
		this.setModal(true);
		this.setTitle("Class Label Inspector");
		cancelled=false;
    	panel= new ClassSelectionPanel(model, ds, minClasses, maxClasses);
    	panel.setObjectsFixed(true);
    	Box totalBox=Box.createVerticalBox();
    	totalBox.add(panel);
    	Box buttonBox=Box.createHorizontalBox();
    	buttonBox.add(Box.createHorizontalGlue());
    	buttonBox.add(new JButton(new CancelAction()));
    	buttonBox.add(new JButton(new OKAction()));
    	totalBox.add(buttonBox);
    	this.add(totalBox);
        pack();
	}
	
	public ClassSelectionDialog(ClassSelectionModel model) {
		this(model, null);
	}

	public void setVisible(boolean vis) {
        if (vis)
        	cancelled = true;
        super.setVisible(vis);
	}
	
	/**
	 * @return The current class selection model.
	 */
	public ClassSelectionModel getModel() 
	{
		ClassSelectionModel model=panel.getModel();
		model.tidyUp();
		return model;
	}

	/**
	 * @param model
	 */
	public void setModel(ClassSelectionModel model) 
	{
		panel.setModel(model);
	}
	
	protected class OKAction extends AbstractAction {

		public OKAction() {
			super( "Ok" );
		}

		public void actionPerformed( ActionEvent event ) {
			cancelled = false;
			dispose();
		}
	}
	
	protected class CancelAction extends AbstractAction {

		public CancelAction() {
			super( "Cancel" );
		}

		public void actionPerformed( ActionEvent event ) {			
			dispose();
		}
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

}
