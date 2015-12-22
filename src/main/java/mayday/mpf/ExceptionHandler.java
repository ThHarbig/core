package mayday.mpf;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/** ExceptionHandler provides a central dispatcher for Exception messages that are to be displayed to the
 *  user. Modify this class to change the behaviour of MPF classes.
 * @author Florian Battke
 *
 */
public class ExceptionHandler {
	
	/** Show the exception message
	 * @param t the Exception or Error (Throwable)
	 * @param parentFrame the parent JFrame to use for displaying the modal message
	 */
	public static void handle(Throwable t, JFrame parentFrame) {
		JOptionPane.showMessageDialog(parentFrame, t.getMessage(), 
				"Exception", 
				JOptionPane.ERROR_MESSAGE);		
	} 
	
	/** Show the exception message
	 * @param t the Exception or Error (Throwable)
	 * @param parentDialog the parent JDialog to use for displaying the modal message
	 */
	public static void handle(Throwable t, JDialog parentDialog) {
		JOptionPane.showMessageDialog(parentDialog, t.getMessage(), 
				"Exception", 
				JOptionPane.ERROR_MESSAGE);				
	}

}
