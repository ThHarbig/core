package mayday.core.gui.classes;

import javax.swing.JPanel;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.gui.components.ExcellentBoxLayout;

/**
 * The ClassSelectionPanel is to be used in a MVC-fashion. The model is the ClassSelectionModel.  
 * @author Stephan Symons
 * @version 0.5
 *
 */
@SuppressWarnings("serial")
public class ClassSelectionPanel extends JPanel {
	
	private ClassSelectionModel partition;
	private boolean objectsFixed;
	private PanelManualSelection pManualSelection;
	private PanelGenerateLabels pGenerateLabels;
	
	public ClassSelectionPanel(ClassSelectionModel partition, DataSet ds, Integer minClasses, Integer maxClasses) {
		this.partition=partition;
		init(ds, minClasses, maxClasses);		
	}
	
	public ClassSelectionPanel(ClassSelectionModel partition, DataSet ds) {
		this(partition, ds, null, null);
	}
	
	public ClassSelectionPanel(ClassSelectionModel partition) {
		this(partition, null);
	}

	/**
	 * Initialize the Dialog
	 */
	private void init(DataSet ds, Integer minClasses, Integer maxClasses) {
		
		setLayout(new ExcellentBoxLayout(true, 5));
		
		JPanel pFileImport = new PanelFileImport(this);
		add(pFileImport);
		
		if (ds!=null) {
			JPanel pImportMIO = new PanelMIOImport(this, ds); 
			add(pImportMIO);

			JPanel pLoadStoreMIO = new PanelLoadStoreClasses(this, ds); 
			add(pLoadStoreMIO);
		}
		
		pGenerateLabels = new PanelGenerateLabels(this, minClasses, maxClasses);
		add(pGenerateLabels);
		
		pManualSelection = new PanelManualSelection(this);
		add(pManualSelection);
	}

	public ClassSelectionModel getClassPartition() {
		return partition;
	}

	public boolean isObjectsFixed() {
		return objectsFixed;
	}

	public void setObjectsFixed(boolean objectsFixed) {
		this.objectsFixed = objectsFixed;
		pGenerateLabels.updateObjectsFixed();
	}

	public ClassSelectionModel getModel() {
		return partition;
	}

	public void setModel(ClassSelectionModel model) {
		this.partition = model;
		pManualSelection.fireChanged();
	}	

}
