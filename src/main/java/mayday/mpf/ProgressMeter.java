package mayday.mpf;

import javax.swing.JProgressBar;


/** ProgressMeter allows the calculation and display of progress indicators. 
 * A ProgressMeter can have a parent that it reports it's values to so that
 * the progress of sub-tasks is incorporated into the progress of the main task. 
 * @author Florian Battke
 */
public class ProgressMeter {
	
	protected double Percentage=0;
	protected double Scaling=1;
	protected double Baseline=0;
	protected String Message="";
	protected ProgressMeter Parent=null;
	protected JProgressBar ProgressBar = null;
	protected java.text.NumberFormat nf;
	protected LogWriter logwriter = null;
	
	public abstract class LogWriter {
		public void writeLine(String line) {}; 
	}
	
	/** Sets the progress value for this task together with an informational message
	 * describing what is currently done
	 * @param percentage the current progress status
	 * @param message a String describing the current action or null to keep the last message
	 * This is equal to calling statusChanged(percentage, message, false) 
	 */
	public final void statusChanged(double percentage, String message) {
		statusChanged(percentage, message, false);
	}
		
	protected void update(String msg) {
		if (ProgressBar!=null) {
			ProgressBar.setString(msg);
			ProgressBar.setValue((int)(Math.round(Percentage*1000)));
		}
		else System.out.println(msg); 
	}
	
	private final void statusChanged(double percentage, String message, boolean subMessage) {
	
		if (percentage>-1) Percentage=percentage;	
		if (message!=null && !subMessage) Message=message;
		
		if (Parent!=null) {
			// Call parent with correct percentage, retain parent's current message
			Parent.statusChanged(Baseline + Scaling*Percentage, 
					Message + 
					((subMessage && message!=null && !message.equals(""))  ? " ("+message+")" : ""), 
					true);			
		} else {
			// We'll handle this ourselves because we have no parent
			update(	nf.format(Percentage*100) +"% "+Message
					+ (subMessage && message!=null && !message.equals("") ? " ("+message+")" : "")
					);		
		}
	}
	
	public void writeLogLine(String logline) {
		if (this.logwriter!=null) {
			logwriter.writeLine(logline+'\n');
		} else {// I don't have my own logwriter
			if (this.Parent!=null) {
				Parent.writeLogLine(logline);
			}
		}
	}
	
	/** Returns the GUI component for this ProgressMeter
	 * @return a JProgressBar component or null if this ProgressMeter represents a subtask
	 */
	public final JProgressBar getProgressBar() {
		return ProgressBar;
	}
	
	/** Constructs a new child instance of ProgressMeter
	 * @param parent Another instance of ProgressMeter that acts as our parent 
	 * @param scaling How big is the share of this task in it's parent's 100% percents
	 * @param baseline At what percentage does this subtask start? 
	 */
	public ProgressMeter(ProgressMeter parent, double scaling, double baseline) {
		Parent = parent;
		Scaling = scaling;
		Baseline = baseline;
	}

	
	/** Constructs a new parent instance of ProgressMeter
	 */
	public ProgressMeter() {
		ProgressBar = new JProgressBar(0,1000);
		ProgressBar.setValue(0);
		ProgressBar.setStringPainted(true);
		nf = new java.text.DecimalFormat("0.00");
	}
	
	

	private int _stepsPerPercent;
	private int _totalSteps = 0;
	private int _currentStep;
	private double _percentage;
	private double _dblPercentPerStep;
	
	/** Initialize the Stepper interface. Do this before using stepStepper() to automatically calculate percentages from a custom number
	 * of steps without having to think about division by zero etc.
	 * @param NumberOfSteps The total number of steps in 100%
	 */
	public void initializeStepper(int NumberOfSteps) {
		_percentage = 0;
		_totalSteps = NumberOfSteps;		
		_currentStep = 0;
		if (_totalSteps!=0) { 
			_dblPercentPerStep = 1.0 / (double)_totalSteps;
			_stepsPerPercent = _totalSteps / 100;
		}
	}
	
	/** Progress a certain number of steps. Call initializeStepper before using this function. 
	 * @param steps the number of steps to increase the current number of finished steps by
	 */
	public void stepStepper(int steps) {
		if (_totalSteps!=0 && _totalSteps!=0) {
			++_currentStep;
			if (_stepsPerPercent > 0) {
				if (_currentStep % _stepsPerPercent == 0)
					statusChanged(_percentage=_dblPercentPerStep*_currentStep, null);
//					statusChanged(_percentage+=0.01, null);  // this had too big rounding errors!
			} else {
				statusChanged(_percentage+=_dblPercentPerStep, null);
			}
		}
	}
	
	
}
