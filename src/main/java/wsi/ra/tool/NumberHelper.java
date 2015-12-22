/**
 *  Filename: $RCSfile: NumberHelper.java,v $ Purpose: Some helper methods for
 *  calling external programs. Language: Java Compiler: JDK 1.2 Authors: Fred
 *  Rapp, Joerg K. Wegner Version: $Revision: 1.2 $ $Date: 2008/12/17 15:18:00 $
 *  $Author: battke $ Copyright (c) Dept. Computer Architecture, University of
 *  Tuebingen, Germany
 */
package wsi.ra.tool;


/*============================================================================
 * IMPORTS
 *============================================================================ */
import java.math.BigInteger;


//import org.apache.log4j.Category;

/*============================================================================
 *  CLASS DECLARATION
 *============================================================================ */

/**
 *  Some helper methods for calling external programs.
 *
 * @author     wegnerj
 */
public class NumberHelper
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /*-------------------------------------------------------------------------*
     * private static member variables
     *------------------------------------------------------------------------- */
    /*-------------------------------------------------------------------------*
     * constructor
     *------------------------------------------------------------------------- */

    /**
     *  Don't let anyone instantiate this class
     */
    private NumberHelper()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /*-------------------------------------------------------------------------*
     * private static methods
     *------------------------------------------------------------------------- */
    public static String bitsFromByte(byte something)
    {
        int foo = something & 0xFF;
        int length = 8;
        int length_1 = length - 1;
        StringBuffer out = new StringBuffer(20);

        if (length != 8)
        {
            return out.toString();
        }

        for (int si = 0; si < length; si++)
        {
            if ((new BigInteger((foo + " ").trim()).divide(new BigInteger("2").pow(length_1 -
                            si))).intValue() > 0)
            {
                foo = (new BigInteger((foo + " ").trim()).subtract(new BigInteger(
                            "2").pow(length_1 - si))).intValue();
                out.append('1');
            }
            else
            {
                out.append('0');
            }
        }

        return out.toString();
    }

    public static byte byteFromBits(String binarystring)
    {
        return (byte) intFromBits(binarystring, 8);
    }

    /**
     *  Description of the Method
     *
     * @param  binarystring  Description of the Parameter
     * @return               Description of the Return Value
     */
    public static int intFromBits(String binarystring, int length)
    {
        int something = 0;
//        byte foo = (byte) something;

        if ((length != 8) && (length != 16))
        {
            return something;
        }

        int length_1 = length - 1;

        if (binarystring.length() == length)
        {
            for (int si = 0; si < length; si++)
            {
                if (new Byte((byte) binarystring.charAt(si)).compareTo(
                            new Byte((byte) '1')) == 0)
                {
                    something = something +
                        ((new BigInteger("2").pow(length_1 - si)).intValue());
                }
                else
                {
                }
            }

//            foo = (byte) something;
        }

        return something;
    }

    public static int intFromBits(String binarystring)
    {
        return intFromBits(binarystring, 16);
    }

    public static void main(String[] args)
    {
        System.out.println("00000001:" + NumberHelper.byteFromBits("00000001") +
            ":" +
            NumberHelper.bitsFromByte(NumberHelper.byteFromBits("00000001")));
        System.out.println("00000010:" + NumberHelper.byteFromBits("00000010") +
            ":" +
            NumberHelper.bitsFromByte(NumberHelper.byteFromBits("00000010")));
        System.out.println("00000100:" + NumberHelper.byteFromBits("00000100") +
            ":" +
            NumberHelper.bitsFromByte(NumberHelper.byteFromBits("00000100")));
        System.out.println("00001000:" + NumberHelper.byteFromBits("00001000") +
            ":" +
            NumberHelper.bitsFromByte(NumberHelper.byteFromBits("00001000")));
        System.out.println("00010000:" + NumberHelper.byteFromBits("00010000") +
            ":" +
            NumberHelper.bitsFromByte(NumberHelper.byteFromBits("00010000")));
        System.out.println("00010010:" + NumberHelper.byteFromBits("00010010") +
            ":" +
            NumberHelper.bitsFromByte(NumberHelper.byteFromBits("00010010")));
        System.out.println("10000000:" + NumberHelper.byteFromBits("10000000") +
            ":" +
            NumberHelper.bitsFromByte(NumberHelper.byteFromBits("10000000")));
        System.out.println("11111111:" + NumberHelper.byteFromBits("11111111") +
            ":" +
            NumberHelper.bitsFromByte(NumberHelper.byteFromBits("11111111")));
        System.out.println("0000000000000001:" +
            NumberHelper.intFromBits("0000000000000001"));
        System.out.println("0000000000000010:" +
            NumberHelper.intFromBits("0000000000000010"));
        System.out.println("0000000000000100:" +
            NumberHelper.intFromBits("0000000000000100"));
        System.out.println("0000000000001000:" +
            NumberHelper.intFromBits("0000000000001000"));
        System.out.println("0000000000010000:" +
            NumberHelper.intFromBits("0000000000010000"));
        System.out.println("0000000000010010:" +
            NumberHelper.intFromBits("0000000000010010"));
        System.out.println("1000000000000000:" +
            NumberHelper.intFromBits("1000000000000000"));
        System.out.println("0100000000000000:" +
            NumberHelper.intFromBits("0100000000000000"));
        System.out.println("0000000011111111:" +
            NumberHelper.intFromBits("0000000011111111"));
        System.out.println("1111111111111111:" +
            NumberHelper.intFromBits("1111111111111111"));
        System.out.println("0111111111111111:" +
            NumberHelper.intFromBits("0111111111111111"));
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
