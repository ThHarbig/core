package mayday.core.gui.properties.items;

import java.awt.BorderLayout;

import javax.swing.JTextField;


@SuppressWarnings("serial")
public class NameItem extends AbstractPropertiesItem {

	private String previous;
	private JTextField nameField = new JTextField();
	
	public NameItem() {
		super("Name");
		this.add(nameField, BorderLayout.CENTER);
	}
	
	public NameItem(String name) {
		this();
		setValue(name);
	}
	
	@Override
	public Object getValue() {
		return nameField.getText();
	}

	@Override
	public boolean hasChanged() {
		return (!previous.equals((String)getValue()));
	}

	@Override
	public void setValue(Object value) {
		nameField.setText(value.toString());
	}
	
	public void setEditable(boolean editable) {
		nameField.setEditable(editable);
	}

}
