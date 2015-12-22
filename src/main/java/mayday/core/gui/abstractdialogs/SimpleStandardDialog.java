/*
 * Created on 16.07.2005
 */
package mayday.core.gui.abstractdialogs;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple dialog containing exactly one AbstractStandardDialogComponent.
 *  
 * 
 * @author Matthias Zschunke
 * @version 0.1
 * Created on 16.07.2005
 *
 */

@SuppressWarnings("serial")
public class SimpleStandardDialog 
extends StandardDialog
{
    /**
     * Creates the dialog through calling 
     * {@link StandardDialog#compose()}.
     * The dialog is tabless ({@link StandardDialog#compose(boolean)}).
     * 
     * @param name the title of the dialog
     * @param component a single dialog component
     * @throws HeadlessException
     * 
     * @see AbstractStandardDialogComponent
     */
    public SimpleStandardDialog(
            String name, 
            AbstractStandardDialogComponent component) 
    throws HeadlessException
    {
        this( name, component, false );
    }
    
    /**
     * Creates the dialog through calling 
     * {@link StandardDialog#compose()}.
     * 
     * @param name the title of the dialog
     * @param component a single dialog component
     * @param withTab should the component be displayed with tab
     * @throws HeadlessException
     * 
     * @see AbstractStandardDialogComponent
     */
    public SimpleStandardDialog(
            String name, 
            AbstractStandardDialogComponent component,
            boolean withTab) 
    throws HeadlessException
    {
        super(name, new ArrayList<AbstractStandardDialogComponent>(
                Arrays.asList(new AbstractStandardDialogComponent[] {
                        component
                })
        ));
        
        compose(withTab);
        
        setAlwaysOnTop(true);
    }

}
