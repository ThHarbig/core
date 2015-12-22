package mayday.mpf;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * VisualNode represents one instance of FilterNode while a Complex Filter
 * is edited in the Designer component. It contains buttons to change connections
 * and set options as well as a button to remove this Node from the complex filter.
 * @author Florian Battke
 */
public class VisualNode extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private FilterNode attachedFilterNode;

	private JButton mOptions;
	private JButton mRemove;
	private JPanel mInputPanel = new JPanel();
	private JPanel mOutputPanel = new JPanel();
	private JButton[] mInputs = new JButton[0];
	private JButton[] mOutputs = new JButton[0];
	private boolean optVis=true;
	private boolean remVis=true;

	/**
	 * Creates a new VisualNode instance for a given FilterNode
	 * @param attachFilterNode The FilterNode to create a new VisualNode for
	 */
	public VisualNode(FilterNode attachFilterNode) {
		attachedFilterNode = attachFilterNode;
		setupComponent();
	}

	/** Returns the FilterNode represented by this VisualNode instance
	 * @return The attached FilterNode
	 */
	public FilterNode getFilterNode() { return attachedFilterNode; }

	/** Sets the caption for this VisualNode
	 * @param newTitle The new caption
	 */
	public void setTitle(String newTitle) {
		((TitledBorder)this.getBorder()).setTitle(newTitle);
	}


	/** Marks an input slot as connected or disconnected
	 * @param index The input slot to mark
	 * @param connected The connection state of this slot
	 */
	public void setInputConnected(int index, boolean connected) {
		mInputs[index].setBackground( connected ? java.awt.Color.GREEN : java.awt.Color.RED );
	}

	/** Marks an output slot as connected or disconnected
	 * @param index The output slot to mark
	 * @param connected The connection state of this slot
	 */
	public void setOutputConnected(int index, boolean connected) {
		mOutputs[index].setBackground( connected ? java.awt.Color.GREEN : java.awt.Color.RED );
	}

	private void colorizeAllSlots() {
        for (int i=0; i!=mOutputs.length; ++i)
            setOutputConnected(i, getFilterNode().Output[i].isConnected());
        for (int i=0; i!=mInputs.length; ++i)
            setInputConnected(i, getFilterNode().Input[i].isConnected());
	}

	/** Sets the visibility of the VisualNode's buttons
	 * @param options Should the options button be visible
	 * @param remove Should the removal button be visible
	 */
	public void setVisibility(boolean options, boolean remove) {
		optVis=options; remVis=remove;
		mOptions.setVisible(optVis);
		mRemove.setVisible(remVis);
	}

	/** Draw lines for all outgoing connections of this VisualNode
	 * @param g2d the Device to draw on
	 * @param P the absolute coordinates of the Container that we belong to
	 */
	public void drawOutgoingConnections(Graphics2D g2d, Point P) {
		for (int i=0; i!=attachedFilterNode.Output.length; ++i) {
			if (attachedFilterNode.Output[i].isConnected()) {
				if (!mOutputs[i].isShowing()) continue; // Prevent errors when components aren't visible
				// Get absolute coordinates
				Point startP = mOutputs[i].getLocationOnScreen();
				startP.move( startP.x+ mOutputs[i].getWidth(), startP.y+mOutputs[i].getHeight()/2 );
				JButton end = attachedFilterNode.Output[i].Node.getVisualNode()
				              .mInputs[attachedFilterNode.Output[i].Slot];
				if (!end.isShowing()) continue; // Prevent errors when components aren't visible
				Point endP = end.getLocationOnScreen();
				endP.move( endP.x, endP.y+mOutputs[i].getHeight()/2 );
				// convert to relative coords
				startP.move(startP.x-P.x, startP.y-P.y);
				endP.move(endP.x-P.x , endP.y-P.y);
				g2d.drawLine(startP.x, startP.y, endP.x, endP.y);
			}
		}

	}

	/**
	 * (Re-)creates the GUI components for this VisualNode
	 */
	public void setupComponent() {
		this.removeAll(); // reset in case we are called during input/output size change
		// init of graphical members
		this.setLayout(new BorderLayout(5,0));
		this.setOpaque(false);
	    java.awt.Font font = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 9);

		TitledBorder mBorder = new TitledBorder(attachedFilterNode.attachedFilter.getName());
		mBorder.setTitleJustification(2);
		mBorder.setTitleFont(font);
		this.setBorder(mBorder);

    	mInputPanel.removeAll();
	    if (attachedFilterNode.attachedFilter.InputSize>0) {
			mInputPanel.setLayout(new BoxLayout(mInputPanel, BoxLayout.Y_AXIS));
			mInputPanel.setOpaque(false);
			JLabel mInLabel = new JLabel("in");
			mInLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			mInLabel.setFont(font);
			mInputPanel.add(mInLabel);
			mInputs = new JButton[attachedFilterNode.attachedFilter.InputSize];
			for (int i=0; i!=attachedFilterNode.attachedFilter.InputSize; ++i) {
				mInputs[i] = new JButton(new Integer(i+1).toString());
				mInputs[i].setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
				mInputs[i].setMargin(new java.awt.Insets(0,5,0,5));
				mInputs[i].setFont(font);
				mInputs[i].addActionListener(this);
				if (attachedFilterNode.Input[i].isConnected()) mInputs[i].setBackground(java.awt.Color.GREEN);
				mInputPanel.add(mInputs[i]);
			}
			this.add(mInputPanel, BorderLayout.WEST);
	    }

    	mOutputPanel.removeAll();
	    if (attachedFilterNode.attachedFilter.OutputSize>0) {
			mOutputPanel.setLayout(new BoxLayout(mOutputPanel, BoxLayout.Y_AXIS));
			mOutputPanel.setOpaque(false);
			JLabel mOutLabel=new JLabel("out");
			mOutLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			mOutLabel.setFont(font);
			mOutputPanel.add(mOutLabel);
			mOutputs = new JButton[attachedFilterNode.attachedFilter.OutputSize];
			for (int i=0; i!=attachedFilterNode.attachedFilter.OutputSize; ++i) {
				mOutputs[i] = new JButton(new Integer(i+1).toString());
				mOutputs[i].setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
				mOutputs[i].setMargin(new java.awt.Insets(0,5,0,5));
				mOutputs[i].setFont(font);
				mOutputs[i].addActionListener(this);
				if (attachedFilterNode.Output[i].isConnected()) mOutputs[i].setBackground(java.awt.Color.GREEN);
				mOutputPanel.add(mOutputs[i]);
			}
			this.add(mOutputPanel, BorderLayout.EAST);
	    }

		JPanel mButtons = new JPanel();
		BoxLayout bl = new BoxLayout(mButtons, BoxLayout.Y_AXIS);
		mButtons.setLayout(bl);
		mButtons.setOpaque(false);

		mOptions = new JButton("Options");
		mRemove = new JButton("Remove");

		mRemove.setFont(font);
		mRemove.setMargin(new java.awt.Insets(2,2,2,2));
		mRemove.setAlignmentX(Component.CENTER_ALIGNMENT);
		mRemove.addActionListener(this);
		mRemove.setVisible(remVis);
		mOptions.setFont(font);
		mOptions.setMargin(new java.awt.Insets(2,2,2,2));
		mOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
		mOptions.addActionListener(this);
		mOptions.setVisible(optVis);

		mButtons.add(mRemove);
		mButtons.add(mOptions);

		this.add(mButtons, BorderLayout.CENTER);
		
		this.colorizeAllSlots();

	    // set panel size, correcting for border width
		Dimension mDim = this.getLayout().preferredLayoutSize(this);
		Insets mIns = this.getInsets();
		this.setSize(mDim.width+mIns.left+mIns.right+1, mDim.height+mIns.bottom+mIns.top+1);
		this.revalidate();
	}

	public static final String OptionClicked = "Options";
	public static final String RemoveClicked = "Remove";
	public static final String SendingSlot = "SendingSlot=";
	public static final String ReceivingSlot = "ReceivingSlot=";

	public void actionPerformed(ActionEvent AE) {
		// send events outward
		if (AE.getSource().equals(mOptions)) postEvent(OptionClicked);
		if (AE.getSource().equals(mRemove))  postEvent(RemoveClicked);
		for (int i=0; i!=mInputs.length; ++i)
			if (AE.getSource().equals(mInputs[i]))
				postEvent(ReceivingSlot+new Integer(i).toString());
		for (int i=0; i!=mOutputs.length; ++i)
			if (AE.getSource().equals(mOutputs[i]))
				postEvent(SendingSlot+new Integer(i).toString());
	}

	/* All the event code taken from Java API 1.5 AWTEventMulticaster documentation */
	ActionListener actionListener = null;
	public synchronized void addActionListener(ActionListener l) {
        actionListener = AWTEventMulticaster.add(actionListener, l);
    }
    public synchronized void removeActionListener(ActionListener l) {
          actionListener = AWTEventMulticaster.remove(actionListener, l);
    }
    protected void postEvent(String ae) {
        // only post event if in Designer mode
    	if (attachedFilterNode!=null && actionListener != null) {
           	actionListener.actionPerformed(new ActionEvent(attachedFilterNode, 0, ae));
    	}
    }
    /* end of event creation code */

    // The target position of this node during Smooth repositioning (see DesignerPanel)
	public int targetX, targetY;


}
