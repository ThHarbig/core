package mayday.core.gui.properties.items;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;

import mayday.core.meta.MIType;
import mayday.core.meta.WrappedMIO;
import mayday.core.meta.gui.AbstractMIRenderer;

@SuppressWarnings("serial")
public class MIORendererItem extends AbstractPropertiesItem {

	private AbstractMIRenderer<MIType> mioField;
	private WrappedMIO wm;
	private EditAction editAction = new EditAction();
	private ApplyAction applyAction = new ApplyAction();
	private JButton editButton = new JButton(editAction);
	private JButton applyButton = new JButton(applyAction);
	
	@SuppressWarnings("unchecked")
	public MIORendererItem(WrappedMIO wrappedMIO) {
		super("Value");		
		mioField = wrappedMIO.getMio().getGUIElement();
		setValue(wrappedMIO);
		this.add(mioField.getEditorComponent(), BorderLayout.CENTER);
		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());
		b.add(applyButton);
		b.add(Box.createHorizontalStrut(5));
		b.add(editButton);
		this.add(b, BorderLayout.SOUTH);
	}


	@Override
	public Object getValue() {
		return wm;
	}

	@Override
	public boolean hasChanged() {
		throw new RuntimeException("MIO Change detection not implemented");
	}

	@Override
	public void setValue(Object value) {
		wm = (WrappedMIO)value;
		mioField.connectToMIO(wm.getMio(), wm.getMioExtendable(), wm.getGroup());
	}
	
	public void setEditable(boolean editable) {
		mioField.setEditable(editable);
	}

	public boolean isEditing() {
		return applyAction.isEnabled();
	}
	
	private class EditAction extends AbstractAction {
		public EditAction() {
			super("Edit");
		}
		public void actionPerformed(ActionEvent arg0) {
			mioField.setEditable(true);				
			editAction.setEnabled(false);
			applyAction.setEnabled(true);
		}
	}
	
	public void applyChanges() {
		applyAction.actionPerformed(null);
	}
	
	private class ApplyAction extends AbstractAction {
		public ApplyAction() {
			super("Apply Changes");
			setEnabled(false);
		}
		public void actionPerformed(ActionEvent arg0) {
			mioField.applyChanges();
			mioField.setEditable(false);				
			applyAction.setEnabled(false);
			editAction.setEnabled(true);
		}
	}
}
