package mayday.genetics.importer;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.LongSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.basic.Strand;

public class DefaultLocusSetting extends HierarchicalSetting {

	protected StringSetting species;
	protected ObjectSelectionSetting<Strand> strand;
	protected StringSetting chrome;
	protected LongSetting length;
	protected boolean showSpecies=true, showChrome=true, showStrand=true, showLength=true;
	
	public DefaultLocusSetting() {
		super("Locus Defaults");
		addSetting(species = new StringSetting("Species","The name of the default species", ""));
		addSetting(chrome = new StringSetting("Chromosome","The identifier of the default chromosome", "X"));
		addSetting(strand = new ObjectSelectionSetting<Strand>("Strand","The default strand for loci", 0,Strand.values()));
		addSetting(length = new LongSetting("Length","The default length of loci", 25));
	}
	
	public void hideElements(boolean _species, boolean _chrome, boolean _strand, boolean _length) {
		if (_species)
			children.remove(species);
		if (_chrome)
			children.remove(chrome);
		if (_strand)
			children.remove(strand);
		if (_length)
			children.remove(length);
	}
	
	public String getSpecies() {
		return species.getStringValue();
	}
	
	public Strand getStrand() {
		return strand.getObjectValue();
	}
	
	public String getChromosome() {
		return chrome.getStringValue();
	}
	
	public long getLength() {
		return length.getLongValue();
	}
	
	
	public DefaultLocusSetting clone() {
		DefaultLocusSetting cs = new DefaultLocusSetting();
		cs.hideElements(showSpecies, showChrome, showStrand, showLength);
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
	
}
