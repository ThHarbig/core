package mayday.core.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;

import mayday.core.tasks.gui.StatusBarItem;

public class StatusBarMemoryInfo implements StatusBarItem {
	
	Thread statusUpdater = null;
	JLabel theInfo = null;
	Runtime theRuntime;

	public int getPosition() {
		return 0;
	}

	public JComponent getRenderingComponent() {
		if (theInfo==null) {
			theInfo = new JLabel();
			theRuntime = Runtime.getRuntime();
			statusUpdater = new Thread("Debug monitor")
		    {
				public void run() {
					while (true) {
						try {
							long maxMemory = theRuntime.maxMemory();
							long allocatedMemory = theRuntime.totalMemory();
							long freeMemory = theRuntime.freeMemory(); 
							long totalFree = (freeMemory + (maxMemory - allocatedMemory));
							theInfo.setText(" "+totalFree / (1024*1024)+ " MB free of "+maxMemory / (1024*1024) +" MB total");
						} catch (Exception e) {
							// most likely OutOfMemory. Give us some time to regain our footing.
						}
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
						}
					}
				}
		    };
		    statusUpdater.start();
		}
		return theInfo;
	}

}
