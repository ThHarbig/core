package mayday.core.gui.dragndrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;

public class MaydayTransferable implements Transferable {

	Object[] myObjects;
	DataFlavor[] myFlavors;
	Class<?> type;
	
	public MaydayTransferable(Class<?> type, Object... o) {
		myObjects = o;
		this.type = type;
		myFlavors = DragSupportManager.getFlavorsFor(type);
	}
	
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		DragSupportPlugin dsp = DragSupportManager.getSupportFor(type, flavor);
		if (dsp==null)
			throw new UnsupportedFlavorException(flavor);

		return dsp.getTransferData(myObjects);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {		
		return myFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return Arrays.asList(myFlavors).contains(flavor);
	}

}
