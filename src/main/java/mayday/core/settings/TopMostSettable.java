package mayday.core.settings;

/** for setting that whose GUI components behave differently when they are the top-level element (such as the only element in a JFrame*/
public interface TopMostSettable<T extends Setting> {
	
	public T setTopMost(boolean tm);
	
}
