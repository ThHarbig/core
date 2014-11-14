/*
 * Created on Feb 4, 2005
 *
 */
package mayday.core.gui.abstractdialogs;

import java.util.ArrayList;

import javax.swing.Action;


/**
 * @author gehlenbo
 *
 */
@SuppressWarnings("serial")
public class StandardDialogComponent
extends AbstractStandardDialogComponent
{
  public StandardDialogComponent( int direction )
  {
    super( direction );
  }
  
  
  public ArrayList<Action> getOkActions()
  {
    return ( new ArrayList< Action >() );
  }
}
