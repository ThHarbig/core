/**
*  Filename: $RCSfile: RegExpFilenameFilter.java,v $
*  Purpose:  Some helper methods for calling external programs.
*  Language: Java
*  Compiler: JDK 1.2
*  Authors:  Joerg K. Wegner
*  Version:  $Revision: 1.2 $
*            $Date: 2009/03/25 22:44:18 $
*            $Author: battke $
*  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
*/
package wsi.ra.tool;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Some helper methods for calling external programs.
 */
public class RegExpFilenameFilter implements FilenameFilter
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Pattern pattern;
    private Vector<String> skipIfExtension;

    //~ Constructors ///////////////////////////////////////////////////////////

    public RegExpFilenameFilter(String regExp)
    {
        pattern = Pattern.compile(regExp);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean accept(File dir, String name)
    {
        Matcher m;
        m = pattern.matcher(name);

        if (m.matches())
        {
            if (skipIfExtension != null)
            {
                int size = skipIfExtension.size();

                for (int i = 0; i < size; i++)
                {
                    if (name.endsWith((String) skipIfExtension.get(i)))
                    {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    public void addSkipExtension(String extension)
    {
        if (skipIfExtension == null)
        {
            skipIfExtension = new Vector<String>();
        }

        skipIfExtension.add(extension);
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
