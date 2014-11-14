package mayday.vis3.plots.histogram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.LastDirListener;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.gui.properties.PropertiesDialogFactory;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.ValueProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.plots.bars.AbstractBarPlotComponent;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public class HistogramPlotComponent extends AbstractBarPlotComponent implements SettingChangeListener {

	protected ValueProvider V;
	protected Histogram hist;
	protected int lastSelIndex=-1;
	
	protected BooleanSetting showCounts;
	protected IntSetting resolution;
	
	public HistogramPlotComponent() {
		selectionBehindPlot=true;
		showCounts = new BooleanSetting("Show counts",null,false);
		resolution = new IntSetting("Resolution",null,25);
		showCounts.addChangeListener(this);
		resolution.addChangeListener(this);
	}
	
	@Override
	public String getPreferredTitle() {
		return "Histogram";
	}
	
	public static String formatNumber(Double number, int significant) {
		String label = ""+ number;
		if (label.length()>significant) {
			if (label.contains("E"))
				label = label.substring(0, significant)+label.substring(label.lastIndexOf('E'));
			else 
				label = label.substring(0,significant);
		}
		return label;
	}

	
	@SuppressWarnings("deprecation")
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		if (V==null) {
			ChangeListener cl = new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					setXLabeling();
					updatePlot();
				}
			};	
			V = initValueProvider();
			plotContainer.addViewSetting(V.getSetting(), this);
			hist = new Histogram(V, resolution.getIntValue());
			hist.addChangeListener(cl);
			setYLabeling(null);
			V.setProvider(V.new ExperimentProvider(0));
		}
		plotContainer.addViewSetting(showCounts, this);
		plotContainer.addViewSetting(resolution, this);
		plotContainer.getMenu(PlotContainer.FILE_MENU, this).add(new AbstractAction("Export histogram data") {

			public void actionPerformed(ActionEvent e) {
				String s_lastExportPath=
					MaydayDefaults.Prefs.NODE_PREFS.get(
							MaydayDefaults.Prefs.KEY_LASTSAVEDIR,
							MaydayDefaults.Prefs.DEFAULT_LASTSAVEDIR
					);

				JFileChooser l_chooser=new JFileChooser();
				l_chooser.addActionListener(new LastDirListener());

				if(!s_lastExportPath.equals(""))
					l_chooser.setCurrentDirectory(new File(s_lastExportPath));

				String l_defaultFileName = viewModel.getDataSet().getName();
				l_defaultFileName = l_defaultFileName.toLowerCase();
				l_defaultFileName = l_defaultFileName.replace( ' ', '_' ); // replace spaces
				l_defaultFileName += "." + MaydayDefaults.DEFAULT_TABULAR_EXPORT_EXTENSION;

				l_chooser.setSelectedFile( new File( l_defaultFileName ) );

				int l_option = l_chooser.showSaveDialog( null );

				if ( l_option  == JFileChooser.APPROVE_OPTION )
				{
					String l_fileName = l_chooser.getSelectedFile().getAbsolutePath();
					MaydayDefaults.s_lastExportPath = l_chooser.getCurrentDirectory().getAbsolutePath();

					// if the user presses cancel, then quit
					if ( l_fileName == null )
						return;
					
					// ask before overwriting file
					if (new File(l_fileName).exists() && 
							JOptionPane.showConfirmDialog(null, 
									"Do you really want to overwrite the existing file \""+l_fileName+"\"?",
									"Confirm file overwrite", 
									JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
									!=JOptionPane.YES_OPTION
					) {
						return;
					}
						
					
					try
					{  
						FileWriter fw = new FileWriter(l_fileName);
						fw.write("Bin\tCount\tFrequency\n");
						for (int i=0; i!=hist.getNumberOfBins(); ++i) {
							fw.write(Double.toString(hist.getBinPosition(i)));
							fw.write('\t');
							fw.write(Integer.toString(hist.getBinCount(i)));
							fw.write('\t');
							fw.write(Double.toString(hist.getBinFrequency(i)));
							fw.write('\n');
						}
						
						fw.flush();
						fw.close();
					}
					catch ( FileNotFoundException exception )
					{
						String l_message = MaydayDefaults.Messages.FILE_NOT_FOUND;
						l_message = l_message.replaceAll( MaydayDefaults.Messages.REPLACEMENT, l_fileName );

						JOptionPane.showMessageDialog( null,
								l_message,
								MaydayDefaults.Messages.ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE ); 
					}
					catch ( IOException exception )
					{
						JOptionPane.showMessageDialog( null,
								exception.getMessage(),
								MaydayDefaults.Messages.ERROR_TITLE,
								JOptionPane.ERROR_MESSAGE ); 
					}
				}
			}
			
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				double[] clicked = getPoint(evt.getX(), evt.getY());
				int idx = (int)(clicked[0]-.5);
				
				if (evt.getClickCount()==2) {
					PropertiesDialogFactory.createDialog(hist.getObjectsInBin(idx)).setVisible(true);
				} else {
					int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
					boolean controlpressed = (evt.getModifiers()&CONTROLMASK) == CONTROLMASK;
					if (evt.isShiftDown() && lastSelIndex!=-1) {
						workSelection(controlpressed, Math.min(lastSelIndex,idx), Math.max(lastSelIndex,idx));
					} else {
						workSelection(controlpressed, idx, idx);
					}
					lastSelIndex=idx;
				}
			}
		});
	}
	
	protected void workSelection(HashSet<Probe> selection, boolean control, int idx) {
		Collection<Probe> newSelection = hist.getObjectsInBin(idx);
		if (control) {
			// remove selected
			HashSet<Probe> switchoff = new HashSet<Probe>(newSelection);
			switchoff.retainAll(selection);
			
			HashSet<Probe> switchon = new HashSet<Probe>(newSelection);
			switchon.removeAll(switchoff);
			
			//switchon
			selection.removeAll(switchoff);
			selection.addAll(switchon);
		} else {
			selection.addAll(newSelection);
		}		
	}
	
	protected void workSelection(boolean ctrl, int start, int end) {
		
		HashSet<Probe> set = new HashSet<Probe>();
		if (ctrl)
			set.addAll(viewModel.getSelectedProbes());
		
		for (int i=start; i<=end; ++i)
			workSelection(set, ctrl, i);
		viewModel.setProbeSelection(set);
	}
	
	protected void setXLabeling() {
		HashMap<Double, String> xLabeling = new HashMap<Double, String>();
		for (int i=0; i!=hist.getNumberOfBins(); ++i)
			xLabeling.put((double)i+1, formatNumber(hist.getBinPosition(i),5));
		setXLabeling(xLabeling);
	}
	
	protected ValueProvider initValueProvider() {
		return new ValueProvider(viewModel,"Values");
	}
	
	public void removeNotify() {
		super.removeNotify();
	}

	public DataSeries doSelect(Collection<Probe> probes) {
		double max=Double.NEGATIVE_INFINITY;
		DataSeries ds = new DataSeries();
		BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.f,
			       new float[]{4.0f,4.0f}, 0);
		ds.setStroke(stroke);
		if (hist==null)
			return ds;
			
		for (int i=0;i!=hist.getNumberOfBins();++i)
			max = Math.max(showCounts.getBooleanValue()?hist.getBinCount(i):hist.getBinFrequency(i),max);
		
		max*=1.05;
		
		for (Probe pb : probes) {
			double d = V.getValue(pb);
			// move d to the correct position
			d = hist.mapValueForPlotting(d);
			ds.addDPoint(d+0.5, 0);
			ds.addDPoint(d+0.5, max);
			ds.jump();
		}
		ds.setConnected(true);
		return ds;
	}

	@Override
	public BarShape getBar(int i) {
		return new BarShape(1, showCounts.getBooleanValue()?hist.getBinCount(i):hist.getBinFrequency(i), Color.gray);
	}

	@Override
	public int getNumberOfBars() {
		if (hist==null)
			return 0;
		else
			return hist.getNumberOfBins();
	}
	
	public void viewModelChanged(ViewModelEvent vme) {
		switch (vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			select(Color.RED);
			break;
		case ViewModelEvent.DATA_MANIPULATION_CHANGED: // fallthrough
		case ViewModelEvent.PROBELIST_ORDERING_CHANGED: // fallthrough
		case ViewModelEvent.PROBELIST_SELECTION_CHANGED:
			hist.update();
			updatePlot();
			break;			
		}
	}

	public void stateChanged(SettingChangeEvent e) {
		if (e.getSource()==resolution) {
			hist.setBinCount(resolution.getIntValue());
		}
		if (e.getSource()==showCounts) {
			updatePlot();		
		}
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		if (showCounts!=null)
			return showCounts.getBooleanValue()?"Count":"Frequency";
		return ytitle;
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		if (V!=null)
			return V.getSourceName(); 
		return xtitle;
	}	
	
}
