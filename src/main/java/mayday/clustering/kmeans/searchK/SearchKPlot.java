package mayday.clustering.kmeans.searchK;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import mayday.clustering.ClusterAlgorithms;
import mayday.clustering.ClusterPlugin;
import mayday.clustering.ClusterTask;
import mayday.clustering.kmeans.KCentroidsClustering;
import mayday.clustering.kmeans.KMeansClustering;
import mayday.clustering.kmeans.KMeansPlugin;
import mayday.clustering.kmeans.KMeansSetting;
import mayday.core.MasterTable;
import mayday.core.Mayday;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;
import mayday.core.math.average.AverageType;
import mayday.core.math.clusterinitializer.IClusterInitializer;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.structures.linalg.matrix.PermutableMatrix;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.tasks.AbstractTask;

/**
 * 
 * @author jaeger
 * @date 19.11.2013
 *
 */
public class SearchKPlot extends MaydayDialog implements SearchKChangeListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3940599866832404350L;
	
	private SearchKSetting setting;
	
	private List<ProbeList> probeLists;
	private MasterTable masterTable;
	
	private JTextField chosenKField;
	private SearchKDrawPanel drawPanel;
	
	public SearchKPlot(SearchKSetting setting, List<ProbeList> probeLists, MasterTable masterTable) {
		this.setting = setting;
		
		this.probeLists = probeLists;
		this.masterTable = masterTable;
		
		this.setTitle("Find optimal k for k-Means clustering");
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(640, 480));
		this.initializeWidgets();
		
		pack();
	}
	
	public void initializeWidgets() {
		this.drawPanel = new SearchKDrawPanel();
		this.drawPanel.addChangeListener(this);
		this.add(drawPanel, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton runKMeansButton = new JButton("Run k-Means");
		runKMeansButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final KMeansSetting kmSetting = new KMeansSetting();
				
				kmSetting.initializeSetting(SearchKPlot.this.setting);
				
				KMeansPlugin kmeansPlugin = new KMeansPlugin();
				ProbeListPluginRunner runner = new ProbeListPluginRunner(kmeansPlugin.getPluginInfo()) {
					protected void runPlugin() {
						ProbelistPlugin ppl = (ProbelistPlugin)(pli.getInstance());
						((KMeansPlugin)ppl).setSettings(kmSetting);
						List<ProbeList> results = ppl.run( SearchKPlot.this.probeLists, SearchKPlot.this.masterTable );
						//remove empty lists
						if (results != null) {
							LinkedList<ProbeList> res2 = new LinkedList<ProbeList>();
							for (ProbeList p : results)
								if (p.getNumberOfProbes() > 0)
									res2.add(p);
							results = res2;
						}
						
						insertIntoProbeListManager(results);
					}
				};
				
				runner.execute();
				
				dispose();
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		JButton settingButton = new JButton("Change settings");
		settingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingDialog sd = new SettingDialog(Mayday.sharedInstance, "Search k for k-Means Setting", setting);
				sd.showAsInputDialog();
				
				if(!sd.closedWithOK()) {
					return;
				}
				
				switchWaitingScreen();
				calculate();
			}
		});
		
		buttonPanel.add(settingButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(runKMeansButton);
		
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JPanel informationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel chosenKLabel = new JLabel("Chosen k:");
		this.chosenKField = new JTextField(10);
		this.chosenKField.setEditable(false);
		this.chosenKField.setText("1");
		
		informationPanel.add(chosenKLabel);
		informationPanel.add(chosenKField);
		
		bottomPanel.add(informationPanel, BorderLayout.NORTH);
		
		this.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void switchWaitingScreen() {
		this.drawPanel.triggerPlotWaiting();
	}

	public void calculate() {
		AbstractTask task = new AbstractTask("Find optimal k for k-means clustering") {

			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				DoubleVector data = new DoubleVector(setting.getMaxCluster());
				
				// new expression matrix
				PermutableMatrix matrix = ClusterPlugin.getClusterData(probeLists, masterTable);

				//Get all the information for k means
				int maxk = setting.getMaxCluster() + 1;
				
				if(maxk < 2)
					return;

				int cycles = setting.getCycleCount();
				double threshold = setting.getErrorThreshold();
				DistanceMeasurePlugin distMeasure = setting.getDistanceMeasure();
				IClusterInitializer initCluster = setting.getInitializer().createInstance();

				ClusterAlgorithms kmeans = null;
				
				for(int i = 2; i <= maxk; i++){
					//create k-means clustering object and just use the much faster one
					if(setting.getCentroidAlgorithm().getName().equals(AverageType.MEAN.getName())) {
						kmeans = new KMeansClustering(matrix, i, cycles, threshold, distMeasure, initCluster);
					} else {
						kmeans= new KCentroidsClustering(matrix, i, cycles, threshold, setting.getCentroidAlgorithm().createInstance(), distMeasure, initCluster);
					}

					//Use a concurrent thread for clustering via class ClusterTask:
				    ClusterTask clTask = new ClusterTask("K-means clustering");
				    clTask.setClAlg(kmeans);
				    
				  	//start the process
				  	clTask.start();

				  	// wait for completion
				  	clTask.waitFor();

				    // get cluster results
				    int[] result = clTask.getClResult();

				    //first compute the #experiments dimensional mean for each of the i clusters
				    double[][] means = new double[i][masterTable.getNumberOfExperiments()];

				    //now very inefficiently go i times through the complete result array
				    //thus computing the means
				    for(int j = 0; j < i; j++) {
				    	int count = 0;
				    	for(int k = 0; k < result.length; k++) {
				    		if(result[k]==j) {
				    			count++;
				    			//add probe vector to means of cluster
				    			for(int l = 0; l < masterTable.getNumberOfExperiments(); l++){
				    				means[j][l] += matrix.getValue(k,l);
				    			}
				    		}
				    	}
				    	//now calculate the mean for cluster j
				    	for(int l = 0; l < masterTable.getNumberOfExperiments(); l++){
		    				means[j][l] /= count;
		    			}
				    }

				    double dist = 0;
				    //now compute the tWCSS (quadratic euclidic distance
			    	for(int k=0; k<result.length;k++){
			    		for(int l=0; l<masterTable.getNumberOfExperiments();l++){
			    			dist+=Math.pow((matrix.getValue(k,l)-means[result[k]][l]),2);
			    		}
			    	}

				    //Remember best K to output it at the end
				    data.set((i-2), dist);

				    if (!hasBeenCancelled()) { // report progress status if necessary
				    	setProgress((int)Math.rint((double) (i-2) / (maxk-2) * 10000));
				    } else {
				    	return;
				    }
				}

				drawPanel.setSetting(setting);
				drawPanel.updatePlot(data);

				switchWaitingScreen();
				updatePlot();
			}
		};
		
		task.start();
	}
	
	private void updatePlot() {
		this.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.chosenKField.setText(Integer.toString((Integer)e.getSource()));
		this.chosenKField.revalidate();
	}
	
	public void removeNotify() {
		this.drawPanel.removeChangeListener(this);
		super.removeNotify();
	}
}
