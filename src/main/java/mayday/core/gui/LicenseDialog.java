package mayday.core.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;

import mayday.core.plugins.StartBrowser;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;

@SuppressWarnings("serial")
public class LicenseDialog extends MaydayFrame {

	public LicenseDialog() {
		super("Mayday is free software");

		setLayout(new BorderLayout());

		String text="<html><body><font face=Arial><b>Mayday</b> - MicroarraY Data AnalYsis<br/>"+
			"Copyright (C) 2003-2011<br><br>"+
			"This program is free software; you can redistribute it <br>" +
			"and/or modify it under the terms of the GNU General Public License <br>" +
			"as published by the Free Software Foundation; version 2.<br><br>" +
			"This program is distributed in the hope that it will be useful, <br>" +
			"but WITHOUT ANY WARRANTY; without even the implied warranty of <br>" +
			"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU <br>" +
			"General Public License for more details. <br><br>" +
			"You should have received a copy of the GNU General Public License <br>" +
			"along with this program; if not, write to the <br>" +
			"Free Software Foundation, Inc.,<br>" +
			"51 Franklin Street, <br>" +
			"Fifth Floor, <br>" +
			"Boston, MA  <br>" +
			"02110-1301, USA.";
		
		setLayout(new BorderLayout());

		JEditorPane jep = new JEditorPane();
		jep.setContentType("text/html");
		jep.setText(text);
		jep.setEditable(false);
		jep.setBorder(BorderFactory.createEmptyBorder(20, 20,20,20));


		add(jep, BorderLayout.CENTER);

		JButton lbutton = new JButton(new AbstractAction("Show full license text") {

			public void actionPerformed(ActionEvent e) {
				boolean success = false;
				try {
					FMFile lic = PluginManager.getInstance().getFilemanager().getFile("/mayday/core/gpl-2.0.txt");
					if (lic!=null && lic.extract()) {
						success = (Boolean)new StartBrowser().run(lic.getFullPath());
					} 
				} catch (Exception e1) {}
				if (!success) {
					JOptionPane.showMessageDialog((Component)null, 
							"Could not start an external program to display the license text.\n" +
							"Please get the GPL v 2.0 at \n" +
							"http://www.gnu.org/licenses/gpl-2.0.txt",
							"Browser could not be started",
							JOptionPane.ERROR_MESSAGE,				
							null
					);
				}

			}

		});
		JButton okbutton = new JButton(new AbstractAction("OK") {

			public void actionPerformed(ActionEvent arg0) {
				dispose();					
			}

		});

		Box bbox = Box.createHorizontalBox();
		bbox.add(Box.createHorizontalGlue());
		bbox.add(lbutton);
		bbox.add(Box.createHorizontalStrut(15));
		bbox.add(okbutton);
		add(bbox, BorderLayout.SOUTH);

		pack();
		setMaximumSize(new Dimension(800,800));
		setResizable(false);
		setVisible(true);

	}
}
