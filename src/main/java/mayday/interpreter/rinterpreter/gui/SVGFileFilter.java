/*
 * Created on 18.07.2004
 *
 */
package mayday.interpreter.rinterpreter.gui;

import java.io.File;
import java.io.FileFilter;

import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RSettings;


public class SVGFileFilter implements FileFilter
{
    private final RSettings settings;

    /**
     * @param frame
     */
    SVGFileFilter(RSettings settings)
    {
        this.settings = settings;
    }

    public boolean accept(File pathname)
    {
        if(pathname.lastModified()<this.settings.getBeginTimeStamp())
            return false;
        
        return pathname.getName().toUpperCase().endsWith(
            RDefaults.TempFiles.GRAPHICS_EXTENTIONS[RDefaults.TempFiles.SVG_PLOT]
        );
    }
}