package mayday.genemining2.cng;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import mayday.core.math.scoring.ScoringResult;


/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class CompareViewer extends JComponent implements MouseListener, MouseMotionListener  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1622189454081486751L;

	private static final int width  = 500;
	private static final int height = 400;
	
	private List<BranchData> dataList = new ArrayList<BranchData>();
	private double originalBranchLength = 0.1;
	
	private int selectedBar = -1;
	private int barLength = 3;
	private double bestLength;
	private double scale;
	
	private Bipartition bipartition;
	private CompareDialog dialog;
	
	/**
	 * @param numberOfResults
	 * @param bipartition 
	 * @param dialog 
	 */
	public CompareViewer(int numberOfResults, Bipartition bipartition, CompareDialog dialog) {
		//setSize( width, height );
		
		this.bipartition = new Bipartition(bipartition);
		this.dialog = dialog;
		
	 	if ( this.bipartition.getNode() != null) {
		 	this.originalBranchLength = this.bipartition.getIncomingBranchLength();
	 	}
	 	this.scale = (double)height / (double)2 / (double)this.originalBranchLength;
	 	this.barLength = width / numberOfResults;
	 	
	 	if (this.barLength < 3) {
	 		this.barLength = 3;
	 	}
	 	
	 	this.bestLength = this.originalBranchLength * 2;
	 	
	 	this.addMouseListener(this);
	 	this.addMouseMotionListener(this);
	}
	
	/**
	 * @param branchLength
	 * @param numberOfGenes
	 */
	public void addBranch(double branchLength, int numberOfGenes) {
		if (branchLength > this.bestLength) {
			this.bestLength = branchLength;
			this.scale = height / (double)this.bestLength;
		}
		BranchData newData = new BranchData();
		newData.length        = branchLength;
		newData.numberOfGenes = numberOfGenes;
		this.dataList.add(newData);
		this.repaint();
	}
	
	/**
	 * @param numberOfGenes
	 */
	public void addBranchNotFound(int numberOfGenes) {
		BranchData newData = new BranchData();
		newData.notFound = true;
		newData.numberOfGenes = numberOfGenes;
		this.dataList.add(newData);
		this.repaint();
	}
	
	/**
	 * @param branchLength
	 * @param numberOfGenes
	 */
	public void addEqualBranch(double branchLength, int numberOfGenes) {
		if (branchLength > this.bestLength) {
			this.bestLength = branchLength;
			this.scale = height / (double)this.bestLength;
		}
		BranchData newData = new BranchData();
		newData.notFound = true;
		newData.numberOfGenes = numberOfGenes;
		newData.length = branchLength;
		this.dataList.add(newData);
		this.repaint();
	}
	
	public void paint( Graphics g ) { 
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont( new Font("SansSerif", Font.BOLD, 16) );
		int size = dataList.size();
		for (int i = 0; i < size; ++i) {
			if (!dataList.get(i).notFound) {
				if (i == selectedBar) {
					g2.setColor(Color.yellow);
				} else  {
					if (i%2 == 0) {
						g2.setColor(Color.blue.darker());
					} else {
						g2.setColor(Color.blue.brighter());
					}
				}
				int length = (int)(dataList.get(i).length * scale);
				g2.fillRect(i * barLength, height - length, barLength, length);
			} else {
				if ( i == selectedBar) {
					g2.setColor(Color.yellow);
				} else {
					g2.setColor(Color.gray.brighter());
				}
				g2.fillRect(i * barLength, 0, barLength, height);
				
				if ( dataList.get(i).length > 0.0 ) {
					if (i%2 == 0) {
						g2.setColor(new Color(130, 130, 130));
					} else {
						g2.setColor(new Color(100, 100, 100));
					}
					int length = (int)(dataList.get(i).length * scale);
					g2.fillRect(i * barLength, height - length, barLength, length);
				}
			}
		}
		
		if (this.selectedBar != -1 && this.selectedBar < this.dataList.size()) {
			this.dialog.updateInformationText(dataList.get(selectedBar).toString());
		}
		
		if (bipartition.getNode() != null) {
			g2.setColor(Color.red);
			g2.drawLine(0, height-(int)(this.originalBranchLength * scale), 600, height-(int)(this.originalBranchLength * scale));
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if ( this.selectedBar >= this.dataList.size() ) {
			return;
		}
		// garbage collector
		System.gc();
		int numberOfGenes = this.dataList.get(this.selectedBar).numberOfGenes;
		ScoringResult result = GeneminingCNGPlugin.runSingleGenemining(numberOfGenes);
		if (result!=null)
			CompareDialog.generateNewTree(result, numberOfGenes);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (e.getX() >= 0 && e.getY() <= 400) {
			int bar = e.getX() / barLength;
			if (bar != selectedBar) {
				selectedBar = bar;
				repaint();
			}
		}
	}
	
	private class BranchData {
		public boolean notFound = false;
		public int numberOfGenes = 0;
		public double length = 0;
		
		public String toString() {
			return "Number Of Genes: " + Integer.toString(numberOfGenes)+"  --> " +
				(notFound ? "Clustering found no matching bipartition." : (" Branch length: " + Double.toString(length)));
		}
	}
}
