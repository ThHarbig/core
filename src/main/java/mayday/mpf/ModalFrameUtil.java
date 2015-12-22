package mayday.mpf;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This class provides a means to make JFrames modal, a feature that is missing
 * from standard Java where only JDialogs can be made modal.
 * @author Vikram Mohan, Florian Battke (see note below)
 * 
 * Most of the following code is taken from 
 * http://vikram.blogspot.com/2005/06/workaround-for-maximize-button-on.html
 * and contains changes made by Florian Battke
 *
 * From the Webpage:
 * Most people know the JDialog window does not have a maximize button 
 * on its title bar and implementing it is not possible in pure Java. 
 * Also a JFrame does not serve the function of a JDialog and cannot 
 * act as a modal window so it cannot be used.
 * 
 */
public class ModalFrameUtil {
	
	private static class EventPump implements InvocationHandler {
		java.awt.Window frame;

		public EventPump(java.awt.Window frame) {	this.frame = frame;	}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return frame.isShowing() ? Boolean.TRUE : Boolean.FALSE;
		}

		@SuppressWarnings("unchecked")
		public void start() throws Exception {
			Class clazz = Class.forName("java.awt.Conditional");
			Object conditional = Proxy.newProxyInstance(clazz.getClassLoader(),	new Class[] { clazz }, this);
			Method pumpMethod = Class.forName("java.awt.EventDispatchThread").getDeclaredMethod("pumpEvents", new Class[] { clazz });
			pumpMethod.setAccessible(true);
			pumpMethod.invoke(Thread.currentThread(), new Object[] { conditional });
		}
	}

	// Show the given frame as modal to the specified owner.
	// NOTE: this method returns only after the modal frame is closed.
	// NOTE: if owner==null, the frame is simply made visible without being modal
	public static void showAsModal(final java.awt.Window frame, final java.awt.Window owner) {
		if (owner!=null) {	
			frame.addWindowListener(new WindowAdapter() {
				public void windowOpened(WindowEvent e) {owner.setEnabled(false);}
				public void windowClosed(WindowEvent e) {
					owner.setEnabled(true);
				    owner.removeWindowListener(this);
				    if (owner.isShowing())
				    	owner.toFront();
				}
			});

			owner.addWindowListener(new WindowAdapter() {
				public void windowActivated(WindowEvent e) {
					if (frame.isShowing()) {
						frame.toFront();
					} else
						owner.removeWindowListener(this);
				}
			});

			frame.setVisible(true);			
			try {
				new EventPump(frame).start();
			} catch (Throwable throwable) {
				if (throwable instanceof IllegalArgumentException) 
					{} // 070203 fb: ignore due to changes in mayday.core.RunPluginAction  
				else
					throw new RuntimeException(throwable);
			}
		} 
		else {
			frame.setVisible(true);
		}
	}	
}