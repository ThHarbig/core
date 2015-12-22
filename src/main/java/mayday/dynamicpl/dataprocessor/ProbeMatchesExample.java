package mayday.dynamicpl.dataprocessor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.Probe;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.io.StorageNode;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.gui.OptionPanelProvider;
import mayday.dynamicpl.miostore.StorageNodeStorable;

public class ProbeMatchesExample extends AbstractDataProcessor<Probe, Boolean> implements StorageNodeStorable, OptionPanelProvider
{
	private Probe exampleProbe;
	private DistanceMeasurePlugin distanceMeasure;
	private double tolerance=0.0;

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
		if(exampleProbe==null) return "(!) Probe matches unconfigured example probe";
		return "Probe similar to "+exampleProbe.getName();
	}


	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.dynamicPL.filter.QueryByExample",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Query by example",
				"Probe matches an example probe"
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

	protected void setExampleProbe(Probe pb) {
		if (exampleProbe==null || !exampleProbe.equals(pb)) {
			exampleProbe = pb;
			if (!silent)
				fireChanged();
		}
	}

	protected Probe getExampleProbe() {
		return exampleProbe;
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
	private static final String PROBE_KEY="threshold";

	public void fromStorageNode(StorageNode storageNode) 
	{
		tolerance=Double.parseDouble(storageNode.getChild(DISTANCE_TOLERANCE_KEY).Value);
		String distanceMeasureName=storageNode.getChild(DISTANCE_MEASURE_KEY).Value;		
		distanceMeasure=(DistanceMeasurePlugin)PluginManager.getInstance().getInstance(distanceMeasureName);
		String probeName=storageNode.getChild(PROBE_KEY).Value;
		exampleProbe=getDynamicProbeList().getDataSet().getMasterTable().getProbe(probeName);
		fireChanged();
	}


	public StorageNode toStorageNode() 
	{
		StorageNode parent = new StorageNode("ExampleProbeList","");
		parent.addChild(DISTANCE_MEASURE_KEY, distanceMeasure==null?"*null*":PluginManager.getInstance().getPluginFromClass(distanceMeasure.getClass()).getIdentifier());
		parent.addChild(DISTANCE_TOLERANCE_KEY, tolerance);
		parent.addChild(PROBE_KEY, exampleProbe!=null?exampleProbe.getName():"*null*");
		return parent;
	}



	@SuppressWarnings("serial")
	public static class ExampleProbeProcessorGUI extends JPanel {

		private JTextField query;
		private JList probes;
		private ProbeMatchesExample processor;

		@SuppressWarnings("unchecked")
		public ExampleProbeProcessorGUI(ProbeMatchesExample epp) {
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
			query=new JTextField(25);
			query.setText("");
			query.getDocument().addDocumentListener(new SearchListener());
			query.setMaximumSize(new Dimension(20000,query.getPreferredSize().height));
			qpnl.add(new JLabel("Probe search: "), BorderLayout.WEST);
			qpnl.add(query, BorderLayout.CENTER);
			qpnl.setMaximumSize(new Dimension(20000,qpnl.getPreferredSize().height));

			add(qpnl);

			Vector<Probe> vprobes = new Vector<Probe>(epp.getDynamicProbeList().getDataSet().getMasterTable().getProbes().values());
			Collections.sort(vprobes);

			probes=new JList(vprobes);

			probes.setVisibleRowCount(8);
			probes.addListSelectionListener(new ProbeChangedListener());
			JScrollPane scroller=new JScrollPane(probes);

			add(scroller);

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

			Probe example = processor.getExampleProbe();
			if (example!=null)
				probes.setSelectedValue(example, true);       
			else 
				probes.setSelectedIndex(0);

			processor.setSilent(false);

		}


		private void search() 	{
			String s = query.getText();
			if (s.length() <= 0) {
				return;
			}
			for(int i=0; i!= probes.getModel().getSize(); ++i)
			{
				try {
					if( probes.getModel().getElementAt(i).toString().matches(".*"+s+".*"))
					{
						probes.ensureIndexIsVisible(i);
						probes.setSelectedIndex(i);
						break;        		
					}
				} catch (Exception e) {}
			}
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

		private class ProbeChangedListener implements ListSelectionListener
		{
			public void valueChanged(ListSelectionEvent e) 
			{			
				Probe example = ((Probe)((JList)e.getSource()).getSelectedValue());
				processor.setExampleProbe(example);
			}		
		}

		private class SearchListener implements DocumentListener {

			public void changedUpdate(DocumentEvent e) {}

			public void insertUpdate(DocumentEvent e) {
				search();		
			}

			public void removeUpdate(DocumentEvent e) {
				search();				
			}		
		}




	}


}
