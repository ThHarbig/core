package mayday.core.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.EventListenerList;

import mayday.core.Mayday;

public class MaydayWindowManager {

	private static HashSet<Window> registeredWindows = new HashSet<Window>();

	private static EventListenerList eventListenerList = new EventListenerList();

	private MaydayWindowManager() {};
	
	public static void addWindow(Window wind) {
		
		// Main window is not registered
		if (wind==Mayday.sharedInstance)
			return; 
			
		if (!registeredWindows.contains(wind)) {
			registeredWindows.add(wind);
			fireEvent(new ActionEvent(new Object(),0,""));
			System.out.println((registeredWindows.size()+1)+ " windows open after opening: "+getTitle(wind));
		}
	    if (Mayday.Mayday_Icon!=null) {
	    	wind.setIconImage(Mayday.Mayday_Icon.getImage());
	    }
	}
	
	public static void removeWindow(Window wind) {
		if (registeredWindows.contains(wind)) {
			registeredWindows.remove(wind);
			fireEvent(new ActionEvent(new Object(),0,""));
			System.out.println((registeredWindows.size()+1)+ " windows open after closing: "+getTitle(wind));
		}
	}
	
	public static String getTitle(Window myWindow) {
    	String title = "Window of unknown type";
    	if (myWindow instanceof JDialog)
    		title = ((JDialog)myWindow).getTitle();
    	else if (myWindow instanceof JFrame)
    		title = ((JFrame)myWindow).getTitle();
    	return title;
	}
	
	public static void setTitle(Window myWindow, String title) {
    	if (myWindow instanceof JDialog)
    		((JDialog)myWindow).setTitle(title);
    	else if (myWindow instanceof JFrame)
    		((JFrame)myWindow).setTitle(title);
	}
	
	public static synchronized Set<Window> getWindows() {
		return Collections.unmodifiableSet(registeredWindows);
	}
	

	public static synchronized void addListener(ActionListener vml) {
		eventListenerList.add(ActionListener.class, vml);		
	}
	
	public static synchronized void removeListener(ActionListener vml) {
		eventListenerList.remove(ActionListener.class, vml);
	}
	

	protected static synchronized void fireEvent(ActionEvent event) {
		Object[] l_listeners = eventListenerList.getListenerList();

		if (l_listeners.length==0)
			return;

		// process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = l_listeners.length-2; i >= 0; i-=2 )  {
			if ( l_listeners[i] == ActionListener.class )  {
				ActionListener list = ((ActionListener)l_listeners[i+1]);
				try {
					list.actionPerformed(event);
				} catch (Exception e) {
					System.err.println("A listener died unexpectedly: "+e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	

}
