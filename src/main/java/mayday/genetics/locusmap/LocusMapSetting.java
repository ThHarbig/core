package mayday.genetics.locusmap;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.genetics.importer.LocusImport;

public class LocusMapSetting extends HierarchicalSetting {

	protected ExtendableObjectSelectionSetting<LocusMap> selection;

	public LocusMapSetting() {
		super("Locus mapping" );
		LocusMapContainer.INSTANCE.updateFromMIOs();
		addSetting( selection = new ExtendableObjectSelectionSetting<LocusMap>(
				"Use existing", null, 0, LocusMapContainer.INSTANCE.list()
		));
		addSetting( new ComponentPlaceHolderSetting("Import from file...", new JButton(new FileImportAction())));
	}
	
	public LocusMapSetting setName(String n) {
		this.name=n;
		return this;
	}
	
	@SuppressWarnings("serial") 
	public class FileImportAction extends AbstractAction {

		public FileImportAction() {
			super("Import from file...");
		}
		
		public void actionPerformed(ActionEvent e) {
			LocusMap lm = LocusImport.run();
			if (lm!=null) {
				selection.addPredefined(lm);
				selection.setObjectValue(lm);
			}			
		}

	}
	
	public SettingComponent getGUIElement() {
		// update locus map list
		selection.updatePredefined(LocusMapContainer.INSTANCE.list());
		return super.getGUIElement();
	}
	
	public LocusMapSetting clone() {
		LocusMapSetting cs = new LocusMapSetting();
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
	
	public LocusMap getLocusMap() {
		return selection.getObjectValue();
	}
	
	public void setLocusMap(LocusMap l) {
		selection.setObjectValue(l);
	}


}
