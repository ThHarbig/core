package mayday.dynamicpl.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.dynamicpl.AbstractDataProcessor;
import mayday.dynamicpl.DataProcessors;
import mayday.dynamicpl.Rule;

@SuppressWarnings({"unchecked","serial"})
public class RuleGUI extends JPanel implements ChangeListener {

	private Rule rule;
	
	private JPanel stackPanel;
	private Stack<AbstractDataProcessor> lastStack = new Stack<AbstractDataProcessor>();
	
	protected JCheckBox activeCheckBox;
	protected JCheckBox invertedCheckBox;
	protected JCheckBox defaultCheckBox;
	
	private boolean changeInitiatedByMe = false;
	
	public RuleGUI(Rule rs) {
		rule = rs;		
		init();
		lastStack = (Stack<AbstractDataProcessor>)rule.getProcessors().clone();
		rule.addChangeListener(this);
	}
	
	protected void init() {		
		setLayout(new BorderLayout());
		
		// checkboxes		
		JPanel cbPanel = new JPanel();		
		activeCheckBox = new JCheckBox("Active"); 
		activeCheckBox.setSelected(rule.isActive());
		activeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeInitiatedByMe=true;
				rule.setActive(activeCheckBox.isSelected());
				changeInitiatedByMe=false;
			}
		});
		cbPanel.add(activeCheckBox);
		
		invertedCheckBox = new JCheckBox("Invert Rule"); 
		invertedCheckBox.setSelected(rule.isInverted());
		invertedCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeInitiatedByMe=true;
				rule.setInverted(invertedCheckBox.isSelected());
				changeInitiatedByMe=false;
			}
		});
		cbPanel.add(invertedCheckBox);
		
		defaultCheckBox = new JCheckBox("Missing values = TRUE");
		defaultCheckBox.setSelected(rule.defaultsToTrue());
		defaultCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeInitiatedByMe=true;
				rule.setDefault(defaultCheckBox.isSelected());
				changeInitiatedByMe=false;
			}
		});
		cbPanel.add(defaultCheckBox);
		
		add(cbPanel, BorderLayout.NORTH);
		
		// layout niceties
		JPanel tempp = new JPanel(new BorderLayout());
		// rule stack
		stackPanel = new JPanel();
		stackPanel.setLayout(new BoxLayout(stackPanel, BoxLayout.Y_AXIS));
		buildStackPanel();
		tempp.add(stackPanel,BorderLayout.NORTH);
		
		add(new JScrollPane(tempp), BorderLayout.CENTER);

	}

	protected void buildStackPanel() {
		// get current panel content
		Vector<Component> lastComponents = new Vector<Component>();
		for (Component jc : stackPanel.getComponents())
			lastComponents.add(jc);
		// find out how many processors remained unchanged
		int firstChanged;
		for (firstChanged=0; 
			 firstChanged!=Math.min(rule.getProcessors().size(), lastStack.size()); 
			 ++firstChanged) {
			if (rule.getProcessors().get(firstChanged)!=lastStack.get(firstChanged))
				break;
		}

		/* stackPanel.removeAll();
		
		// copy all stuff we can keep
		Class<?> inputClass = Probe.class; //first input is always Probe
		for (int i=0; i!=firstChanged; ++i) {
			stackPanel.add(lastComponents.get(i));
			AbstractDataProcessor adp = lastStack.get(i);
			((DataProcessorGUI)lastComponents.get(i)).doUpdate();
			inputClass = adp.getDataClass();
		}
		*/
						
		// remove panels that we don't need any more		
		for(int i=firstChanged; i!=lastComponents.size(); ++i) {
			Component jc = lastComponents.get(i);
			stackPanel.remove(jc);
		}
			
		// get (changed) list of possible next processors
		Class<?>[] inputClass;
		if (firstChanged-1>=0) {
			AbstractDataProcessor adp = lastStack.get(firstChanged-1);
			inputClass = adp.getDataClass();
		} else 
			inputClass = new Class[]{Probe.class}; 
			
		// create new panels where we need them
		for (int i=firstChanged; i!=rule.getProcessors().size(); ++i) {
			AbstractDataProcessor adp = rule.getProcessors().get(i);
//			if (inputClass!=null) {
//				System.out.print("Offered class is ");
//				for (Class<?> c : inputClass)
//					System.out.print(c+" ");
//				System.out.println();
//			}
			Set<DataProcessors.Item> candidates = DataProcessors.getProcessorsAccepting(inputClass);
//			System.out.println(candidates.size()+" candidates");
			DataProcessorGUI dpg = new DataProcessorGUI(rule, adp, candidates);
			stackPanel.add(dpg);
			inputClass = adp.getDataClass();
		}
		
		lastStack = (Stack<AbstractDataProcessor>)rule.getProcessors().clone();
		
		// add one more panel if the rule is not finished
		if (!rule.isFinishedChain()) {
//			if (inputClass!=null) {
//				System.out.print("Final offered class is ");
//				for (Class<?> c : inputClass)
//					System.out.print(c+" ");
//				System.out.println();
//			}
			Set<DataProcessors.Item> candidates = DataProcessors.getProcessorsAccepting(inputClass);
//			System.out.println(candidates.size()+" candidates");
			final DataProcessorGUI dpg = new DataProcessorGUI(rule, null, candidates);
			stackPanel.add(dpg);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					KeyEvent k =new KeyEvent(
							dpg.getComboBox(),
							KeyEvent.KEY_PRESSED,
							(long)0, //when
							0, // key mask
							KeyEvent.VK_DOWN,
							KeyEvent.CHAR_UNDEFINED
					);
					dpg.getComboBox().processKeyEvent(k);					
				}
			});
		}
		
	}
	
	public void stateChanged(ChangeEvent arg0) {
		if (changeInitiatedByMe)
			return;		
		activeCheckBox.setSelected(rule.isActive());		
		invertedCheckBox.setSelected(rule.isInverted());
		buildStackPanel();
	}
	
	public void removeNotify() {
		rule.removeChangeListener(this);
		super.removeNotify();
	}
}
