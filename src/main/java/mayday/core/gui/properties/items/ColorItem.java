package mayday.core.gui.properties.items;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import mayday.core.gui.components.ColorPreview;

@SuppressWarnings("serial")
public class ColorItem extends AbstractPropertiesItem {

	private Color previous;
	private ColorPreview colorField = new ColorPreview(true);
	
	public ColorItem() {
		super("Color");
		this.add(colorField, BorderLayout.CENTER);
		this.add(new JButton(new EditColorAction()), BorderLayout.EAST);
	}
	
	public ColorItem(Color col) {
		this();
		setValue(col);
	}
		
	public Object getValue() {
		return colorField.getColor();
	}
	
	
	@Override
	public boolean hasChanged() {
		return (!previous.equals((Color)getValue()));
	}

	@Override
	public void setValue(Object value) {
		if (value==null)
			return;
		colorField.setColor((Color)value);
		if (colorField.getGraphics()!=null)
			colorField.update( colorField.getGraphics());
	}
	

	protected class EditColorAction extends AbstractAction {
	  	public EditColorAction() {
	  		super( "Change" );  		
	  	}		  	
	  	public void actionPerformed( ActionEvent event ) {			
	  		Color color = (Color)ColorItem.this.getValue();
	  		color = JColorChooser.showDialog( (Component)event.getSource(),
	  				"Color",
					color );
	  		setValue(color);
	  	}
	  }
	

}
