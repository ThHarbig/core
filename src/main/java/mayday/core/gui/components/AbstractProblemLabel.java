/*
 * Created on 23.06.2005
 */
package mayday.core.gui.components;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import mayday.core.Utilities;

@SuppressWarnings("serial")
public abstract class AbstractProblemLabel 
extends JLabel
implements DocumentListener
{
    private Icon   originalIcon;
    private String originalTipText;
    private boolean hasProblem = false;
    
    /**
     * 
     */
    public AbstractProblemLabel()
    {
        super();
    }

    /**
     * @param image
     * @param horizontalAlignment
     */
    public AbstractProblemLabel(Icon image, int horizontalAlignment)
    {
        super(image, horizontalAlignment);
        originalIcon = image;
    }

    /**
     * @param image
     */
    public AbstractProblemLabel(Icon image)
    {
        super(image);
        originalIcon = image;
    }

    /**
     * @param text
     * @param icon
     * @param horizontalAlignment
     */
    public AbstractProblemLabel(String text, Icon icon, int horizontalAlignment)
    {
        super(text, icon, horizontalAlignment);
        originalIcon = icon;            
    }

    /**
     * @param text
     * @param horizontalAlignment
     */
    public AbstractProblemLabel(String text, int horizontalAlignment)
    {
        super(text, horizontalAlignment);
    }

    /**
     * @param text
     */
    public AbstractProblemLabel(String text)
    {
        super(text);
    }

    public void insertUpdate(DocumentEvent e)
    {
        checkChange(e.getDocument());            
    }

    public void removeUpdate(DocumentEvent e)
    {
        checkChange(e.getDocument());            
    }

    public void changedUpdate(DocumentEvent e)
    {
        checkChange(e.getDocument());            
    }
    
    protected void checkChange(Document d)
    {   
        try
        {
            if (accept(d.getText(0, d.getLength())))
            {
                hasProblem = false;
                setToolTipText(originalTipText);
                setIcon(originalIcon);

            } else
            //here we do something to indicate that a problem occured
            {
                hasProblem = true;
                setToolTipText(getProblemText());
                setIcon(getProblemIcon());
            }
        } catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    public void setIcon(Icon icon)
    {
        if(!hasProblem) originalIcon = icon;                
        super.setIcon(icon);
    }

    public void setToolTipText(String text)
    {
        if(!hasProblem) originalTipText = text;
        super.setToolTipText(text);
    }

    protected abstract boolean accept(String text);
    
    protected Icon getProblemIcon()
    {
        return Utilities.getWarningIcon();
    }
    
    protected abstract String getProblemText();
}
