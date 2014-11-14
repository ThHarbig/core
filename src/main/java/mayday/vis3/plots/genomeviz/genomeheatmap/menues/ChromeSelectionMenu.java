package mayday.vis3.plots.genomeviz.genomeheatmap.menues;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.genetics.basic.ChromosomeSet;
import mayday.genetics.basic.Species;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller_cb;
import mayday.vis3.plots.genomeviz.genomeheatmap.usergestures.UserGestures;
import mayday.vis3.plots.genomeviz.genomeorganisation.Chrome;


public class ChromeSelectionMenu {
 
	protected MenuManager menuManager;
	protected MasterManager master;
	protected Controller c;
	protected Controller_cb cc;
	
	protected DefaultListModel speciesModel = new DefaultListModel();
	protected DefaultListModel chromeModel = new DefaultListModel();

	protected ListSelectionModel speciesSelectionModel;
	protected ListSelectionModel chromeSelectionModel;
	
	protected Species tempSelectedSpecies;
	protected Chrome tempSelectedChrome;
	
	protected JPanel wholePanel;
	protected JPanel selectionPanel;
	
	protected JList chromeList;
	protected JList speciesList;
	
	protected int chromeIndex;
	protected String chromeName;
	
	public ChromeSelectionMenu(MenuManager menuManager, MasterManager master, Controller c){
		this.menuManager = menuManager;
		this.master = master;
		this.c = c;
		this.cc = c.getC_cb();
		
		tempSelectedSpecies = menuManager.getSelectedSpecies();
		tempSelectedChrome = menuManager.getSelectedChrome();
		
		master.setTempSelectedChrome(tempSelectedChrome);
		
	}

	public JScrollPane getSpeciesScrollPane() {
		// ------------------------
		// Species Selection
		JList speciesList = new JList();
		speciesList.setVisibleRowCount(2);		
		speciesList.setSelectedIndex(0);	
		speciesList.setForeground(Color.BLACK);
		speciesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		speciesList.setModel(speciesModel);
		speciesList.setSelectionModel(speciesSelectionModel);
		//speciesList.setSelectedIndex(speciesModel.indexOf(menuManager.getSelectedSpecies()));
		//speciesSelectionModel.addListSelectionListener(specManager);
		
		JScrollPane listScrollPane = new JScrollPane(speciesList);	
		// End of Species Selection
		// ------------------------
		
		return listScrollPane;
		
	}
	
	public JScrollPane getChromosomeScrollPane() {
		// ------------------------
		// Species Selection
		JList chromeList = new JList();
		chromeList.setVisibleRowCount(2);		
		chromeList.setSelectedIndex(0);	
		chromeList.setForeground(Color.BLACK);
		chromeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// set data for the JList
//		ChromosomeSetContainer container = menuManager.getChromeSetContainer();

		chromeList.setModel(chromeModel);
		chromeList.setSelectionModel(chromeSelectionModel);

		JScrollPane listScrollPane = new JScrollPane(chromeList);	
		// End of Species Selection
		// ------------------------
		
		return listScrollPane;
		
	}
	
