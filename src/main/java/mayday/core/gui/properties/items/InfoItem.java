package mayday.core.gui.properties.items;

import java.awt.BorderLayout;

import javax.swing.JLabel;


@SuppressWarnings("serial")
public class InfoItem extends AbstractPropertiesItem {

	private JLabel infoField = new JLabel();
	
	public InfoItem(String title) {
		super(title);
		this.add(infoField, BorderLayout.CENTER);
	}
	
	public InfoItem(String title, String value) {
		this(title);
		setValue(value);
	}
	
	@Override
	public Object getValue() {
		return infoField.getText();
	}

	@Override
	public boolean hasChanged() {
		return false;
	}

	@Override
	public void setValue(Object value) {
		infoField.setText(value.toString());
	}
	
	public void setEditable(boolean editable) {
	}

}
