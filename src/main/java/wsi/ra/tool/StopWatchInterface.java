///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: StopWatchInterface.java,v $
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.2 $
//          $Date: 2009/01/15 16:47:45 $
//          $Author: battke $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package wsi.ra.tool;

/**
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.2 $, $Date: 2009/01/15 16:47:45 $
 */
public interface StopWatchInterface
{
    //~ Methods ////////////////////////////////////////////////////////////////

    int getPassedTime();

    /**
     * Prints the passed time since last 'reset stop watch' call in millis to 'stdout'.
     */
    void printPassedTime(String text);

    /**
     * Resets the time to the time when the stop watch was suspended.
     */
    void proceed();

    /**
     * Saves the current system time in a local variable.
     */
    void resetTime();

    /**
     * Freezes the time when the stop watch is suspended.
     */
    void suspend();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
