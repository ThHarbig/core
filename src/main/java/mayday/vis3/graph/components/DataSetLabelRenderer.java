package mayday.vis3.graph.components;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

import mayday.core.DataSet;
import mayday.core.datasetmanager.DataSetManager;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.GraphCanvas;

@SuppressWarnings("serial")
public class DataSetLabelRenderer extends LabelRenderer
{
	private ColorGradientSetting colorGradient=new ColorGradientSetting("Color Gradient","Colors for the class labels",ColorGradient.createRainbowGradient(0, 1));
	private Map<DataSet,Color> colors;
	
	public DataSetLabelRenderer() {

		super();
		setupColors();
	}
	
	private void setupColors() 
	{
		colors=new HashMap<DataSet, Color>();
		int numDS=DataSetManager.singleInstance.getDataSets().size();
		int i=0;
		for(DataSet ds:DataSetManager.singleInstance.getDataSets())
		{
			double val=(1.0*i)/(1.0*numDS);
			val= val* 0.666 + 0.333;
			colors.put(ds, colorGradient.getColorGradient().mapValueToColor(val));
			++i;
		}	
	}


	public JLabel getLabelComponent(GraphCanvas canvas, CanvasComponent component, boolean selected)
	{
		setText(component.getLabel());
		setSize(calculateSize(component));
		setLocation(calculateLocation(component));
		if(selected)
			setBackground(Color.red);
		else
			setBackground(getColor(component));

		return this;
	}
	
	public JLabel getLabelComponent(GraphCanvas canvas, CanvasComponent component, boolean selected, Orientation orientation)
	{
		Orientation bak=getOrientation();
		setOrientation(orientation);
		setText(component.getLabel());
		setSize(calculateSize(component));
		setLocation(calculateLocation(component));
		if(selected)
			setBackground(Color.red);
		else
			setBackground(getColor(component));

		setOrientation(bak);
		return this;
	}

	private Color getColor(CanvasComponent cc)
	{
		if(cc instanceof MultiProbeComponent)
		{
			if(((MultiProbeComponent) cc).getProbes().isEmpty())
				return Color.lightGray;
			else
			{
				DataSet ds=((MultiProbeComponent) cc).getProbes().get(0).getMasterTable().getDataSet();
				return colors.get(ds);
			}
		}else
		{
			return Color.lightGray; 
		}
	}
}
