package mayday.core.settings.generic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.gui.components.RadioButtonTitledBorder;
import mayday.core.settings.AbstractSettingComponent;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;

public class SelectableHierarchicalSettingComponent_Panel extends AbstractSettingComponent<SelectableHierarchicalSetting> {

	protected boolean verticalLayout;
	protected boolean topMost = false;
	protected JRadioButton[] rb;
	protected ButtonGroup bg;
	protected JPanel pnl;
	protected List<SettingComponent> settingComponents = new ArrayList<SettingComponent>();

	public SelectableHierarchicalSettingComponent_Panel(SelectableHierarchicalSetting s, boolean VerticalLayout, boolean TopMost) {
		super(s);
		verticalLayout = VerticalLayout;
		topMost = TopMost;
	}


	protected boolean needsLabel() {
		return false;
	}


	public boolean updateSettingFromEditor(boolean failSilently) {
		if (!super.updateSettingFromEditor(failSilently))
			return false;

		int selectedButton = 0;
		for (JRadioButton jrb : rb)
			if (jrb.isSelected())
				break;
			else
				selectedButton++;

		for (int i=0; i!=settingComponents.size(); ++i) {
			SettingComponent sc = settingComponents.get(i);
			if (sc!=null && !sc.updateSettingFromEditor(i!=selectedButton)) // only warn on active subsettings
				// only break if a subsetting is false AND selected
				if (i==selectedButton)
					return false;
		}
		return true;
	} 

	public void setTopMost(boolean TopMost) {
		topMost = TopMost;
		if (editorComponent==null)
			return;
		if (topMost)
			editorComponent.setBorder(BorderFactory.createEmptyBorder());
		else
			editorComponent.setBorder(BorderFactory.createTitledBorder(mySetting.getName()));
	}

	public void stateChanged(SettingChangeEvent e) {
		setSelected();
	}

	public void setSelected() {			
		String newVal = mySetting.getStringValue();
		int i;
		for (i=0; i!=mySetting.getPredefinedValues().length; ++i)
			if (newVal.equals(mySetting.getPredefinedValues()[i].toString()))
				break;
		if (i<mySetting.getPredefinedValues().length)
			rb[i].setSelected(true);			
	}

	protected String getCurrentValueFromGUI() {
		for (JRadioButton jrb : rb)
			if (jrb.isSelected())
				return jrb.getText();
		return null;
	}

	@SuppressWarnings("unchecked")
	protected Component getSettingComponent() {
		if (bg==null) {
			bg = new ButtonGroup();
			pnl = new JPanel();
			pnl.setLayout(new ExcellentBoxLayout(verticalLayout,3));
			rb = new JRadioButton[mySetting.getPredefinedValues().length];
			int i=0;

			for (Object o : mySetting.getPredefinedValues()) {
				rb[i] = new JRadioButton( o.toString() );
				JComponent comp;
				if (o instanceof Setting) {
					Setting setting = (Setting)o; 
					SettingComponent sc = setting.getGUIElement();
					settingComponents.add(sc);
					if (setting instanceof HierarchicalSetting) {
						comp = sc.getEditorComponent();
						comp.setBorder(new RadioButtonTitledBorder(rb[i], comp));
					} else {
						JPanel p2 = new JPanel(new BorderLayout());
						JPanel labelAlign = new JPanel(new BorderLayout());
						labelAlign.add(rb[i], BorderLayout.NORTH);
						p2.add(labelAlign, BorderLayout.WEST);
						if (sc instanceof AbstractSettingComponent) {
							// remove duplicate label where possible
							((AbstractSettingComponent)sc).hideLabel(true);
						} 
						comp = sc.getEditorComponent();
						p2.add(comp, BorderLayout.CENTER);

						// MIN SIZE
						int delta = p2.getInsets().bottom + p2.getInsets().top;
						Dimension s = comp.getMinimumSize();
						s.width += rb[i].getPreferredSize().width+10;
						s.height = Math.min(p2.getPreferredSize().height, s.height+delta);
						p2.setMinimumSize(s);
						s = comp.getMaximumSize();
						s.width += rb[i].getPreferredSize().width+10;
						s.height = Math.max(p2.getPreferredSize().height, s.height+delta);
						p2.setMaximumSize(s);
												
						final JRadioButton myRB = rb[i];
						MouseListener mouseFresser = new MouseAdapter() {
							public void mousePressed(MouseEvent evt) {
								myRB.setSelected(true);
							}
						};
						dirtilyAddMouseListener(comp, mouseFresser);

						comp = p2;		

					}
				} else {
					comp = rb[i];
					settingComponents.add(null);
				}
				pnl.add( comp );
				++i;				
			}

			for (JRadioButton jrb : rb) {
				bg.add(jrb);
				jrb.addChangeListener(new ChangeListener() {

					public void stateChanged(ChangeEvent e) {
						pnl.repaint();						
					}

				});
			}
			setSelected();
		}
		return pnl;
	}

	public JComponent getEditorComponent() {
		JComponent c = super.getEditorComponent();
		setSelected();
		setTopMost(topMost);
		return c;
	}

	/* This is dirty, but necessary :( */
	protected void dirtilyAddMouseListener(Container c, MouseListener ml) {
		for (Component subC : c.getComponents()) {
//			System.out.println("Add listener to "+c.getClass().getSimpleName()+": "+c.getName());
			subC.addMouseListener(ml);
			if (subC instanceof Container)
				dirtilyAddMouseListener((Container)subC, ml);
		}
		
	}
	
}
