package mayday.core.structures;

public class LongTools {
	
	public static long[] masks_to;
	public static long[] masks_from;
	public static long[] masks_pos;
		
	static {
		masks_to = new long[64];
		long newmask = 1;
		for (int i=0; i!=64; ++i) {
			masks_to[i] = newmask;
//			System.out.println("to "+i+": "+debugString(newmask));
			newmask <<= 1;
			newmask += 1;
		}			
		masks_from = new long[64];
		newmask = masks_to[63];
		for (int i=0; i!=64; ++i) {
			masks_from[i] = newmask;
//			System.out.println("from "+i+": "+debugString(newmask));
			newmask <<= 1;
		}
		masks_pos = new long[64];
		for (int i=0; i!=64; ++i) {
			masks_pos[i] = fromTo(i, i);
		}
	}
	
	public static long fromTo(int from, int to) {		
//		System.out.println(debugString(masks_to[to]));
//		System.out.println(debugString(masks_from[from]));
//		System.out.println(debugString(masks_to[to] & masks_from[from]));
		
		return masks_to[to] & masks_from[from];
	}
	
	public static boolean covered(long mask, int position) {
		long posMask = masks_pos[position];
//		System.out.println(debugString(mask));
//		System.out.println(debugString(posMask));
//		System.out.println(debugString(posMask & mask));
		return (posMask & mask) != 0;
	}
	
	public static String debugString(long l) {
		String s = Long.toBinaryString(l);
		while (s.length()<64)
			s = "0"+s;
		return s;
	}
	
	public static long extract(long mask, int from, int to) {
		long posMask = fromTo(0, to-from);		
//		System.out.println(debugString(mask));
		mask >>= from;
//		System.out.println(debugString(mask));
//		System.out.println(debugString(posMask));
		mask &= posMask;
//		System.out.println(debugString(mask));
		return mask;
	}
	
	public static long set(long mask, int from, int to, long bits) {
		long posMask = fromTo(from, to);
//		System.out.println(debugString(posMask));
//		System.out.println(debugString(bits));
		bits <<= from;
//		System.out.println(debugString(bits));
		bits &= posMask; // protect unaffected bits
//		System.out.println(debugString(bits));
//		System.out.println(debugString(mask));
		long clearMask = 0;
		if (from>0) {
			long pm1=fromTo(0,from-1);
			clearMask |= pm1; // clear affected bits			
		} if (to<63) {
			long pm2=fromTo(to+1,63);
			clearMask |= pm2;
		}
//		System.out.println(debugString(clearMask));
		mask &= clearMask; // clear affected bits
		mask |= bits;
//		System.out.println(debugString(mask));
		return mask;
	}
	
}
