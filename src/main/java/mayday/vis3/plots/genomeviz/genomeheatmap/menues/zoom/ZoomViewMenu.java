package mayday.vis3.plots.genomeviz.genomeheatmap.menues.zoom;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import mayday.core.gui.MaydayFrame;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.ZoomLevel;
import mayday.vis3.plots.genomeviz.genomeheatmap.additional.MasterManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeheatmap.controllercollection.Controller_zl;
import mayday.vis3.plots.genomeviz.genomeheatmap.menues.MenuManager;
import mayday.vis3.plots.genomeviz.genomeheatmap.owndatastructures.ComboObject;
import mayday.vis3.plots.genomeviz.genomeheatmap.usergestures.UserGestures;

public class ZoomViewMenu {

	protected MenuManager menuManager;
	protected MasterManager master;
	protected Controller c;
	protected Controller_zl c_zl;
	
	protected JRadioButtonMenuItem oneItem;
	protected JRadioButtonMenuItem twoItem;
	protected JRadioButtonMenuItem threeItem;
	protected JRadioButtonMenuItem fiveItem;
	protected JRadioButtonMenuItem tenItem;
	protected JRadioButtonMenuItem fifteenItem;
	protected JRadioButtonMenuItem twentyItem;
	protected JRadioButtonMenuItem twentyfiveItem;
	protected JRadioButtonMenuItem fiftyItem;
	protected JRadioButtonMenuItem hundredItem;
	protected JRadioButtonMenuItem twohundredItem;
	protected JRadioButtonMenuItem thousandItem;
	protected JRadioButtonMenuItem twothousandItem;
	protected JRadioButtonMenuItem fivethousandItem;
	protected JRadioButtonMenuItem fitItem;
	
	protected JButton fitButton = null;
	
	protected String fitLabel = "fit";
	
	protected DefaultListModel listModel;
	
	protected JComboBox combo;
	protected DefaultComboBoxModel comboModel;
	
	protected JPanel zoompanel;
	
	public ZoomViewMenu(MenuManager menuManager, MasterManager master, Controller c){
		this.menuManager = menuManager;
		this.master = master;
		this.listModel = new DefaultListModel();
		this.c = c;
		this.c_zl = c.getC_zl();
		
		this.combo = new JComboBox();
		fitButton = new JButton("Fit to window");
		fitButton.setEnabled(false);
		
		combo.addItemListener(c_zl);
		
		initializeZoomButtonGroup();
		
		zoompanel = anotherZoomPanel();
	}
	
	public JPanel getZoomPanel(){
		return zoompanel;
	}
	
	//#############################################################
	/*
	 * Zoom Level
	 * 
	 */
	//#############################################################
	
	protected void initializeZoomButtonGroup(){
		oneItem = new  JRadioButtonMenuItem("1x");
		oneItem.addActionListener(master.getController());
		oneItem.setActionCommand(UserGestures.ZOOM_ONE);
		
		twoItem = new  JRadioButtonMenuItem("2x");
		twoItem.addActionListener(master.getController());
		twoItem.setActionCommand(UserGestures.ZOOM_TWO);
		
		fiveItem = new  JRadioButtonMenuItem("5x");
		fiveItem.addActionListener(master.getController());
		fiveItem.setActionCommand(UserGestures.ZOOM_FIVE);
		
		tenItem = new  JRadioButtonMenuItem("10x");
		tenItem.addActionListener(master.getController());
		tenItem.setActionCommand(UserGestures.ZOOM_TEN);
		
		fifteenItem = new  JRadioButtonMenuItem("15x");
		fifteenItem.addActionListener(master.getController());
		fifteenItem.setActionCommand(UserGestures.ZOOM_FIFTEEN);
		
		twentyItem = new  JRadioButtonMenuItem("20x");
		twentyItem.addActionListener(master.getController());
		twentyItem.setActionCommand(UserGestures.ZOOM_TWENTY);
		
		twentyfiveItem = new  JRadioButtonMenuItem("25x");
		twentyfiveItem.addActionListener(master.getController());
		twentyfiveItem.setActionCommand(UserGestures.ZOOM_TWENTYFIVE);
		
		fiftyItem = new  JRadioButtonMenuItem("50x");
		fiftyItem.addActionListener(master.getController());
		fiftyItem.setActionCommand(UserGestures.ZOOM_FIFTY);
		
		hundredItem = new  JRadioButtonMenuItem("100x");
		hundredItem.addActionListener(master.getController());
		hundredItem.setActionCommand(UserGestures.ZOOM_HUNDRED);
		
		twohundredItem = new  JRadioButtonMenuItem("200x");
		twohundredItem.addActionListener(master.getController());
		twohundredItem.setActionCommand(UserGestures.ZOOM_TWOHUNDRED);
		
		
		thousandItem = new  JRadioButtonMenuItem("1000x");
		thousandItem.addActionListener(master.getController());
		thousandItem.setActionCommand(UserGestures.ZOOM_THOUSAND);
		
		twothousandItem = new  JRadioButtonMenuItem("2000x");
		twothousandItem.addActionListener(master.getController());
		twothousandItem.setActionCommand(UserGestures.ZOOM_TWOTHOUSAND);
		
		fivethousandItem = new  JRadioButtonMenuItem("5000x");
		fivethousandItem.addActionListener(master.getController());
		fivethousandItem.setActionCommand(UserGestures.ZOOM_FIVETHOUSAND);
		
		fitItem = new  JRadioButtonMenuItem(fitLabel);
		fitItem.addActionListener(master.getController());
		fitItem.setActionCommand(UserGestures.ZOOM_FIT);
		fitItem.setEnabled(false);
	}
	
