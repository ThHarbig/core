package mayday.vis3.plots.genomeviz.genomeoverview.menues;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.UserGestures;

public class ChromeSelectionMenu {
	 
	protected MenuModel menuModel;
	protected GenomeOverviewModel model;
	protected Controller c;
	
	protected Chrome tempSelectedChrome;
		
	public ChromeSelectionMenu(MenuModel menuModel, GenomeOverviewModel model, Controller c){
		this.menuModel = menuModel;
		this.c = c;
		this.model = model;
		
		tempSelectedChrome = model.getSelectedChrome();
		
		model.setTempSelectedChrome(tempSelectedChrome);
	}

	@SuppressWarnings("serial")
	protected class ChromeSelectionAction extends AbstractAction {
		Chrome myC;
		public ChromeSelectionAction(Chrome c) {
			super(c.getId());
			myC = c;
		}
		public void actionPerformed(ActionEvent e) {
			model.setTempSelectedChrome(myC);
			c.actionPerformed(new ActionEvent(this, 0, UserGestures.SHOW_CHROME));
		}
	}
	
	
	public JMenu speciesAndChromeSelectionMenu(){
					
		ChromosomeSetContainer csc = model.getDAO().getContainer();
		
		JMenu speciesMenu = new JMenu();
		ButtonGroup bg = new ButtonGroup();

		for (Species s : csc.keySet()) {
			JMenu chromosomeMenu = new JMenu(s.getName());
			for (Chromosome c : csc.getChromosomes(s).values()) {
				JRadioButtonMenuItem mi = new JRadioButtonMenuItem(new ChromeSelectionAction((Chrome)c));
				bg.add(mi);
				if (c.equals(model.getSelectedChrome())) {
					mi.setSelected(true);					
				}
				chromosomeMenu.add(mi);
			}
			if (chromosomeMenu.getMenuComponentCount()>0)
				speciesMenu.add(chromosomeMenu);
		}
		
		if (speciesMenu.getMenuComponentCount()==1) { // only one species
			speciesMenu = (JMenu)speciesMenu.getMenuComponent(0);
		}

		speciesMenu.setText("Select Chromosome");
		
		return speciesMenu;
	}

}
