package mayday.vis3.graph.vis3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.Statistics;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.NumericMIO;
import mayday.vis3.ColorProvider;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;

public class SuperColorProvider extends ColorProvider 
{
	protected List<Double> expMinValues;
	protected List<Double> expMaxValues;
	protected double globalMinimum;
	protected double globalMaximum;
	protected int numExp;
	
	public SuperColorProvider(ViewModel vm, String name) 
	{
		super(vm);	
		setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
		setExperimentExtremes();
		numExp=viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
	}
	
	public SuperColorProvider(ViewModel vm) 
	{
		this(vm, "Coloring");
	}
	
	public void setExperimentExtremes() 
	{
		updateExperimentExtremes();
		updateGradient();		
		coloringChanged=true;
		globalMinimum=Collections.min(expMinValues);
		globalMaximum=Collections.max(expMaxValues);
	}			
	
	private void updateExperimentExtremes()
	{
		numExp=viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		expMinValues=new ArrayList<Double>(numExp);
		expMaxValues=new ArrayList<Double>(numExp);
		
		for(int i=0; i!=numExp; ++i )
		{
			expMaxValues.add(Double.MIN_VALUE);
			expMinValues.add(Double.MAX_VALUE);
		}
		
		for(Probe p: viewModel.getProbes())
		{
			for(int i=0; i!=p.getNumberOfExperiments(); ++i)
			{
				double v=getProbeValue(p, i);
				if( v> expMaxValues.get(i))
				{
					expMaxValues.set(i,v);
				}
				if( v<  expMinValues.get(i))
				{
					expMinValues.set(i,v);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void updateGradient() 
	{
		updateExperimentExtremes();
		
		switch(colorMode) {
		case ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE:
			expmax = Collections.max(expMaxValues);				
			expmin = Collections.min(expMinValues);				
			colorGradient.setMax(expmax);
			colorGradient.setMin(expmin);
			break;
		case ColorProviderSetting.COLOR_BY_MIO_VALUE:
			if (categoricalMIO)
				break;
			miomin=Double.MAX_VALUE;
			miomax=Double.MIN_VALUE;
			for (Entry<Object,MIType> e :mg.getMIOs()) {
				MIType mt = e.getValue();
				double value = ((NumericMIO<Number>)mt).getValue().doubleValue();
				miomin = miomin<=value?miomin:value;
				miomax = miomax>=value?miomax:value;
			}
			colorGradient.setMax(miomax);
			colorGradient.setMin(miomin);
			break;
		case ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST: //nothing to do 
			break; 
		}
	}
	
	public List<Color> getColors(double... value) 
	{
		List<Color> res=new ArrayList<Color>();
		for(double v:value)
			res.add(getColorFromGradient(v, colorGradient));
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public List<Color> getColors(Probe p)
	{		
		List<Color> res=new ArrayList<Color>(numExp);
		ProbeList pl;
		try{
			pl = viewModel.getTopPriorityProbeList(p);
		}catch(Throwable t)
		{
			pl=new ProbeList(viewModel.getDataSet(), false);
			pl.setColor(Color.black);
		}
		for(int i=0; i!=numExp; ++i)
		{
			Color c = Color.black; // default to black
			switch(colorMode) 
			{
			case ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST:				
				if (pl!=null) c = pl.getColor();
				break;
			case ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE:
				double v = getProbeValue(p, i);
				c= getColorFromGradient(v, colorGradient);
				break;
			case ColorProviderSetting.COLOR_BY_MIO_VALUE:
				GenericMIO mt = (GenericMIO)mg.getMIO(p);
				if (mt!=null) 
				{
					if (categoricalMIO) 
					{
						c = categoricalColors.get(mt.getValue());
					} else 
					{
						v = ((NumericMIO<Number>)mt).getValue().doubleValue();
						c = getColorFromGradient(v, colorGradient);
					}								
				}
				break;				
			}
			res.add(c);
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public List<Color> getMeanColors(Iterable<Probe> probes)
	{		
		Iterator<Probe> iter=probes.iterator();
		if(!iter.hasNext())
			return Collections.emptyList();
		Probe p=iter.next(); // get first element.
		if(!iter.hasNext())
		{
			return getColors(p);
		}
		List<Color> res=new ArrayList<Color>(numExp);
		ProbeList pl;
		try{
			pl = viewModel.getTopPriorityProbeList(p);
		}catch(Throwable t)
		{
			pl=new ProbeList(viewModel.getDataSet(), false);
			pl.setColor(Color.black);
		}
		for(int i=0; i!=numExp; ++i)
		{
			Color c = Color.black; // default to black
			switch(colorMode) 
			{
			case ColorProviderSetting.COLOR_BY_TOP_PRIORITY_PROBELIST:				
				if (pl!=null) c = pl.getColor();
				break;
			case ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE:
				double v = getProbesMeanValue(probes, i);
				c= getColorFromGradient(v, colorGradient);
				break;
			case ColorProviderSetting.COLOR_BY_MIO_VALUE:
				GenericMIO mt = (GenericMIO)mg.getMIO(p);
				if (mt!=null) 
				{
					if (categoricalMIO) 
					{
						c = categoricalColors.get(mt.getValue());
					} else 
					{
						v = ((NumericMIO<Number>)mt).getValue().doubleValue();
						c = getColorFromGradient(v, colorGradient);
					}								
				}
				break;				
			}
			res.add(c);
		}
		return res;
	}
	
	public Color getMeanColor(Iterable<Probe> probes)
	{
		return RendererTools.averageColor(getMeanColors(probes));
	}
	
	public double getProbeValue(Probe p, int exp) 
	{
		return viewModel.getProbeValues(p)[exp];
	}
	
	public double getProbesMeanValue(Iterable<Probe> probes, int exp) 
	{
		double res=0.0;
		int c=0;
		for(Probe p:probes)
		{
			res+=getProbeValue(p, exp);
			c++;
		}
		return res/c;
	}
	
	public List<Double> getProbesMeanValue(Iterable<Probe> probes) 
	{
		List<Double> result=new ArrayList<Double>();
		double res=0.0;
		int c=0;
		for(int exp=0; exp!=numExp; ++exp)
		{
			for(Probe p:probes)
			{
				res+=getProbeValue(p, exp);
				c++;
			}
			result.add(res/c);
		}
		return result;
	}
	
	public double grandMean()
	{
		double v=0.0;
		int c=0;
		for(Probe p:viewModel.getProbes())
		{
			v+=p.getMean();
			c++;
		}
		return v/(1.0*c);
	}
	
	public double getProbesMedianValue(Iterable<Probe> probes, int exp) 
	{
		List<Double> vals=new ArrayList<Double>();
		for(Probe p: probes)
		{
			vals.add(getProbeValue(p, exp));
		}		
		return Statistics.median(vals);
	}
	
	public double[] getProbeValues(Probe p) 
	{
		return viewModel.getProbeValues(p);
	}

	/**
	 * @return the expMinValues
	 */
	public List<Double> getExpMinValues() {
		return expMinValues;
	}


	/**
	 * @return the expMaxValues
	 */
	public List<Double> getExpMaxValues() {
		return expMaxValues;
	}

	public double minimum()
	{
		return globalMinimum;
//		int[] exps=new int[viewModel.getDataSet().getMasterTable().getNumberOfExperiments()];
//		for(int i=0; i!= exps.length; ++i)
//			exps[i]=i;
//		return viewModel.getMinimum(exps, viewModel.getProbes());
//		
//		double r=Double.MAX_VALUE;
//		for(Probe p:viewModel.getProbes())
//		{
//			if(p.getMinValue() < r)
//				r=p.getMinValue();
//		}		
//		return r;
	}
	
	public double maximum()
	{
		return globalMaximum;		
//		int[] exps=new int[viewModel.getDataSet().getMasterTable().getNumberOfExperiments()];
//		for(int i=0; i!= exps.length; ++i)
//			exps[i]=i;
//		return viewModel.getMaximum(exps, viewModel.getProbes());
		
//		double r=Double.MIN_VALUE;
//		for(Probe p:viewModel.getProbes())
//		{
//			if(p.getMaxValue() > r)
//				r=p.getMaxValue();
//		}		
//		return r;
	}
	
	public ViewModel getViewModel()
	{
		return viewModel;
	}
	
	public void viewModelChanged(ViewModelEvent vme) {
		super.viewModelChanged(vme);
		
		if(vme.getChange()==ViewModelEvent.DATA_MANIPULATION_CHANGED || 
				vme.getChange()==ViewModelEvent.TOTAL_PROBES_CHANGED ||
				vme.getChange()==ViewModelEvent.PROBELIST_SELECTION_CHANGED)
		{
			setExperimentExtremes();
		}
	}

}