	// change chromosome view from whole-view to condensed-view
	@SuppressWarnings("serial")
	public JMenu setZoomMenu(){
		
		ZoomLevel level = master.getZoomLevel();
		setZoomViewButtons(level);
		
			//super("Change Chromosome View");
			JMenu menu = new JMenu( "Select Zoom" );
			menu.setMnemonic( KeyEvent.VK_Z );
			
			ButtonGroup buttonGroup = new ButtonGroup();

			buttonGroup.add(oneItem);
			menu.add(oneItem);

			buttonGroup.add(twoItem);
			menu.add(twoItem);

			buttonGroup.add(fiveItem);
			menu.add(fiveItem);

			buttonGroup.add(tenItem);
			menu.add(tenItem);

			buttonGroup.add(fifteenItem);
			menu.add(fifteenItem);

			buttonGroup.add(twentyItem);
			menu.add(twentyItem);

			buttonGroup.add(twentyfiveItem);
			menu.add(twentyfiveItem);
			
			buttonGroup.add(fiftyItem);
			menu.add(fiftyItem);
			
			buttonGroup.add(hundredItem);
			menu.add(hundredItem);
			
			buttonGroup.add(twohundredItem);
			menu.add(twohundredItem);
			
			buttonGroup.add(thousandItem);
			menu.add(thousandItem);
			
			buttonGroup.add(twothousandItem);
			menu.add(twothousandItem);
			
			buttonGroup.add(fivethousandItem);
			menu.add(fivethousandItem);
	
			buttonGroup.add(fitItem);
			menu.add(fitItem);
			
			menu.addSeparator();
			menu.add(new AbstractAction("Detach menu") {

				public void actionPerformed(ActionEvent e) {
					MaydayFrame mf = new MaydayFrame("Zoom");
					mf.add(anotherZoomPanel());
					mf.pack();
					mf.setVisible(true);
				}
				
			});
			
			return menu;

	}
	
