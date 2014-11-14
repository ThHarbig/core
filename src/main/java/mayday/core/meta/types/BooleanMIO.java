package mayday.core.meta.types;

import java.util.HashMap;

import javax.swing.JCheckBox;

import mayday.core.meta.GenericMIO;
import mayday.core.meta.NominalMIO;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class BooleanMIO extends GenericMIO<Boolean> implements NominalMIO<Boolean> {

	public final static String myType = "PAS.MIO.Boolean";
	
	public BooleanMIO() {}
	
	public BooleanMIO(Boolean value) {
		Value=value;
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Represents boolean values as meta informations",
				"Boolean Value MIO"
				);
		
	}


	public boolean deSerialize(int serializationType, String serializedForm) {
		// no difference whether xml or text
		try {
			Value = Boolean.parseBoolean(serializedForm);
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new BooleanMIRenderer();
	}


	public BooleanMIO clone() {
		return new BooleanMIO(Value);
	}

	
	public int compareTo(Object arg0) {
		if (arg0 instanceof BooleanMIO) {
			Boolean v = ((BooleanMIO)arg0).getValue();
			return Value.compareTo(v);
		}
		return 0;
	}

	public String getType() {
		return myType;
	}

	public String serialize(int serializationType) {
		return Value!=null?Value.toString():null;
	}
	
	@SuppressWarnings("serial")
	public static class BooleanMIRenderer extends AbstractMIRenderer<BooleanMIO> {
		private JCheckBox checkbox = new JCheckBox();
		
		public BooleanMIRenderer() {
			checkbox.setEnabled(false);
		}

		public String getEditorValue() {
			return Boolean.toString(checkbox.isSelected());
		}

		public void setEditable(boolean editable) {
			checkbox.setEnabled(editable);
		}

		public void setEditorValue(String serializedValue) {
			checkbox.setSelected(Boolean.parseBoolean(serializedValue));
			getEditorComponent().repaint();
		}
		
		public JCheckBox getEditorComponent() {
			return checkbox;
		}
		
		public void setLabel(String label) {
			checkbox.setText(label);
		}
	}

}
