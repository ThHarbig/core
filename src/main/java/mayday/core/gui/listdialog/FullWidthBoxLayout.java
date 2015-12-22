package mayday.core.gui.listdialog;

import java.awt.Component;
import java.awt.Container;

import javax.swing.BoxLayout;

@SuppressWarnings("serial")
final class FullWidthBoxLayout extends BoxLayout {

	FullWidthBoxLayout(Container target, int axis) {
		super(target, axis);
	}

	@Override
	public void layoutContainer(Container target) {
		super.layoutContainer(target);
		int mWidth = 0;
		for (int i = 0; i < target.getComponentCount(); i++) {
			Component c = target.getComponent(i);
			mWidth = Math.max(mWidth , c.getWidth());
		}
		for (int i = 0; i < target.getComponentCount(); i++) {
			Component c = target.getComponent(i);
			c.setSize(mWidth, c.getHeight());
		}
	}
	
}