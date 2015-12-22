/*
 *  Created on Aug 27, 2004
 *
 */
package mayday.clustering.hierarchical_bootstrap;

import mayday.clustering.hierarchical.HierarchicalClusterSettings;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;

public class HierarchicalClusterWithBootstrapSettings extends HierarchicalClusterSettings {

    private IntSetting numberOfResamplings;
    private BooleanHierarchicalSetting doResampling;
    private BooleanSetting overrideEdgeLengths; 
    
    public HierarchicalClusterWithBootstrapSettings() {
    	super();    	
    	numberOfResamplings = new IntSetting("Resamplings",null,10,1,null,true,false);
    	overrideEdgeLengths = new BooleanSetting("Use bootstrap value as edge length",
    			"If selected, replaces original clustering edge lengths with bootstrap distance.\n" +
    			"Otherwise, adds bootstrap edge lengths as edge labels.",false);
    	doResampling = new BooleanHierarchicalSetting("Perform bootstrapping", "Use resampling to compute bootstrap values for all internal edges", false)
    	.addSetting(numberOfResamplings).addSetting(overrideEdgeLengths);
    	root.addSetting(doResampling);
    	connectToPrefTree(PluginInfo.getPreferences("PAS.clustering.hierarchical.bootstrap"));
    	PluginInfo.loadDefaultSettings(root, "PAS.clustering.hierarchical.bootstrap");
    }

    // Using legacy serialization to keep old MIO values working
    public String serialize() {
    	return super.serialize()+"\n"+getNumberOfResamplings(); 
    }
    
    // Using legacy serialization to keep old MIO values working
    public void deserialize(String s) {
    	super.deserialize(s);
    	String[] ess = s.split("\n");
    	int resamplings = Integer.parseInt(ess[4]);
    	setNumberOfResamplings(resamplings);
    }

	public int getNumberOfResamplings() {
		return doResampling.getBooleanValue()?numberOfResamplings.getIntValue():0;
	}

	public void setNumberOfResamplings(int numberOfResamplings) {
    	if (numberOfResamplings>0)
    		this.numberOfResamplings.setIntValue(numberOfResamplings);    		    	
    	doResampling.setBooleanValue(numberOfResamplings>0);
	}

	public boolean getOverrideLengths() {
		return overrideEdgeLengths.getBooleanValue();
	}
	
}
