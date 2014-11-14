package mayday.core.io.dataset.tabular;


import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.table.TableModel;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.gui.abstractdialogs.SimpleStandardDialog;
import mayday.core.io.csv.CSVImportSettingComponent;
import mayday.core.io.csv.ParsingTableModel;
import mayday.core.io.gudi.GUDIConstants;
import mayday.core.io.gudi.prototypes.DatasetFileImportPlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.tasks.AbstractTask;

public class TabularImport extends AbstractPlugin implements DatasetFileImportPlugin {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"PAS.io.TabularImport",
				new String[]{"PAS.core.MIManager"},
				Constants.MC_DATASET_IMPORT,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Imports a dataset from a tabular text file.",
				"Tabular Import"
		);
		pli.getProperties().put(GUDIConstants.IMPORTER_TYPE, GUDIConstants.IMPORTERTYPE_FILESYSTEM);
		pli.getProperties().put(GUDIConstants.FILESYSTEM_IMPORTER_TYPE, GUDIConstants.ONEFILE);
		pli.getProperties().put(GUDIConstants.FILE_EXTENSIONS,"*");
//		pli.getProperties().put(GUDIConstants.IMPORTER_DESCRIPTION,"Tabular Importer 2");
		pli.getProperties().put(GUDIConstants.TYPE_DESCRIPTION,"Text-based tabular format (CSV,TSV,...)");		
		return pli;
	}

	private static TreeMap<String, PluginInfo> MITYPES = new TreeMap<String, PluginInfo>();
	
	public void init() {
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(Constants.MC_METAINFO);
		for (PluginInfo pli : plis)
			if (pli.getInstance() instanceof MIType)
				MITYPES.put(pli.getName(), pli);	
	}

	
	public List<DataSet> importFrom(final List<String> files) {
		final String baseFile = files.get(0);
		
		CSVImportSettingComponent comp = null;
		ParsingTableModel ptm;
		
		try {

			// Step 1: Parse the data into a flexible model
			ptm = new ParsingTableModel(new File(baseFile));

			// Step 2: Create the first dialog  
			comp = new CSVImportSettingComponent(ptm);
			
			SimpleStandardDialog dlg = new SimpleStandardDialog("Load Tabular Data",comp,false);
			dlg.setVisible(true);
			if (!dlg.okActionsCalled())
			       return Collections.emptyList();		
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return Collections.emptyList();
		}

		// Step 3 : Assign columns to three groups: Experiments, ProbeLists, MIOs				
        final ParsingTableModel model = (ParsingTableModel)comp.getTableModel();
        
        // free memory
        model.dropComments();
        
        // Step 4: Process data
        // Create DataSet
		DataSet ds = parseDataset(model, baseFile);

		LinkedList<DataSet> result = new LinkedList<DataSet>();
		if (ds!=null)
			result.add(ds);
        
		return result;
	}
	
	public static DataSet parseDataset(TableModel model, String name) { 
        
		final ColumnTypeDialog ctd = new ColumnTypeDialog(model, MITYPES);
		ctd.setVisible(true);

		if (ctd.canceled())
			return null;
	        
        final DataSet ds = new DataSet(name);
        
        AbstractTask createTable = new ParseDatasetTask(model, ds, ctd);
        createTable.start();
        createTable.waitFor();        
        
        return ds;
	}
        
    protected static class ParseDatasetTask extends AbstractTask {
    	
    	protected TableModel model;
    	protected DataSet ds;
    	protected ColumnTypeDialog ctd;
    	private MIGroup uniqueNames = null;
    	private HashSet<String> namesInUse = new HashSet<String>();
    	private HashMap<String, StringMIO> displayNames = new HashMap<String, StringMIO>();
		
    	public ParseDatasetTask(TableModel tm, DataSet emptyDataset, ColumnTypeDialog ctd) {
    		super("Importing Data");
    		model = tm;
    		ds = emptyDataset;    		
    		this.ctd = ctd;
    	}
    	

		@Override
		protected void doWork() throws Exception {
			
			setProgress(0);

			HashMap<String, ProbeList> probeLists = new HashMap<String, ProbeList>();
			HashMap<String, MIGroup> miGroups = new HashMap<String, MIGroup>();
			ArrayList<String> expNames = new ArrayList<String>();
			HashMap<Integer, String> miClashes = new HashMap<Integer, String>();
			
			
			
			// enumerate columns
			int noe=0;
			for (int i=1; i!=model.getColumnCount(); ++i) {

				switch (ctd.getColumnType(i)) {
				case Experiment:
					expNames.add(model.getColumnName(i));
					++noe;
					break;
				case ProbeList:
					ProbeList pl = new ProbeList(ds, true);
					pl.setName( model.getColumnName(i));
					ds.getProbeListManager().addObject(pl);
					System.out.println("Adding Probe List: "+pl.getName());
					probeLists.put(pl.getName(), pl);
					break;
				case MetaInfo:
					String mioType = ctd.getMIOType(i);
					MIGroup mg = ds.getMIManager().newGroup(mioType, model.getColumnName(i));
					System.out.println("Adding MIGroup: "+mg.getName());
					miGroups.put(mg.getName(), mg);
					
					if(!mg.getName().equals(model.getColumnName(i))) {
						//meta info column name changed due to name clashes with already existing 
						//meta information objects
						System.out.println("Meta-Information Name Clash");
						System.out.println("Using new name: " + mg.getName());
						miClashes.put(i, mg.getName());
					}
					break;
				}
			}
			ds.getMasterTable().setNumberOfExperiments(noe); 
			
	        int nop=model.getRowCount();
			
			HashMap<MIGroup, HashMap<String, MIType>> singleMIOs = new HashMap<MIGroup, HashMap<String, MIType>>();
			for (MIGroup mg : miGroups.values())
				singleMIOs.put(mg, new HashMap<String, MIType>());
			
			for (int curInd=0; curInd!=model.getRowCount();++curInd) {
				Probe p = new Probe(ds.getMasterTable());
				p.setName((String)model.getValueAt(curInd, 0));					
				// for each line: add experiments...
				// add probe to probe lists...
				// add meta information
				for (int colInd=1; colInd!=model.getColumnCount(); ++colInd) {
					String value = (String)model.getValueAt(curInd, colInd);
					switch(ctd.getColumnType(colInd)) {
					case Experiment:
						Double d = null;
						if (value!=null && value.length()>0)
							try {
								d = Double.parseDouble(value);
							} catch (NumberFormatException nfe) {};
						p.addExperiment(d);
						break;
					case ProbeList:
						if (value!=null && value.length()>0) {
							String plname = model.getColumnName(colInd);
							ProbeList pl = probeLists.get(plname);
							pl.addProbe(p);
						}
						break;
					case MetaInfo:
						if (value != null  && value.length()>0) {
							MIGroup mg;
							if(miClashes.containsKey(colInd)) {
								mg = miGroups.get(miClashes.get(colInd));
							} else {
								mg = miGroups.get(model.getColumnName(colInd));
							}
							// save memory
							MIType mt = singleMIOs.get(mg).get(value);
							if (mt==null) {
								mt = MIManager.newMIO(ctd.getMIOType(colInd));
								if (mt.deSerialize(MIType.SERIAL_TEXT, value)) {
									mg.add(p,mt);
									singleMIOs.get(mg).put(value, mt);
								}
							} else {
								mg.add(p, mt);
							}
						}
					}
					
				}
				
				// add the probe object to the mastertable. if names are nonunique, start uniquifying them
				if (uniqueNames!=null) {
					addProbeUnique(p);
				} else {
					// so far, only unique names were found
					try {
						ds.getMasterTable().addProbe(p);							
					} catch (RuntimeException e) {
						writeLog("Probe Name \""+p.getName()+"\" occured twice!\n");
						writeLog("The data contains non-unique probe names. \n" +
								"Unique names will be created automatically, \n" +
								"and the original names will be attached as display names");
						uniqueNames = ds.getMIManager().newGroup("PAS.MIO.String", "Original names");
						// add display names for all probes added so far
						for (Probe pb : ds.getMasterTable().getProbes().values()) {
							StringMIO sm = new StringMIO(pb.getName());
							displayNames.put(pb.getName(), sm);
							uniqueNames.add(pb, sm);		
							namesInUse.add(pb.getName());								
						}
						ds.setProbeDisplayNames(uniqueNames);
						// now add the offending probe with a unique name
						addProbeUnique(p);
					}
				}
				// free memory
				if (model instanceof ParsingTableModel)
					((ParsingTableModel)model).dropRow(curInd);
				
				setProgress((int)((double)curInd*10000.0)/nop);					
			}
			
			// try to simplify names
			int li;
			if ((li = ds.getName().lastIndexOf("/")) > -1) {
				if (li<ds.getName().length());
				String name = ds.getName().substring(li+1).trim();
				if (name.length()>0)
					ds.setName(name);
			}
			
			Color[] colors = GUIUtilities.rainbow( probeLists.size(), 0.75 );
			int k=0;
			for (ProbeList pl : probeLists.values()) {
				pl.setColor(colors[k++]);
			}
			ds.getProbeListManager().addObjectAtBottom(ds.getMasterTable().createGlobalProbeList(true));
			
			for (int i=0; i!=ds.getMasterTable().getNumberOfExperiments(); ++i) 
				ds.getMasterTable().setExperimentName(i, expNames.get(i));
			
			setProgress(10000);

		}

		
		protected String findUniqueName(String name) {
			int add=0;
			String suggested = name;				
			boolean satisfied = true;
			do {
				if (!satisfied)
					suggested = name+" ("+(++add)+")";
				satisfied = !namesInUse.contains(suggested);
			} while (!satisfied);
			namesInUse.add(suggested);
			if (!displayNames.containsKey(name))
				displayNames.put(name, new StringMIO(name));
			return suggested;
		}
		
		protected void addProbeUnique(Probe p) {
			String originalName = p.getName();
			String uniqueName = findUniqueName(originalName);
			StringMIO displayName = displayNames.get(originalName);
			uniqueNames.add(p, displayName);
			p.setName(uniqueName);
			ds.getMasterTable().addProbe(p);
		}
		
		@Override
		protected void initialize() {}
    	
    }
	
} 
