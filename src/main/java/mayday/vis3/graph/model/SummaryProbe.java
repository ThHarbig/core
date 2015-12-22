package mayday.vis3.graph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.Statistics;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.linalg.vector.AbstractVector;

public class SummaryProbe extends Probe implements SettingChangeListener
{
	private Graph graph;
	private Node node;

	private SummaryMode mode;
	private WeightMode weightMode;

	private boolean empty=false;

	public SummaryProbe(MasterTable masterTable, Graph g, Node node, SummaryMode mode) throws RuntimeException 
	{
		this(masterTable,g,node,mode,false);
//		super(masterTable);
//		for(int i=0; i!= masterTable.getNumberOfExperiments();++i)
//			values[i]=Double.NaN;
//
//		this.node=node;
//		this.graph=g;
//		this.mode=mode;
//		weightMode=WeightMode.ALL_POSITIVE;
//		updateSummary();
	}
	
	public SummaryProbe(MasterTable masterTable, Graph g, Node node, SummaryMode mode, boolean updateNow) throws RuntimeException 
	{
		super(masterTable);
		for(int i=0; i!= masterTable.getNumberOfExperiments();++i)
			values[i]=Double.NaN;
		this.node=node;
		this.graph=g;
		this.mode=mode;		
		weightMode=WeightMode.ALL_POSITIVE;
		if(updateNow)
			updateSummary();
	}

	public void updateSummary()
	{
		List<Probe> inProbes=new ArrayList<Probe>();
		List<Probe> outProbes=new ArrayList<Probe>();
		// collect probes from neighbor nodes. Collect in and out neighbor probes separately,
		// since they can be weighted differently. 
		for(Node n: graph.getInNeighbors(node))
		{
			if(n instanceof MultiProbeNode)
			{
				inProbes.addAll(((MultiProbeNode) n).getProbes());
			}
		}
		for(Node n: graph.getOutNeighbors(node))
		{
			if(n instanceof MultiProbeNode)
			{
				outProbes.addAll(((MultiProbeNode) n).getProbes());
			}
		}

		if(inProbes.size()==0 && outProbes.size()==0)
		{
			empty=true;
			return;
		}

		DoubleMatrix mat=new DoubleMatrix(inProbes.size()+outProbes.size(),getNumberOfExperiments());
		int i=0; 
		// add inProbes with inWeight
		for(Probe p: inProbes)
		{
			if(p.getMasterTable()!=getMasterTable())
				continue;
			double[] pv= p.getValues();
			for(int j=0; j!= p.getNumberOfExperiments(); ++j)
			{
				mat.setValue(i, j, pv[j]*weightMode.inWeight());
			}
			++i;
		}
		// add outProbes with outWeight
		for(Probe p: outProbes)
		{
			if(p.getMasterTable()!=getMasterTable())
				continue;
			double[] pv= p.getValues();
			for(int j=0; j!= p.getNumberOfExperiments(); ++j)
			{
				mat.setValue(i, j, pv[j]*weightMode.outWeight());
			}
			++i;
		}

		for(int c=0; c!= getNumberOfExperiments(); ++c)
		{
			AbstractVector vec=mat.getColumn(c);
			switch(mode)
			{
			case MEAN:
				values[c]=vec.mean();
				break;
			case MEDIAN:
				values[c]=Statistics.median(vec.asList());
				break;
			case MIN:
				values[c]=Collections.min(vec.asList());
				break;
			case MAX:
				values[c]=Collections.min(vec.asList());
				break;
			case SUM:
				values[c]=sum(vec);
				break;	
			case PRODUCT:
				values[c]=prod(vec);
				break;		
			}

		}

	}

	private double sum(AbstractVector vec)
	{
		double s=0; 
		for(Double d:vec)
			s+=d;
		return s;
	}

	private double prod(AbstractVector vec)
	{
		double s=0; 
		for(Double d:vec)
			s*=d;
		return s;
	}

	public enum SummaryMode
	{
		MEAN,
		MEDIAN,
		MIN,
		MAX,
		SUM,
		PRODUCT;

		public static final String[] SUMMARY_OPTIONS={"Mean","Median", "Minimum","Maximum","Sum","Product"};

		public int toInt()
		{
			switch(this)
			{
			case MEAN: return 0;
			case MEDIAN: return 1;
			case MIN: return 2;
			case MAX: return 3;
			case SUM: return 4;
			case PRODUCT: return 5;
			}
			return 0;
		}

