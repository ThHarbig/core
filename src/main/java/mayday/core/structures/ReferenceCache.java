package mayday.core.structures;

import java.lang.ref.WeakReference;


public class ReferenceCache< CachedType > {
	
	protected WeakReference<Object>[] masters;
	protected WeakReference<CachedType> cacheContent;
	
	public ReferenceCache(CachedType content, Object... masterObjects) {
		setCache(content, masterObjects);
	}
	
	public ReferenceCache() {
		
	}
	
	public synchronized CachedType getCache(Object... masterObjects) {
		if (cacheContent == null)
			return null;
		CachedType cc = cacheContent.get();
		if (cc==null)
			return cc;
		for (int i=0; i!=masterObjects.length; ++i) {
 			if (masters[i].get()!=masterObjects[i])
 				return null;
		}
 		return cc;
	}

	
	@SuppressWarnings("unchecked")
	public synchronized void setCache(CachedType content, Object... masterObjects) {
		masters = new WeakReference[masterObjects.length];
		for (int i=0; i!=masters.length; ++i) {
			masters[i] = new WeakReference<Object>(masterObjects[i]);
		}
		cacheContent = new WeakReference<CachedType>(content);

	}
	
	
}
