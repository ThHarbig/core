package mayday.vis3.gui.probemover;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.vis3.components.BasicPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;


@SuppressWarnings("serial")
public class ProbeMoverComponent extends BasicPlotPanel implements ViewModelListener, ProbeListListener{
	
	private ProbeList pl;
	private List<ProbeList> probeLists;
	private ViewModel vm;
	
	private JButton copy;
	private JButton remove;
	private JButton move;
	private JLabel label;
	
	public ProbeMoverComponent(ProbeList pl) {		
		this.pl=pl;
		setLayout(new ExcellentBoxLayout(true, 0));
		copy = new JButton(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionCopy();
			}
		});
		move = new JButton(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionMove();
			}
		});
		remove = new JButton(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionRemove();
			}
		});
		label = new JLabel();
		
		copy.setBackground(Color.white);
		move.setBackground(Color.white);
		remove.setBackground(Color.white);
		
		copy.setOpaque(false);
		move.setOpaque(false);
		remove.setOpaque(false);
		
		copy.setBorder(BorderFactory.createEmptyBorder());
		move.setBorder(BorderFactory.createEmptyBorder());
		remove.setBorder(BorderFactory.createEmptyBorder());

		
		add(label);
		add(copy);
		add(move);
		add(remove);

		probeLists = new LinkedList<ProbeList>();
		probeLists.add(pl);
	}
	
	public List<ProbeList> getProbeLists() {
		return probeLists;
	}
	
	public String getPreferredTitle() {
		if (pl!=null)
			return pl.getName();
		else 
			return "No ProbeList";
	}

	@Override
	public void updatePlot() {
	}

	@Override
	public void setup(PlotContainer plotContainer) {
		this.vm = plotContainer.getViewModel();
		vm.addViewModelListener(this);
		pl.addProbeListListener(this);	
		updateButtons();
		updateLabel();
	}
	
	private void updateLabel() {
		label.setText("<html><b>"+pl.getName()+"</b></html>");
		label.setBackground(Color.white);
		label.setOpaque(true);		
		label.setForeground(pl.getColor());
		this.setBackground(pl.getColor());
	}

	private void updateButtons() {
		Set<Probe> myProbes = pl.getAllProbes();
		Set<Probe> vmProbes = new TreeSet<Probe>(vm.getSelectedProbes());
		vmProbes.removeAll(myProbes);
		myProbes.retainAll(vm.getSelectedProbes());
		copy.setText("<html><div style='background:#ffffff; padding:5px'>Copy "+vmProbes.size()+" probes" );
		move.setText("<html><div style='background:#ffffff; padding:5px'>Move "+vmProbes.size()+" probes" );
		remove.setText("<html><div style='background:#ffffff; padding:5px'>Delete "+myProbes.size()+" probes");
		copy.setVisible(vmProbes.size()>0);
		move.setVisible(vmProbes.size()>0);
		remove.setVisible(myProbes.size()>0);	
	}

	public void removeNotify() {
		if (vm!=null)
			vm.removeViewModelListener(this);
		if (pl!=null)
			pl.removeProbeListListener(this);
	}
	
	protected void actionMoveCopy(boolean move) {
		Set<Probe> vmProbes = new TreeSet<Probe>(vm.getSelectedProbes());
		vmProbes.removeAll(pl.getAllProbes()); // these i will add now
		if (move) {
			for (ProbeList otherPL: vm.getProbeLists(false)) {
				if (otherPL!=pl) {
					for (Probe pb : vmProbes)
						if (otherPL.contains(pb))
							otherPL.removeProbe(pb);
				}
			}
		}
		for (Probe pb : vmProbes) {
			pl.addProbe(pb);
		}
		updateButtons();
	}
	
	protected void actionCopy() {
		actionMoveCopy(false);
	}
	
	protected void actionMove() {
		actionMoveCopy(true);
	}
	
	protected void actionRemove() {
		Set<Probe> myProbes = pl.getAllProbes();
		myProbes.retainAll(vm.getSelectedProbes());
		for (Probe pb : myProbes)
			pl.removeProbe(pb);
		updateButtons();
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED) {
			updateButtons();
			updateLabel();
		}
	}

	@Override
	public void probeListChanged(ProbeListEvent event) {
		if (event.getChange()==ProbeListEvent.LAYOUT_CHANGE)
			updateLabel();
	}
	
	
}

