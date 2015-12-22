/*
 * Created on Jan 24, 2005
 *
 */
package mayday.core.plugins.mio.relevance;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mayday.core.DataSet;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.NumericMIO;
import mayday.core.meta.gui.MIGroupSelectionDialog;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.meta.types.RelevanceMIO;


/**
 * @author Nils Gehlenborg
 *
 */
public abstract class RelevanceFunctionPlugin
extends AbstractMetaInfoPlugin
{
	protected double l_min, l_max;
	protected Map<Object,MIType> inputMIOList;
	protected MIGroup selectedGroup;
	
	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_ASK_USER);
		registerAcceptableClass(getMIOClass());
	}
	
	
	@SuppressWarnings("unchecked")
	public void run2(MIGroupSelection<MIType> selection,MIManager miManager) {
		
		selectedGroup = selection.get(0);
		
		Map< Object, MIType > l_inputMIOList = selection.computeUniqueSelection();
	    
	    
	    if ( l_inputMIOList.size() == 0 )
	    {
	      return;
	    }
	    
	    inputMIOList = l_inputMIOList;
	
	    prepare();
	    
	    MIGroup l_outputMIOGroup = miManager.newGroup(
	    		"PAS.MIO.Relevance",
	    		"Relevance rating of \"" + selectedGroup.getName() + "\""
	    		+getMIODescription(),
	    		selectedGroup);

	    for ( Entry<Object,MIType> e : l_inputMIOList.entrySet())
	    {
	      NumericMIO l_originalMIO = (NumericMIO)e.getValue();
	      
	      double newValue = transformValue(((Number)l_originalMIO.getValue()).doubleValue());
	      
	      RelevanceMIO l_mio = new RelevanceMIO( newValue );
	      l_outputMIOGroup.add( e.getKey(), l_mio );
	    }
	    
	}

	public void run(MIGroupSelection<MIType> selection,MIManager miManager) {
		MIGroupSelection<MIType> sel = new MIGroupSelection<MIType>();
		for (MIGroup mg : selection)
			if (getMIOClass().isAssignableFrom(mg.getMIOClass()))
					sel.add(mg);
		if (sel.size()>0)
			run2(sel, miManager);
	}

	
	public void run( List<DataSet> datasets ) {
		  DataSet ds = datasets.get(0);
		    
		  MIGroupSelectionDialog l_dialog = new MIGroupSelectionDialog( ds.getMIManager(), getMIOClass() );
		    		    
		  l_dialog.setVisible( true );
		    
		   if (l_dialog.getSelection().size()==0)
		    	return;
		    
		   run(l_dialog.getSelection(), ds.getMIManager());
		    
	}
	
	protected void prepare() {};
	
	@SuppressWarnings("unchecked")
	protected void computeMinMax() {
	    
		l_min = Double.MAX_VALUE;
	    l_max = Double.MIN_VALUE;

	    for ( Entry<Object,MIType> e : inputMIOList.entrySet() )
	    {
	      double l_double = ((NumericMIO<Number>)e.getValue()).getValue().doubleValue(); 
	      
	      if ( l_double > l_max )
	        l_max = l_double;      
	      if ( l_double < l_min )
	        l_min = l_double;
	    }
	}
	
	protected Class<? extends MIType> getMIOClass() {
		return NumericMIO.class;
	}
	
	protected abstract String getMIODescription();
	
	protected abstract double transformValue(double oldvalue);
}