	public JMenu speciesAndChromeSelectionMenu(){
		JMenu speciesMenu = new JMenu("Select Species/Chrome");
		
		wholePanel = new JPanel();
		
		GridBagLayout gbl = new GridBagLayout();
		wholePanel.setLayout(gbl);
		
		GridBagConstraints gbc = new GridBagConstraints(); 
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(1,1,1,1);
		
		// ------------------------
		// Species Label
		JLabel speciesLabel = new JLabel("Species");
		
		gbc.gridx = 0;  // x-Position im gedachten Gitter
		gbc.gridy = 0;  // y-Position im gedachten Gitter
		gbc.gridheight = 1;  // zwei Gitter-Felder hoch
		
		gbl.setConstraints(speciesLabel, gbc);
		wholePanel.add(speciesLabel);
		// End of Species Label
		// ------------------------
		
		
		
		// ------------------------
		// Species Selection
		speciesList = new JList();
		speciesList.setVisibleRowCount(2);		
		speciesList.setSelectedIndex(0);	
		speciesList.setForeground(Color.BLACK);
		speciesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// set data for the JList
		speciesModel = new DefaultListModel();
		for(Species species: master.getDAO().getContainer().keySet()){			
			speciesModel.addElement(species);
		}
		speciesList.setModel(speciesModel);

		speciesList.setSelectedIndex(speciesModel.indexOf(menuManager.getSelectedSpecies()));
		speciesSelectionModel = speciesList.getSelectionModel();	// get selectionModel for the second JList
		speciesSelectionModel.addListSelectionListener(new SpeciesSelectionManager());
		
		JScrollPane listScrollPane = new JScrollPane(speciesList);	
		

		gbc.gridx = 0;  // x-Position im gedachten Gitter
		gbc.gridy = 1;  // y-Position im gedachten Gitter
		gbc.gridheight = 2;  // zwei Gitter-Felder hoch
		
		gbl.setConstraints(listScrollPane, gbc);
		wholePanel.add(listScrollPane);
		// End of Species Selection
		// ------------------------
		
		
		// ------------------------
		// Chromosome Label
		JLabel chromosomeLabel = new JLabel("Chromosome");
		
		gbc.gridx = 0;  // x-Position im gedachten Gitter
		gbc.gridy = 4;  // y-Position im gedachten Gitter
		gbc.gridheight = 1;  // ein Gitter-Feld hoch
		
		gbl.setConstraints(chromosomeLabel, gbc);
		wholePanel.add(chromosomeLabel);
		// End of Chromosome Label
		// ------------------------
		
		
		// ------------------------
		// Chromosome Selection
		chromeList = new JList();
		chromeList.setVisibleRowCount(4);		
			
		chromeList.setForeground(Color.BLACK);
		chromeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// set data for the JList
		//ChromeSetContainer container = master.getChromeSetContainer();
		//ChromeSet chromeSet = container.getChromosomes(master.getSelectedSpecies());
		//speciesModel
//		ChromosomeSetContainer container = menuManager.getChromeSetContainer();
		Collection<Chromosome> chromes = master.getDAO().getContainer().getChromosomes(menuManager.getSelectedSpecies()).getAllChromosomes();
		
		for(Chromosome chrome:chromes){
			chromeModel.addElement(chrome);
		}

		chromeList.setModel(chromeModel);
		chromeList.setSelectedIndex(chromeModel.indexOf(menuManager.getSelectedChrome()));
		
		chromeSelectionModel = chromeList.getSelectionModel();	// get selectionModel for the second JList
		chromeSelectionModel.addListSelectionListener(new ChromeSelectionManager());
		
		JScrollPane chromeListScrollPane = new JScrollPane(chromeList);	
		
		gbc.gridx = 0;  // x-Position im gedachten Gitter
		gbc.gridy = 5;  // y-Position im gedachten Gitter
		gbc.gridheight = 4;  // zwei Gitter-Felder hoch
		
		gbl.setConstraints(chromeListScrollPane, gbc);
		wholePanel.add(chromeListScrollPane);
		
		// End of Chromosome Selection
		// ------------------------
		
		// ------------------------
		// Checkbox
		JCheckBox newWindowCheckbox = new JCheckBox("Open in new Window...");
		cc.setChromeNewWindowCheckbox_Menu(newWindowCheckbox);
		newWindowCheckbox.addItemListener(cc);
		
		gbc.gridx = 0;  // x-Position im gedachten Gitter
		gbc.gridy = 9;  // y-Position im gedachten Gitter
		gbc.gridheight = 1;  // vier Gitter-Felder hoch
		
		gbl.setConstraints(newWindowCheckbox, gbc);
		wholePanel.add(newWindowCheckbox);
		
		// End of Checkbox
		// ------------------------
		
		
		// ------------------------
		// Button
		JButton showButton = new JButton("Show");
		showButton.addActionListener(c.getC_dt());
		showButton.setActionCommand(UserGestures.SHOW_CHROME);
		
		gbc.gridx = 0;  // x-Position im gedachten Gitter
		gbc.gridy = 10;  // y-Position im gedachten Gitter
		gbc.gridheight = 1;  // vier Gitter-Felder hoch
		
		gbl.setConstraints(showButton, gbc);
		wholePanel.add(showButton);
		
		// End of Button
		// ------------------------
		
		speciesMenu.add(wholePanel);

		return speciesMenu;
	}
	
	protected void changeChromeModel(Species actualSpecies){
		chromeModel.clear();
//		ChromosomeSetContainer container = menuManager.getChromeSetContainer();
		ChromosomeSet chromeSet = master.getDAO().getContainer().getChromosomes(actualSpecies);
		//speciesModel
		Collection<Chromosome> chromes = chromeSet.getAllChromosomes();
		
		for(Chromosome chrome:chromes){
			chromeModel.addElement(chrome);
		}
	}
	
	protected class SpeciesSelectionManager implements ListSelectionListener{

		private int selectedSpeciesIndex;
		
		public void valueChanged(ListSelectionEvent event) {
			ListSelectionModel listSelectionModel = (ListSelectionModel)event.getSource();
			
			if(!listSelectionModel.isSelectionEmpty()){
				selectedSpeciesIndex = listSelectionModel.getMinSelectionIndex();
				tempSelectedSpecies = (Species)speciesModel.elementAt(selectedSpeciesIndex);
				if(master.getTempSelectedSpecies() != tempSelectedSpecies){
					changeChromeModel(tempSelectedSpecies);
					chromeList.setSelectedIndex(0);	
					setTempChrome(chromeSelectionModel);
				}
			}
		}
	}
	
	protected class ChromeSelectionManager implements ListSelectionListener{

		
		
		public void valueChanged(ListSelectionEvent event) {
			//ListSelectionModel listSelectionModel = (ListSelectionModel)event.getSource();
			
			if(!chromeSelectionModel.isSelectionEmpty()){
				setTempChrome(chromeSelectionModel);
				
				System.out.println("tempSelectedChrome " + tempSelectedChrome + " tempSelectedChrome " +tempSelectedChrome);
			}
		}
	}
	
	private void setTempChrome(ListSelectionModel listSelectionModel) {
		chromeIndex = listSelectionModel.getMinSelectionIndex();
		tempSelectedChrome = (Chrome)chromeModel.elementAt(chromeIndex);
		chromeName = tempSelectedChrome.getId();		
		if(master.getTempSelectedChrome() != tempSelectedChrome){
			master.setTempSelectedChrome(tempSelectedChrome);
		}
	}
}