		public static SummaryMode fromInt(int i)
		{
			switch(i)
			{
			case 0: return MEAN;
			case 1: return MEDIAN;
			case 2: return MIN;
			case 3: return MAX;
			case 4: return SUM;
			case 5: return PRODUCT;
			}
			return MEAN;
		}
	}

	public enum WeightMode
	{
		ALL_POSITIVE(1,1),
		IN_POSITIVE_OUT_NEGATIVE(1,-1),
		IN_POSITIVE_OUT_ZERO(1,0),
		IN_NEGATIVE_OUT_POSITIVE(-1,1),
		IN_NEGATIVE_OUT_ZERO(-1,0);

		private final int inWeight;
		private final int outWeight;

		public static final String[] WEIGHT_OPTIONS={"All positive","In + / Out - ","In + / Out 0 ","In - / Out + ","In - / Out 0 "};


		private WeightMode(int in, int out)
		{
			inWeight=in;
			outWeight=out;
		}

		public int inWeight()
		{
			return inWeight;
		}

		public int outWeight()
		{
			return outWeight;
		}

		public int toInt()
		{
			switch(this)
			{
			case ALL_POSITIVE: return 0;
			case IN_POSITIVE_OUT_NEGATIVE: return 1;
			case IN_POSITIVE_OUT_ZERO: return 2;
			case IN_NEGATIVE_OUT_POSITIVE: return 3;
			case IN_NEGATIVE_OUT_ZERO: return 4;
			}
			return 0;
		}

		public static WeightMode fromInt(int i)
		{
			switch(i)
			{
			case 0:return ALL_POSITIVE;
			case 1:return IN_POSITIVE_OUT_NEGATIVE;
			case 2:return IN_POSITIVE_OUT_ZERO;
			case 3:return IN_NEGATIVE_OUT_POSITIVE;
			case 4:return IN_NEGATIVE_OUT_ZERO;
			}
			return ALL_POSITIVE;
		}
	}

	@Override
	public void addExperiment(Double experiment) throws RuntimeException 
	{
		// do nothing. 
	}

	@Override
	public Object clone() 
	{
		throw new RuntimeException("This particular Probe is not cloneable");
		//		return super.clone();
	}

	/* (non-Javadoc)
	 * @see mayday.core.Probe#getFirstMissingValue()
	 */
	@Override
	public int getFirstMissingValue() 
	{
		return -1; 
	}

	/* (non-Javadoc)
	 * @see mayday.core.Probe#getNumberOfExperiments()
	 */
	@Override
	public int getNumberOfExperiments() 
	{
		return masterTable.getNumberOfExperiments();
	}

	/* (non-Javadoc)
	 * @see mayday.core.Probe#getNumberOfProbeLists()
	 */
	@Override
	public int getNumberOfProbeLists() 
	{
		return 0;
	}

	/* (non-Javadoc)
	 * @see mayday.core.Probe#getProbeLists()
	 */
	@Override
	public List<ProbeList> getProbeLists() 
	{
		return new LinkedList<ProbeList>();
	}

	@Override
	public void setValue(Double value, int experiment) throws RuntimeException 
	{
		//do nothing
	}

	@Override
	public void setValues(double[] values, boolean doCopy) 
	{
		// do nothing
	}


	@Override
	public void setValues(double[] values) 
	{
		// do nothing
	}

	/**
	 * @return the mode
	 */
	public SummaryMode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(SummaryMode mode) {
		this.mode = mode;
	}

	/**
	 * @return the weightMode
	 */
	public WeightMode getWeightMode() {
		return weightMode;
	}

	/**
	 * @param weightMode the weightMode to set
	 */
	public void setWeightMode(WeightMode weightMode) {
		this.weightMode = weightMode;
	}

	public void stateChanged(SettingChangeEvent e) 
	{

		for(Object o: e.getAdditionalSources())
		{
			if(o instanceof SummaryProbeSetting)
			{
				SummaryProbeSetting setting=(SummaryProbeSetting)o;
				setMode(setting.getSummaryMode());
				setWeightMode(setting.getWeightMode());
				updateSummary();
			}
		}

	}

	public Probe spawn()
	{
		Probe p=new Probe(getMasterTable());
		p.setValues(getValues());
		p.setName(getName());
		getMasterTable().addProbe(p);

		return p;
	}

	public boolean isEmpty()
	{
		return empty;
	}

	public String getName()
	{
		return "Summary"+hashCode()+":"+mode.toInt()+":"+weightMode.toInt()+":";
	}


}
