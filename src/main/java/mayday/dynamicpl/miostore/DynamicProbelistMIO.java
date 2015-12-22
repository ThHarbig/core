package mayday.dynamicpl.miostore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.ProbeListListener;
import mayday.core.StoreEvent;
import mayday.core.StoreListener;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.io.StorageNode;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupEvent;
import mayday.core.meta.MIGroupListener;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.MIRendererDefault;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.dynamicpl.DynamicProbeList;
import mayday.dynamicpl.RuleSet;

public class DynamicProbelistMIO extends GenericMIO<StorageNode> {

	protected DynamicProbeList dynamicProbeList;


	public final static String myType = "PAS.dynamicPL.MIO";

	@Override
	public MIType clone() {
		DynamicProbelistMIO dplm = new DynamicProbelistMIO();
		dplm.deSerialize(SERIAL_TEXT, this.serialize(SERIAL_TEXT));
		return dplm;
	}

	@Override
	public void init() {
	}
	

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Represents the ruleset of a dynamic probelist",
				"Dynamic Probe List Rule Set MIO"
				);	}

	public boolean deSerialize(int serializationType, String serializedForm) {		
		if (serializationType==MIType.SERIAL_TEXT)
			try {
				Value.loadFrom(new BufferedReader(new StringReader(serializedForm)));
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		return false;
	}

	@SuppressWarnings("unchecked")
	public AbstractMIRenderer getGUIElement() {
		MIRendererDefault mrd = new MIRendererDefault();
		mrd.setEditorValue("Internal data");
		return mrd;
	}
	
	public String getType() {
		return myType;
	}

	public String serialize(int serializationType) {
		getDataFromDPL();
		if (serializationType==MIType.SERIAL_TEXT) {
			StringWriter sw = new StringWriter();
			try {
				Value.saveTo(new BufferedWriter(sw));
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			return sw.toString();
		} else {
			throw new RuntimeException("Dynamic Probe List MIO has not implemented SERIAL_XML serialization.");
		}
	}
	
	protected void getDataFromDPL() {
		Value = new StorageNode();
		if (dynamicProbeList!=null) {
			Value = dynamicProbeList.getRuleSet().toStorageNode();
		}
	}
	
	protected void convertProbeList(ProbeList pl) {
		DynamicProbeList dpl = new DynamicProbeList(pl.getDataSet());
		dpl.setName(pl.getName());
		dpl.setColor(pl.getColor());
		
		RuleSet rs = dpl.getRuleSet();
		rs.fromStorageNode(Value);

		dpl.getAnnotation().setInfo(pl.getAnnotation().getInfo());
		dpl.getAnnotation().setQuickInfo(pl.getAnnotation().getQuickInfo());
		
		// copy existing mios except for ourselves
		MIManager mim = pl.getDataSet().getMIManager();
		MIGroupSelection<MIType> mgs = mim.getGroupsForObject(pl);
		for (MIGroup mg : mgs) {
			MIType mt = mg.getMIO(pl);
			if (mt!=this && ! (mt instanceof AnnotationMIO))
				mg.add(dpl, mg.getMIO(pl));
		}

		// Now this is important, because it fixes an annoying bug when loading DPLs which depend on other
		// DPLs where the dependent DPL would turn up empty.
		// --> Re-link all listeners of pl into dpl BEFORE closing pl
		for (ProbeListListener list : pl.getProbeListListeners()) {
			pl.removeProbeListListener(list);
			dpl.addProbeListListener(list);
		}
		
		// now do the switchover
		pl.getDataSet().getProbeListManager().replaceObject(pl, dpl);
		pl.propagateClosing();
	}
	
	
	// And now the horrible hacks that enable us to recreate dpls at runtime
	public DynamicProbelistMIO() {
//		System.out.println("DPLMIO created");
		Value = new StorageNode();
		insertionHackStep1();
	}
	
	
	private GroupAddedListener insertionGroupAddedListener;
	private DataSetAddedListener insertionDataSetAddedListener;
	
	private void insertionHackStep1() {
		// now try to find out who we belong to
		StackTraceElement[] callingStack = Thread.getAllStackTraces().get(Thread.currentThread());
		boolean calledFromPlumaInit = false;
		for (StackTraceElement ste : callingStack) {
			if (ste.getClassName().equals(PluginManager.class.getCanonicalName())
					&& ste.getMethodName().equals("init")) {
				calledFromPlumaInit = true;
				break;
			}
		}
		// 1. if PluginManager.init() is in the current stack trace, ignore
		if (calledFromPlumaInit) return;
		// 2. put listeners into all datasets to listen for the moment where i am put into a miogroup
		insertionGroupAddedListener= new GroupAddedListener();
		for (Object ods : DataSetManager.singleInstance.getObjects()) {
			DataSet ds = (DataSet)ods;
			ds.getMIManager().addListenerForObject(insertionGroupAddedListener);
		}
		// 3. put listener into the datasetmanager to wait for new datasets
		insertionDataSetAddedListener = new DataSetAddedListener();
		DataSetManager.singleInstance.addStoreListener(insertionDataSetAddedListener);
		// wait for GroupAddedListener to fire
	}
	
	private void insertionHackStep2(ProbeList pl) {
		// remove all listeners
		DataSetManager.singleInstance.removeStoreListener(insertionDataSetAddedListener);
		for (Object ods : DataSetManager.singleInstance.getObjects()) {
			DataSet ds = (DataSet)ods;
			ds.getMIManager().removeListenerForObject(insertionGroupAddedListener);
		}
		if (pl!=null)
			convertProbeList(pl);
	}
	
	private class DataSetAddedListener implements StoreListener {

		@SuppressWarnings("deprecation")
		public void objectAdded(StoreEvent event) {
			// try to find ourselves in the new dataset
			boolean itIsDone=false;
			DataSet ds = ((DataSet)event.getObject());
			MIGroupSelection<MIType> mgs = ds.getMIManager().getGroupsForType(myType);
			for (MIGroup mg : mgs) {
				List<Object> lo = (mg.getObjectsForMIO(DynamicProbelistMIO.this));
				for (Object o : lo)
					if (o instanceof ProbeList) {
						insertionHackStep2((ProbeList)o);
						itIsDone = true;
					}
			}
			if (!itIsDone) // wait for later
				((DataSet)event.getObject()).getMIManager().addListenerForObject(insertionGroupAddedListener);
		}

		public void objectRemoved(StoreEvent event) {
		}
		
	}
	
	private class GroupAddedListener implements MIGroupListener {

		public Object getWatchedObject() {
			return null;
		}

		public void miGroupChanged(MIGroupEvent event) {
			if (event.getChange()==MIGroupEvent.MIO_ADDED) {
				if (event.getMioExtendable() instanceof ProbeList) {// now it gets interesting
					MIGroup mg = ((MIGroup)event.getSource());
					Object extended = event.getMioExtendable();
					Object MIO = mg.getMIO(extended);
					// if the MIO is myself, this is interesting for me
					if (MIO==DynamicProbelistMIO.this) {
						// if I am already attached to a DPL, I remove my listeners
						ProbeList conversionTarget = null;
						// if I am attached to a regular PL, I need to convert it
						if (!(extended instanceof DynamicProbeList))
							conversionTarget = (ProbeList)extended;
						insertionHackStep2(conversionTarget);
					}					
				}
			}
		}
		
	}
	
	
	public DynamicProbeList getDynamicProbeList() {
		return dynamicProbeList;
	}

	public void setDynamicProbeList(DynamicProbeList dynamicProbeList) {
		this.dynamicProbeList = dynamicProbeList;
	}
	
	
	
	public String toString() {
		return (dynamicProbeList!=null?dynamicProbeList.getRuleSet().toDescription():"");
	}


	
}
