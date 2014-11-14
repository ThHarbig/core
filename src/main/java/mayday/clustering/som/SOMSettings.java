package mayday.clustering.som;

import mayday.core.math.clusterinitializer.InitializerType;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.math.functions.RotationalKernelFunctionType;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;

public class SOMSettings extends Settings
{
	private IntSetting cycles;
	private RestrictedStringSetting gridTopology;
	private IntSetting mapRows;
	private IntSetting mapCols;
	private RestrictedStringSetting rotationalKernel;
	private DoubleSetting initialKernelRadius;
	private DoubleSetting finalKernelRadius;
	private RestrictedStringSetting mapUnitInitializer;
	private DistanceMeasureSetting distanceMeasure;
	private BooleanSetting normalizeData;
	private StringSetting clusterIdentifierPrefix;

	private String[] gridTopologies={"rectangular","hexagonal"};
	private String[] kernels={"Gaussian","Cut gaussian", "Bubble","Parabola" };
	private String[] initializers={"Random initializer","Random data point" };


	public SOMSettings() 
	{
		super(new HierarchicalSetting("SOM Settings"), PluginInfo.getPreferences("PAS.clustering.batchsom"));
		cycles=new IntSetting("Cycles","Number of Cycles to perform",250);
		gridTopology=new RestrictedStringSetting("Grid Topology","Topology of the SOM-map:",0,gridTopologies);
		mapRows=new IntSetting("Rows","Rows of the map",2);
		mapCols=new IntSetting("Columns","Columns of the map",2);
		rotationalKernel=new RestrictedStringSetting("Kernel function",null,0,kernels);
		initialKernelRadius=new DoubleSetting("Initial kernel radius", null, 2.0);
		finalKernelRadius=new DoubleSetting("Final kernel radius", null, 0.1);
		mapUnitInitializer=new RestrictedStringSetting("Initializer of the SOM-units: ",null, 1, initializers);
		distanceMeasure=new DistanceMeasureSetting("Distance Measure", null, DistanceMeasureManager.get("Euclidean"));
		normalizeData=new BooleanSetting("Normalize Data", null, true);
		clusterIdentifierPrefix=new StringSetting("Prefix of the cluster identifier",null, "SOM");

		root.
		addSetting(cycles).
		addSetting(gridTopology).
		addSetting(mapRows).
		addSetting(mapCols).
		addSetting(rotationalKernel).
		addSetting(initialKernelRadius).
		addSetting(finalKernelRadius).
		addSetting(mapUnitInitializer).
		addSetting(distanceMeasure).
		addSetting(normalizeData).
		addSetting(clusterIdentifierPrefix);	
	}

	/**
	 * This method is used to get cycles. 
	 *
	 * @return Returns the cycles.
	 */
	public int getCycles() {
		return cycles.getIntValue();
	}


	/**
	 * This method is used to set the cycles 
	 *
	 * @param cycles The cycles to set.
	 */
	public void setCycles(int cycles) {
		this.cycles.setIntValue(cycles);
	}


	/**
	 * This method is used to get clusterIdentifierPrefix. 
	 *
	 * @return Returns the clusterIdentifierPrefix.
	 */
	public String getClusterIdentifierPrefix() {
		return clusterIdentifierPrefix.getStringValue();
	}


	/**
	 * This method is used to set the clusterIdentifierPrefix 
	 *
	 * @param clusterIdentifierPrefix The clusterIdentifierPrefix to set.
	 */
	public void setClusterIdentifierPrefix(String clusterIdentifierPrefix) {
		this.clusterIdentifierPrefix.setStringValue(clusterIdentifierPrefix);
	}


	/**
	 * This method is used to get distanceMeasure. 
	 *
	 * @return Returns the distanceMeasure.
	 */
	public DistanceMeasurePlugin getDistanceMeasure() {
		return distanceMeasure.getInstance();
	}


	/**
	 * This method is used to set the distanceMeasure 
	 *
	 * @param distanceMeasure The distanceMeasure to set.
	 */
	public void setDistanceMeasure(DistanceMeasurePlugin distanceMeasure) {
		this.distanceMeasure.setInstance(distanceMeasure);
	}


	/**
	 * This method is used to get finalKernelRadius. 
	 *
	 * @return Returns the finalKernelRadius.
	 */
	public double getFinalKernelRadius() {
		return finalKernelRadius.getDoubleValue();
	}


	/**
	 * This method is used to set the finalKernelRadius 
	 *
	 * @param finalKernelRadius The finalKernelRadius to set.
	 */
	public void setFinalKernelRadius(double finalKernelRadius) {
		this.finalKernelRadius.setDoubleValue(finalKernelRadius);
	}


