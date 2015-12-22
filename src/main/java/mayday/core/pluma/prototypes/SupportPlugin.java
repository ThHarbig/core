package mayday.core.pluma.prototypes;

public interface SupportPlugin {

	// run is called after all plugins are registered and initialized.	
	public abstract Object run(Object input);


}