	public JPanel anotherZoomPanel(){
		ZoomLevel level = master.getZoomLevel();
		setZoomViewButtons(level);
		
		JPanel zoomPanel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		zoomPanel.setLayout(gbl);
		
		GridBagConstraints gbc = new GridBagConstraints(); 
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(1,1,1,1);
		
		gbc.gridx = 0;  // x-position in grid
		gbc.gridy = 0;  // y-position in grid
		gbc.gridheight = 1;  // height is one cell
		gbc.gridwidth = 1;
		JLabel label = new JLabel("Zoom:");
		gbl.setConstraints(label, gbc);
		zoomPanel.add(label);
		
		
		gbc.gridx = 1;  // x-position in grid
		gbc.gridy = 0;  // y-position in grid
		gbc.gridheight = 1;  // height is one cell
		gbc.gridwidth = 1;
		JButton minusButton = new JButton("-");
		minusButton.addActionListener(c_zl);
		minusButton.setActionCommand(UserGestures.ZOOM_MINUS_BUTTON);
		gbl.setConstraints(minusButton, gbc);
		zoomPanel.add(minusButton);
		
		gbc.gridx = 2;  // x-position in grid
		gbc.gridy = 0;  // y-position in grid
		gbc.gridheight = 1;  // height is one cell
		gbc.gridwidth = 2;
		initializeComboBox();
		gbl.setConstraints(combo, gbc);
		zoomPanel.add(combo);
		
		gbc.gridx = 4;  // x-position in grid
		gbc.gridy = 0;  // y-position in grid
		gbc.gridheight = 1;  // height is one cell
		gbc.gridwidth = 1;
		JButton plusButton = new JButton("+");
		plusButton.addActionListener(c_zl);
		plusButton.setActionCommand(UserGestures.ZOOM_PLUS_BUTTON);
		gbl.setConstraints(plusButton, gbc);
		zoomPanel.add(plusButton);
		
		gbc.gridx = 2;  // x-position in grid
		gbc.gridy = 1;  // y-position in grid
		gbc.gridheight = 1;  // height is one cell
		gbc.gridwidth = 1;
		fitButton.addActionListener(c_zl);
		fitButton.setActionCommand(UserGestures.Zoom_FIT_TO_WINDOW_BUTTON);
		gbl.setConstraints(fitButton, gbc);
		zoomPanel.add(fitButton);
		return zoomPanel;
	}

	public void setZoomViewButtons(ZoomLevel level) {
		
		switch(level){
		case one:
			oneItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.one);
			break;
		case two:
			twoItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.two);
			break;
		case five:
			fiveItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.five);
			break;
		case ten:
			tenItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.ten);
			break;
		case fifteen:
			fifteenItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.fifteen);
			break;
		case twenty:
			twentyItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.twenty);
			break;
		case twentyfive:
			twentyfiveItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.twentyfive);
			break;
			
		case fifty:
			fiftyItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.fifty);
			break;
		case hundred:
			hundredItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.hundred);
			break;
		case twohundred:
			twohundredItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.twohundred);
			break;
		case thousand:
			thousandItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.thousand);
			break;
		case twothousand:
			twothousandItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.twothousand);
			break;
		case fivethousand:
			fivethousandItem.setSelected(true);
			setComboBoxSelection(ZoomLevel.fivethousand);
			break;
		case fit:
			fitItem.setSelected(true);
			break;
		
		default:
				System.err.println("ZoomViewMenu - setZoomViewButtons: Selected ZoomLevel not existing");
		}

	}

	private void setComboBoxSelection(ZoomLevel zoomLevel) {
		if(combo!=null){
			if(combo.getModel().getSize() != 0){
				for(int i = 0; i < combo.getModel().getSize(); i++){
					ComboObject obj = (ComboObject)combo.getModel().getElementAt(i);
					if(obj!=null){
						if(obj.getZoomLevel().equals(zoomLevel)){
							combo.getModel().setSelectedItem(obj);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * enables the fit buttons.
	 * @param kindOfChromeView
	 */
	public void enableZoomView_FitButton() {

			fitItem.setEnabled(true);
			fitButton.setEnabled(true);
	}

	/**
	 * disables the fit buttons.
	 * @param kindOfChromeView
	 */
	public void disableZoomView_FitButton() {

			fitItem.setEnabled(false);
			fitButton.setEnabled(false);
	}

	/**
	 * sets the label of the fit button.
	 * @param zoomMultiplikator
	 */
	public void setFitLabel(Integer zoomMultiplikator) {
		if(zoomMultiplikator >= 1)
		this.fitLabel = "fit: " + Integer.toString(zoomMultiplikator);
		fitItem.setText(fitLabel);
	}
	
	
	/**
	 * initializes the model for the combobox and sets the model.
	 */
	public void initializeComboBox(){
		
		comboModel = new DefaultComboBoxModel();
		if(master.getWindowRanges() != null){
			TreeMap<Integer,ComboObject> map = master.getWindowRanges();
			for(Integer multiplikator: map.keySet()){
				comboModel.addElement(map.get(multiplikator));
			}
		}
		combo.setModel(comboModel);
	}
}
