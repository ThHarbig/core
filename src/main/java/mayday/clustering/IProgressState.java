/*
 * File IProgressState.java
 * Created on 24.06.2005
 * As part of package mayday.clustering
 * By Janko Dietzsch
 *
 */
package mayday.clustering;

/**
 * This interface is intended to provide a call-back mechanism for reporting 
 * the progress state of lengthy operations and to receive a possible 
 * Cancel-Signal from the GUI.  
 * 
 * @author Janko Dietzsch
 *
 */
public interface IProgressState {

	/**
	 * This function evaluates a progress status as a fractional value ranging 
	 * from 0 to 1. And informs with its return parameter if a Cancel-Signal 
	 * was received by the GUI.
	 * 
	 * @param FractionalStatus value between 0 (no progress) and 1 (all done)
	 * @return boolean value to signal the a received Cancel-Event: true - cancel operation, false - go ahead   
	 */
	public boolean reportCurrentFractionalProgressStatus(double FractionalStatus);
	
	/**
	 * This function reports the integer value that is reached at the end of 
	 * the process. This method should only be used in combination with the methods 
	 * {@link #reportCurrentAbsolutProgressStatus(int) reportCurrentAbsolutProgressStatus} 
	 * and 
	 * {@link #increaseProgressStatus(int) countUpProgressStatus}.
	 * 
	 * @param ProgressMax integer value reached at the end of the operation
	 */
	public void setMaxProgressStatus(int ProgressMax);
	
	/**
	 * This function sets the initial value of the progress status at the beginning of the 
	 * operation. This method should only be used in combination with the methods 
	 * {@link #reportCurrentAbsolutProgressStatus(int) reportCurrentAbsolutProgressStatus} 
	 * and 
	 * {@link #increaseProgressStatus(int) countUpProgressStatus}.
	 * 
	 * @param ProgressMin initial integer value at the beginning of the operation
	 */
	public void setMinProgressStatus(int ProgressMin);
	
	
	/**
	 * This method evaluates an absolut value representing the progress status between 
	 * the initial value seted by method  
	 * {@link #setMinProgressStatus(int) setMinProgressStatus} and the maximal value 
	 * seted by method {@link #setMaxProgressStatus(int) setMaxProgressStatus}.
	 * It informs with its return parameter if a Cancel-Signal was received by the GUI.
	 * 
	 * @param CurrentStatus absolut value of the current progress status
	 * @return boolean value to signal the a received Cancel-Event: true - cancel operation, false - go ahead 
	 */
	public boolean reportCurrentAbsolutProgressStatus(int CurrentStatus);
	
	/**
	 * This method evaluates the actual progress status by getting the current increase 
	 * of progress status. This function shoul only be used in combination with method 
	 *  {@link #setMaxProgressStatus(int) setMaxProgressStatus} and/or
	 *  {@link #setMinProgressStatus(int) setMinProgressStatus}
	 * It informs with its return parameter if a Cancel-Signal was received by the GUI.
	 * 
	 * @param Progress integer value that should be used to increase the current progress status
	 * @return boolean value to signal the a received Cancel-Event: true - cancel operation, false - go ahead  
	 */
	public boolean increaseProgressStatus(int Progress);
}
