package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.sequence;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.genetics.sequences.SequenceContainer;
import mayday.genetics.sequences.SequenceImport;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackPlugin;
import mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.AbstractTrackSettings;

public class SequenceTrackSettings extends AbstractTrackSettings{
	
	@SuppressWarnings("serial")
	public SequenceTrackSettings(GenomeOverviewModel Model, AbstractTrackPlugin Tp) {
		super(Model, Tp);		
		root.addSetting( strand = new ObjectSelectionSetting<Object>("Strand",
						null, 2, new Object[] { PLUS,MINUS,BOTH }).setLayoutStyle(
						ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS)) ;
		root.addSetting( new ComponentPlaceHolderSetting(
				"Import new sequences...", 
				new JButton(
						new AbstractAction(getButtonName()) {
							public void actionPerformed(ActionEvent e) {
								SequenceImport.run(getData());
							}
						}
						)
				)
		);
	}

	protected void getInternalIdentifierLabel(){
		identString = "Sequence Data ";
	}

	public SequenceContainer getData() {
		return SequenceContainer.getDefault();
	}

	@Override
	public void setInitialExperiment(int experiment) {
		// don't care
	}
	
	public String getButtonName() {
		return "Add/replace sequence data...";
	}
	
}
