package mayday.vis3.graph.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.AbstractAction;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;

@SuppressWarnings("serial")
public class HelpAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		FMFile rconn = PluginManager.getInstance().getFilemanager().getFile("mayday/vis3/gradoku.html");
		BufferedReader br = new BufferedReader(new InputStreamReader(rconn.getStream()));
		
		StringBuffer help=new StringBuffer();
		try 
		{
			String line=br.readLine();
			while (line!=null)
			{
				help.append(line+"\n");
				line=br.readLine();
			}
				
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		MaydayDialog helpDialog=new MaydayDialog();
		JTextPane pane=new JTextPane();
		pane.setContentType("text/html;");
		pane.setText(help.toString());
//		JTextArea area=new JTextArea(help.toString());
//		area.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
//		area.setEditable(false);
		helpDialog.setTitle("Anaconda Graph Viewer Help");
		helpDialog.setLayout(new BorderLayout());
		helpDialog.add(new JScrollPane(pane),BorderLayout.CENTER);
		helpDialog.pack();
		helpDialog.setVisible(true);
		
	}
}
