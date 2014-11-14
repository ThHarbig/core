package mayday.vis3.components;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.PlotContainer;

@SuppressWarnings("serial")
public abstract class BasicPlotPanel extends JPanel implements PlotComponent {

	public abstract void setup(final PlotContainer plotContainer); 
	
	@Override  /* From Component */
	public void addNotify() {
		super.addNotify();
		Component comp = this;
		while (comp!=null && !(comp instanceof PlotContainer)) {
			comp=comp.getParent();
		}
		if (comp!=null) {
			setup((PlotContainer)comp);
		}
	}	
	
	protected Window getOutermostJWindow() {
		Component comp = this;
		while (comp!=null && !(comp instanceof Window)) {
			comp=comp.getParent();
		}
		return((Window)comp);
	}
	
	@Override
	public void addKeyListener(KeyListener l) {
		Window w = getOutermostJWindow();
		if (w!=null) w.addKeyListener(l);
	}
	
	@Override  
	public void removeKeyListener(KeyListener l) {
		Window w = getOutermostJWindow();
		if (w!=null) w.removeKeyListener(l);		
	}
	
}
