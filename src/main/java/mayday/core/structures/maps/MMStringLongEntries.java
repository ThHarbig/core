package mayday.core.structures.maps;

import java.util.Map;

import mayday.core.structures.natives.mmap.MMIntArray;
import mayday.core.structures.natives.mmap.MMLongArray;
import mayday.core.structures.natives.mmap.MMStringArray;

public class MMStringLongEntries {

	MMStringArray keys;
	MMLongArray values;
	MMIntArray hash; 
	MMLongArray next; // pointer to another index
	
	public MMStringLongEntries(int stringLen) {
		keys = new MMStringArray(1000,stringLen,true,true,false);
		values = new MMLongArray(1000);
		hash = new MMIntArray(1000);
		next = new MMLongArray(1000);
	}
	
	public long size() {
		return keys.size();
	}
	
	public void setSize(long size) {
		keys.ensureSize(size);
		values.ensureSize(size);
		next.ensureSize(size);
		hash.ensureSize(size);
	}
	
	public void clear() {
		keys.trimToSize(0);
		values.trimToSize(0);
		next.trimToSize(0);
		hash.trimToSize(0);
	}
	
	public void finalize() {
		keys.finalize();
		values.finalize();
		hash.finalize();
		next.finalize();
	}
	
	public SLEntry getEntry(long index) {
		if (index==0)
			return null;
		return new SLEntry(index-1);
	}
	
	public long addEntry(String key, Long value, long nextIndex, int theHash) {
		long index = keys.add(key);
		values.add(value);
		hash.add(theHash);
		next.add(nextIndex);
		// SLEntry ret = new SLEntry(index);
		return index+1;
	}
	
	public class SLEntry implements Map.Entry<String, Long> {
		private long index;
		
		public SLEntry(long index) {
			this.index = index;
		}
		
		public String getKey() {
			return keys.get(index);
		}

		@Override
		public Long getValue() {
			return values.get(index);
		}

		@Override
		public Long setValue(Long value) {
			long old = values.get(index);
			values.set(index, value);
			return old;
		}
		
		public SLEntry getNext() {
			long ne = next.get(index);
			if (ne==0)
				return null;			
			return new SLEntry(ne-1);
		}
		
		public long getIndex() {
			return index+1;
		}
		
		public void setNext(SLEntry ne) {
			next.set(index,ne.index);
		}
		
		public void setNext(long nindex) {
			next.set(index,nindex);
		}
		
		public int getHash() {
			return hash.get(index);
		}
		
		public void setHash(int thash) {
			hash.set(index, thash);
		}
		
		@SuppressWarnings("unchecked")
		public final boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        public final int hashCode() {
            return (getKey()==null   ? 0 : getKey().hashCode()) ^
                   (getValue()==null ? 0 : getValue().hashCode());
        }

        public final String toString() {
            return getKey() + "=" + getValue();
        }
	}
	

}
