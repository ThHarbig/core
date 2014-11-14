package mayday.mushell.autocomplete;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import mayday.mushell.InputField;

@SuppressWarnings("serial")
public class DefaultChoiceChooser extends JPopupMenu implements KeyListener, ChoiceChooser
{
	protected DefaultListModel model;
	protected JList list;
	
	InputField input;

	public DefaultChoiceChooser(InputField field)
	{
		super();
		this.input=field;
		model=new DefaultListModel();
		list=new JList(model);
		setSize(200, 80);
		JScrollPane scroller=new JScrollPane(list);
		scroller.setMinimumSize(new Dimension(200,160));
		scroller.setPreferredSize(new Dimension(200,160));
		add(scroller);
//		scroller.setBorder(BorderFactory.createEmptyBorder());
		setBorder(BorderFactory.createEmptyBorder());
		
		list.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount()==2 && e.getButton()==MouseEvent.BUTTON1) 
				{					
					try {
						input.autoComplete((Completion)list.getSelectedValue());
					} catch (BadLocationException e1) {} // die silently.
					DefaultChoiceChooser.this.setVisible(false);
				}
			}
		});
		list.addKeyListener(this);
		
		initList();
	}
	
	protected void initList()
	{
		list.setVisibleRowCount(8);		
		list.setSelectedIndex(0);
	}

	public void setChoice(List<Completion> choice)
	{
		model.clear();
		for(Completion c:choice)
		{
			model.addElement(c);
		}
		initList();
		list.setSelectedIndex(0);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#requestFocus()
	 */
	@Override
	public void requestFocus() 
	{	
		super.requestFocus();
		list.requestFocus();
		list.requestFocusInWindow();
	}
	
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
		{
			input.backspace();			
			e.consume();
			DefaultChoiceChooser.this.setVisible(false);
		}
		if( e.getKeyCode()==KeyEvent.VK_DELETE)
		{
			input.deleteChar();			
			e.consume();
			DefaultChoiceChooser.this.setVisible(false);
		}
		if(e.getKeyCode()==KeyEvent.VK_SPACE || e.getKeyCode()==KeyEvent.VK_ENTER)
		{
			Completion choice=(Completion)list.getSelectedValue();
			try {
				input.autoComplete(choice);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			DefaultChoiceChooser.this.setVisible(false);
			e.consume();
			return;
		}
		if(e.getKeyCode()==KeyEvent.VK_UP)
		{
			if(list.getSelectedIndex()!=0)
			{
				list.setSelectedIndex(list.getSelectedIndex()-1);
				list.ensureIndexIsVisible(list.getSelectedIndex());
				e.consume();
				return;
			}
		}
		if(e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			if(list.getSelectedIndex()!=model.getSize()-1)
			{
				list.setSelectedIndex(list.getSelectedIndex()+1);
				list.ensureIndexIsVisible(list.getSelectedIndex());
				e.consume();
				return;
			}
		}
		
	}

	public void keyReleased(KeyEvent e) 
	{	
	}

	public void keyTyped(KeyEvent e) 
	{
		char c= e.getKeyChar();		
		input.insertSnippet(c+"");	
		e.consume();
		updateChoice(c+"");
		if(model.size()==0)
			DefaultChoiceChooser.this.setVisible(false);
	}
	
	private void updateChoice(String prefix)
	{
		DefaultListModel newModel=new DefaultListModel();
		for(int i=0; i!= model.size(); ++i)
		{
			if(((Completion)model.get(i)).startsWith(prefix))
			{
				((Completion)model.get(i)).pop();
				newModel.addElement(model.get(i));
			}
		}
		
		model.clear();
		model=newModel;		
		list.setModel(model);	
		list.setSelectedIndex(0);
		list.ensureIndexIsVisible(0);
		list.requestFocus();
		list.requestFocusInWindow();
		list.setVisibleRowCount(8);
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				requestFocus();							
			}});
		Point p=input.getCaret().getMagicCaretPosition();
		if(p!=null)
			show(input, p.x, p.y);
		initList();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JPopupMenu#show(java.awt.Component, int, int)
	 */
	@Override
	public void show(Component invoker, int x, int y) 
	{
		super.show(invoker, x, y);
		initList();
		requestFocus();
	}
	

	


}
