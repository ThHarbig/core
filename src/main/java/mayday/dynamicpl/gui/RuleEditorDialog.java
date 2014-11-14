package mayday.dynamicpl.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JLabel;

import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.MaydayFrame;
import mayday.dynamicpl.DynamicProbeList;

@SuppressWarnings("serial")
public class RuleEditorDialog extends MaydayFrame {

	protected DynamicProbeList dpl;
	protected JLabel filterSizeLabel = new JLabel();
	
	public RuleEditorDialog(DynamicProbeList pl) {
		setName(pl);
		getContentPane().add(new RuleEditorPanel(pl), BorderLayout.CENTER);
		
        setMinimumSize(new Dimension(800,600));
    	pack();
    	
	}
	
	protected void setName(DynamicProbeList dpl) {
		setTitle("Rule Set Editor: "+dpl.getName()); 
	}
	
	protected ProbeListListener plListener = new ProbeListListener() {

		public void probeListChanged(ProbeListEvent event) {
			switch(event.getChange()){
			case ProbeListEvent.PROBELIST_CLOSED:
				dispose();
				break;
			case ProbeListEvent.ANNOTATION_CHANGE:
				setName((DynamicProbeList)event.getSource());
				break;
			}
		}
	};
	

	protected WindowListener windowClosingAdapter = new WindowAdapter() {
		
		 public void windowClosed(WindowEvent e) {
			 finalizeWork();
		 }		
	};

	
	protected void addListenersTo(DynamicProbeList dpl) {
		addWindowListener(windowClosingAdapter);
		dpl.addProbeListListener(plListener);		
	}
	
	protected void removeListenersFrom(DynamicProbeList dpl) {
		removeWindowListener(windowClosingAdapter);
		dpl.removeProbeListListener(plListener);
	}
	
	protected void finalizeWork() {
		removeListenersFrom(dpl);
		dpl.setIgnoreChanges(false);
	}
	
	protected void startWork() {
		dpl.setIgnoreChanges(true);
		addListenersTo(dpl);
	}
	

}
