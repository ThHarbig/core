package mayday.core.gui.dragndrop;

import java.awt.Component;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public abstract class MaydayTransferHandler<SupportedType> extends TransferHandler {

	protected Class<SupportedType> supportType;
	
	public MaydayTransferHandler(Class<SupportedType> supType) {
		supportType = supType;
	}
	
	public boolean canImport(TransferHandler.TransferSupport info) {
		Transferable tf = info.getTransferable();		
		return DragSupportManager.getSupportFor(supportType, tf.getTransferDataFlavors())!=null;
	}

	protected Transferable createTransferable(JComponent c) {
		MaydayTransferable mtf = new MaydayTransferable(supportType, getDragObject(c));
		return mtf;
	}
	
	public abstract SupportedType[] getDragObject(JComponent c);

	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;
	}

	public boolean importData(TransferHandler.TransferSupport info) {
		if (!info.isDrop()) 
			return false;		
		
		DragSupportPlugin dsp = DragSupportManager.getSupportFor(supportType, info.getDataFlavors());
		if (dsp==null)
			return false;
		
		dsp.setContext(getContextObject());
		SupportedType[] droppedObjects = dsp.processDrop(supportType, info.getTransferable());		
		processDrop(info.getComponent(), droppedObjects, info);

		return true;
	}

	protected abstract void processDrop(Component c, SupportedType[] droppedObjects, TransferHandler.TransferSupport info);
	
	protected abstract Object getContextObject();
}
