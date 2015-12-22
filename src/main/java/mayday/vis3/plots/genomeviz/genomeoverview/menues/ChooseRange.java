package mayday.vis3.plots.genomeviz.genomeoverview.menues;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.gui.MaydayFrame;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.Controller;
import mayday.vis3.plots.genomeviz.genomeoverview.controllercollection.UserGestures;
@SuppressWarnings("serial")
public class ChooseRange extends AbstractAction {

	protected GenomeOverviewModel model = null;
	protected Controller c;

	protected SpinnerNumberModel numberModel_from = null;
	protected JSpinner spinner_from = null;

	protected SpinnerNumberModel numberModel_to = null;
	protected JSpinner spinner_to = null;

	protected JTextField rangeTextField = null;

	protected long startPosition = 0;
	protected long endPosition = 0;
	protected ChromeModelListener cml = null;
	
	protected String ug = null;
	protected JButton selectRangeButton;
	/**
	 * Frame with Title.
	 * @param text
	 */
	public ChooseRange(String text, GenomeOverviewModel ChromeModel,
			Controller C, String Ug) {
		super(text);
		
		model = ChromeModel;
		c = C;
		cml = new ChromeModelListener();
		model.addRangeListener(cml);
		ug = Ug;
		
		resetPositions();

		rangeTextField = new JTextField();
		rangeTextField.setEditable(false);
		rangeTextField.setPreferredSize(new Dimension(300, 30));
		setSelectionRangeText();
		
		selectRangeButton = new JButton("Select range");
		selectRangeButton.addActionListener(c);
		
		if(ug==UserGestures.SELECT_RANGE_FOR_DETAILS)
			selectRangeButton.setActionCommand(UserGestures.SELECT_RANGE_FOR_DETAILS);
		else if(ug==UserGestures.SELECT_RANGE_TO_VIEW)
			selectRangeButton.setActionCommand(UserGestures.SELECT_RANGE_TO_VIEW);

	}
	
	protected void resetPositions(){
		startPosition = model.getChromosomeStart();
		endPosition = model.getChromosomeEnd();

		numberModel_from = new SpinnerNumberModel(startPosition, startPosition,
				endPosition - 1, 1);
		numberModel_from.addChangeListener(new SpinnerChangeListener_From());
		
		model.setFromPosition_RangeSelection(startPosition);
		
		if (startPosition + 1 <= endPosition) {
			numberModel_to = new SpinnerNumberModel(startPosition + 1,
					startPosition, endPosition, 1);
			model.setToPosition_RangeSelection(startPosition + 1);
		} else {
			numberModel_to = new SpinnerNumberModel(startPosition,
					startPosition, endPosition, 1);
			model.setToPosition_RangeSelection(startPosition);
		}
		
		numberModel_to.addChangeListener(new SpinnerChangeListener_To());
		
	}

