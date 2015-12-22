package mayday.core.gui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class ToolbarOverflowLayout implements java.awt.LayoutManager
{
	private JPopupMenu extenderPopup = new JPopupMenu();
	private JButton extenderButton = new JButton(new PopupAction());
	private boolean modifyButtons = false;
	private int buttoninsets = 0;
	private boolean center = false; 
	
	/** Creates a new instance */
	public ToolbarOverflowLayout()	{
	}
	
	public ToolbarOverflowLayout(boolean modifybuttons, int insets)	{
		this (modifybuttons, insets, false);
	}
	
	public ToolbarOverflowLayout(boolean modifybuttons, int insets, boolean center)	{
		modifyButtons = modifybuttons;
		buttoninsets = insets;
		this.center = center;
	}

	/** If the layout manager uses a per-component string,
	 * adds the component <code>comp</code> to the layout,
	 * associating it
	 * with the string specified by <code>name</code>.
	 *
	 * @param name the string to be associated with the component
	 * @param comp the component to be added
	 */
	public void addLayoutComponent(String name, Component comp)
	{
	}

	/**
	 * Lays out the specified container.
	 * @param parent the container to be laid out
	 */
	public void layoutContainer(Container parent)
	{
		//  Position all buttons in the container

		Insets insets = parent.getInsets();
		int x = insets.left;
		int y = insets.top;
		int spaceUsed = insets.right + insets.left;
		
		int barheight=extenderButton.getPreferredSize().height;

		for (int i = 0; i < parent.getComponentCount(); i++ )
		{
			Component aComponent = parent.getComponent(i);
			aComponent.setSize(aComponent.getPreferredSize());
			aComponent.setLocation(x,y);
			if (modifyButtons && (aComponent instanceof JButton))
				((JButton)aComponent).setBorder(BorderFactory.createEmptyBorder(buttoninsets, buttoninsets, buttoninsets, buttoninsets));
			int componentWidth = aComponent.getPreferredSize().width;
			x += componentWidth;
			spaceUsed += componentWidth;
			barheight = Math.max(barheight, aComponent.getPreferredSize().height);
		}

		//  All the buttons won't fit, add extender button
		//  Note: the size of the extender button changes once it is added
		//  to the container. Add it here so correct width is used.

		int parentWidth = parent.getSize().width;

		if (spaceUsed > parentWidth)
		{
			parent.add(extenderButton);
			Dimension s = extenderButton.getPreferredSize();
			barheight = Math.max(barheight, s.height);
			s.height = barheight; 
			if (modifyButtons)
				extenderButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			extenderButton.setSize( s );
			spaceUsed += extenderButton.getSize().width;			
		}

		//  Remove buttons that don't fit and add to the popup menu

		while (spaceUsed > parentWidth)
		{
			int last = parent.getComponentCount() - 2;
			if (last<0)
				break; // no way to fix this situation
			Component aComponent = parent.getComponent( last );
			parent.remove( last );
			extenderPopup.insert(aComponent, 0);
			if (modifyButtons && (aComponent instanceof JButton))
				((JButton)aComponent).setBorder(BorderFactory.createEmptyBorder(buttoninsets, buttoninsets, buttoninsets, buttoninsets));
			extenderButton.setLocation( aComponent.getLocation() );
			spaceUsed -= aComponent.getSize().width;
		}
		
		if (center && spaceUsed < parentWidth) {
			int remainder = parentWidth-spaceUsed;
			remainder /= 2;
			for (int i = 0; i < parent.getComponentCount(); i++ ) {
				Component aComponent = parent.getComponent(i);
				int xx = aComponent.getLocation().x;
				int yy = aComponent.getLocation().y;
				aComponent.setLocation(xx+remainder,yy);
			}
		}
			
		// make all same height
		for (int i = 0; i < parent.getComponentCount(); i++ ) {
			Component aComponent = parent.getComponent(i);
			Dimension s = aComponent.getSize();
			s.height = barheight;
			aComponent.setSize(s);
		}
	}

	/**
	 * Calculates the minimum size dimensions for the specified
	 * container, given the components it contains.
	 * @param parent the component to be laid out
	 * @see #preferredLayoutSize
	 */
	public Dimension minimumLayoutSize(Container parent)
	{
		return extenderButton.getMinimumSize();
	}

	/** Calculates the preferred size dimensions for the specified
	 * container, given the components it contains.
	 * @param parent the container to be laid out
	 *
	 * @see #minimumLayoutSize
	 */
	public Dimension preferredLayoutSize(Container parent)
	{
		//  Move all components to the container and remove the extender button

		parent.remove(extenderButton);

		while ( extenderPopup.getComponentCount() > 0 )
		{
			Component aComponent = extenderPopup.getComponent(0);
			extenderPopup.remove(aComponent);
			parent.add(aComponent);
		}

		//  Calculate the width of all components in the container

		Dimension d = new Dimension();
		d.width += parent.getInsets().right + parent.getInsets().left;

		for (int i = 0; i < parent.getComponents().length; i++)
		{
			d.width += parent.getComponent(i).getPreferredSize().width;
			d.height = Math.max(d.height,parent.getComponent(i).getPreferredSize().height);
		}

		d.height += parent.getInsets().top + parent.getInsets().bottom + 5;
		return d;
	}

	/** Removes the specified component from the layout.
	 * @param comp the component to be removed
	 */
	public void removeLayoutComponent(Component comp)
	{
	}

	@SuppressWarnings("serial")
	protected class PopupAction extends AbstractAction
	{
		public PopupAction()
		{
			super("More...");
		}

		public void actionPerformed(ActionEvent e)
		{
			JComponent component = (JComponent)e.getSource();
			extenderPopup.show(component,0,component.getHeight());
		}
	}
}