	/**
	 * This method is used to get initialKernelRadius. 
	 *
	 * @return Returns the initialKernelRadius.
	 */
	public double getInitialKernelRadius() {
		return initialKernelRadius.getDoubleValue();
	}


	/**
	 * This method is used to set the initialKernelRadius 
	 *
	 * @param initialKernelRadius The initialKernelRadius to set.
	 */
	public void setInitialKernelRadius(double initialKernelRadius) {
		this.initialKernelRadius.setDoubleValue(initialKernelRadius);
	}


	/**
	 * This method is used to get kernelFunction. 
	 *
	 * @return Returns the kernelFunction.
	 */
	public RotationalKernelFunctionType getKernelFunction() 
	{
		int idx=rotationalKernel.getSelectedIndex();
		switch (idx) 
		{
		case 0:		return RotationalKernelFunctionType.GAUSSIAN;
		case 1:		return RotationalKernelFunctionType.CUT_GAUSSIAN;
		case 2:		return RotationalKernelFunctionType.BUBBLE;
		case 3:		return RotationalKernelFunctionType.PARABOLA;
		default:
			return null;
		}
	}


	/**
	 * This method is used to set the kernelFunction 
	 *
	 * @param kernelFunction The kernelFunction to set.
	 */
	public void setKernelFunction(RotationalKernelFunctionType kernelFunction) {
		switch (kernelFunction) 
		{
		case GAUSSIAN: rotationalKernel.setSelectedIndex(0);			
		break;
		case CUT_GAUSSIAN: rotationalKernel.setSelectedIndex(1);			
		break;
		case BUBBLE: rotationalKernel.setSelectedIndex(2);			
		break;
		case PARABOLA: rotationalKernel.setSelectedIndex(3);			
		break;
		}
	}


	/**
	 * This method is used to get mapCols. 
	 *
	 * @return Returns the mapCols.
	 */
	public int getMapCols() {
		return mapCols.getIntValue();
	}


	/**
	 * This method is used to set the mapCols 
	 *
	 * @param mapCols The mapCols to set.
	 */
	public void setMapCols(int mapCols) {
		this.mapCols.setIntValue(mapCols);
	}


	/**
	 * This method is used to get mapRows. 
	 *
	 * @return Returns the mapRows.
	 */
	public int getMapRows() {
		return mapRows.getIntValue();
	}


	/**
	 * This method is used to set the mapRows 
	 *
	 * @param mapRows The mapRows to set.
	 */
	public void setMapRows(int mapRows) {
		this.mapRows.setIntValue(mapRows);
	}


	/**
	 * This method is used to get mapTopology. 
	 *
	 * @return Returns the mapTopology.
	 */
	public GridTopology getMapTopology() 
	{
		switch (gridTopology.getSelectedIndex()) 
		{
		case 0: return GridTopology.RECTANGULAR;
		case 1: return GridTopology.HEXAGONAL;
		default:
			return null;
		}
	}


	/**
	 * This method is used to set the mapTopology 
	 *
	 * @param mapTopology The mapTopology to set.
	 */
	public void setMapTopology(GridTopology mapTopology) 
	{
		switch (mapTopology) 
		{
		case RECTANGULAR: 
			this.gridTopology.setSelectedIndex(0);
			return;
		case HEXAGONAL:
			this.gridTopology.setSelectedIndex(1);
			return;	

		}
	}


	/**
	 * This method is used to get mapUnitInitializer. 
	 *
	 * @return Returns the mapUnitInitializer.
	 */
	public InitializerType getMapUnitInitializer()
	{
		switch (mapUnitInitializer.getSelectedIndex()) 
		{
		case 0: return InitializerType.RANDOM;
		case 1: return InitializerType.RANDOM_DATA_POINT;
		default:return null;
		}
	}


	/**
	 * This method is used to set the mapUnitInitializer 
	 *
	 * @param mapUnitInitializer The mapUnitInitializer to set.
	 */
	public void setMapUnitInitializer(InitializerType mapUnitInitializer) 
	{
		switch (mapUnitInitializer) 
		{
			case RANDOM:
				this.mapUnitInitializer.setSelectedIndex(0);
			break;
			case RANDOM_DATA_POINT:
				this.mapUnitInitializer.setSelectedIndex(1);
				break;
		default:
			break;
		}		
	}


	/**
	 * This method is used to get normalizeData. 
	 *
	 * @return Returns the normalizeData.
	 */
	public boolean isNormalizeData() {
		return normalizeData.getBooleanValue();
	}


	/**
	 * This method is used to set the normalizeData 
	 *
	 * @param normalizeData The normalizeData to set.
	 */
	public void setNormalizeData(boolean normalizeData) {
		this.normalizeData.setBooleanValue(normalizeData);
	}

}
