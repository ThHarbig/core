package mayday.core.structures;

public class ByteTools {
	
	public static byte[] masks_to;
	public static byte[] masks_from;
	public static byte[] masks_pos;
		
	static {
		masks_to = new byte[8];
		byte newmask = 1;
		for (int i=0; i!=8; ++i) {
			masks_to[i] = newmask;
//			System.out.println("to "+i+": "+debugString(newmask));
			newmask <<= 1;
			newmask += 1;
		}			
		masks_from = new byte[8];
		newmask = masks_to[7];
		for (int i=0; i!=8; ++i) {
			masks_from[i] = newmask;
//			System.out.println("from "+i+": "+debugString(newmask));
			newmask <<= 1;
		}
		masks_pos = new byte[8];
		for (int i=0; i!=8; ++i) {
			masks_pos[i] = fromTo(i, i);
		}
	}
	
	public static byte fromTo(int from, int to) {		
//		System.out.println(debugString(masks_to[to]));
//		System.out.println(debugString(masks_from[from]));
//		System.out.println(debugString(masks_to[to] & masks_from[from]));
		
		return (byte)(masks_to[to] & masks_from[from]);
	}
	
	public static boolean covered(long mask, int position) {
		byte posMask = masks_pos[position];
//		System.out.println(debugString(mask));
//		System.out.println(debugString(posMask));
//		System.out.println(debugString(posMask & mask));
		return (posMask & mask) != 0;
	}
	
	public static String debugString(long l) {
		String s = Long.toBinaryString(l);
		while (s.length()<8)
			s = "0"+s;
		return s;
	}
	
	public static byte extract(byte mask, int from, int to) {
		byte posMask = fromTo(0, to-from);		
//		System.out.println(debugString(mask));
		mask >>= from;
//		System.out.println(debugString(mask));
//		System.out.println(debugString(posMask));
		mask &= posMask;
//		System.out.println(debugString(mask));
		return mask;
	}
	
	public static byte set(byte mask, int from, int to, byte bits) {
		byte posMask = fromTo(from, to);
//		System.out.println(debugString(posMask));
//		System.out.println(debugString(bits));
		bits <<= from;
//		System.out.println(debugString(bits));
		bits &= posMask; // protect unaffected bits
//		System.out.println(debugString(bits));
//		System.out.println(debugString(mask));
		long clearMask = 0;
		if (from>0) {
			byte pm1=fromTo(0,from-1);
			clearMask |= pm1; // clear affected bits			
		} if (to<7) {
			byte pm2=fromTo(to+1,7);
			clearMask |= pm2;
		}
//		System.out.println(debugString(clearMask));
		mask &= clearMask; // clear affected bits
		mask |= bits;
//		System.out.println(debugString(mask));
		return mask;
	}
	
}
