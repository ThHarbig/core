package mayday.dynamicpl.dataprocessor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.io.StorageNode;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

public class ProbeMatchesProbeListMedian extends AbstractDataProcessor<Probe, Boolean> implements StorageNodeStorable, OptionPanelProvider, ProbeListListener
{
	private Probe exampleProbe;
	private DistanceMeasurePlugin distanceMeasure;
	private double tolerance=0.0;
	private ProbeList medianSourceList;

	private boolean silent=false;

	protected Boolean convert(Probe value) 
	{
		if(exampleProbe==null) return null;
		if(value==null) return null;
		double dist=distanceMeasure.getDistance(value.getValues(), exampleProbe.getValues());
		//System.out.println(dist+"\t"+ (dist/exampleProbe.getNumberOfExperiments()));
		if(dist <= tolerance) return true;
		return false;
	}


	public Class<?>[] getDataClass() 
	{
		if(exampleProbe==null) return null;
		return new Class[]{Boolean.class};
	}


	public boolean isAcceptableInput(Class<?>[] inputClass) 
	{
		return (Probe.class.isAssignableFrom(inputClass[0]));
	}


	public String toString() 
	{
		if(medianSourceList==null) return "Probe matches unconfigured probelist median";
		return "Probe similar to median of "+medianSourceList.getName();
	}


	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.dynamicPL.filter.QueryByPLMedian",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons, Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Probe matches a probelist median",
				"Probe matches a probelist median"
		);
		return pli;
	}


	public JPanel getOptionPanel() 
	{
		return new ExampleProbeProcessorGUI(this);
	}

	protected void setDistanceMeasure(DistanceMeasurePlugin dist) {
		if (distanceMeasure==null || !distanceMeasure.equals(dist)) {
			distanceMeasure = dist;
			if (!silent)
				fireChanged();
		}
	}

	protected void setTolerance(double tol) {
		if (tol!=tolerance) {
			tolerance = tol;
			if (!silent)
				fireChanged();

		}
	}

	protected void setProbeList(ProbeList pl) {
		if (medianSourceList==null || !medianSourceList.equals(pl)) {
			if (medianSourceList!=null)
				medianSourceList.removeProbeListListener(this);		
			medianSourceList=pl;
			if (pl!=null)  {
				medianSourceList.addProbeListListener(this);
				exampleProbe = pl.getMedian();
			}
			if (!silent)
				fireChanged();
		}			
	}

	protected ProbeList getProbeList() {
		return medianSourceList;
	}

	protected double getTolerance() {
		return tolerance;
	}

	protected DistanceMeasurePlugin getDistanceMeasure() {
		return distanceMeasure;
	}

	protected boolean isSilent() {
		return silent;
	}

	protected void setSilent(boolean b) {
		silent = b;
	}


	private static final String DISTANCE_MEASURE_KEY="distance";
	private static final String DISTANCE_TOLERANCE_KEY="tolerance";
	private static final String PROBELIST_KEY="probelist";

	public void fromStorageNode(StorageNode storageNode) 
	{
		tolerance=Double.parseDouble(storageNode.getChild(DISTANCE_TOLERANCE_KEY).Value);
		String distanceMeasureName=storageNode.getChild(DISTANCE_MEASURE_KEY).Value;		
		distanceMeasure=(DistanceMeasurePlugin)PluginManager.getInstance().getInstance(distanceMeasureName);
		String probeListName=storageNode.getChild(PROBELIST_KEY).Value;
		setProbeList(getDynamicProbeList().getDataSet().getProbeListManager().getProbeList(probeListName));
//		fireChanged();
	}


	public StorageNode toStorageNode() 
	{
		StorageNode parent = new StorageNode("ExampleProbeList","");
		parent.addChild(DISTANCE_MEASURE_KEY, distanceMeasure==null?"*null*":PluginManager.getInstance().getPluginFromClass(distanceMeasure.getClass()).getIdentifier());
		parent.addChild(DISTANCE_TOLERANCE_KEY, tolerance);
		parent.addChild(PROBELIST_KEY, medianSourceList!=null?medianSourceList.getName():"*null*");
		return parent;
	}



	public void probeListChanged(ProbeListEvent event) {
		if (event.getChange()==ProbeListEvent.CONTENT_CHANGE) {
			exampleProbe = ((ProbeList)(event.getSource())).getMedian();
			fireChanged();
		} else if (event.getChange()==ProbeListEvent.PROBELIST_CLOSED) {
			setProbeList(null);
			exampleProbe = null;			
		}
	}

	public void dispose() {
		setProbeList(null);
	}

	@SuppressWarnings("serial")
	public static class ExampleProbeProcessorGUI extends JPanel {

		private ProbeMatchesProbeListMedian processor;
		private JTextField selectedPL = new JTextField(30);
		private AbstractAction selectAction; 


		public ExampleProbeProcessorGUI(ProbeMatchesProbeListMedian epp) {
			super(new ExcellentBoxLayout(true, 3));

			processor = epp;

			JPanel distpnl = new JPanel(new BorderLayout());
			JComboBox distanceMeasureBox= new JComboBox(DistanceMeasureManager.values().toArray());
			distanceMeasureBox.setSelectedItem(DistanceMeasureManager.get("Euclidean"));
			distanceMeasureBox.setEditable(false);
			distanceMeasureBox.setMaximumRowCount(4);

			distanceMeasureBox.addActionListener(new DistanceChangedActionListener());

			distpnl.add(distanceMeasureBox, BorderLayout.CENTER);
			distpnl.add(new JLabel("Distance Measure: "),BorderLayout.WEST);
			distpnl.setMaximumSize(new Dimension(20000,distpnl.getPreferredSize().height));

			add(distpnl);

			JPanel qpnl = new JPanel(new BorderLayout());
			selectedPL.setEditable(false);
			JButton selectPL = new JButton(selectAction = new AbstractAction("Select") {
				public void actionPerformed(ActionEvent e) {
					ProbeListSelectionDialog plsd = new ProbeListSelectionDialog(
							processor.getDynamicProbeList().getDataSet().getProbeListManager()
					);
					plsd.setModal(true);
					plsd.setVisible(true);
					List<ProbeList> mgs = plsd.getSelection();
					if (mgs.size()>0) {
						setProbeList(mgs.get(0));
					} else {
						setProbeList(null);
					}
				}
			});
			qpnl.add(selectedPL, BorderLayout.CENTER);
			qpnl.add(selectPL, BorderLayout.EAST);

			add(qpnl);

			SpinnerNumberModel model=new SpinnerNumberModel(0.0,0.0,100.0,0.1);
			JSpinner spinner=new JSpinner(model);
			model.addChangeListener(new ToleranceChangedActionListener());

			JPanel spinnerBox=new JPanel(new BorderLayout());
			spinnerBox.add(new JLabel("Tolerance: "), BorderLayout.WEST);
			spinnerBox.add(spinner, BorderLayout.CENTER);

			spinnerBox.setMaximumSize(new Dimension(20000,spinnerBox.getPreferredSize().height));

			add(spinnerBox);


			// update from processor

			processor.setSilent(true);

			DistanceMeasurePlugin dmp = processor.getDistanceMeasure();
			if (dmp!=null) {
				distanceMeasureBox.setSelectedItem(dmp);
			} else {
				processor.setDistanceMeasure((DistanceMeasurePlugin)distanceMeasureBox.getSelectedItem());
			}

			double tol = processor.getTolerance();
			spinner.setValue(tol);

			if (processor.getProbeList()!=null)
				selectedPL.setText(processor.getProbeList().getName());
			else
				selectAction.actionPerformed(null);

			processor.setSilent(false);

		}

		private class DistanceChangedActionListener implements ActionListener {
			public void actionPerformed(ActionEvent arg0) 
			{
				JComboBox distanceMeasureBox=(JComboBox)arg0.getSource();
				processor.setDistanceMeasure((DistanceMeasurePlugin)distanceMeasureBox.getSelectedItem());	        
			}
		}

		private class ToleranceChangedActionListener implements ChangeListener {
			public void stateChanged(ChangeEvent e) {
				double tolerance=((SpinnerNumberModel)e.getSource()).getNumber().doubleValue();
				processor.setTolerance(tolerance);				
			}
		}

		public void setProbeList(ProbeList pl) {
			processor.setProbeList(pl);
			if (pl!=null)
				selectedPL.setText(pl.getName());
			else
				selectedPL.setText("-- nothing selected --");
		}




	}


}
