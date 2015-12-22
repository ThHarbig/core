/*
 * Created on 19.03.2005
 */
package mayday.interpreter.rinterpreter.core.mi;

import java.util.HashMap;

import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author Matthias Zschunke
 *
 */
public class DoubleMIOParser extends AbstractNumericMIOParser<DoubleMIO>
{

   
	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.Rinterpreter.parser.DoubleMIO",
				new String[0],
				MIParserFactory.MC_MITYPEPARSER,
				new HashMap<String, Object>(),
				"Matthias Zschunke",
				"-",
				"MIO Parser for Double MIOs",
				"Double MIO"
				);
		pli.getProperties().put(MIParserFactory.KEY_PARSEDTYPE, "PAS.MIO.Double");
		return pli;
	}
}
