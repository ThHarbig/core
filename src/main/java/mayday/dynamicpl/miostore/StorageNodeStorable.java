package mayday.dynamicpl.miostore;

import mayday.core.io.StorageNode;

public interface StorageNodeStorable {

	public StorageNode toStorageNode();
	public void fromStorageNode(StorageNode storageNode);
	
}
