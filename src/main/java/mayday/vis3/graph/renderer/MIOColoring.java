package mayday.vis3.graph.renderer;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import mayday.core.gui.GUIUtilities;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.NumericMIO;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;


public class MIOColoring 
{
	private Map<Object, Color> categoricalColors= new HashMap<Object, Color>();

	protected double miomax, miomin;
	protected MIGroup mg;
	protected boolean categoricalMIO;	
	protected ColorGradientSetting colorGradient = new ColorGradientSetting("Numerical Gradient",null,ColorGradient.createDefaultGradient(0, 16));
	
	public ColorGradientSetting getColorGradient() {
		return colorGradient;
	}

	public void setColorGradient(ColorGradientSetting colorGradient) {
		this.colorGradient = colorGradient;
	}

	public void setMISelection(MIGroupSelection<MIType> mgs) {
		MIGroup mg = null;
		if (mgs.size()>0)
			mg = mgs.get(0);
		setMIGroup(mg);
	}
	
	@SuppressWarnings("unchecked")
	public void setMIGroup(MIGroup mg)
	{
		if (mg!=null) {
			if (mg!=this.mg) {
				this.mg=mg;
				categoricalMIO = !(mg.getMIOs().iterator().next().getValue() instanceof NumericMIO );
				if (categoricalMIO) {
					// find all distinct values
					HashSet<Object> valuesFound = new HashSet<Object>();
					for (Entry<Object,MIType> e :mg.getMIOs()) {
						MIType mt = e.getValue();
						Object value = ((GenericMIO)mt).getValue();
						valuesFound.add(value);
					}
					Color[] r = GUIUtilities.rainbow(valuesFound.size(), 0.75);
					int c = 0;
					for (Object o :valuesFound) {
						categoricalColors.put(o, r[c++]);
					}					
				} else {
					// find min, max
					updateGradient();
				}				
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void updateGradient() 
	{
		if (categoricalMIO)
			return;
		miomin=Double.MAX_VALUE;
		miomax=Double.MIN_VALUE;
		for (Entry<Object,MIType> e :mg.getMIOs()) 
		{
			MIType mt = e.getValue();
			double value = ((NumericMIO<Number>)mt).getValue().doubleValue();
			miomin = miomin<=value?miomin:value;
			miomax = miomax>=value?miomax:value;
		}
		colorGradient.getColorGradient().setMax(miomax);
		colorGradient.getColorGradient().setMin(miomin);
	}
	
	@SuppressWarnings("unchecked")
	public Color getColor(GenericMIO mt) 
	{
		Color c = Color.black; // default to black
		if (mt!=null) 
		{
			if (categoricalMIO) 
			{
				c = categoricalColors.get(mt.getValue());
			} else 
			{
				double v = ((NumericMIO<Number>)mt).getValue().doubleValue();
				c = getColor(v);
			}								
		}	
		return c;
	}
	
	protected Color getColorFromGradient(double position, ColorGradient gradient) 
	{
		Color color = gradient.mapValueToColor(position);
		return color;
	}
	
	public Color getColor(Double value) 
	{
		return getColorFromGradient(value, colorGradient.getColorGradient());
	}

	public void setColorGradient(ColorGradient grad)
	{
		colorGradient.setColorGradient(grad);
		updateGradient();
	}
	
	
	
	
}
