package mayday.core.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JLabel;

import mayday.core.DataSet;
import mayday.core.DataSetEvent;
import mayday.core.DataSetListener;
import mayday.core.DelayedUpdateTask;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.ProbeListEvent;
import mayday.core.ProbeListListener;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.probelistmanager.gui.ProbeListManagerView;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;

public class ProbeListImageStorage
{
	private class ImageInfo {
		ProbeListImage data;
		Color currentCol;
		int numOfProbes;
		int hashCode;
	}
	
	@SuppressWarnings("serial")
	private class ProbeListMap extends HashMap<ProbeList, ImageInfo> 
	implements ProbeListListener {

		public ImageInfo put(ProbeList pl) {
			return put(pl, new ImageInfo());
		}
		
		public ImageInfo put(ProbeList pl, ImageInfo ii) {
			if (!this.containsKey(pl))
					pl.addProbeListListener(this);
			return super.put(pl, ii);
		}
		
		public ImageInfo remove(Object o) {
			if (o instanceof ProbeList)
				((ProbeList)o).removeProbeListListener(this);
			return super.remove(o);
		}
		
		public void probeListChanged(ProbeListEvent event) {
			if (event.getChange()==ProbeListEvent.PROBELIST_CLOSED) {
				this.remove(event.getSource());
			}			
		}
		
		
		public ImageInfo getOrCreate(ProbeList ds) {
			ImageInfo plm = get(ds);
			if (plm==null) {
				put(ds);
				plm=get(ds);
			}
			return plm;
		}
		
		public void clear() {
			for (ProbeList p : keySet())
				p.removeProbeListListener(this);
			super.clear();
		}
		
	}
	
	@SuppressWarnings("serial")
	private class DataSetMap extends HashMap<DataSet, ProbeListMap> 
	implements DataSetListener {

		public ProbeListMap put(DataSet ds) {
			return put(ds, new ProbeListMap());
		}
		
		public ProbeListMap getOrCreate(DataSet ds) {
			ProbeListMap plm = get(ds);
			if (plm==null) {
				put(ds);
				plm = get(ds);
			}
			return plm;
		}
		
		public ProbeListMap put(DataSet ds, ProbeListMap plm) {
			if (!this.containsKey(ds))
				ds.addDataSetListener(this);
			return super.put(ds,plm);
		}	
		
		public ProbeListMap remove(Object ds) {
			if (ds instanceof DataSet) 
				((DataSet)ds).removeDataSetListener(this);
			return super.remove(ds);
		}
		
		public void dataSetChanged(DataSetEvent event) {
			if (event.getChange()==DataSetEvent.CLOSING_CHANGE) {
				this.remove(event.getSource());
			}
		}
		
		public void clear() {
			for (ProbeListMap plm : values())
				plm.clear();
			super.clear();
		}
		
	}
	
	private DataSetMap content = new DataSetMap();
	
	private Color defaultBackground;
	
	private static ProbeListImageStorage instance;
	
	private ProbeListImageStorage()	{
		defaultBackground = new JLabel().getBackground();
		// register a listener to changes in the settings for the images
		ProbeListImage.setting.addChangeListener(new SettingChangeListener() {

			public void stateChanged(SettingChangeEvent arg0) {
		    	singleInstance().clearCache();
		    	for (DataSet ds : DataSetManager.singleInstance.getDataSets())
		    		ds.getProbeListManager().getProbeListManagerView().updateCellRenderer();
			}
			
		});

	}
	
	public static ProbeListImageStorage singleInstance()
	{
		if(instance==null)
			instance=new ProbeListImageStorage();
		return instance;
	}
	
	public ProbeListImage getImage(ProbeList pl)
	{		

		ProbeListMap plm = content.getOrCreate(pl.getDataSet());
		ImageInfo ii = plm.getOrCreate(pl);		
		ProbeListImage pli = ii.data;
		
		if(pli==null) { 			// not in cache
			ii.currentCol = pl.getColor();
			ii.numOfProbes = pl.getNumberOfProbes();
			ii.hashCode=pl.hashCode();
			//start a task to draw the actual image
			updateCache(ii,pl);			
			// while this is computing, return a renderer with no image
			pli = new ProbeListImage(null, defaultBackground);
			// and put this empty image into the cache so we won't start more than one thread
			synchronized(ii) {
				if (ii.data==null)
					ii.data=pli;
			}
		} else {
			// check if the color is still correct. If not, update the color as soon as possible, but return the old image for speedup
			// same for changed content
			
			if ( ii.currentCol==null || !ii.currentCol.equals(pl.getColor()) || ii.numOfProbes!=pl.getNumberOfProbes()
					|| ii.hashCode!=pl.hashCode() ) {
				ii.currentCol=pl.getColor();
				ii.numOfProbes=pl.getNumberOfProbes();
				ii.hashCode=pl.hashCode();
				updateCache(ii,pl);						
			}
			
		}		
		return pli;
	}
	
	protected HashMap<ProbeList,ImageInfo> images_to_do = new HashMap<ProbeList,ImageInfo>();
	
	protected void addForUpdating(ImageInfo ii, ProbeList p) {
		synchronized(this) {
			images_to_do.put(p,ii);
		}
	}
	
	protected Object[] getForUpdating() {
		synchronized(this) {
			if (images_to_do.size()==0)
				return null;
			Entry<ProbeList, ImageInfo> e = images_to_do.entrySet().iterator().next();
			Object[] ret = new Object[]{e.getKey(), e.getValue()};
			images_to_do.remove(e.getKey());
			return ret;
		}
	}
	
	protected boolean hasWork() {
		synchronized(this) {
			return images_to_do.size()>0;
		}
	}
	
	protected DelayedUpdateTask imageMaker = new DelayedUpdateTask("Creating ProbeList images", 1000) {

		protected boolean needsUpdating() {
			return hasWork();
		}

		protected void performUpdate() {
			Object[] itd = getForUpdating();
			Set<DataSet> waiting_for_repaint = new HashSet<DataSet>();
			while ( itd!=null ) {
				ProbeList p = (ProbeList) itd[0];
				
				ImageInfo ii = (ImageInfo) itd[1];
				ProbeListImage img=null;
				if (MaydayDefaults.isDebugMode())
					System.out.println("Creating ProbeList image for "+p.getName());
				while (img==null) {
					try {
						img=new ProbeListImage(p);
					} catch (Exception e) {
						// trying again in a moment
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
						}
					}
				}					
				synchronized(ii) {
					ii.data=img;
				}
				waiting_for_repaint.add(p.getDataSet());
				itd = getForUpdating();
			}
			
			for (DataSet ds : waiting_for_repaint) {
				ProbeListManagerView plmv = ds.getProbeListManager().getProbeListManagerView();
				if (plmv!=null)
					plmv.getComponent().repaint();
			}
		}
		
	};
	
	protected void updateCache(final ImageInfo ii, final ProbeList p) {
		addForUpdating(ii, p);
		imageMaker.trigger();
	}
	
	public void clearCache()
	{
		content.clear();
	}

}
