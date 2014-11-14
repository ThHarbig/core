package mayday.core.io.dataset.tabular;

import javax.swing.table.TableModel;

import mayday.core.pluma.PluginManager;
import mayday.core.tasks.AbstractTask;

public class ColumnTypeEstimator {
	
	TableModel model;
	ColumnType[] estimations;	
	String[] miTypeEstimations;
	AbstractTask estim;
	
	public ColumnTypeEstimator(TableModel tableModel) {
		model=tableModel;
		estimations = new ColumnType[model.getColumnCount()];
		miTypeEstimations = new String[model.getColumnCount()];
		doEstimation();
	}
	
	public ColumnType getType(int columnIndex) {
		waitForEstimation();
		return estimations[columnIndex];
	}
	
	public String getMIType(int columnIndex) {
		waitForEstimation();
		return miTypeEstimations[columnIndex];
	}
	
	private void waitForEstimation() {
		if (estim!=null) {
			estim.waitFor();
			estim=null;
		}
	}
	
	private void doEstimation() {
		estim = new AbstractTask("Estimating Column Types") {

			protected void doWork() throws Exception {
				boolean ExperimentsEnded = false;
				estimations[0] = ColumnType.Ignore;
				for (int i=1; i!=estimations.length; ++i) {
					estimations[i] = estimateColumn(i, ExperimentsEnded);
					if (estimations[i]!=ColumnType.Experiment)
						ExperimentsEnded = true;
					miTypeEstimations[i] = asMIType(i);
					setProgress((i*10000)/estimations.length);
				}
			}

			protected void initialize() {}
			
		};
		estim.start();
	}
	
	private ColumnType estimateColumn(int i, boolean ExperimentsEnded){
		if (!ExperimentsEnded) {
			if (asExperiment(i))
				return ColumnType.Experiment;
		}
		if (asProbeList(i))
			return ColumnType.ProbeList;
		if (asNonEmpty(i)) 
			return ColumnType.MetaInfo;
		return ColumnType.Ignore;
	}
	
	public boolean asExperiment(int columnIndex) {
		
		int endPoint = 1000;
		boolean noValueYet = true;
		
		for (int i=0; i!=Math.min(endPoint, model.getRowCount()); ++i) {
			String v = (String)model.getValueAt(i, columnIndex);
			//remove empty values
			if (v==null || v.length()==0 || v.equals("NA")) {
				if (noValueYet && i+1==endPoint)
					endPoint+=1000; // go on searching
				continue;			
			}
			try { 
				noValueYet = false;
				Double.parseDouble(v);
			} catch (NumberFormatException nfe) {
				return false;
			}
		}
		return true;
	}
	
	public boolean asNonEmpty(int columnIndex) {	
		for (int i=0; i!=model.getRowCount(); ++i) {
			String v = (String)model.getValueAt(i, columnIndex);
			//remove empty values
			if (v!=null && v.length()>0 && !v.equals("NA")) 
				return true;
		}
		return false;
	}
	
	private boolean asProbeList(int columnIndex) {
		String val1="";
		String val2=null;
		
		int endPoint = 1000;
		boolean noValueYet = true;
		
		for (int i=0; i!=Math.min(endPoint, model.getRowCount()); ++i) {
			String v = (String)model.getValueAt(i, columnIndex);
			if (v==null) {
				if (noValueYet && i+1==endPoint)
					endPoint+=1000; // go on searching
				continue;
			}
			if (!v.equals(val1)) {
				if (val2==null && !v.equals("NA")) {
					val2=v;
				} else {
					if (!v.equals(val2)) {
						return false;
					}
				}					
			}
		}
		return val2!=null;
	}

	
	
	private String checkType(String value) {
		try {
			if (value.length()>0 && !value.toUpperCase().startsWith("NA"))
				Double.parseDouble(value);
				if (value.contains(".") || value.contains("E") || value.contains("e"))
					return "PAS.MIO.Double";
				else
					return "PAS.MIO.Integer";			
		} catch (Exception e) {
			if (value.contains(",")) {
				if (value.contains("="))
					return "PAS.MIO.StringMap";
				else
					return "PAS.MIO.StringList";					
			}
			return "PAS.MIO.String";
		}
	}
	
	private String asMIType(int columnIndex) {
		String selectedType = null;
		
		int endPoint = 1000;
		
		for (int j=0; j!=Math.min(endPoint, model.getRowCount()); ++j) {
			String v = (String)model.getValueAt(j, columnIndex);
					
			if (v==null || v.length()==0) {
				if (selectedType==null && j+1==endPoint)
					endPoint+=1000; // go on searching
				continue; // no more checks on empty cell
			}
			
			if (selectedType == null) {
				selectedType = checkType(v);
			} else {
				String ct = checkType(v);
				if (selectedType!=ct) {
					// expand int to double
					if (ct.equals("PAS.MIO.Double") && selectedType.equals("PAS.MIO.Integer") || 
							ct.equals("PAS.MIO.Integer") && selectedType.equals("PAS.MIO.Double"))
						selectedType = "PAS.MIO.Double";
					else
						selectedType = "PAS.MIO.String"; // catch-all
					break;
				}
			}			
		}
		
		if (selectedType==null)
			selectedType="PAS.MIO.String";
		
		return PluginManager.getInstance().getPluginFromID(selectedType).getName();
	}
	
}
