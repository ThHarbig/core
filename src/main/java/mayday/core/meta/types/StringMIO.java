package mayday.core.meta.types;

import java.util.HashMap;

import mayday.core.meta.GenericMIO;
import mayday.core.meta.NominalMIO;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.MIRendererDefault;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class StringMIO extends GenericMIO<String> implements NominalMIO<String> {

	public final static String myType = "PAS.MIO.String";
	
	public StringMIO() {}
	
	public StringMIO(String value) {
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
				"Represents character strings as meta informations",
				"String MIO"
				);
	}


	public boolean deSerialize(int serializationType, String serializedForm) {
		// no difference whether xml or text
		Value = serializedForm;
		return true;
	}
	
	public String serialize(int serializationType) {
		return Value;
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new MIRendererDefault();
	}


	public StringMIO clone() {
		return new StringMIO(Value);
	}

	
	public int compareTo(Object arg0) {
		if (arg0 instanceof StringMIO) {
			String v = ((StringMIO)arg0).getValue();
			return Value.compareTo(v);
		}
		return 0;
	}

	public String getType() {
		return myType;
	}
	
}