	public void actionPerformed(ActionEvent e) {
		resetPositions();
		setSelectionRangeText();
		
		
		MaydayFrame chooseProbesForNewWindow = new MaydayFrame(
				"Select range for new window");
		chooseProbesForNewWindow.addWindowListener(c);
	
		GridBagLayout gbl = new GridBagLayout();
		chooseProbesForNewWindow.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(1, 1, 1, 1);
		gbc.gridx = 0; // x-Position im gedachten Gitter
		gbc.gridy = 0; // y-Position im gedachten Gitter
		gbc.gridheight = 1; // zwei Gitter-Felder hoch
		gbc.gridwidth = 4;
		JLabel find = new JLabel("Select Range:");
		gbl.setConstraints(find, gbc);
		chooseProbesForNewWindow.add(find);

		gbc.gridx = 0; // x-Position im gedachten Gitter
		gbc.gridy = 1; // y-Position im gedachten Gitter
		gbc.gridheight = 1; // zwei Gitter-Felder hoch
		gbc.gridwidth = 1;
		JLabel from = new JLabel("from");
		gbl.setConstraints(from, gbc);
		chooseProbesForNewWindow.add(from);

		gbc.gridx = 1; // x-Position im gedachten Gitter
		gbc.gridy = 1; // y-Position im gedachten Gitter
		gbc.gridheight = 1; // zwei Gitter-Felder hoch
		gbc.gridwidth = 1;
		spinner_from = new JSpinner(numberModel_from);
		spinner_from.setPreferredSize(new Dimension(100, 20));
		gbl.setConstraints(spinner_from, gbc);
		chooseProbesForNewWindow.add(spinner_from);

		gbc.gridx = 2; // x-Position im gedachten Gitter
		gbc.gridy = 1; // y-Position im gedachten Gitter
		gbc.gridheight = 1; // zwei Gitter-Felder hoch
		gbc.gridwidth = 1;
		JLabel to = new JLabel("to");
		gbl.setConstraints(to, gbc);
		chooseProbesForNewWindow.add(to);

		gbc.gridx = 3; // x-Position im gedachten Gitter
		gbc.gridy = 1; // y-Position im gedachten Gitter
		gbc.gridheight = 1; // zwei Gitter-Felder hoch
		gbc.gridwidth = 1;
		spinner_to = new JSpinner(numberModel_to);
		spinner_to.setPreferredSize(new Dimension(100, 20));
		gbl.setConstraints(spinner_to, gbc);
		chooseProbesForNewWindow.add(spinner_to);

		gbc.gridx = 0; // x-Position im gedachten Gitter
		gbc.gridy = 2; // y-Position im gedachten Gitter
		gbc.gridheight = 1; // zwei Gitter-Felder hoch
		gbc.gridwidth = 4;
		gbl.setConstraints(rangeTextField, gbc);
		chooseProbesForNewWindow.add(rangeTextField);

		gbc.gridx = 0; // x-Position im gedachten Gitter
		gbc.gridy = 3; // y-Position im gedachten Gitter
		gbc.gridheight = 1; // zwei Gitter-Felder hoch
		gbc.gridwidth = 4;
				
		gbl.setConstraints(selectRangeButton, gbc);
		chooseProbesForNewWindow.add(selectRangeButton);

		chooseProbesForNewWindow.setSize(new Dimension(200, 400));
		chooseProbesForNewWindow.pack(); // choose size of frame that
		// children of frame fit window
		chooseProbesForNewWindow.setVisible(true);
	}

	public class ChromeModelListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			double from = ChooseRange.this.model
			.getFromPosition_RangeSelection();
			double to = ChooseRange.this.model
			.getToPosition_RangeSelection();
			
			numberModel_from.setValue(from);
			numberModel_to.setValue(to);
			
			setPosition_From();
			setPosition_To();
		}
	}

	public class SpinnerChangeListener_From implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			Double selectedPosition = (Double) numberModel_from.getValue();
			
			model.setFromPosition_RangeSelection(selectedPosition
					.intValue());
			setPosition_From();
		}
	}

	protected class SpinnerChangeListener_To implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			Double selectedPosition = (Double) numberModel_to.getValue();
			model.setToPosition_RangeSelection(selectedPosition
					.intValue());
			setPosition_To();
		}
	}

	public void setPosition_From() {

		double toVal = (Double) numberModel_to.getValue();
		double fromVal = (Double) numberModel_from.getValue();
		if (toVal <= fromVal) {
			if ((Double) numberModel_to.getValue() < endPosition) {
				numberModel_to.setValue(toVal + 1.);
			}
		}
		setSelectionRangeText();
	}

	protected void setPosition_To() {

		double toVal = (Double) numberModel_to.getValue();
		double fromVal = (Double) numberModel_from.getValue();
		if (fromVal>=toVal)
			numberModel_to.setValue(fromVal+1);
		
//		if (toVal <= fromVal) {
//			if ((Double) numberModel_from.getValue() > startPosition) {
//				numberModel_from.setValue(toVal - 1.);
//			}
//		}
		setSelectionRangeText();
	}

	protected void setSelectionRangeText() {
		rangeTextField.setText("Selected range: " + "\n" + " from: "
				+ model.getFromPosition_RangeSelection() + " to "
				+ model.getToPosition_RangeSelection());
	}
}
