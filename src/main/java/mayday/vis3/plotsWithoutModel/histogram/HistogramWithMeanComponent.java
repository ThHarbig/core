package mayday.vis3.plotsWithoutModel.histogram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class HistogramWithMeanComponent extends JPanel {

	protected HistogramPlotComponent ahist = new HistogramPlotComponent();
	
	public HistogramWithMeanComponent(Dimension minSize) {

		final JLabel meanLabel = new JLabel();
		meanLabel.setOpaque(true);
		meanLabel.setBackground(Color.WHITE);

		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {				
				double m = 0;
				for (Double d : ahist.getValueProvider().getValues()) {
					m+=d;
				}
				m/=ahist.getValueProvider().getValues().size();
				meanLabel.setText("Mean: "+HistogramPlotComponent.formatNumber(m,5));
			}
		};	

		ahist.getValueProvider().addChangeListener(cl);		

		ahist.setPreferredSize(minSize);
		ahist.setMinimumSize(minSize);
		
		setLayout(new BorderLayout());
		
		Box underBox = Box.createHorizontalBox();
		underBox.add(Box.createHorizontalGlue());
		underBox.add(meanLabel);
		underBox.add(Box.createHorizontalGlue());
		underBox.setOpaque(true);
		underBox.setBackground(Color.WHITE);
		
		add(underBox, BorderLayout.SOUTH);		
		add(ahist, BorderLayout.CENTER);

		cl.stateChanged(null);
	}
	
	public HistogramWithMeanComponent() {
		this(new Dimension(200,200));
	}

	public HistogramPlotComponent getHistogramPlotComponent() {
		return ahist;
	}


}
