/*
 * Created on 26.04.2005
 */
package mayday.interpreter.rinterpreter.core.mi;

import java.util.HashMap;

import mayday.core.meta.types.RelevanceMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author Matthias Zschunke
 *
 */
public class RelevanceMIOParser 
extends AbstractNumericMIOParser<RelevanceMIO> {

@Override
public void init() {
}

@SuppressWarnings("unchecked")
@Override
public PluginInfo register() throws PluginManagerException {
	PluginInfo pli = new PluginInfo(
			(Class)this.getClass(),
			"PAS.Rinterpreter.parser.RelevanceMIO",
			new String[0],
			MIParserFactory.MC_MITYPEPARSER,
			new HashMap<String, Object>(),
			"Matthias Zschunke",
			"-",
			"MIO Parser for Relevance MIOs",
			"Relevance MIO"
			);
	pli.getProperties().put(MIParserFactory.KEY_PARSEDTYPE, "PAS.MIO.Relevance");
	return pli;
}

}
