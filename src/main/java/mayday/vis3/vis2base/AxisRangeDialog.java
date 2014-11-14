package mayday.vis3.vis2base;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class AxisRangeDialog extends MaydayDialog {
	
	private ChartComponent parent;
	
	private AxisSettingsPanel xSettings, ySettings; 
	

	
	public AxisRangeDialog(final ChartComponent parent) {
		setLayout(new BorderLayout());
		getInsets().set(10,10,10,10);
		setTitle("Axis Settings");
		
		this.parent = parent;
		
		String xtitle = parent.getArea().getAxisTitleX();
		String ytitle = parent.getArea().getAxisTitleY();
		
		xSettings = new AxisSettingsPanel("X axis", 0, 0, xtitle);
		ySettings = new AxisSettingsPanel("Y axis", 0, 0, ytitle);
		
		initSettings();
		
		add(xSettings, BorderLayout.NORTH);
		add(ySettings, BorderLayout.CENTER);
		
		JButton bt_ok = new JButton( new OkAction() );
		bt_ok.setAlignmentX(Component.RIGHT_ALIGNMENT);

//		JButton bt_cancel = new JButton( new AbstractAction("Cancel") { 
//			public void actionPerformed(ActionEvent evt) {
//				dispose();
//			} 
//		});

		JButton bt_restore = new JButton( new AbstractAction("Fit content") {
			public void actionPerformed(ActionEvent evt) {				
				parent.getArea().setAutoFocus(true);
				parent.updatePlot();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						initSettings();
					}					
				});
			} 
		});
		
		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());
//		b.add(bt_cancel);
//		b.add(Box.createHorizontalStrut(15));
		b.add(bt_restore);
		b.add(Box.createHorizontalStrut(5));
		b.add(bt_ok);
		
		add(b, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton(bt_ok);
		
		pack();
		
		setVisible(true);
	}
	
	protected void initSettings() {
		double xmin = parent.getArea().getDRectangle().x;
		double xmax = parent.getArea().getDRectangle().width + parent.getArea().getDRectangle().x;
		xSettings.updateValues(xmin, xmax);
		double ymin = parent.getArea().getDRectangle().y;
		double ymax = parent.getArea().getDRectangle().height + parent.getArea().getDRectangle().y;
		ySettings.updateValues(ymin, ymax);
	}
	
	public class OkAction extends AbstractAction
	{
		public OkAction() {
			super("Apply");
		}
		public void actionPerformed(ActionEvent arg0) 
		{
			if (xSettings.useInput() && ySettings.useInput()) {
				parent.getArea().setVisibleRectangle(xSettings.min, ySettings.min, 
						xSettings.max-xSettings.min, ySettings.max-ySettings.min);
				parent.getArea().setAxisTitle(xSettings.title, ySettings.title);
				parent.updatePlot();
			}
		}
		
	}
	
	protected class AxisSettingsPanel extends JPanel {
	
		public double min, max;
		public String title;
		protected JTextField minField, maxField, titleField;
		
		public AxisSettingsPanel(String name, double Min, double Max, String Title) {
			setBorder(BorderFactory.createTitledBorder(name));
			title=Title;
			min=Min;
			max=Max;
			JLabel minLabel = new JLabel("Minimum");
			JLabel maxLabel = new JLabel("Maximum");
			JLabel titleLabel = new JLabel("Title");
			minField = new JTextField(min+"");
			maxField = new JTextField(max+"");
			titleField = new JTextField(title);
			setLayout(new GroupLayout(this));
			GroupLayout gl = ((GroupLayout)getLayout());
			gl.setHorizontalGroup(gl.createSequentialGroup()
					.addGroup(gl.createParallelGroup()
							.addComponent(minLabel)
							.addComponent(maxLabel)
							.addComponent(titleLabel)
							)
					.addGroup(gl.createParallelGroup()
							.addComponent(minField)
							.addComponent(maxField)
							.addComponent(titleField)
							)
					);
			gl.setVerticalGroup(gl.createSequentialGroup()
					.addGroup(gl.createParallelGroup()
							.addComponent(minLabel)
							.addComponent(minField)
							)
					.addGroup(gl.createParallelGroup()						
							.addComponent(maxLabel)
							.addComponent(maxField)
							)
					.addGroup(gl.createParallelGroup()						
							.addComponent(titleLabel)
							.addComponent(titleField)
							)
					);		
		}
		
		public boolean useInput() {
			boolean res=false;
			title = titleField.getText();
			try {
				min = Double.parseDouble(minField.getText().trim());
				max = Double.parseDouble(maxField.getText().trim());
				res = true;
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, "Axis minimum/maximum is not valid", "Check your input", JOptionPane.ERROR_MESSAGE, null);
			}
			return res;
		}
		
		public void updateValues(double Min, double Max) {
			min = Min;
			max = Max;
			minField.setText(min+"");
			maxField.setText(max+"");
		}
		
	}
}
