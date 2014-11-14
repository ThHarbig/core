package mayday.dynamicpl.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import mayday.core.DelayedUpdateTask;
import mayday.dynamicpl.DynamicProbeList;
import mayday.dynamicpl.RuleSet;

@SuppressWarnings("serial")
public class RuleEditorPanel extends JPanel {

	protected DynamicProbeList xdpl;
	protected DynamicProbeList clonedDpl;
	protected JLabel filterSizeLabel = new JLabel();
	protected RuleSet clonedRS;
	
	public RuleEditorPanel(DynamicProbeList pl) {
		super(new BorderLayout());
		xdpl = pl;
		
		filterSizeLabel.setText(pl.getNumberOfProbes()+" matching probes.");
		
		clonedDpl = new DynamicProbeList(pl.getDataSet(), "Temporary clone of \""+pl.getName()+"\"");
		clonedDpl.getRuleSet().fromStorageNode(pl.getRuleSet().toStorageNode());
		
		startWork();

		final JPanel rightPane = new JPanel();
		rightPane.add(new JLabel("Select a node in the tree to edit its properties"));
		
		final RuleTreeEditorPane leftPane = new RuleTreeEditorPane(clonedDpl.getRuleSet());

		final JSplitPane jsli  = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
		jsli.setContinuousLayout(true);

		TreeSelectionListener sel;
		
		leftPane.addTreeSelectionListener(sel=new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent arg0) {
				DefaultMutableTreeNode tn = (DefaultMutableTreeNode)leftPane.getSelectedNode();
				if (tn!=null) {
					Object uo = tn.getUserObject();
					if (uo instanceof OptionPanelProvider) {
						jsli.setRightComponent(((OptionPanelProvider)uo).getOptionPanel());
					}
				} else
					jsli.setRightComponent(rightPane);
			}
		});

		add(jsli, BorderLayout.CENTER);
		
		
        Box l_buttonPanel = Box.createHorizontalBox();
        
        l_buttonPanel.add(filterSizeLabel);
        
        l_buttonPanel.add( Box.createHorizontalGlue() ); // right-aligns the buttons

        l_buttonPanel.add(new JButton(new ApplyAction()));   

        add(l_buttonPanel, BorderLayout.SOUTH);
        setMinimumSize(new Dimension(550,300));
        sel.valueChanged(null);
    	    	
	}
	
	protected class ApplyAction extends AbstractAction	
	{
		public ApplyAction() {
			super( "Apply" );
		}
		public void actionPerformed( ActionEvent event )
		{			
			doExternalApply();
			clonedDpl.setIgnoreChanges(true);
		}
	}
	

	

	protected DelayedUpdateTask counter = new DelayedUpdateTask("Testing filter",100) {
	
		@Override
		protected void performUpdate() {
			int size = DynamicProbeList.countProbes(clonedDpl.getDataSet(), clonedDpl.getRuleSet());
			filterSizeLabel.setText(size+ " matching probes.");
		}
	
		@Override
		protected boolean needsUpdating() {
			return true;
		}
	};
	

	
	protected ChangeListener sizeListener = new ChangeListener() {

		public void stateChanged(ChangeEvent e) {
			filterSizeLabel.setText("Evaluating ...");
			counter.trigger();
		}
		
	};
	
	protected void addListenersTo(DynamicProbeList dpl) {
		RuleSet rs = dpl.getRuleSet();
		rs.addChangeListener(sizeListener);
	}
	
	protected void removeListenersFrom(DynamicProbeList dpl) {
		RuleSet rs = dpl.getRuleSet();	
		rs.removeChangeListener(sizeListener);
	}
	
	protected void finalizeWork() {
		removeListenersFrom(clonedDpl);
		clonedDpl.propagateClosing();
		clonedDpl = null;
	}
	
	protected void startWork() {
		clonedDpl.setIgnoreChanges(true);
		addListenersTo(clonedDpl);
	}
	
	public void doExternalApply() {
		xdpl.getRuleSet().clear();
		xdpl.setIgnoreChanges(false);
		xdpl.getRuleSet().fromStorageNode(clonedDpl.getRuleSet().toStorageNode());
	}
	
	public void removeNotify() {
		super.removeNotify();
		finalizeWork();
	}
	

}
