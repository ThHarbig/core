package mayday.vis3.model;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.EventFirer;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.model.manipulators.None;

public class ProbeDataManipulator implements SettingChangeListener {

	protected ManipulationMethod[] manipulationMethods;

	protected ManipulationMethod manipulation; 
	
	public ProbeDataManipulator() {
		manipulationMethods = ManipulationMethodManager.values().toArray(new ManipulationMethod[0]);
		setManipulation(new None());
	}
	
	public double[] getProbeValues(Probe pb) {
		return manipulation.manipulate(pb.getValues());
	}
	
	/**
	 * compute the minimum for a set of probes
	 * @param experiments the experiments to evaluate or null to take all experiments
	 * @param subset the probes to look at 
	 * @return the minimum or NaN if there is no probe in the subset
	 */
	public Double getMinimum( int[] experiments , Collection<Probe> subset ) {
		Double min = Double.POSITIVE_INFINITY;
		for (Probe pb : subset) {
			double[] val = getProbeValues(pb);
			if (experiments == null)
				for(int i=0; i!=val.length; ++i) {
					if (!Double.isInfinite(val[i]) && !Double.isNaN(val[i]))
						min=min<val[i]?min:val[i];				
				}
			else
				for(int i=0; i!=experiments.length; ++i)
					if (experiments[i]<val.length && experiments[i]>=0)
						if (!Double.isInfinite(val[experiments[i]]) && !Double.isNaN(val[experiments[i]]))
							min=min<val[experiments[i]]?min:val[experiments[i]];
		}
		if (min==Double.POSITIVE_INFINITY)
			min=Double.NaN;
		return min;
	}

	/**
	 * compute the maximum for a set of probe
	 * @param experiments the experiments to evaluate or null to take all experiments
	 * @param subset the probes to look at
	 * @return the maximum or NaN if there is no probe in the subset
	 */
	public Double getMaximum( int[] experiments, Collection<Probe> subset ) {
		Double max = Double.NEGATIVE_INFINITY;
		for (Probe pb : subset) {
			double[] val = getProbeValues(pb);
			if (experiments == null)				
				for(int i=0; i!=val.length; ++i) {
					double nv = val[i];
					if (!Double.isInfinite(nv) && !Double.isNaN(nv))
						max=max>nv?max:nv;
				}
			else
				for(int i=0; i!=experiments.length; ++i)
					if (experiments[i]<val.length && experiments[i]>=0) {
						double nv = val[experiments[i]];
						if (!Double.isInfinite(nv) && !Double.isNaN(nv))
							max=max>nv?max:nv;
					}
		}
		if (max==Double.NEGATIVE_INFINITY)
			max=Double.NaN;
		return max;

	}
	
	/**
	 * compute the mean for a set of probes
	 * @param experiments the experiments to evaluate or null to take all experiments
	 * @param subset the probes to look at 
	 * @return
	 */
	public Double getMean(int[] experiments, Collection<Probe> subset) {
		Double mean=0.0d;
		int num=0;
		for (Probe pb : subset) {
			double[] val = getProbeValues(pb);
			if (experiments == null)
				for(int i=0; i!=val.length; ++i) 
				{
					if (!Double.isInfinite(val[i]) && !Double.isNaN(val[i])) {
						mean+=val[i];
						num++;
					}
				}
			else
				for(int i=0; i!=experiments.length; ++i) 
				{
					if (experiments[i]<val.length && experiments[i]>=0) {
						if (!Double.isInfinite(val[experiments[i]]) && !Double.isNaN(val[experiments[i]])) {
							mean+=val[experiments[i]];
							num++;
						}
					}
				}
					
		}
		return mean/num;
	}
	
	/**
	 * compute the standard deviation for a set of probes
	 * @param experiments the experiments to evaluate or null to take all experiments
	 * @param subset the probes to look at 
	 * @return
	 */
	public Double getStdev(int[] experiments, Collection<Probe> subset) {
		Double mean=getMean(experiments, subset);
		Double stdev=0.0d;
		int num=0;
		for (Probe pb : subset) {
			double[] val = getProbeValues(pb);
			if (experiments == null)
				for(int i=0; i!=val.length; ++i) 
				{
					stdev+=(val[i]-mean)*(val[i]-mean);
					num++;
				}
			else
				for(int i=0; i!=experiments.length; ++i) 
				{
					if (experiments[i]<val.length && experiments[i]>=0) {
						stdev+=(val[experiments[i]]-mean)*(val[experiments[i]]-mean);					
						num++;
					}
				}					
		}
		return Math.sqrt(stdev/(num-1));

	}
	
