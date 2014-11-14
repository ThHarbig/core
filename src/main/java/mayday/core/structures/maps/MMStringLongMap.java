package mayday.core.structures.maps;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import mayday.core.structures.maps.MMStringLongEntries.SLEntry;

public class MMStringLongMap extends AbstractMap<String, Long> implements
		Map<String, Long> {

	/**
	 * The default initial capacity - MUST be a power of two.
	 */
	static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load factor used when none specified in constructor.
	 */
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * The table, resized as necessary. Length MUST Always be a power of two.
	 */
	transient MMStringLongEntries table2;
	transient long[] table; // the identifiers of table2 objects (0=nothing)

	/**
	 * The number of key-value mappings contained in this map.
	 */
	transient int size;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 * 
	 * @serial
	 */
	int threshold;

	/**
	 * The load factor for the hash table.
	 * 
	 * @serial
	 */
	final float loadFactor;

	/**
	 * The number of times this HashMap has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the HashMap or otherwise modify its internal structure (e.g., rehash).
	 * This field is used to make iterators on Collection-views of the HashMap
	 * fail-fast. (See ConcurrentModificationException).
	 */
	transient volatile int modCount;

	/**
	 * Constructs an empty <tt>HashMap</tt> with the specified initial capacity
	 * and load factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity
	 * @param loadFactor
	 *            the load factor
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative or the load factor is
	 *             nonpositive
	 */
	public MMStringLongMap(int stringLen, int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal initial capacity: "
					+ initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal load factor: "
					+ loadFactor);

		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;

		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new long[capacity];
		table2 = new MMStringLongEntries(stringLen);
		init();
	}

	/**
	 * Constructs an empty <tt>HashMap</tt> with the specified initial capacity
	 * and the default load factor (0.75).
	 * 
	 * @param initialCapacity
	 *            the initial capacity.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative.
	 */
	public MMStringLongMap(int stringLen, int initialCapacity) {
		this(stringLen, initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs an empty <tt>HashMap</tt> with the default initial capacity
	 * (16) and the default load factor (0.75).
	 */
	public MMStringLongMap(int stringLen) {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
		table = new long[DEFAULT_INITIAL_CAPACITY];
		table2 = new MMStringLongEntries(stringLen);
		init();
	}

	// internal utilities

	/**
	 * Initialization hook for subclasses. This method is called in all
	 * constructors and pseudo-constructors (clone, readObject) after HashMap
	 * has been initialized but before any entries have been inserted. (In the
	 * absence of this method, readObject would require explicit knowledge of
	 * subclasses.)
	 */
	void init() {
	}

	/**
	 * Applies a supplemental hash function to a given hashCode, which defends
	 * against poor quality hash functions. This is critical because HashMap
	 * uses power-of-two length hash tables, that otherwise encounter collisions
	 * for hashCodes that do not differ in lower bits. Note: Null keys always
	 * map to hash 0, thus index 0.
	 */
	static int hash(int h) {
		// This function ensures that hashCodes that differ only by
		// constant multiples at each bit position have a bounded
		// number of collisions (approximately 8 at default load factor).
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/**
	 * Returns index for hash code h.
	 */
	static int indexFor(int h, int length) {
		return h & (length - 1);
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * 
	 * @return the number of key-value mappings in this map
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 * 
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the value to which the specified key is mapped, or {@code null}
	 * if this map contains no mapping for the key.
	 * 
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a
	 * value {@code v} such that {@code (key==null ? k==null : key.equals(k))},
	 * then this method returns {@code v}; otherwise it returns {@code null}.
	 * (There can be at most one such mapping.)
	 * 
	 * <p>
	 * A return value of {@code null} does not <i>necessarily</i> indicate that
	 * the map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to {@code null}. The {@link #containsKey
	 * containsKey} operation may be used to distinguish these two cases.
	 * 
	 * @see #put(Object, Object)
	 */
	public Long get(Object key) {
		if (key == null)
			return getForNullKey();
		int hash = hash(key.hashCode());
		for (SLEntry e = table2.getEntry(table[indexFor(hash, table.length)]); e != null; e = e
				.getNext()) {
			Object k;
			if (e.getHash() == hash
					&& ((k = e.getKey()) == key || key.equals(k)))
				return e.getValue();
		}
		return null;
	}

	/**
	 * Offloaded version of get() to look up null keys. Null keys map to index
	 * 0. This null case is split out into separate methods for the sake of
	 * performance in the two most commonly used operations (get and put), but
	 * incorporated with conditionals in others.
	 */
	private Long getForNullKey() {
		for (SLEntry e = table2.getEntry(table[0]); e != null; e = e.getNext()) {
			if (e.getKey() == null)
				return e.getValue();
		}
		return null;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 * 
	 * @param key
	 *            The key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 *         key.
	 */
	public boolean containsKey(Object key) {
		return getEntry(key) != null;
	}

	/**
	 * Returns the entry associated with the specified key in the HashMap.
	 * Returns null if the HashMap contains no mapping for the key.
	 */
	final SLEntry getEntry(Object key) {
		int hash = (key == null) ? 0 : hash(key.hashCode());
		for (SLEntry e = table2.getEntry(table[indexFor(hash, table.length)]); e != null; e = e
				.getNext()) {
			Object k;
			if (e.getHash() == hash
					&& ((k = e.getKey()) == key || (key != null && key
							.equals(k))))
				return e;
		}
		return null;
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for the key, the old value is
	 * replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>.)
	 */
	public Long put(String key, Long value) {
		if (key == null)
			return putForNullKey(value);
		int hash = hash(key.hashCode());
		int i = indexFor(hash, table.length);
		for (SLEntry e = table2.getEntry(table[i]); e != null; e = e.getNext()) {
			Object k;
			if (e.getHash() == hash
					&& ((k = e.getKey()) == key || key.equals(k))) {
				Long oldValue = e.getValue();
				e.setValue(value);
				return oldValue;
			}
		}

		modCount++;
		addEntry(hash, key, value, i);
		return null;
	}

	/**
	 * Offloaded version of put for null keys
	 */
	private Long putForNullKey(Long value) {
		for (SLEntry e = table2.getEntry(table[0]); e != null; e = e.getNext()) {
			if (e.getKey() == null) {
				Long oldValue = e.getValue();
				e.setValue(value);
				return oldValue;
			}
		}
		modCount++;
		addEntry(0, null, value, 0);
		return null;
	}

//	/**
//	 * This method is used instead of put by constructors and pseudoconstructors
//	 * (clone, readObject). It does not resize the table, check for
//	 * comodification, etc. It calls createEntry rather than addEntry.
//	 */
//	private void putForCreate(String key, Long value) {
//		int hash = (key == null) ? 0 : hash(key.hashCode());
//		int i = indexFor(hash, table.length);
//
//		/**
//		 * Look for preexisting entry for key. This will never happen for clone
//		 * or deserialize. It will only happen for construction if the input Map
//		 * is a sorted map whose ordering is inconsistent w/ equals.
//		 */
//		for (SLEntry e = table2.getEntry(table[i]); e != null; e = e.getNext()) {
//			Object k;
//			if (e.getHash() == hash
//					&& ((k = e.getKey()) == key || (key != null && key
//							.equals(k)))) {
//				e.setValue(value);
//				return;
//			}
//		}
//
//		createEntry(hash, key, value, i);
//	}

//	private void putAllForCreate(Map<String, Long> m) {
//		for (Iterator<? extends Map.Entry<String, Long>> i = m.entrySet()
//				.iterator(); i.hasNext();) {
//			Map.Entry<String, Long> e = i.next();
//			putForCreate(e.getKey(), e.getValue());
//		}
//	}

	/**
	 * Rehashes the contents of this map into a new array with a larger
	 * capacity. This method is called automatically when the number of keys in
	 * this map reaches its threshold.
	 * 
	 * If current capacity is MAXIMUM_CAPACITY, this method does not resize the
	 * map, but sets threshold to Integer.MAX_VALUE. This has the effect of
	 * preventing future calls.
	 * 
	 * @param newCapacity
	 *            the new capacity, MUST be a power of two; must be greater than
	 *            current capacity unless current capacity is MAXIMUM_CAPACITY
	 *            (in which case value is irrelevant).
	 */
	void resize(int newCapacity) {
		long[] oldTable = table;
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		long[] newTable = new long[newCapacity];
		transfer(newTable);
		table = newTable;
		threshold = (int) (newCapacity * loadFactor);
	}

	/**
	 * Transfers all entries from current table to newTable.
	 */
	void transfer(long[] newTable) {
		long[] src = table;
		int newCapacity = newTable.length;
		for (int j = 0; j < src.length; j++) {
			SLEntry e = table2.getEntry(src[j]);
			if (e != null) {
				src[j] = 0;
				do {
					SLEntry next = e.getNext();
					int i = indexFor(e.getHash(), newCapacity);
					e.setNext(newTable[i]);
					newTable[i] = e.getIndex();
					e = next;
				} while (e != null);
			}
		}
	}

	/**
	 * Copies all of the mappings from the specified map to this map. These
	 * mappings will replace any mappings that this map had for any of the keys
	 * currently in the specified map.
	 * 
	 * @param m
	 *            mappings to be stored in this map
	 * @throws NullPointerException
	 *             if the specified map is null
	 */
	@SuppressWarnings("unchecked")
	public void putAll(Map m) {
		int numKeysToBeAdded = m.size();
		if (numKeysToBeAdded == 0)
			return;

		/*
		 * Expand the map if the map if the number of mappings to be added is
		 * greater than or equal to threshold. This is conservative; the obvious
		 * condition is (m.size() + size) >= threshold, but this condition could
		 * result in a map with twice the appropriate capacity, if the keys to
		 * be added overlap with the keys already in this map. By using the
		 * conservative calculation, we subject ourself to at most one extra
		 * resize.
		 */
		if (numKeysToBeAdded > threshold) {
			int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
			if (targetCapacity > MAXIMUM_CAPACITY)
				targetCapacity = MAXIMUM_CAPACITY;
			int newCapacity = table.length;
			while (newCapacity < targetCapacity)
				newCapacity <<= 1;
			if (newCapacity > table.length)
				resize(newCapacity);
		}

		for (Iterator<? extends Map.Entry<String, Long>> i = m.entrySet()
				.iterator(); i.hasNext();) {
			Map.Entry<String, Long> e = i.next();
			put(e.getKey(), e.getValue());
		}
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 * 
	 * @param key
	 *            key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>.)
	 */
	public Long remove(Object key) {
		SLEntry e = removeEntryForKey(key);
		return (e == null ? null : e.getValue());
	}

	/**
	 * Removes and returns the entry associated with the specified key in the
	 * HashMap. Returns null if the HashMap contains no mapping for this key.
	 */
	final SLEntry removeEntryForKey(Object key) {
		int hash = (key == null) ? 0 : hash(key.hashCode());
		int i = indexFor(hash, table.length);
		SLEntry prev = table2.getEntry(table[i]);
		SLEntry e = prev;

		while (e != null) {
			SLEntry next = e.getNext();
			Object k;
			if (e.getHash() == hash
					&& ((k = e.getKey()) == key || (key != null && key
							.equals(k)))) {
				modCount++;
				size--;
				if (prev == e)
					table[i] = next.getIndex();
				else
					prev.setNext(next);
				return e;
			}
			prev = e;
			e = next;
		}

		return e;
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after
	 * this call returns.
	 */
	public void clear() {
		modCount++;
		table2.clear();
		for (int i = 0; i < table.length; i++)
			table[i] = 0;
		size = 0;
	}

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the specified
	 * value.
	 * 
	 * @param value
	 *            value whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map maps one or more keys to the specified
	 *         value
	 */
	public boolean containsValue(Object value) {
		if (value == null)
			return containsNullValue();

		long[] tab = table;
		for (int i = 0; i < tab.length; i++)
			for (SLEntry e = table2.getEntry(tab[i]); e != null; e = e.getNext())
				if (value.equals(e.getValue()))
					return true;
		return false;
	}

	/**
	 * Special-case code for containsValue with null argument
	 */
	private boolean containsNullValue() {
		long[] tab = table;
		for (int i = 0; i < tab.length; i++)
			for (SLEntry e = table2.getEntry(tab[i]); e != null; e = e.getNext())
				if (e.getValue() == null)
					return true;
		return false;
	}


	/**
	 * Adds a new entry with the specified key, value and hash code to the
	 * specified bucket. It is the responsibility of this method to resize the
	 * table if appropriate.
	 * 
	 * Subclass overrides this to alter the behavior of put method.
	 */
	void addEntry(int hash, String key, Long value, int bucketIndex) {
		table[bucketIndex] = table2.addEntry(key, value, table[bucketIndex], hash);
		if (size++ >= threshold)
			resize(2 * table.length);
	}

	/**
	 * Like addEntry except that this version is used when creating entries as
	 * part of Map construction or "pseudo-construction" (cloning,
	 * deserialization). This version needn't worry about resizing the table.
	 * 
	 * Subclass overrides this to alter the behavior of HashMap(Map), clone, and
	 * readObject.
	 */
	void createEntry(int hash, String key, Long value, int bucketIndex) {
		table[bucketIndex] = table2.addEntry(key, value, table[bucketIndex], hash);
		size++;
	}

	private abstract class HashIterator<E> implements Iterator<E> {
		SLEntry next; // next entry to return
		int expectedModCount; // For fast-fail
		int index; // current slot
		Entry<String, Long> current; // current entry

		HashIterator() {
			expectedModCount = modCount;
			if (size > 0) { // advance to first entry
				long[] t = table;
				while (index < t.length && (next = table2.getEntry(t[index++])) == null)
					;
			}
		}

		public final boolean hasNext() {
			return next != null;
		}

		final SLEntry nextEntry() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			SLEntry e = next;
			if (e == null)
				throw new NoSuchElementException();

			if ((next = e.getNext()) == null) {
				long[] t = table;
				while (index < t.length && (next =table2.getEntry(t[index++])) == null)
					;
			}
			current = e;
			return e;
		}

		public void remove() {
			if (current == null)
				throw new IllegalStateException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			Object k = current.getKey();
			current = null;
			MMStringLongMap.this.removeEntryForKey(k);
			expectedModCount = modCount;
		}

	}

	private final class ValueIterator extends HashIterator<Long> {
		public Long next() {
			return nextEntry().getValue();
		}
	}

	private final class KeyIterator extends HashIterator<String> {
		public String next() {
			return nextEntry().getKey();
		}
	}

	private final class EntryIterator extends
			HashIterator<Map.Entry<String, Long>> {
		public Map.Entry<String, Long> next() {
			return nextEntry();
		}
	}

	// Subclass overrides these to alter behavior of views' iterator() method
	Iterator<String> newKeyIterator() {
		return new KeyIterator();
	}

	Iterator<Long> newValueIterator() {
		return new ValueIterator();
	}

	Iterator<Map.Entry<String, Long>> newEntryIterator() {
		return new EntryIterator();
	}

	Set<Map.Entry<String,Long>> entrySet;
	
	/**
	 * Returns a {@link Set} view of the mappings contained in this map. The set
	 * is backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own <tt>remove</tt> operation, or
	 * through the <tt>setValue</tt> operation on a map entry returned by the
	 * iterator) the results of the iteration are undefined. The set supports
	 * element removal, which removes the corresponding mapping from the map,
	 * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>
	 * , <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support
	 * the <tt>add</tt> or <tt>addAll</tt> operations.
	 * 
	 * @return a set view of the mappings contained in this map
	 */
	public Set<Map.Entry<String, Long>> entrySet() {
		return entrySet0();
	}

	private Set<Map.Entry<String, Long>> entrySet0() {
		Set<Map.Entry<String, Long>> es = entrySet;
		return es != null ? es : (entrySet = new EntrySet());
	}

	private final class EntrySet extends AbstractSet<Map.Entry<String, Long>> {
		public Iterator<Map.Entry<String, Long>> iterator() {
			return newEntryIterator();
		}

		public boolean contains(Object o) {
			if (!(o instanceof SLEntry))
				return false;
			SLEntry e = (SLEntry) o;
			SLEntry candidate = getEntry(e.getKey());
			return candidate != null && candidate.equals(e);
		}

		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return size;
		}

		public void clear() {
			MMStringLongMap.this.clear();
		}
	}
	
	public void finalize() {
		table2.finalize();
		table = null;
	}
	
}
