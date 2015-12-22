package mayday.core.structures.generic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class TypedMatrix<E> {

	private final int n,m;
	private final Object elements;
	private E defaultValue = null;
	private boolean primitive;
	private Class<?> typeClass;
	private ArrayGetterSetter arrayaccess;
	
	private Class<E> ctr_elementClass;
	private boolean ctr_noPrimitive;
	
	public TypedMatrix(int columns, int rows, Class<E> elementClass) {
		this(columns,rows,elementClass,false);
	}
	
	// this is the main constructor
	public TypedMatrix(int columns, int rows, Class<E> elementClass, boolean noPrimitive) {
		ctr_noPrimitive = noPrimitive;
		ctr_elementClass = elementClass;
		n=columns; m=rows;
		elements = createTypedArray(elementClass, rows*columns, noPrimitive);
		// speed up for often-used primitive types
		if (primitive) {
			if (typeClass == Double.TYPE)
				arrayaccess = new ArrayGetterSetter<Double>() {

				public Double get(Object array, int index) {
					return ((double[])array)[index];
				}

				public void set(Object array, int index, Double value) {
					((double[])array)[index] = value;
				}
			};
			if (typeClass == Integer.TYPE) {
				arrayaccess = new ArrayGetterSetter<Integer>() {

					public Integer get(Object array, int index) {
						return ((int[])array)[index];
					}

					public void set(Object array, int index, Integer value) {
						((int[])array)[index] = value;
					}
				};
			}
			if (arrayaccess==null) {
				// slow accessor for all undefined types
				arrayaccess = new ArrayGetterSetter<E>() {

					public E get(Object array, int index) {
						return (E)Array.get(array, index);
					}

					public void set(Object array, int index, E value) {
						Array.set(array,index,value);
					}
				};
			}
		} else {
			arrayaccess = new ArrayGetterSetter<E>() {

				public E get(Object array, int index) {
					return ((E[])array)[index];
				}

				public void set(Object array, int index, E value) {
					((E[])array)[index]=value;
				}
			};	
		}
	}
	
	public TypedMatrix<E> clone() {		
		TypedMatrix<E> clone = new TypedMatrix<E>(n,m, ctr_elementClass, ctr_noPrimitive);
		clone.defaultValue= defaultValue;
		for (int i=0; i!=n*m; ++i)
			clone.arrayaccess.set(clone.elements,i, this.arrayaccess.get(elements,i));
		return clone;
	}
	
	public TypedMatrix(int columns, int rows, E Default) {
		this(columns,rows, (Class<E>)Default.getClass());
		defaultValue=Default;
		if (primitive)
			for (int i=0; i!=columns*rows; ++i)
				Array.set(elements,i,defaultValue);
	}
	
	public E get(int x, int y) {
		Object ret = arrayaccess.get(elements, x*m+y); 
		if (ret==null)
			return defaultValue;
		return (E)ret;
	}

	public void set(int x, int y, E o) {
		arrayaccess.set(elements, x*m+y, o);
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		for(int row=0; row!=getRowCount(); row++) {
			s.append(getRow(row).toString());
			s.append("\n");
		}
		return s.toString();
	}

	/* (non-Javadoc)
	 * @see mayday.raw.data.IMatrix#getColumnCount()
	 */
	public int getColumnCount() {
		return n;		
	}
	
	/* (non-Javadoc)
	 * @see mayday.raw.data.IMatrix#getRowCount()
	 */
	public int getRowCount() {
		return m;
	}
	
	/* (non-Javadoc)
	 * @see mayday.raw.data.IMatrix#getColumn(int)
	 */
	public TypedMatrixVector<E> getColumn(final int x) {
		return new TypedMatrixVector<E>(new MatrixAccessor<E>() {
			public E Get(int position) {
				return get(x,position);
			}
			public void Set(int position, E o) {
				set(x, position, o);
			}
			public int size() {
				return getRowCount();
			}
		});
	}

	
	/* (non-Javadoc)
	 * @see mayday.raw.data.IMatrix#getRow(int)
	 */
	public TypedMatrixVector<E> getRow(final int y) {
		return new TypedMatrixVector<E>(new MatrixAccessor<E>() {
			public E Get(int position) {
				return get(position,y);
			}
			public void Set(int position, E o) {
				set(position,y, o);
			}
			public int size() {
				return getColumnCount();
			}
		});
	}
	
	interface MatrixAccessor<E> {
		public E Get(int position);
		public void Set(int position, E o);
		public int size();
	}
	
	interface ArrayGetterSetter<E> {
		public void set(Object array, int index, E value);
		public E get(Object array, int index);
	}
	
	
	public Object createTypedArray(Class<E> elementClass, int size, boolean noPrimitive) {

		typeClass = elementClass;
		primitive=false;

		// catch primitives types 		
		if (!noPrimitive) {
			try {
				Field typeField = elementClass.getDeclaredField("TYPE");
				Object typeValue = typeField.get(null);
				typeClass = (Class<?>)typeValue;
				primitive = true;
			} catch (NoSuchFieldException nsfe) {
				;
			} catch (IllegalAccessException iae) {
				;
			}
		}

		return Array.newInstance(typeClass, size);
	}
	
	
	public void dump(OutputStream os, Object[] rownames) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		for (int i=0; i!=getRowCount(); ++i) {
			//bw.write(get(0,i).toString());
			bw.write(rownames[i].toString());
			for (int j=0; j!=getColumnCount(); ++j) {
				bw.write("\t");
				bw.write(get(j,i).toString());
			}
			bw.write("\n");
		}
		bw.flush();
		bw.close();
	}
	
	private static class MemoryMeasurement{
		private static long fSLEEP_INTERVAL = 100;

		private static long getMemoryUse(){
			long totalMemory = Runtime.getRuntime().totalMemory();
			long freeMemory = Runtime.getRuntime().freeMemory();
			return (totalMemory - freeMemory);
		}

		public static void putOutTheGarbage() {
			collectGarbage();
			collectGarbage();
		}

		private static void collectGarbage() {
			try {
				System.gc();
				Thread.sleep(fSLEEP_INTERVAL);
				System.runFinalization();
				Thread.sleep(fSLEEP_INTERVAL);
			}
			catch (InterruptedException ex){
				ex.printStackTrace();
			}
		}
	}
	
	public static long[] check(Runnable r) {
		long beforemem=0, aftermem=0, mem=0;
		long beforetime=0, aftertime=0, time=0;
		MemoryMeasurement.putOutTheGarbage();
		beforemem = MemoryMeasurement.getMemoryUse(); 
		beforetime = System.currentTimeMillis();
		r.run();
		aftertime = System.currentTimeMillis();
		time = aftertime - beforetime;			
		aftermem = MemoryMeasurement.getMemoryUse();
		mem = aftermem - beforemem;
		System.out.println("["+mem+" "+time+"ms] ");
		return new long[]{mem,time};
	}
		
	public static void main(String[] a) {
		/*
		 Matrix<Double> dm = new Matrix<Double>(5,5,0.0);		 
		Matrix<String> sm = new Matrix<String>(5,5,"b");
		System.out.println(dm.getRow(3));
		System.out.println(sm.getColumn(4));
		dm.set(4,3,2.0);
		sm.set(4,3,"a");
		*/

		long nativemem=0, complexmem=0, arraymem=0;
		long nativetime=0, complextime=0, arraytime=0;
		int iter=a.length==0?10:  Integer.parseInt(a[0]);
		
		for (int i=0; i!=iter; ++i) {
			long[] res = check(new Runnable() {

				public void run() {
					TypedMatrix<Double> m_primitive = new TypedMatrix<Double>(1000, 1000, Double.class, false);
					for (int x=0; x!=m_primitive.getRowCount(); ++x)
						for (int y=0; y!=m_primitive.getColumnCount(); ++y)
							m_primitive.set(x,y,(double)x*m_primitive.getColumnCount()+y);
				}
			});
			nativemem+=res[0];
			nativetime+=res[1];
		}
		
		for (int i=0; i!=iter; ++i) {
			long[] res = check(new Runnable() {

				public void run() {
					TypedMatrix<Double> m_complex = new TypedMatrix<Double>(1000, 1000, Double.class, true);
					for (int x=0; x!=m_complex.getRowCount(); ++x)
						for (int y=0; y!=m_complex.getColumnCount(); ++y)
							m_complex.set(x,y,(double)x*m_complex.getColumnCount()+y);
				}
			});
			complexmem+=res[0];
			complextime+=res[1];
		}
		
		for (int i=0; i!=iter; ++i) {
			long[] res = check(new Runnable() {

				public void run() {
					double[] primitivearray = new double[1000*1000];
					for (int x=0; x!=1000; ++x)
						for (int y=0; y!=1000; ++y)
							//Array.set(primitivearray, x*1000+y,x*1000+y);
							primitivearray[x*1000+y]=x*1000+y;
				}
			});
			arraymem+=res[0];
			arraytime+=res[1];
		}

		

		
		System.out.println("\n--- TOTALS:");
		System.out.println("double[]           "+arraymem/iter+"\t  time: "+arraytime/iter+"ms");
		System.out.println("Primitive Doubles: "+nativemem/iter+"\t  time: "+nativetime/iter+"ms");
		System.out.println("Complex Doubles:   "+complexmem/iter+"\t  time: "+complextime/iter+"ms");
		
	}
	
}