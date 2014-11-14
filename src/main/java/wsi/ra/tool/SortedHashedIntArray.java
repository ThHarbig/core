/**
 *  Filename: $RCSfile: SortedHashedIntArray.java,v $ Purpose: Some helper
 *  methods for calling external programs. Language: Java Compiler: JDK 1.2
 *  Authors: Fred Rapp, Joerg K. Wegner Version: $Revision: 1.2 $ $Date:
 *  2002/09/04 07:48:50 $ $Author: battke $ Copyright (c) Dept. Computer
 *  Architecture, University of Tuebingen, Germany
 */
package wsi.ra.tool;

import java.util.Arrays;


//import org.apache.log4j.Category;

/**
 *  Some helper methods for calling external programs.
 *
 * @author     wegnerj
 */
public class SortedHashedIntArray extends HashedIntArray
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Don't let anyone instantiate this class
     */
    public SortedHashedIntArray()
    {
    }

    /**
     *  Constructor for the SortedHashedIntArray object
     *
     * @param  _array  Description of the Parameter
     */
    public SortedHashedIntArray(int[] _array)
    {
        setArray(_array);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Sets the array attribute of the SortedHashedIntArray object
     *
     * @param  _array  The new array value
     */
    @Override
	public void setArray(int[] _array)
    {
        super.setArray(_array);
        Arrays.sort(array);
    }

    /**
     *  Gets the contained attribute of the SortedHashedIntArray object
     *
     * @param  _shia  Description of the Parameter
     * @return        The contained value
     */
    public final boolean isContained(SortedHashedIntArray _shia)
    {
        return _shia.contains(this);
    }

    /**
     *  Description of the Method
     *
     * @param  _shia  Description of the Parameter
     * @return        Description of the Return Value
     */
    public boolean contains(SortedHashedIntArray _shia)
    {
        if (_shia.array.length > this.array.length)
        {
            return false;
        }

        if (_shia.array.length == this.array.length)
        {
            return equals(_shia);
        }

        int length = _shia.array.length;
        int tLength = this.array.length;
        int index = 0;
        boolean found;

        for (int i = 0; i < length; i++)
        {
            found = false;

            do
            {
                if (array[index] == _shia.array[i])
                {
                    found = true;
                }

                index++;
            }
             while (!found && (index < tLength));

            if (!found)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Number of equal entries.
     * If one array contains the same number more than once all this number are counted also.
     *
     * @param  _shia  Description of the Parameter
     * @return        Description of the Return Value
     */
    public int equalEntries(SortedHashedIntArray _shia)
    {
        int counter = 0;
        int length = _shia.array.length;
        int tLength = this.array.length;
        int index = 0;
//        boolean found;

        // if you want count identical number only once a time
        // remove this comments !!!
        //    int last=_shia.array[0]-1;
        for (int i = index; i < length; i++)
        {
            for (int ii = 0; ii < tLength; ii++)
            {
                if (array[ii] > _shia.array[i])
                {
                    index = i + 1;

                    break;
                }

                if (array[ii] == _shia.array[i])
                {
                    //          if(last!=_shia.array[i])
                    //          {
                    counter++;

                    //            last=_shia.array[i];
                    //          }
                    break;
                }
            }
        }

        return counter;
    }

    /**
     *  The main program for the SortedHashedIntArray class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        int index = 1;
        int[] test = new int[]{5, 2, 7, 4, 9, 6};
        SortedHashedIntArray hashArr = new SortedHashedIntArray();
        SortedHashedIntArray hashArr2 = new SortedHashedIntArray();

        hashArr.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString());
        test = new int[]{2, 5, 4};
        hashArr2.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString() + " contains: " +
            hashArr2.toString() + ": " + hashArr.contains(hashArr2) +
            " equalCounts: " + hashArr.equalEntries(hashArr2));
        test = new int[]{9, 4, 6};
        hashArr2.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString() + " contains: " +
            hashArr2.toString() + ": " + hashArr.contains(hashArr2) +
            " equalCounts: " + hashArr.equalEntries(hashArr2));
        test = new int[]{9, 4, 1};
        hashArr2.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString() + " contains: " +
            hashArr2.toString() + ": " + hashArr.contains(hashArr2) +
            " equalCounts: " + hashArr.equalEntries(hashArr2));
        test = new int[]{9, 4, 1, 10, 13};
        hashArr2.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString() + " contains: " +
            hashArr2.toString() + ": " + hashArr.contains(hashArr2) +
            " equalCounts: " + hashArr.equalEntries(hashArr2));
        test = new int[]{12, 4, 2};
        hashArr2.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString() + " contains: " +
            hashArr2.toString() + ": " + hashArr.contains(hashArr2) +
            " equalCounts: " + hashArr.equalEntries(hashArr2));
        test = new int[]{12, 4, 2, 1, 5, 6, 7, 16, 23, 9, 10};
        hashArr2.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString() + " contains: " +
            hashArr2.toString() + ": " + hashArr.contains(hashArr2) +
            " equalCounts: " + hashArr.equalEntries(hashArr2));
        test = new int[]{12, 5, 5, 5, 12, 4, 2, 1, 5, 6, 7, 16, 23, 9, 10};
        hashArr2.setArray(test);
        System.out.println("" + (index++) + " Hash: " + hashArr.hashCode() +
            " from: " + hashArr.toString() + " contains: " +
            hashArr2.toString() + ": " + hashArr.contains(hashArr2) +
            " equalCounts: " + hashArr.equalEntries(hashArr2));
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
