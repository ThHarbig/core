package mayday.core.meta.gui;

import java.awt.Dimension;

import javax.swing.JTextField;


@SuppressWarnings({ "serial", "unchecked" })
public class MIRendererDefault extends AbstractMIRenderer {

	private JTextField textfield = new JTextField(15);
	
	public MIRendererDefault() {
		textfield.setEditable(false);
		textfield.setMaximumSize(new Dimension(Integer.MAX_VALUE, textfield.getPreferredSize().height));
	}

	@Override
	public String getEditorValue() {
		return textfield.getText();
	}

	@Override
	public void setEditable(boolean editable) {
		textfield.setEditable(editable);
	}

	@Override
	public void setEditorValue(String serializedValue) {
		textfield.setText(serializedValue);
		getEditorComponent().repaint();
	}
	
	public JTextField getEditorComponent() {
		return textfield;
	}

}
