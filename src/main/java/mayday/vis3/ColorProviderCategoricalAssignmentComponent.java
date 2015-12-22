/**
 * 
 */
package mayday.vis3;

import java.util.HashSet;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.Probe;
import mayday.core.meta.GenericMIO;
import mayday.vis3.categorical.CategoricalAssignmentComponent;

@SuppressWarnings("serial") 
public class ColorProviderCategoricalAssignmentComponent extends CategoricalAssignmentComponent {
	
	public ColorProviderCategoricalAssignmentComponent(ColorProvider colorProvider) {
		super(colorProvider);
	}

	public void init() {
		super.init();
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@SuppressWarnings("unchecked")
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				// get the selection and push it to the viewmodel
				HashSet<Object> o = new HashSet<Object>();
				for (int i=e.getFirstIndex(); i<=e.getLastIndex(); ++i) {
					if (table.isRowSelected(i))
						o.add(table.getValueAt(i, 1));
				}
				HashSet<Probe> newSelection = new HashSet<Probe>();
				for (Probe pb : catColoring.getViewModel().getProbes()) {
					GenericMIO mt = (GenericMIO)((ColorProvider)catColoring).mg.getMIO(pb);
					if (mt!=null && o.contains(mt.getValue()))
						newSelection.add(pb);						
				}			
				silent = true;
				catColoring.getViewModel().setProbeSelection(newSelection);
				silent = false;
			}
			
		});
	}
	
}