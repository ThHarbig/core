package mayday.core.gui.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/** This layout adds a "More..." submenu if the menu is too large to fit the screen
 * WARNING: If the menu is TWICE too large to fit, the "More..." submenu will still be too large. */
public class MenuOverflowLayout implements java.awt.LayoutManager
{
	private JMenu extenderMenu = new JMenu();

	/** Creates a new instance */
	public MenuOverflowLayout()	{
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
		if (!parent.isShowing())
			return;
			
		JPopupMenu ppm = ((JMenu)parent).getPopupMenu();
		
		// find out which screen we are running on
		Point myLoc = parent.getLocationOnScreen();
		GraphicsDevice[] sd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Rectangle myScreen = null;
		for (GraphicsDevice gd : sd) {  
			Rectangle screenRect = gd.getDefaultConfiguration().getBounds();
			if (screenRect.contains(myLoc))
				myScreen = screenRect;
		}

		if (myScreen==null)
			return;

		// move all components from extendermenu back into main menu
		if (extenderMenu!=null) {
			for (Component c : extenderMenu.getMenuComponents()) {
				((JMenu)parent).add(c);    		
			}
			parent.remove(extenderMenu);
			extenderMenu = null;
		}

		// are we too high?
		int currentSize = ppm.getPreferredSize().height;
		Point currentLoc = myLoc;
		currentLoc.x-=myScreen.x;
		currentLoc.y-=myScreen.y;
		if (currentSize + currentLoc.y > myScreen.height) {
			// move the menu up    		
			ppm.setLocation(ppm.getLocation().x, ppm.getLocation().y-currentLoc.y);

			if (currentSize > myScreen.height) {
				// menu is too large, remove elements
				LinkedList<Component> removedElements = new LinkedList<Component>();
				if (extenderMenu==null)
					extenderMenu = new JMenu("More...");
				currentSize+=extenderMenu.getPreferredSize().height;
				while (currentSize > myScreen.height) {
					int last = ((JMenu)parent).getMenuComponentCount()-1;
					Component removed = ((JMenu)parent).getMenuComponent(last);
					removedElements.addFirst(removed);
					((JMenu)parent).remove(removed);
					currentSize-=removed.getPreferredSize().height;
				}
				for (Component c : removedElements)
					extenderMenu.add(c);
				parent.add(extenderMenu);
			}
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
		//		return extenderButton.getMinimumSize();
		return new Dimension(100,100);
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

		//		parent.remove(extenderButton);
		//
		//		while ( extenderPopup.getComponentCount() > 0 )
		//		{
		//			Component aComponent = extenderPopup.getComponent(0);
		//			extenderPopup.remove(aComponent);
		//			parent.add(aComponent);
		//		}
		//
		//		//  Calculate the width of all components in the container
		//
		//		Dimension d = new Dimension();
		//		d.width += parent.getInsets().right + parent.getInsets().left;
		//
		//		for (int i = 0; i < parent.getComponents().length; i++)
		//		{
		//			d.width += parent.getComponent(i).getPreferredSize().width;
		//			d.height = Math.max(d.height,parent.getComponent(i).getPreferredSize().height);
		//		}
		//
		//		d.height += parent.getInsets().top + parent.getInsets().bottom + 5;
		//		return d;
		return new Dimension(100,100);
	}

	/** Removes the specified component from the layout.
	 * @param comp the component to be removed
	 */
	public void removeLayoutComponent(Component comp)
	{
	}

}
