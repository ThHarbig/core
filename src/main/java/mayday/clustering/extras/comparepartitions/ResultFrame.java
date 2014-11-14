package mayday.clustering.extras.comparepartitions;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import mayday.core.gui.MaydayFrame;

@SuppressWarnings("serial")
public class ResultFrame extends MaydayFrame {
	
	public ResultFrame( ConfusingMatrix cr ) {
		setLayout(new BorderLayout());
		setTitle("Comparison of two partitions: Results");
		
		JTabbedPane tabPane = new JTabbedPane();
		
		// first tab: General result
		
		JPanel tabOne = new JPanel(new BorderLayout());
		JTextPane text = new JTextPane();
		text.setContentType("text/html");
		text.setEditable(false);
		text.setText(
				"<html>" +
				"Size of the first partition: <b>"+(cr.p1.getPartitionNames().size()-1)+" clusters</b> <br>" +
				"Size of the second partition: <b>"+(cr.p2.getPartitionNames().size()-1)+" clusters</b> <br><br>"+
				
				"These two partitions together create "+cr.getShardInfo()+"<br><br>"+
				
				"In each partition, a pair of probes can either be in the same cluster or in different clusters.<br>"+
				cr.getPairInfo()
		);
		tabOne.add(text, BorderLayout.CENTER);
		tabPane.add("General", tabOne);
		
	
		// Second Tab: Best pairs
		
		JPanel tabTwo = new JPanel(new BorderLayout());		
		ResultTable rt = new ResultTable( cr.getInclusions() );		
		tabTwo.add(new JScrollPane(rt), BorderLayout.CENTER);
		tabPane.add("Best overlaps", tabTwo);
		
		
		// Third tab: Confusing Matrix
		JPanel tabThree = new JPanel(new BorderLayout());
		ResultMatrix rm = new ResultMatrix(cr);
		tabThree.add(new JScrollPane(rm), BorderLayout.CENTER);
		tabPane.add("Overlap matrix", tabThree);
		
		add(tabPane, BorderLayout.CENTER);
		
		pack();
		setVisible(true);
	}

}
