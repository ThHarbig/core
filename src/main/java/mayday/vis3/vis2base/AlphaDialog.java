package mayday.vis3.vis2base;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;

import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class AlphaDialog extends MaydayDialog {
	
	private JSlider sl_alpha;
	private JButton bt_ok;
	private ChartComponent parent;
	
	public AlphaDialog(ChartComponent parent) {
		setLayout(new BorderLayout());
		getInsets().set(10,10,10,10);
		setTitle("Transparancy (alpha value)");
		
		this.parent = parent;
		
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		for(int i=0; i <= 5; i++)
			labels.put(20*i, new JLabel(20*i+"%"));			
		sl_alpha = new JSlider(0, 100, (int)(100 * parent.farea.getAlpha()));
		sl_alpha.setLabelTable(labels);
		sl_alpha.setPaintLabels(true);
		sl_alpha.setSize(200, 30);
		
		bt_ok = new JButton( new OkAction() );
		bt_ok.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());
		b.add(bt_ok);
		
		add(new JLabel("Please set up the alpha value:"), BorderLayout.NORTH);
		add(sl_alpha, BorderLayout.CENTER);
		add(b, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(bt_ok);
		
		pack();
		
		setVisible(true);
	}
	
	public float getAlpha()
	{
		return (float) sl_alpha.getValue()/100;
	}

	
	public class OkAction extends AbstractAction
	{
		public OkAction() {
			super("OK");
		}
		public void actionPerformed(ActionEvent arg0) 
		{
			if (getAlpha()>99) {
				parent.farea.setAntalias(false);
			} else {
				parent.farea.setAntalias(true);
				parent.farea.setAlpha(getAlpha());
			}
			parent.updatePlot();
			dispose();			
		}
		
	}
}
