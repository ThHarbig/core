package mayday.core.meta.types;

import java.util.HashMap;

import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class RelevanceMIO extends DoubleMIO {

	public final static String myType="PAS.MIO.Relevance";
	
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Represents relevance values (between 0.0 and 1.0) as meta informations",
				"Relevance Value MIO"
				);
	}
	
	public RelevanceMIO() {
		super();
	}

	public RelevanceMIO(Double value) {
		super(value);
		checkValue();
	}
	
	public boolean deSerialize(int serializationType, String serializedForm) {
		boolean ret = super.deSerialize(serializationType, serializedForm);
		checkValue();
		return ret;
	}
	
	public void setValue(Double value) {
		super.setValue(value);
		checkValue();
	}
	
	private void checkValue() {
		if (Value==null)
			Value=Double.NaN;
		else if (Value<0.0) 
			Value = 0.0;
		else if (Value>1.0)
			Value = 1.0;		
	}

}
