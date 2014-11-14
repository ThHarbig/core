package mayday.core.gui.columnparse;

import javax.swing.table.TableModel;

import mayday.core.meta.MIType;
import mayday.core.tasks.AbstractTask;

public abstract class ColumnTypeEstimator<ColumnType> {
	
	protected TableModel model;
	ColumnType[] estimations;	
	AbstractTask estim;
	
	@SuppressWarnings("unchecked")
	public ColumnTypeEstimator(TableModel tableModel) {
		model=tableModel;
		estimations = (ColumnType[])new Object[model.getColumnCount()];
	}
	
	public ColumnType getType(int columnIndex) {
		waitForEstimation();
		return estimations[columnIndex];
	}
	
	private void waitForEstimation() {
		if (estim==null) {
			doEstimation();
			estim.waitFor();
		}
	}
	
	private void doEstimation() {
		estim = new AbstractTask("Estimating Column Types") {

			protected void doWork() throws Exception {
				for (int i=0; i!=estimations.length; ++i) {
					estimations[i] = estimateColumn(i);
					setProgress((i*10000)/estimations.length);
				}
			}

			protected void initialize() {}
			
		};
		estim.start();
	}
	
	protected abstract ColumnType estimateColumn(int i);
	
	protected <T> boolean isValidColumn(int columnIndex, ValueChecker<T> checker, T memory ) {

		int endPoint = 1000;
		boolean noValueYet = true;
		
		for (int i=0; i!=Math.min(endPoint, model.getRowCount()); ++i) {
			String v = (String)model.getValueAt(i, columnIndex);
			if (v==null) {
				if (noValueYet && i+1==endPoint)
					endPoint+=1000; // go on searching
				continue;
			}
			if (!checker.isValid(v, memory))
				return false;
		}
		return true;
	}
	
	public interface ValueChecker<T> {
		public boolean isValid( String value , T memory);
	}
	
	@SuppressWarnings("unchecked")
	public class NumericChecker implements ValueChecker {

		public boolean isValid(String value, Object memory) {
			try {
				Double.parseDouble(value);
				return true;
			} catch (Exception e) {
				return false;				
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	public class IntegerChecker implements ValueChecker {

		public boolean isValid(String value, Object memory) {
			try {
				Integer.parseInt(value);
				return true;
			} catch (Exception e) {
				return false;				
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	public class NoMissingChecker implements ValueChecker {

		public boolean isValid(String value, Object memory) {
			return value!=null && value.trim().length()>0;
		}		
	}
	
	public class MIOChecker implements ValueChecker<MIType> {
		
		public boolean isValid(String value, MIType memory) {
			return memory.deSerialize(MIType.SERIAL_TEXT, value);
		}
		
	}
	
}
