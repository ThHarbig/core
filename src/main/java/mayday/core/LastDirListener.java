package mayday.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

/**
 * @author Matthias Zschunke
 *
 *  
 */
public class LastDirListener implements ActionListener
{
    private static LastDirListener lastDirListener;
    
    public synchronized static LastDirListener getInstance()
    {
        if(lastDirListener==null) lastDirListener=new LastDirListener();
        return lastDirListener;
    }
    
	public void actionPerformed(ActionEvent event)
	{
        if(!(event.getSource() instanceof JFileChooser)) return;
        
		JFileChooser fc=(JFileChooser)event.getSource();
		if(event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION))
		{
			Preferences prefs=MaydayDefaults.Prefs.NODE_PREFS;
			prefs.put(
			  ((fc.getDialogType()==JFileChooser.OPEN_DIALOG)?
			    MaydayDefaults.Prefs.KEY_LASTOPENDIR
			   :MaydayDefaults.Prefs.KEY_LASTSAVEDIR),
			  fc.getCurrentDirectory().toString());
			try
			{
				prefs.flush();
				MaydayDefaults.Prefs.save();
			}catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
