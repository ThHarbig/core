package mayday.vis3.gui.actions;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public class ProbeSelectionAddProbeAction extends AbstractAction {
	
	protected ViewModel viewModel;
	
	public ProbeSelectionAddProbeAction(ViewModel vm)	{
		super( "Add Probe..." );
		viewModel = vm;
	}

	public void actionPerformed( ActionEvent event ) {      
		String l_probeName = new String();

		do {
			l_probeName = (String)JOptionPane.showInputDialog( null,
					"Enter a probe identifier or display name.",
					MaydayDefaults.Messages.INFORMATION_TITLE,
					JOptionPane.INFORMATION_MESSAGE,
					null,
					null,
					"" );
			if ( l_probeName == null )         
				return;

			if ( l_probeName.trim().equals( "" ) ) {
				JOptionPane.showMessageDialog( null,
						"Empty probe names do not exist.",                                           
						MaydayDefaults.Messages.ERROR_TITLE,
						JOptionPane.ERROR_MESSAGE ); 
			}
		}
		while ( l_probeName.trim().equals( "" ) );

		Probe pb = viewModel.getDataSet().getMasterTable().getProbe(l_probeName);
		
		LinkedList <Probe> probesToFind = new LinkedList<Probe>();
		
		if (pb==null) {
			for (Probe npb : viewModel.getDataSet().getMasterTable().getProbes().values())
				if (npb.getDisplayName().equals(l_probeName)) {
					probesToFind.add(npb);					
				}					
		} else {
			probesToFind.add(pb);
		}

		TreeSet<Probe> probesToAdd = new TreeSet<Probe>();
		
		for (Probe npb : probesToFind)
			if (viewModel.getProbes().contains(npb))
				probesToAdd.add(npb);
		
		if (probesToFind.size()==0) {
			JOptionPane.showMessageDialog( null,
					"No Probes found with name or display name equal to \"" + l_probeName + "\".",                                           
					MaydayDefaults.Messages.INFORMATION_TITLE,
					JOptionPane.INFORMATION_MESSAGE ); 
		} else
			for (Probe npb : probesToAdd)
				viewModel.selectProbe(npb);
		
	}  
	
}