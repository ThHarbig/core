package mayday.core.pluginrunner;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.gui.DataSetManagerView;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.UnionProbeList;

public class ProbeListPluginRunner {

	protected List<ProbeList> probeLists;
	protected DataSet ds;
	protected PluginInfo pli;

	public ProbeListPluginRunner(PluginInfo pli, List<ProbeList> probeLists, DataSet ds) {
		this.probeLists=probeLists;
		this.ds = ds;
		this.pli = pli;
	}

	public ProbeListPluginRunner(PluginInfo pli) {
		this.pli=pli;			
	}

	public void execute() {
		prepare();
		if (ds==null || probeLists==null) 
			inferInput();
		if (ds==null || probeLists==null)
			return;

		Thread RunPluginThread = new Thread("PluginRunner")	{
			public void run() {	    			
				try {
					runPlugin();
				} catch ( final Exception exception ) {
					exception.printStackTrace();	  
					SwingUtilities.invokeLater(new Runnable(){
						public void run() {
							JOptionPane.showMessageDialog( null,
									exception.toString(),//getMessage(),
									MaydayDefaults.Messages.ERROR_TITLE,
									JOptionPane.ERROR_MESSAGE );

						}
					});
				}
			}
		};

		RunPluginThread.start();
	}

	protected static void insertIntoProbeListManager(ProbeListManager plm, ProbeList pl, UnionProbeList p) {
		pl.setParent(p);
		plm.addObjectAtTop( pl );
	}

	protected void insertIntoProbeListManager(List<ProbeList> results) {
		if (results==null || results.size()==0)
			return;
		ProbeListManager plm = results.get(0).getDataSet().getProbeListManager();	    	
		UnionProbeList insertionParent = (UnionProbeList) plm.getSharedAncestor(probeLists);
		if (results.size()>1) {
			UnionProbeList upl = new UnionProbeList(plm.getDataSet(), null);
			String namePrefix = pli.getName();
			String name = namePrefix;
			int nameSuffix=0;
			if (plm.contains(name)) 
				name = namePrefix+" ("+(++nameSuffix)+")";	    		
			upl.setName(name);
			upl.setParent(insertionParent);
			DateFormat df = DateFormat.getDateTimeInstance();
			upl.getAnnotation().setQuickInfo("Created "+df.format(new Date())+", using "+probeLists.size()+" input lists (see annotation)");
			String infoString = "Input ProbeLists:";
			for (ProbeList pl : probeLists)
				infoString+="\n- "+pl.getName();
			upl.getAnnotation().setInfo(infoString);
			plm.addObjectAtTop(upl);
			insertionParent=upl;
		}

		for (ProbeList pl : results) {
			plm = pl.getDataSet().getProbeListManager();
			insertIntoProbeListManager(plm, pl, insertionParent);
		}
	}
	
	public static void insertProbeListsIntoProbeListManager(List<ProbeList> theInput, List<ProbeList> toInsert, ProbeListManager plm, String namePrefix) {
		if (toInsert==null || toInsert.size()==0)
			return;
		UnionProbeList insertionParent = (UnionProbeList) plm.getSharedAncestor(theInput);
		if (toInsert.size()>1) {
			UnionProbeList upl = new UnionProbeList(plm.getDataSet(), null);
			String name = namePrefix;
			int nameSuffix=0;
			if (plm.contains(name)) 
				name = namePrefix+" ("+(++nameSuffix)+")";	    		
			upl.setName(name);
			upl.setParent(insertionParent);
			DateFormat df = DateFormat.getDateTimeInstance();
			upl.getAnnotation().setQuickInfo("Created "+df.format(new Date())+", using "+theInput.size()+" input lists (see annotation)");
			String infoString = "Input ProbeLists:";
			for (ProbeList pl : theInput)
				infoString+="\n- "+pl.getName();
			upl.getAnnotation().setInfo(infoString);
			plm.addObjectAtTop(upl);
			insertionParent=upl;
		}

		for (ProbeList pl : toInsert)
			insertIntoProbeListManager(plm, pl, insertionParent);
	}

	protected void prepare() {	    	
//		System.runFinalization();
//		System.gc();
	}

	protected void inferInput() {
		if (DataSetManagerView.getInstance().getSelectedDataSets().size()==0)
			return;
		ds = DataSetManagerView.getInstance().getSelectedDataSets().get(0);
		ProbeListManager probeListManager = ds.getProbeListManager();

		if (probeListManager==null) {
			ds = null;
			return;
		}

		if (probeLists==null) {
			probeLists = new ArrayList<ProbeList>();
			for (Object p:probeListManager.getProbeListManagerView().getSelectedValues())
				probeLists.add( (ProbeList)p );
		}
	}

	protected void runPlugin() {    	
		ProbelistPlugin ppl = (ProbelistPlugin)(pli.getInstance());
		List<ProbeList> results = ppl.run( probeLists, ds.getMasterTable() );
		//remove empty lists
		if (results!=null) {
			LinkedList<ProbeList> res2 = new LinkedList<ProbeList>();
			for (ProbeList p : results)
				if (p.getNumberOfProbes()>0)
					res2.add(p);
			results=res2;
		}
		insertIntoProbeListManager(results);
	}


}