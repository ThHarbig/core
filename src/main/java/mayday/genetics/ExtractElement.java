/*
 * Created on Jan 24, 2005
 *
 */
package mayday.genetics;

import java.util.HashMap;
import java.util.Map.Entry;

import mayday.core.gui.PreferencePane;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.meta.types.IntegerMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;

public class ExtractElement
extends AbstractMetaInfoPlugin
{

	public Setting getSetting() {
		return null;
	}
	
	public PreferencePane getPreferencePane() {
		return null;
	}

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_BY_REPEAT);
		registerAcceptableClass(LocusMIO.class);
	}

	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		MIGroup in = selection.get(0);
		MIGroup Species = miManager.newGroup("PAS.MIO.String", "Species", in);
		MIGroup Chrome = miManager.newGroup("PAS.MIO.String", "Chromosome", in);
		MIGroup From = miManager.newGroup("PAS.MIO.Integer", "From", in);
		MIGroup To = miManager.newGroup("PAS.MIO.Integer", "To", in);
		MIGroup Length = miManager.newGroup("PAS.MIO.Integer", "Length", in);
		MIGroup Strand = miManager.newGroup("PAS.MIO.String", "Strand", in);
		StringMIO sPlus = new StringMIO("+");
		StringMIO sMinus = new StringMIO("-");
		StringMIO sBoth = new StringMIO("#");
		
		HashMap<String, StringMIO> chromes = new HashMap<String, StringMIO>();
		HashMap<String, StringMIO> species = new HashMap<String, StringMIO>();
		
		for (Entry<Object, MIType> e : in.getMIOs()) {
			Object k = e.getKey();
			AbstractGeneticCoordinate gc = ((LocusMIO)e.getValue()).getValue().getCoordinate();
			
			String specname = gc.getChromosome().getSpecies().getName();
			StringMIO speMIO = species.get(specname);
			if (speMIO==null) {
				species.put(specname, speMIO=new StringMIO(specname));
			}
			Species.add(k,speMIO);
			
			String chrname = gc.getChromosome().getId();
			StringMIO chrMIO = chromes.get(chrname);
			if (chrMIO==null) {
				chromes.put(chrname, chrMIO=new StringMIO(chrname));
			}
			Chrome.add(k,chrMIO);

			((IntegerMIO)From.add(k)).setValue((int)gc.getFrom());
			((IntegerMIO)To.add(k)).setValue((int)gc.getTo());
			((IntegerMIO)Length.add(k)).setValue((int)gc.length());
			
			switch (gc.getStrand()) {
			case PLUS: Strand.add(k, sPlus); break;
			case MINUS: Strand.add(k, sMinus); break;
			case BOTH: Strand.add(k, sBoth); break;
			}
			
		}
		
	}
	
	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.extractlocus",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Extracts individual value elements from a locus mio group",
		"Extract locus elements");
		pli.addCategory("Locus Data");
		return pli;
	}

}
