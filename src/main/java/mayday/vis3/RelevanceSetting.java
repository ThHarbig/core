package mayday.vis3;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.NumericMIO;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class RelevanceSetting extends HierarchicalSetting {

	public final static String LINEAR = "linear";
	public final static String EXPONENTIAL = "exponential";
	public final static String LOGARITHMIC = "logarithmic";

	public final static String RELEVANT = "consider as relevant (map to 1)";
	public final static String IRRELEVANT = "consider as irrelevant (map to 0)";

	protected MIGroupSetting mg;
	protected RestrictedStringSetting method;
	protected RestrictedStringSetting missing;
	protected BooleanSetting invert;
	protected MIManager mim;

	protected boolean needUpdate=true;

	public RelevanceSetting(MIManager mim) {
		super("Relevance Information Source");
		addSetting(mg = new MIGroupSetting("Source MI Group",null, null, mim, false).setAcceptableClass(NumericMIO.class));
		addSetting(method = new RestrictedStringSetting("Mapping",null,0,new String[]{LINEAR,EXPONENTIAL,LOGARITHMIC})
		.setLayoutStyle(mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS));
		addSetting(invert = new BooleanSetting("Invert relevance",null,false));
		addSetting(missing = new RestrictedStringSetting("Missing values",null,0, new String[]{IRRELEVANT,RELEVANT})
		.setLayoutStyle(mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS));
		this.mim = mim;
	}

	public RelevanceSetting clone() {
		RelevanceSetting rs = new RelevanceSetting(mim);
		rs.fromPrefNode(this.toPrefNode());
		return rs;
	}

	public void stateChanged(SettingChangeEvent evt) {
		needUpdate = true;
		super.stateChanged(evt);
	}

	// the provider part
	private MIGroup mig;
	private double missingVal;
	private int m;
	private double min;
	private double scale;
	private boolean i;

	protected void update() {
		mig = mg.getMIGroup();		
		if (mig==null) {
			needUpdate = true;
			return;
		}
		if (missing.getStringValue().equals(IRRELEVANT))
			missingVal = 0;
		else
			missingVal = 1;
		m = method.getSelectedIndex();
		i = false;
		// find min and scale			
		min=0;
		double minx=Double.POSITIVE_INFINITY; 
		double max=Double.NEGATIVE_INFINITY;
		scale=1;
		
		for (Object o : mig.getObjects()) {
			double v = getRelevance(o);
			if (!Double.isNaN(v) && !Double.isInfinite(v)) {
				minx = Math.min(v, minx);
				max = Math.max(v, max);
			}
		}
		min=minx;
		scale = 1.0/(max-min);
		i = invert.getBooleanValue();
	}

	@SuppressWarnings("unchecked")
	public double getRelevance(Object o) {
		if (needUpdate) {
			needUpdate=false;
			update();
		}
		double curVal = missingVal;
		if (mig!=null) {
			Object mo = mig.getMIO(o);
			if (mo != null && mo instanceof NumericMIO) {
				Double miv = ((Number)((NumericMIO)mo).getValue()).doubleValue();
				curVal = miv;
			}
		}
		// apply stuff
		switch(m) {
		case 1: 
			curVal = Math.exp(curVal);
			break;
		case 2:
			curVal = Math.log(curVal);
			break;
		} 
		// scale in any case
		curVal = (curVal - min)*scale;
		if (i)
			curVal = 1.0-curVal;
		if (Double.isInfinite(curVal))
			curVal = Double.NaN;
		return curVal;
	}

}