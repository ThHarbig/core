package mayday.mushell.dispatch;

import java.util.EventListener;

public interface DispatchListener extends EventListener {

	public void dispatching(DispatchEvent evt);
	
}
