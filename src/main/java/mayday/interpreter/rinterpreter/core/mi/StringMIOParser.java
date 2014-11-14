/*
 * Created on 19.03.2005
 */
package mayday.interpreter.rinterpreter.core.mi;

import java.util.HashMap;

import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;


/**
 * Dummy parser for StringMIOs.
 * 
 * @author Matthias Zschunke
 *
 */
public class StringMIOParser extends MITypeParser<StringMIO>
{

    /* (non-Javadoc)
     * @see mayday.interpreter.rinterpreter.core.mi.MIOTypeParser#parseR()
     */
    public String parseR()
    {
        return "function(s){as.character(s)}";
    }

    /* (non-Javadoc)
     * @see mayday.interpreter.rinterpreter.core.mi.MIOTypeParser#outputR()
     */
    public String outputR()
    {
        return "function(v){as.character(v)}";
    }

	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.Rinterpreter.parser.StringMIO",
				new String[0],
				MIParserFactory.MC_MITYPEPARSER,
				new HashMap<String, Object>(),
				"Matthias Zschunke",
				"-",
				"MIO Parser for String MIOs",
				"String MIO"
				);
		pli.getProperties().put(MIParserFactory.KEY_PARSEDTYPE, "PAS.MIO.String");
		return pli;
	}
}
