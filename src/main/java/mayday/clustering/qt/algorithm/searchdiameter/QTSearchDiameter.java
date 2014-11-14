package mayday.clustering.qt.algorithm.searchdiameter;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.WindowConstants;

import mayday.clustering.qt.QTPClusterPlugin;
import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.structures.linalg.matrix.AbstractMatrix;
import mayday.core.tasks.AbstractTask;
import mayday.clustering.qt.algorithm.QTPMath;
import mayday.clustering.qt.algorithm.QTPSettings;

/**
 * @author Sebastian Nagel
 * @author G&uuml;nter J&auml;ger
 * @version 0.1
 */
@SuppressWarnings("serial")
public class QTSearchDiameter extends MaydayDialog {
	private QTPSettings settings = null;
	private QTDiameterPlot plot;
	private double[] distances;
	private int[] distribution;
	private double distancesMaxValue;
	private int distributionMaxValue;

	private List<ProbeList> probeLists;
	private MasterTable masterTable;

	/**
	 * @param matrix
	 */
	public QTSearchDiameter(AbstractMatrix matrix) {
		this.setTitle("Search threshold diameter");
		this.plot = new QTDiameterPlot(matrix, this);
		this.add(this.plot);

		this.plot.getOK().setAction(new RunAction());
		this.plot.getCancel().setAction(new CancelAction());

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.pack();
	}

	/**
	 * Calculate new distance and distribution values 
	 * and update plotRegion
	 */
	public void updateValues() {
		//update in a new task
		AbstractTask updateTask = new AbstractTask("Updating Search Diameter Window ...") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				try {
					plot.setEnabled(false);
					try {
						long startTime = System.currentTimeMillis();
						
						double[] allDistances = getDistances();
						distances = QTPMath.removeDuplicatedEntries(allDistances);
						distribution = QTStatistics.distribution(distances.length, allDistances);
						distancesMaxValue = QTPMath.getArrayMax(distances);
						distributionMaxValue = QTPMath.getArrayMax(distribution);
						
						long timeRequired = ((System.currentTimeMillis() - startTime)/1000);
						this.writeLog("time required: " + timeRequired + " sec");
						showPlot();
					} catch(OutOfMemoryError e) {
						this.writeLog("Out of memory: try smaller subset");
						this.cancel();
						return;
					}
				} finally {
					plot.setEnabled(true);
				}
			}
		};

		updateTask.start();
	}

	private double[] getDistances() throws OutOfMemoryError {
		final DistanceMeasurePlugin measure = plot.getDistanceMeasure();
		final AbstractMatrix matrix = plot.getMatrix();
		int n = matrix.nrow() * (matrix.nrow() - 1) / 2;
		final int numberOfDistances = (int)Math.floor(n*plot.getSubset());
		
		if(numberOfDistances > 0) {
			final ArrayList<Double> allDistancesList = new ArrayList<Double>();
			int rows = matrix.nrow();
			int rowsrows=(rows*(rows-1))/2;
			final int spaceCount = (int) Math.floor(rowsrows/numberOfDistances);
			
			if (rowsrows<0)
				throw new OutOfMemoryError();
			
			AbstractTask task = new AbstractTask("Calculating distances for qt search diameter plugin ...") {
				@Override
				protected void initialize() {}
				
				@Override
				protected void doWork() throws Exception {
					ArrayList<QTSearchDiameterThread> listOfThreads = new ArrayList<QTSearchDiameterThread>();
					int subCount = (int) Math.floor(numberOfDistances / plot.getThreadCount());
					
					for (int i=1; i<=plot.getThreadCount(); i++) {
						QTSearchDiameterThread thread;
						
						if (i!=plot.getThreadCount())
							thread = new QTSearchDiameterThread(
									measure, matrix, (i-1)*subCount+1, i*subCount, spaceCount);
						else
							thread = new QTSearchDiameterThread(
									measure, matrix, (i-1)*subCount+1, numberOfDistances, spaceCount);
						
						thread.execute();
						listOfThreads.add(thread);
					}
					
					while (true) {
						boolean done = true;
						
						for (QTSearchDiameterThread thread : listOfThreads) 
								done = done && thread.isDone();
						
						if (done) {
							for (QTSearchDiameterThread thread : listOfThreads) 
								allDistancesList.addAll(thread.getDistances());
							break;
						}
					}
				}
			};
			task.start();
			task.waitFor();
			
			double[] allDistances = QTPMath.toArray(allDistancesList);
			Arrays.sort(allDistances);
			QTPMath.round(allDistances);
			return allDistances;
		}
		return null;
	}

	/**
	 * display SearchDiameter-Plot
	 */
	public void showPlot() {
		if (distances==null)
			updateValues();
		
		this.plot.getDrawRegion().updatePlot(this.distances, this.distribution, 
				this.distancesMaxValue, this.distributionMaxValue);
	}

	protected class RunAction extends AbstractAction {
		/**
		 * Default Constructor
		 * Defines an action named "Run"
		 */
		public RunAction() {
			super("Induce QTP-Clust");
		}

		public void actionPerformed(ActionEvent e) {
			settings = new QTPSettings();
			settings.setDistanceMeasure(plot.getDistanceMeasure());			
			Double diameter = plot.getDiameter();
			if (settings.getChild(QTPSettings.DIAMETER, true).isValidValue(diameter.toString())) {
				settings.setDiameterThreshold(diameter);
				dispose();
			}

			QTPClusterPlugin pl = new QTPClusterPlugin();
			ProbeListPluginRunner runner = new ProbeListPluginRunner(pl.getPluginInfo()) {
				protected void runPlugin() {
					ProbelistPlugin ppl = (ProbelistPlugin)(pli.getInstance());
					((QTPClusterPlugin)ppl).setSetting(settings);
					List<ProbeList> results = ppl.run( QTSearchDiameter.this.probeLists, QTSearchDiameter.this.masterTable );
					//remove empty lists
					if (results!=null) {
						LinkedList<ProbeList> res2 = new LinkedList<ProbeList>();
						for (ProbeList p : results)
							if (p.getNumberOfProbes()>0)
								res2.add(p);
						results=res2;
					}
					insertIntoProbeListManager(results);
				}
			};
			runner.execute();
		}
	}

	protected class CancelAction extends AbstractAction {
		/**
		 * Default Constructor
		 * Defines an action named "Cancel"
		 */
		public CancelAction() {
			super("Cancel");
		}

		public void actionPerformed(ActionEvent e) {
			settings = null;
			dispose();
		}
	}

	/**
	 * @param probeLists
	 */
	public void setProbeLists(List<ProbeList> probeLists) {
		this.probeLists = probeLists;
	}

	/**
	 * @param masterTable
	 */
	public void setMasterTable(MasterTable masterTable) {
		this.masterTable = masterTable;
	}

	/**
	 * @return QTSettings
	 */
	public QTPSettings getSettings() {
		return this.settings;
	}
}
