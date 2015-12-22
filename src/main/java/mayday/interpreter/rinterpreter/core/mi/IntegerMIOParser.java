/*
 * Created on 19.03.2005
 */
package mayday.interpreter.rinterpreter.core.mi;

import java.util.HashMap;

import mayday.core.meta.types.IntegerMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author Matthias Zschunke
 *
 */
public class IntegerMIOParser
extends AbstractNumericMIOParser<IntegerMIO>
{

	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.Rinterpreter.parser.IntegerMIO",
				new String[0],
				MIParserFactory.MC_MITYPEPARSER,
				new HashMap<String, Object>(),
				"Matthias Zschunke",
				"-",
				"MIO Parser for Integer MIOs",
				"Integer MIO"
				);
		pli.getProperties().put(MIParserFactory.KEY_PARSEDTYPE, "PAS.MIO.Integer");
		return pli;
	}
	
}
