package mayday.core.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

@SuppressWarnings("serial")
public class ReorderableJList extends JList implements DragSourceListener,
		DropTargetListener, DragGestureListener {

	static DataFlavor localObjectFlavor;
	static {
		try {
			localObjectFlavor = new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	static DataFlavor[] supportedFlavors = { localObjectFlavor };
	
	DragSource dragSource;
	DropTarget dropTarget;
	int dropTargetCell = -1;
	int draggedIndex = -1;

	public ReorderableJList() {
		super();
		setCellRenderer(new ReorderableListCellRenderer());
		setModel(new DefaultListModel());
		dragSource = new DragSource();
		@SuppressWarnings("unused")
		DragGestureRecognizer dgr = dragSource
				.createDefaultDragGestureRecognizer(this,
						DnDConstants.ACTION_MOVE, this);
		dropTarget = new DropTarget(this, this);
	}

	// DragGestureListener
	public void dragGestureRecognized(DragGestureEvent dge) {
		Point clickPoint = dge.getDragOrigin();
		int index = locationToIndex(clickPoint);
		if (index == -1)
			return;
		Object target = getModel().getElementAt(index);
		Transferable trans = new RJLTransferable(target);
		draggedIndex = index;
		dragSource.startDrag(dge, Cursor.getDefaultCursor(), trans, this);
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
		dropTargetCell = -1;
		draggedIndex = -1;
		repaint();
	}

	// DropTargetListener events
	public void dragEnter(DropTargetDragEvent dtde) {
		if (dtde.getSource() != dropTarget)
			dtde.rejectDrag();
		else 
			dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);		
	}

	public void dragOver(DropTargetDragEvent dtde) {
		// figure out which cell it's over, no drag to self
		if (dtde.getSource() != dropTarget)
			dtde.rejectDrag();
		Point dragPoint = dtde.getLocation();
		int index = locationToIndex(dragPoint);		
		if (index == -1)
			dropTargetCell = -1;
		else {
			// why not drop it right away ?
			dropTargetCell = index;			
			if (dropTargetCell!=draggedIndex && dtde.getSource()==dropTarget) {
				try {
					Object dragged = dtde.getTransferable().getTransferData(localObjectFlavor);
					doMove(dragged);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		repaint();
	}

	public void drop(DropTargetDropEvent dtde) {
		if (dtde.getSource() != dropTarget) {
			dtde.rejectDrop();
			return;
		}
		dtde.acceptDrop(DnDConstants.ACTION_MOVE);
		dtde.dropComplete(true);
	}
	
	public void doMove(Object dragged) {
		boolean sourceBeforeTarget = (draggedIndex < dropTargetCell);
		int target = (sourceBeforeTarget ? dropTargetCell - 1 : dropTargetCell);
		DefaultListModel mod = (DefaultListModel) getModel();
		mod.remove(draggedIndex);
		mod.add(target, dragged);
		draggedIndex = dropTargetCell= target; // prepare for next move
	}
	
	
	public void dragEnter(DragSourceDragEvent dsde) {}
	public void dragExit(DragSourceEvent dse) {}
	public void dragOver(DragSourceDragEvent dsde) {}
	public void dropActionChanged(DragSourceDragEvent dsde) {}
	public void dragExit(DropTargetEvent dte) {}
	public void dropActionChanged(DropTargetDragEvent dtde) {}

	

	class RJLTransferable implements Transferable {
		Object object;

		public RJLTransferable(Object o) {
			object = o;
		}

		public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
			if (!isDataFlavorSupported(df))
				throw new UnsupportedFlavorException(df);
			return object;
		}

		public boolean isDataFlavorSupported(DataFlavor df) {
			return (df.equals(localObjectFlavor));
		}

		public DataFlavor[] getTransferDataFlavors() {
			return supportedFlavors;
		}
	}

	class ReorderableListCellRenderer extends DefaultListCellRenderer {
		boolean isTargetCell;
		boolean isLastItem;

		public ReorderableListCellRenderer() {
			super();
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus) {
			isTargetCell = (index == dropTargetCell);
			isLastItem = (index == list.getModel().getSize() - 1);
			boolean showSelected = isSelected & (dropTargetCell == -1);
			return super.getListCellRendererComponent(list, value, index,
					showSelected, hasFocus);
		}

		public void paintComponent(Graphics g) {
			if (isTargetCell) {
				setBackground(Color.green);
//				g.setColor(Color.black);
//				g.drawLine(0, 0, getSize().width, 0);
			}
			super.paintComponent(g);
		}
	}

}
