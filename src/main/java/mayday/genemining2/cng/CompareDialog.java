package mayday.genemining2.cng;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import mayday.clustering.hierarchical.HierarchicalClustering;
import mayday.clustering.hierarchical.HierarchicalClusterSettings;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayFrame;
import mayday.core.gui.components.VerticalLabel;
import mayday.core.math.scoring.ScoringResult;
import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.structures.trees.tree.Node;
import mayday.genemining2.GeneminingSettings;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class CompareDialog extends MaydayFrame{

	private static final long serialVersionUID = 1L;
	private CompareViewer dialog;
	
	private JButton cancelButton;
	//private JButton stopButton;
	JProgressBar progressBar;
	private JLabel informationText;
	
	private JLabel xlabel;
	private VerticalLabel ylabel;
	
	private int addedResults = 0;
	private int numberOfResults;
	private boolean stop = false;
	
	private static HierarchicalClusterSettings clusterSettings;
	private static GeneminingSettings gmSettings;
	private ScoringResult result;
	
	/**
	 * @param numberOfResults
	 * @param gmSettings 
	 * @param clusterSettings
	 */
	public CompareDialog(GeneminingSettings gmSettings, HierarchicalClusterSettings clusterSettings) {
		this.setTitle("CompareViewer");
		CompareDialog.clusterSettings = clusterSettings;
		CompareDialog.gmSettings = gmSettings;
		
		Bipartition bipartition = new Bipartition();
		if(clusterSettings != null) {
			bipartition = new Bipartition(clusterSettings.getBipartition());
		}
		
		this.numberOfResults = gmSettings.getNumberOfGenes() - gmSettings.getCNG();
		this.dialog = new CompareViewer(this.numberOfResults, bipartition, this);

		this.initializeWidgets();
	}
	
	private void initializeWidgets() {
		this.setLayout(new BorderLayout());
		JPanel infoPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel progressPanel = new JPanel();
		
		//initialize draw region
		JScrollPane scrollPane = new JScrollPane(dialog, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		dialog.setPreferredSize(new Dimension(Math.max(500, (this.numberOfResults + 1) * 3), 400));
		scrollPane.setPreferredSize(new Dimension(500, 400));
		//initialize information text
		this.informationText = new JLabel("Use the mouse to select the number of genes from the image above!", JLabel.CENTER);
		
		this.xlabel = new JLabel("Number of genes", JLabel.CENTER);
		
		this.ylabel = new VerticalLabel("Separation Length", VerticalLabel.CENTER);
		
		infoPanel.setLayout(new BorderLayout());
		infoPanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel xpanel = new JPanel(new BorderLayout());
		xpanel.add(xlabel, BorderLayout.NORTH);
		xpanel.add(informationText, BorderLayout.SOUTH);
		
//		infoPanel.add(this.informationText, BorderLayout.SOUTH);
		infoPanel.add(this.ylabel, BorderLayout.WEST);
		infoPanel.add(xpanel, BorderLayout.SOUTH);
		
		//initialize button panel
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		this.cancelButton = new  JButton(new AbstractAction("Close") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed( ActionEvent event ) {
				stop = true;
				dispose();
			}
		} );
		//this.stopButton = new  JButton(new StopAction());
		//buttonPanel.add(this.stopButton);
		buttonPanel.add(this.cancelButton);
		
		//initialize progress bar
		this.progressBar = new JProgressBar();
		this.progressBar.setMaximum(numberOfResults);
		this.progressBar.setStringPainted(true);
		this.progressBar.setPreferredSize(new Dimension(600, 20));
		
		progressPanel.add(this.progressBar);
		
		//add everything to this component
		this.add(infoPanel, BorderLayout.NORTH);
		this.add(progressPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		this.pack();
		this.setResizable(false);
	}
	
	/**
	 * @param text
	 */
	public void updateInformationText(String text) {
		this.informationText.setText(text);
	}
	/*
	private class StopAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		StopAction() {
			super("Stop");
		}
		
		public void actionPerformed( ActionEvent event ) {
			stop = !stop;
			if ( stop ) {
				stopButton.setText("Continue");
			} else {
				stopButton.setText("Stop");
				run();
			}
		}
	}
	*/
	
	/**
	 * 
	 */
	public void run() {
		CompareThread task = new CompareThread("Gene Mining");
   		task.start();
	}
	
	private class CompareThread extends Thread {
        public CompareThread(String s) { 
          	super(s); 
        };
            
        public void run() {
            List<ProbeList> probeLists = new ArrayList<ProbeList>();
    		
    		for (int i= gmSettings.getNumberOfGenes() - addedResults; i>= gmSettings.getCNG() && !stop; --i) {
            	probeLists.clear();
            	
            	ProbeList actResult = getTop(result, i, gmSettings.getMasterTable());
            	probeLists.add(actResult);
            	
            	if(clusterSettings != null) {
            		HierarchicalClustering treeGenerator = new HierarchicalClustering();
            		Node tree = treeGenerator.getTreeOutOfSettings(probeLists, gmSettings.getMasterTable(), clusterSettings);
            		if (tree!=null) {
            			Bipartition bipartition = clusterSettings.getBipartition().searchInTree(tree);
            			if ( bipartition != null ) {
            				if ( bipartition.equals( clusterSettings.getBipartition() ) ) {
            					addBranch(bipartition.getIncomingBranchLength(), i);
            				} else {
            					addEqualBranch(bipartition.getIncomingBranchLength(), i);
            				}
            			} else {
            				addBranchNotFound( i );
            			}
            		}
            	}
            }
        }
	}
	
	/**
	 * @param res
	 */
	public void setCurrentScoringResult(ScoringResult res) {
		this.result = res;
	}
	
	private static ProbeList getTop(ScoringResult result, int numGenes,
			MasterTable masterTable) {
		ProbeList pl = new ProbeList(masterTable.getDataSet(), true);

		Gene[] ranking = doRanking(result);
		// return all probes if numGenes is too large
		if (ranking.length < numGenes)
			numGenes = ranking.length;
		
		// get top genes and put them into the corresponding probeList
		for (int k = 0; k < numGenes; k++) {
			pl.addProbe((Probe) ranking[k].o);
		}
		
		return pl;
	}
	
	private static Gene[] doRanking(
			ScoringResult res) {
		int size = 0;
		Gene[] ranking = new Gene[0];
		
		if(res.hasRawScore()){
			size = res.getRawScore().size();
			ranking = new Gene[size];
			int i = 0;
			for(Object o:res.getRawScore().getObjects()){
				double val = ((DoubleMIO)res.getRawScore().getMIO(o)).getValue().doubleValue();
				ranking[i] = new Gene(o, val);
				i++;
			}
			Arrays.sort(ranking);
		} else if(res instanceof StatTestResult){
			size = ((StatTestResult)res).getPValues().size();
			ranking = new Gene[size];
			int i = 0;
			for(Object o:((StatTestResult)res).getPValues().getObjects()){
				double val = ((DoubleMIO)((StatTestResult)res).getPValues().getMIO(o)).getValue().doubleValue();
				ranking[i] = new Gene(o, val);
				i++;
			}
			Arrays.sort(ranking);
		}
		
		for (int i = 0; i < ranking.length / 2; i++) {
			Gene tmp = ranking[i];
			ranking[i] = ranking[ranking.length - 1 - i];
			ranking[size - 1 - i] = tmp;
		}
		
		return ranking;
	}

	private void addBranch(double branchLength, int numberOfGenes) {
		dialog.addBranch(branchLength, numberOfGenes);
		this.addResult();
	}
	
	private void addBranchNotFound(int numberOfGenes) {
		dialog.addBranchNotFound(numberOfGenes);
		this.addResult();
	}
	
	private void addEqualBranch(double branchLength, int numberOfGenes) {
		dialog.addEqualBranch(branchLength, numberOfGenes);
		this.addResult();
	}
	
	private void addResult() {
		++this.addedResults;
		progressBar.setValue( this.addedResults );
		if (addedResults>=numberOfResults) {
			progressBar.setVisible(false);
			invalidate();
			validate();
		}
	}
	
	/*
	 * helper class, that simplifies ranking of genes
	 */
	private static class Gene implements Comparable<Gene> {
		public Object o;
		public double value;

		public Gene(Object o, double value) {
			this.o = o;
			this.value = value;
		}

		@Override
		public int compareTo(Gene g) {
			if (value < g.value)
				return -1;
			if (value > g.value)
				return 1;
			return 0;
		}
	}

	/**
	 * @param result
	 * @param numberOfGenes
	 */
	public static void generateNewTree(ScoringResult result, int numberOfGenes) {
		ProbeList probeList = getTop(result, numberOfGenes, gmSettings.getMasterTable());
		HierarchicalClustering newView = new HierarchicalClustering();
		List<ProbeList> probeLists = new ArrayList<ProbeList>();
		probeLists.add( probeList );
		newView.runWithSettings(probeLists, gmSettings.getMasterTable(), clusterSettings);
	}
}
