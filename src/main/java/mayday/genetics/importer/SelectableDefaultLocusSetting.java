package mayday.genetics.importer;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.LongSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.basic.Strand;

public class SelectableDefaultLocusSetting extends HierarchicalSetting {

	protected StringSetting species;
	protected ObjectSelectionSetting<Strand> strand;
	protected StringSetting chrome;
	protected LongSetting length;
	protected BooleanHierarchicalSetting bsp,bst,bc,bl;
	protected boolean showSpecies=true, showChrome=true, showStrand=true, showLength=true;
	
	public SelectableDefaultLocusSetting(String name) {
		super(name);
		addSetting(
				bsp = new BooleanHierarchicalSetting("Override default species", null, false)
					.addSetting( species = new StringSetting("Species","The name of the default species", "") )
				);
		addSetting(
				bc = new BooleanHierarchicalSetting("Override default chromosome", null, false)
				 	.addSetting( chrome = new StringSetting("Chromosome","The identifier of the default chromosome", "X") )
				);
		addSetting(
				bst = new BooleanHierarchicalSetting("Override default strand", null, false)
					.addSetting( strand = new ObjectSelectionSetting<Strand>("Strand","The default strand for loci", 0,Strand.values())) 
				);
		addSetting(
				bl = new BooleanHierarchicalSetting("Override default length", null, false)
					.addSetting( length = new LongSetting("Length","The default length of loci", 25) )
				);		
	}
	
	public void hideElements(boolean _species, boolean _chrome, boolean _strand, boolean _length) {
		this.showSpecies=_species;
		this.showChrome=_chrome;
		this.showStrand=_strand;
		this.showLength=_length;
		if (_species)
			children.remove(bsp);
		if (_chrome)
			children.remove(bc);
		if (_strand)
			children.remove(bst);
		if (_length)
			children.remove(bl);
	}
	
	public void setOverride(boolean species, boolean chrome, boolean strand, boolean length) {
		bsp.setBooleanValue(species);
		bc.setBooleanValue(chrome);
		bst.setBooleanValue(strand);
		bl.setBooleanValue(length);		
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
	
	public boolean overrideSpecies() {
		return bsp.getBooleanValue();
	}
	
	public boolean overrideStrand() {
		return bst.getBooleanValue();
	}

	public boolean overrideChromosome() {
		return bc.getBooleanValue();
	}

	public boolean overrideLength() {
		return bl.getBooleanValue();
	}

	
	public SelectableDefaultLocusSetting clone() {
		SelectableDefaultLocusSetting cs = new SelectableDefaultLocusSetting(getName());
		cs.hideElements(showSpecies, showChrome, showStrand, showLength);
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
}
