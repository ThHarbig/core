/*
 * Created on 05.02.2005
 */
package mayday.interpreter.rinterpreter.gui;

import javax.swing.JToolTip;

/**
 * This tooltip makes it available to use multiple lines in
 * a single tooltip. It exploits the facility to use html
 * in tooltips.
 * 
 * @author Matthias
 *
 */
@SuppressWarnings("serial")
public class MultiLineToolTip 
extends JToolTip
{
    //  the pattern expression separates by each form of newline
    private static final String REGEX="[\\n(\\n\\r)\\r\\u0085\\u2028\\u2029]{1}";
    
    public MultiLineToolTip()
    {
        super();
    }
    
    public void setTipText(String text)
    {
        setTipText(
            text.trim().split(REGEX)
        );        
    }
    
    public void setTipText(String[] lines)
    {
        if(lines==null || lines.length==0) return;
        
        boolean hasHeader=false;
        if(lines[0].startsWith("{") && lines[0].endsWith("}"))
        {
            hasHeader=true;
            lines[0]=lines[0].substring(1,lines[0].length()-1);
        }
        
        StringBuffer buf=new StringBuffer();
        buf.append(
            "<html><head><style type=\"text/css\">" +
            "<!-- " +
            "body {" +
            " margin:1px;}" +
            " --></style></head><body>"+
            (hasHeader?
              "<b>"+lines[0]+"</b><hr>":
              ""
            )
        );
        int start=(hasHeader?1:0);
        for(int i=start; i<lines.length; ++i)
        {
            buf.append((i==start?"":"<br>")+lines[i]);
        }
        
        buf.append("</body></html>");
        
        super.setTipText(buf.toString());
    }
}