	public ProbeList.Statistics getStatistics(Collection<Probe> subset, MasterTable mata) {
        
        Probe meanProbe = new Probe( mata );
        Probe medianProbe = new Probe( mata );
        Probe q1Probe = new Probe( mata );
        Probe q3Probe = new Probe( mata );

        
        ArrayList<ArrayList<Double>> lists = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> means = new ArrayList<Double>();
        for (int i=0; i!=mata.getNumberOfExperiments(); ++i) { 
        	lists.add(new ArrayList<Double>());
        	means.add(0.0);
        }

        for (Probe pb : subset) {
        	double[] val = getProbeValues(pb);
        	for (int i=0; i!=val.length; ++i) {
        		double value = val[i];
        		if (Double.isNaN(value))
        			continue;
        		lists.get(i).add(value);
        		means.set(i, means.get(i)+value);
        	}
        }
        
        for(int i=0; i!=mata.getNumberOfExperiments(); ++i) {
            // mean
        	if (lists.get(i).size()>0)
                meanProbe.addExperiment( means.get(i)/(double)lists.get(i).size() );
        	else
        		meanProbe.addExperiment( null );

        	// quantiles
        	ArrayList<Double> list = lists.get(i);
            Collections.sort( list );
            Double l_median = null, q1=null, q3=null;
            
            if (list.size()>0) {
                if ( ( list.size() % 2 ) != 0 ) {
                	l_median =  list.get( ( list.size() - 1 ) / 2 );                    
                } else {
                    l_median =  list.get( ( list.size() / 2 ) - 1 ) +
                    			list.get( ( list.size() / 2 ) );                    
                    l_median /= 2.0; 
                }
                
                int index = (int)Math.floor( ((double)list.size())*0.25* 3 ); 
                q1 = list.get(index);
                
                index = (int)Math.floor( ((double)list.size())*0.25* 1 );
                q3 = list.get(index);
            }

            medianProbe.addExperiment( l_median );                      	
            q1Probe.addExperiment(q1);
            q3Probe.addExperiment(q3);
        }
                
        return ( new ProbeList.Statistics( meanProbe, medianProbe, q1Probe, q3Probe ) );

	}


	
	public JMenu getMenu() {
		JMenu mnu = new JMenu("Data Manipulation");
		ButtonGroup bg = new ButtonGroup();
		for (ManipulationMethod m : manipulationMethods) {
			JRadioButtonMenuItem jcbmi = new UpdatedJRadioButtonMenuItem(m);		
			if (m.equals(manipulation))
				jcbmi.setSelected(true);
			mnu.add(jcbmi);
			bg.add(jcbmi);
		}
		return mnu;
	}

	@SuppressWarnings("serial")
	protected class UpdatedJRadioButtonMenuItem extends JRadioButtonMenuItem {
		protected ManipulationMethod m;
		public UpdatedJRadioButtonMenuItem(ManipulationMethod m) {
			super(new SwitchAction(m));
			this.m=m;
		}
		public String getText() {
			if (m==null)
				return "";
			return m.toString();
		}
	}
	
	public void setManipulation(ManipulationMethod manipulationMethod) {
		if (manipulation!=manipulationMethod) {
			if (manipulation!=null && manipulation.getSetting()!=null) {
				manipulation.getSetting().removeChangeListener(this);
			}
			manipulation=manipulationMethod;
			if (manipulation!=null && manipulation.getSetting()!=null) {
				manipulation.getSetting().addChangeListener(this);
			}
		}
		eventFirer.fireEvent(new ChangeEvent(this));
	}
	
	
	public ManipulationMethod getManipulation() {
		return manipulation;
	}
	
	protected EventFirer<ChangeEvent, ChangeListener> eventFirer = new EventFirer<ChangeEvent, ChangeListener>() {
		protected void dispatchEvent(ChangeEvent event, ChangeListener listener) {
			listener.stateChanged(event);
		}
	};
	
	public void addChangeListener(ChangeListener cl) {
		eventFirer.addListener(cl);		
	}
	
	public void removeChangeListener(ChangeListener cl) {
		eventFirer.removeListener(cl);
	}
	
	
	@SuppressWarnings("serial")
	public class SwitchAction extends AbstractAction {

		protected ManipulationMethod _manipulationMethod;
		
		public SwitchAction(ManipulationMethod manipulationMethod) {
			super(manipulationMethod.toString());
			_manipulationMethod = manipulationMethod;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			if (_manipulationMethod.getSetting()!=null) {
	    		SettingDialog md = new SettingDialog(null, "Manipulation Parameters", _manipulationMethod.getSetting());
	    		md.setVisible(true);
	    	}
			setManipulation(_manipulationMethod);
		}

	}


	public void stateChanged(SettingChangeEvent e) {
		setManipulation(manipulation);
	}
	
}
