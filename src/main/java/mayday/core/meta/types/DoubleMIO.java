package mayday.core.meta.types;

import java.util.HashMap;

import mayday.core.meta.NumericMIO;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.MIRendererDefault;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class DoubleMIO extends NumericMIO<Double> {

	public final static String myType = "PAS.MIO.Double";
	
	public DoubleMIO() {}
	
	public DoubleMIO(Double value) {
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
				"Represents double values as meta informations",
				"Double Value MIO"
				);
	}


	public boolean deSerialize(int serializationType, String serializedForm) {
		// no difference whether xml or text
		try {
			Value = Double.parseDouble(serializedForm);
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new MIRendererDefault();
	}


	public DoubleMIO clone() {
		return new DoubleMIO(Value);
	}

	
	public int compareTo(Object arg0) {
		if (arg0 instanceof DoubleMIO) {
			Double v = ((DoubleMIO)arg0).getValue();
			return Value.compareTo(v);
		}
		return 0;
	}

	public String getType() {
		return myType;
	}

}
