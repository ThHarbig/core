package mayday.core.gui.classes;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import mayday.core.ClassSelectionModel;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.structures.trie.Trie;
import mayday.core.structures.trie.TrieNode;
import mayday.core.structures.trie.TrieNodeMap;

@SuppressWarnings("serial")
public class PanelGenerateLabels extends JPanel {
	
	protected ClassSelectionPanel panel;
	
	private JSpinner classNumberSpinner;	//set and adjust the number of classes 
	private JSpinner objectNumberSpinner;	//set and adjust the number of objects

	private SpinnerModel objectModel;
	private SpinnerModel classModel;
	
	public PanelGenerateLabels(ClassSelectionPanel panel, Integer minClasses, Integer maxClasses) {
		super(new ExcellentBoxLayout(true,5));
		this.panel = panel;
		
		setBorder(BorderFactory.createTitledBorder("Generate Class Labels"));

		ClassSelectionModel partition = panel.getModel();		
		
		JPanel spinners = new JPanel(new ExcellentBoxLayout(false, 5));
		
		spinners.add(new JLabel("Number of Objects:"));
		objectNumberSpinner=new JSpinner();
		objectModel=new SpinnerNumberModel(1,1,10000,1);
		if(partition.getNumObjects()!=0)
			objectModel=new SpinnerNumberModel(partition.getNumObjects(),partition.getNumObjects(),partition.getNumObjects(),1);
		objectNumberSpinner.setModel(objectModel);
		spinners.add(objectNumberSpinner);
		spinners.add(Box.createHorizontalGlue());

		if (minClasses==null)
			minClasses = 2;
		if (maxClasses==null)
			maxClasses = 1000;
		
		spinners.add(new JLabel("Number of Classes:"));
		classNumberSpinner=new JSpinner();
		classModel=new SpinnerNumberModel(2,minClasses.intValue(),maxClasses.intValue(),1);
		classNumberSpinner.setModel(classModel);
		spinners.add(classNumberSpinner);

		add(spinners);		
		
		JPanel buttons = new JPanel(new ExcellentBoxLayout(false, 5));
		buttons.add(new JButton(new SplitEquallyAction()));
		buttons.add(new JButton(new SplitAlternatingAction()));
		buttons.add(new JButton(new SplitRandomAction()));
		buttons.add(Box.createHorizontalStrut(15));
		buttons.add(Box.createHorizontalGlue());
		buttons.add(new JButton(new GuessAction()));
		add(buttons);

		setMaximumSize(getPreferredSize());
	}
	
	public void updateObjectsFixed() {
		ClassSelectionModel partition = panel.getModel();
		if (panel.isObjectsFixed())
			objectModel=new SpinnerNumberModel(partition.getNumObjects(),partition.getNumObjects(),partition.getNumObjects(),1);
	}
	
	
	private class SplitEquallyAction extends AbstractCreateAction {
		public SplitEquallyAction() {
			super("Split equally");
		}
		@Override
		public void changeModel(ClassSelectionModel partition, int numObject,
				int numClass, boolean objectsFixed, List<String> classNames) {
			double num=(double)numObject/(double)numClass;
			if (numObject%numClass!=0) 
				num++;
			int i=0;
			int nextindex=0;
			while(i!=numObject) {
				if (i+1>num) {
					++nextindex;
					num += (double)numObject/(double)numClass;
				}
				partition.setClass(i, classNames.get(nextindex));
				++i;
			}
		}
	}
	
	private class SplitAlternatingAction extends AbstractCreateAction {
		public SplitAlternatingAction() {
			super("Split alternating");
		}
		@Override
		public void changeModel(ClassSelectionModel partition, int numObject,
				int numClass, boolean objectsFixed, List<String> classNames) {
			int i=0;
			while(i!=numObject) {
				partition.setClass(i, classNames.get(i%numClass));
				++i;
			}
		}
	}
	
	private class SplitRandomAction extends AbstractCreateAction {
		public SplitRandomAction() {
			super("Split randomly");
		}
		@Override
		public void changeModel(ClassSelectionModel partition, int numObject,
				int numClass, boolean objectsFixed, List<String> classNames) {
			Random r=new Random();
			for(int i=0; i!= numObject; ++i) {
				partition.setClass(i,classNames.get(r.nextInt(numClass)));
			}
		}
	}
	

	
	abstract class AbstractCreateAction extends AbstractAction {
		public AbstractCreateAction(String s) {
			super(s);
		}
		public final void actionPerformed(ActionEvent e) {
			ClassSelectionModel partition = panel.getModel();
			int numClass= (Integer)classModel.getValue();
			int numObject= (Integer)objectModel.getValue();
			boolean objectsFixed = panel.isObjectsFixed();
			
			// create new objects
			if (!objectsFixed) {
				partition.clear();
				for(int i=0; i!=numObject; ++i)
					partition.addObject("Object"+i);
			} 

			// New class names:
			List<String> classNames=new ArrayList<String>();			
			for(int i=0; i!=numClass; ++i) {
				classNames.add("Class "+i);				
			}
			changeModel(partition, numObject, numClass, objectsFixed, classNames);
			panel.setModel(partition);
		}
		protected abstract void changeModel(ClassSelectionModel partition, int numObject, int numClass, boolean objectsFixed, List<String> classNames);
	}



	private class GuessAction extends AbstractAction
	{

		public GuessAction()
		{
			super("Infer from names");
		}

		public void actionPerformed(ActionEvent e) 
		{
			
			ClassSelectionModel partition = panel.getModel();

			int numClass= (Integer)classModel.getValue();

			// do not create new objects

			// find numClass different prefixes
			Trie t = new Trie();
			for (String s : partition.getObjectNames()) {
				t.add(s);
			}

			Set<String> prefixes = new HashSet<String>();

			String p = t.longestInfix("");
			prefixes.add(p);

			expandPrefixes(prefixes, t, numClass);

			// assign classes
			for (int i=0; i!=partition.getNumObjects(); ++i) {
				String o = partition.getObjectName(i);
				for (String prefix : prefixes) {
					if (o.startsWith(prefix))
						partition.setClass(i, prefix);
				}				
			}
			
			panel.setModel(partition);
		}

		protected void expandPrefixes(Set<String> prefixes, Trie t, int nC) {
			boolean change = true;
			while (change && prefixes.size()<nC) {
				change=false;
				Set<String> current = new HashSet<String>(prefixes);				
				for (String s : current) {
					TrieNodeMap tnm = t.getNode(s).getChildren();
					while (tnm.size()==1) {
						tnm = tnm.values().iterator().next().getChildren();
					}
					if (tnm.size()+prefixes.size()-1 <= nC) {
						prefixes.remove(s);
						change = true;
						for (TrieNode tn : tnm.values())
							prefixes.add(tn.getPrefix());
					}
					if (prefixes.size()==nC)
						break;
				}
			}
		}

	}


}
