package mayday.mpf.options;

/** OptSpacer is not really an option. It only shows a certain string but stores no value. This can be used to 
 * give structure to FilterOptions dialog windows. Make sure to use only very short strings here, to keep the size+
 * of the option window small. 
 * @author Florian Battke
 */
public class OptSpacer extends OptBase {

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#validate()
	 */
	public boolean validate() { return true; }
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#accept()
	 */
	public void accept() { }
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#cancel()
	 */
	public void cancel() { }

	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueToString()
	 */
	public String ValueToString() { return ""; }
	/* (non-Javadoc)
	 * @see mayday.mpf.options.OptBase#ValueFromString(java.lang.String)
	 */
	public void ValueFromString(String valueStr) {}
	
	/** Creates a new instance with a given text
	 * @param Text the text to display.
	 */
	public OptSpacer(String Text) {
		super(Text,"");
		Description=null;
	}
	
}
