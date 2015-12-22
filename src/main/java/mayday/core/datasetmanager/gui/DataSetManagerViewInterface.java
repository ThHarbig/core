package mayday.core.datasetmanager.gui;

import java.awt.Component;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.event.ListSelectionListener;

import mayday.core.DataSet;
import mayday.core.StoreListener;

public interface DataSetManagerViewInterface extends StoreListener {

	/**
	 * return a list of selected datasets. the most recently selected dataset must always be
	 * at index 0 for historical reasons! 
	 * @return the list
	 */
	public List<DataSet> getSelectedDataSets();
	
	public void setSelectedDataSets(Collection<DataSet> ds);

	/**
	 * udpate name and tooltip for a given dataset
	 * @param ds the dataset with changed information
	 * internally update the correct datasetview
	 */
	public void updateInfo(DataSet ds); 

	/** 
	 * add DataSet 
	 * @param ds the dataset to add
	 * internally create a new datasetview and add that.
	 */
	public void addDataSet(DataSet ds);
		
	/**
	 * close a given dataset and remove it from memory
	 */
	public void closeDataSet(DataSet ds);
	
	/** 
	 * tell the datasetmanagerview which menu is providing dataset plugins
	 * @param dsmenu the menu
	 */
	public void setDataSetMenu(JMenu dsmenu);
	
	/** 
	 * tell the datasetmanagerview which menu is providing probelist plugins
	 * @param plmenu the menu
	 */
	public void setProbeListMenu(JMenu plmenu);
	
	public JMenu getProbeListMenu();
	
	public JMenu getMenu();
	
	public Component getGUIComponent();
	
	public void addSelectionListener(ListSelectionListener lsl);
	public void removeSelectionListener(ListSelectionListener lsl);

	void moveListenersTo(DataSetManagerViewInterface other);
}
