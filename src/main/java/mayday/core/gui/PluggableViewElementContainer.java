package mayday.core.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import mayday.core.gui.components.ExcellentBoxLayout;

@SuppressWarnings("serial")
public class PluggableViewElementContainer extends JPanel {

	protected HashMap<Component, CollapsiblePanel> panels = new HashMap<Component, CollapsiblePanel>();

	public PluggableViewElementContainer() {
		setLayout(new ExcellentBoxLayout(true,5));
	}
	
	public void addElement(Component element, String title) {
		if (panels.containsKey(element)) {
			removeElement(element);
		}
		boolean wasEmpty = panels.isEmpty();
		CollapsiblePanel rhp = new CollapsiblePanel(element, title);
		panels.put(element, rhp);
		add(rhp);
		if (wasEmpty) {
			Component p = getParent();
			if (p!=null && (p instanceof JSplitPane)) {
				((JSplitPane)p).setDividerLocation(.8);
			}
		}
		invalidate();
		validate();
			
	}

	public void removeElement(Component element) {
		CollapsiblePanel rhp = panels.get(element);
		if (rhp==null)
			return;
		rhp.setVisible(false);
		remove(rhp);
		panels.remove(element);
		if (panels.isEmpty()) {
			Component p = getParent();
			if (p!=null && (p instanceof JSplitPane)) {
				((JSplitPane)p).setDividerLocation(1d);
			}
		}
		invalidate();
		validate();
	}


	protected class CollapsiblePanel extends JPanel {

		protected Component element;
		protected String title;
		protected boolean collapsed = false;

		public CollapsiblePanel(Component element, String title) {
			this.title=title;
			setLayout(new ExcellentBoxLayout(true, 0));
			JPanel collapser = new JPanel(new ExcellentBoxLayout(false, 0));
			collapser.add(new CollapseButton(), BorderLayout.WEST);
			collapser.add(new MoveUpButton(), BorderLayout.EAST);
			collapser.add(new CloseButton(), BorderLayout.EAST);
			collapser.setMaximumSize(new Dimension(100000, collapser.getPreferredSize().height));
			this.element=element;
			add(collapser);
			updateState();
		}
		
		public void updateState() {
			if (!collapsed) {				
				add(element, BorderLayout.CENTER);
			} else {
				remove(element);
			}
			invalidate();
			validate();
			repaint();
		}

		protected class CollapseButton extends JButton {
			public CollapseButton() {
				super("<< "+title);
				setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
				setHorizontalAlignment(SwingConstants.LEFT);
				this.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						collapsed=!collapsed;
						if (collapsed) {
							setText("> "+title);
						} else {
							setText("< "+title);
						}
						CollapsiblePanel.this.updateState();
					}
				});				
			}
			
			public Dimension getMaximumSize() {
				Dimension d = super.getMaximumSize();
				d.width=100000;
				return d;				
			}
			
			public Dimension getMinimumSize() {
				Dimension d = super.getMinimumSize();
				d.width=50;
				return d;				
			}

		}

		protected class CloseButton extends JButton {
			public CloseButton() {
				super("x");
				setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
				this.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PluggableViewElementContainer.this.removeElement(CollapsiblePanel.this.element);
					}
				});
			}

		}
		
		protected class MoveUpButton extends JButton {
			public MoveUpButton() {
				super("^");
				setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
				this.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Component[] comps = PluggableViewElementContainer.this.getComponents();
						
						for (Component c : comps)
							PluggableViewElementContainer.this.removeElement(((CollapsiblePanel)c));
						
						Component skipped = null;
						
						for (int i=0; i!=comps.length; ++i) {
							Component c = comps[i];
							if (i<comps.length-1 && comps[i+1]==CollapsiblePanel.this) {
								Component c2 = comps[i+1];
								skipped = c2;
								PluggableViewElementContainer.this.addElement(((CollapsiblePanel)c2).element, ((CollapsiblePanel)c2).title);
							} 
							if (c!=skipped)
								PluggableViewElementContainer.this.addElement(((CollapsiblePanel)c).element, ((CollapsiblePanel)c).title);
						}
					}
				});
			}
		}


	}

}

