/*
 * Created on 18.07.2004
 *
 */
package mayday.interpreter.rinterpreter.gui;

import java.io.File;
import java.io.FileFilter;

import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.RSettings;


/**
 * This file filter accepts all graphic files that were 
 * created during the R evaluation process.
 * 
 * Possible extensions are taken from RDefaults.TempFiles.GRAPHICS_EXTENTIONS.
 * 
 * @author Matthias
 *
 */
public class GraphicsFileFilter implements FileFilter
{
    private RSettings settings;

    /**
     * @param frame
     */
    public GraphicsFileFilter(RSettings settings)
    {
        this.settings = settings;
    }

    public boolean accept(File pathname)
    {
        if(pathname.lastModified()<this.settings.getBeginTimeStamp())
            return false;
        String cmp=pathname.getName().toUpperCase();
        String[] ext=RDefaults.TempFiles.GRAPHICS_EXTENTIONS;
        for(int i=0; i!=ext.length;++i)
        {
            if(cmp.endsWith("."+ext[i]))
                return true;
        }
        return false;
    }
}