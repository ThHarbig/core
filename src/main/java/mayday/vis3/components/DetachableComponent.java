package mayday.vis3.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import mayday.core.gui.MaydayFrame;

@SuppressWarnings("serial")
public class DetachableComponent extends JPanel  {

	protected JLabel title = new JLabel();
	protected JButton detachButton = new JButton(new DetachAction());	
	protected Component plot;
	protected JButton placeHolder = new JButton("[detached]");
	protected JMenuBar menubar = new JMenuBar();
	protected MaydayFrame detachedWindow;
	protected Component detachedPlot;

	protected boolean allowCollapse = true;
	protected CollapseButton collapseButton;
	protected Component centerElement = null;
	protected CollapsibleBorderLayout layout;

	protected static final String TXT_DETACH="[Detach]";
	protected static final String TXT_ATTACH="[Attach]";
	
	protected static final String TXT_COLLAPSE="[Hide]";
	protected static final String TXT_EXPAND="[Show]";
	
	public DetachableComponent(Component plot, String titleString) {
		this(plot, titleString, new CollapsibleBorderLayout());
	}
	
	public DetachableComponent(Component plot, String titleString, LayoutManager mgr) {
		super(mgr);
		
		if (plot!=null)
			setPlot(plot);
		
		title.setText(titleString);
		
		menubar.add(title);
		menubar.add(Box.createHorizontalGlue());
		add(menubar, BorderLayout.NORTH);		
		
		detachButton.setBorder(BorderFactory.createEmptyBorder());
		
		placeHolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (detachedWindow!=null)
					detachedWindow.toFront();				
			}			
		});
		
		layout = (CollapsibleBorderLayout)getLayout();
		collapseButton = new CollapseButton();
	}
	
	public void setCollapsible(boolean collapsible) {
		allowCollapse = collapsible;
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		// build the menu only once		
		if (detachButton.getParent()!=menubar) 
			buildMenu();
	}
	
	public DetachableComponent(String titleString) {
		this(null, titleString);
	}
	
	public void setPlot(Component plot) {
		if (this.plot!=plot) {
			if (detachedWindow!=null) {
				detachedWindow.remove(detachedPlot);
				detachedPlot = createDetachableComponent(plot);
				detachedWindow.add(detachedPlot);
			} else {
				if (this.plot!=null)
					remove(this.plot);
				add(plot, BorderLayout.CENTER);
			}
			this.plot=plot;
		}
	}
	
	protected void buildMenu() {
		if (allowCollapse)
			menubar.add(collapseButton);		
		menubar.add(detachButton);
	}
	
	protected Component createDetachableComponent(Component plot) {
		return new JScrollPane(plot);
	}
	
	protected MaydayFrame createExternalWindow() {
		MaydayFrame mf = new MaydayFrame();
		mf.setTitle(this.getName());
		detachedPlot = createDetachableComponent(plot);
		mf.add(detachedPlot);
		remove(plot);				
		mf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				attach();
			}
		});		
		mf.pack();
		mf.setSize(640, 480);
		mf.setVisible(true);
		return mf;
	}
	
	protected void detach() {		
		detachedWindow = createExternalWindow();
		add(placeHolder, BorderLayout.CENTER);
		DetachableComponent.this.invalidate();
		DetachableComponent.this.validate();
		DetachableComponent.this.repaint();
		detachButton.setText(TXT_ATTACH);
		collapse(true);
	}
	
	protected void attach() {
		collapse(false);
		remove(placeHolder);
		add(plot, BorderLayout.CENTER); // get the plot back;						
		plot.validate();
		DetachableComponent.this.invalidate();
		DetachableComponent.this.validate();
		DetachableComponent.this.repaint();
		detachedWindow.dispose();
		detachedWindow = null;
		detachButton.setText(TXT_DETACH);
	}
	
	
	public void collapse(boolean coll) {
		if (!allowCollapse)
			return;		
		if (coll==layout.isCollapsed())
			return;
		layout.setCollapsed(coll);
		invalidate();
		validate();
		repaint();
		Component c = getParent();
		if (c!=null) {
			c.invalidate();
			c.validate();
			c.doLayout();
			c.repaint();
		}
		if (layout.isCollapsed()) {
			collapseButton.setText(TXT_EXPAND);
		} else {
			collapseButton.setText(TXT_COLLAPSE);
		}
	}
	
	
	public void removeNotify() {
		if (detachedWindow!=null)
			detachedWindow.dispose();
		super.removeNotify();			
	}
		
		
	
	protected class DetachAction extends AbstractAction {
		
		public DetachAction() {
			super(TXT_DETACH);
		}

		public void actionPerformed(ActionEvent e) {
			if (detachedWindow==null)
				detach();
			else
				attach();
		}
	}
	

	
	protected class CollapseButton extends JButton {
		public CollapseButton() {
			super(TXT_COLLAPSE);
			setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
			setHorizontalAlignment(SwingConstants.LEFT);
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					collapse(!layout.isCollapsed());
				}
			});				
		}
	}

}
