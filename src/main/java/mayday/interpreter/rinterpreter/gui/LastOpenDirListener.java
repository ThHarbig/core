package mayday.interpreter.rinterpreter.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import mayday.core.MaydayDefaults;
import mayday.core.Preferences;
import mayday.interpreter.rinterpreter.RDefaults;

/**
 * This listener should be added to JFileChooser dialogs.
 * If it is, the last applied directory is stored in the
 * RInterpreter Preferences tree.
 * 
 * @author Matthias
 *  
 */
public class LastOpenDirListener implements ActionListener
{
	public void actionPerformed(ActionEvent event)
	{
		if(event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION))
		{
			Preferences prefs=RDefaults.getPrefs();
			prefs.put(
			  RDefaults.Prefs.LASTSRCDIR_KEY,
			  ((JFileChooser)event.getSource()).getCurrentDirectory().toString());
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
