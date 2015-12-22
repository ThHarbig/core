/*
 * Created on 09.06.2005
 */
package mayday.genetics;

import java.util.HashMap;

import mayday.core.meta.ComparableMIO;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.MIRendererDefault;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.GeneticCoordinate;


/**
 * An MIO representing a genetic coordinate, that
 * is a 4-tuple 
 * (<i>
 *   Chomosome ,
 *   Direction (one out of {<code>+</code>,<code>-</code>,<code>#</code>,<code>'_'</code>}) ,
 *   Start-Position  ,
 *   End-Position 
 * </i>)
 * 
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 09.06.2005
 *
 */
public class LocusMIO extends GenericMIO<Locus> implements ComparableMIO
{

	 
	
	public final static String myType = "PAS.MIO.Locus";

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Matthias Zschunke",
				"zschunke@informatik.uni-tuebingen.de",
				"Represents locus information as meta information",
				"Locus MIO"
				);
	}

	public LocusMIO() {
		
	}
	
	public LocusMIO(Locus value) {
		Value=value;
	}
	
	@Override
	public MIType clone() {
		return new LocusMIO(Value);
	}

	@Override
	public void init() {
	}

	public boolean deSerialize(int serializationType, String serializedForm) {
		// XML is handled just like text
		// find out whether this is a "normal" GeneticCoordinate or a "complex" genetic coordinate
		AbstractGeneticCoordinate agc = null;
		
		try {
			agc= new GeneticCoordinate( serializedForm, ChromosomeSetContainer.getDefault() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (agc==null)
			return false;
		
		Value = new Locus(agc);
		return true;
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		return new MIRendererDefault();
	}

	public String getType() {
		return myType;
	}

	public String serialize(int serializationType) {
		// XML is handled as a text string
		return Value.getCoordinate().serialize();
	}

	@Override
	public int compareTo(Object o) {
		return Value.compareTo(((LocusMIO)o).getValue());
	}


}
