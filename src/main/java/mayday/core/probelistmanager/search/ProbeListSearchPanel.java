package mayday.core.probelistmanager.search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import mayday.core.probelistmanager.gui.ProbeListManagerView;

/**
 * GUI for Probe List search. 
 * @author Stephan Symons
 * @version 1.0
 * @since 2.9
 */
@SuppressWarnings("serial")
public class ProbeListSearchPanel extends JPanel 
{
	/**
	 * The search strategy used
	 */
	private ProbeListSearchInterface search;

	/**
	 * The view to be searched
	 */
	private ProbeListManagerView view;

	/**
	 * This is where the user enters the query. 
	 */
	private JTextField query;
	
	/** indicator for whether the query was successfully found */
	private Border defaultBorder;


	/**
	 * The index of the last found object
	 */
	private int lastIndex=-1;

	public ProbeListSearchPanel(ProbeListSearchInterface search, ProbeListManagerView view) 
	{
		setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		this.search=search;
		this.view=view;

		query=new JTextField(30);
		query.getDocument().addDocumentListener(new SearchListener());
		query.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
					search(lastIndex, true);
				if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
					setVisible(false);
			}
		});

		setLayout(new BorderLayout(5,0));
		
		add(new JLabel("Find "), BorderLayout.WEST);
		add(query,BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		
		panel.add(new SmallButton(new AbstractAction("Prev") {
			public void actionPerformed(ActionEvent e) {
				search(lastIndex, false);
			}			
		}));
		panel.add(new SmallButton(new AbstractAction("Next") {
			public void actionPerformed(ActionEvent e) {
				search(lastIndex, true);
			}			
		}));
		panel.add(new SmallButton(new AbstractAction("All") {
			public void actionPerformed(ActionEvent e) {
				selectAll();
			}			
		}));
		panel.add(new SmallButton(new AbstractAction("x") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}			
		}));
		
		add(panel, BorderLayout.EAST);
		panel.setBorder(BorderFactory.createEmptyBorder());
		
		query.requestFocus();
		defaultBorder = query.getBorder();
	}

	private void selectAll() {
		String s = query.getText();
		int i=-1;
		ArrayList<Object> toSel = new ArrayList<Object>();
		ListModel model = view.getModel();
		do {
			i=search.find(view, s, i);
			if (i>=0) {
				toSel.add(model.getElementAt(i));
				view.getSelectionModel().addSelectionInterval(i, i);
				view.ensureIndexIsVisible(i); 
			}
		} while(i>=0);
		forceShowToolTip(toSel.size()+" elements selected.", 0, 0);
	}
	

	protected void forceShowToolTip(String text, int x, int y) {
		setToolTipText(text);
		boolean canShowImmediately = false;
		try {
			Field f = ToolTipManager.class.getDeclaredField("showImmediately");
			f.setAccessible(true);
			f.set(ToolTipManager.sharedInstance(), Boolean.TRUE);
			canShowImmediately = true;
		} catch (Exception whatever) {}
		ToolTipManager.sharedInstance().mouseMoved(new MouseEvent(ProbeListSearchPanel.this, 0, 0, 0, x, y, 0, false));
		if (canShowImmediately) // remove text now
			setToolTipText(null);
	}
	
	private void search(int start, boolean down)
	{
		String s = query.getText();		
		int i=down
				?
				search.find(view, s, start)
				:
				search.findPrevious(view, s, start);

		if(i>=0) {
			view.selectIndex(i);
			view.ensureIndexIsVisible(i);
			lastIndex=i;
			// update the indicator
			query.setBorder(BorderFactory.createLineBorder(Color.green, 3));			
		} else {
			view.getSelectionModel().clearSelection();		
			query.setBorder(BorderFactory.createLineBorder(Color.red, 3));			
			if(start<0) {
				// if nothing was found AND we started at the top of the tree,
				// i.e. there was no previous hit with the same query,
				// we remove the last character from the query and retry.
				SwingUtilities.invokeLater(new Runnable(){
					public void run() {
						try {
							Document d = query.getDocument();							
							if(d.getLength()>0)
								d.remove(d.getLength()-1, 1);
						} catch (BadLocationException e) {
							e.printStackTrace();
						}					
					}
				});
			}
		}
	}

	private class SmallButton extends JButton {
		
		public SmallButton(AbstractAction aa) {
			super(aa);
			this.setBorder(BorderFactory.createEmptyBorder());
			this.getInsets().set(0, 0, 0, 0);			
		}
		
	}
	
	// when the entered query changes, restart the search from the top
	private class SearchListener implements DocumentListener {
		
		public void changedUpdate(DocumentEvent e) { }

		public void insertUpdate(DocumentEvent e) {
			search(-1, true);
		}

		public void removeUpdate(DocumentEvent e) {
			search(-1, true);				
		}		
	}

	
	public class ShowAction extends AbstractAction	{
		
		public ShowAction() {
			super("Find");
		}

		public void actionPerformed(ActionEvent e) 
		{
			ProbeListSearchPanel.this.setVisible(true);
		}
	}

	public ShowAction getFindAction()
	{
		return new ShowAction();
	}
	
	@Override
	public void setVisible(boolean flag) 
	{
		super.setVisible(flag);
		// remove any previous indicator
		query.setBorder(defaultBorder);			
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run() {
						query.requestFocus();
						query.requestFocusInWindow();
					}
				}
			);		
		
	}
	

}
