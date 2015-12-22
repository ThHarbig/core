package mayday.mpf;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.meta.types.StringMapMIO;


/**
 * MaydayDataObject encapsulates a probelist in such a way that adding and removing
 * probes is made easier for people who want to create their own filter plugins for
 * the MPF. This class makes sure that Maydays data structures remain consistent
 * including but not limited to cases wherein a filtering pipeline terminates with
 * an Exception.
 * @author Florian Battke
 */
// reading comments in this class could provide interesting insights into some aspects
// of Probe and ProbeList handling in Mayday. -fb

public class MaydayDataObject implements Iterable<Probe> {

	private static ProbeContainerMap silencedContainers = new ProbeContainerMap();
	
	private ProbeList pl;
	private ProbeList pl_orig; // probelist before this.makeUnique()
	private String pl_name;
	/* 
	 * ProbeList.equals compares NAMES! ==> probe.removeProbeList(xx) does not remove xx by pointer value but by NAME! Can you believe it!
	 */

    private HashSet<Probe> newlyCreatedProbes = new HashSet<Probe>(); //should do contains in near constant / logn time
	private boolean isCopy = false; // lazy copy
	private String temporaryModifier = "~"+new Random().nextInt()+"~";
	private String nameModifier=temporaryModifier;
	private Map<String,String> annotation;
	private String slotName = "";

	/** Constructs a new instance for a given ProbeList
	 * @param pl the probelist to wrap
	 */
	public MaydayDataObject(ProbeList pl) {
		this.pl = pl;
		this.pl_orig=pl;
		this.pl_name = pl.getName();
		silencedContainers.silenceThis(pl);
		silencedContainers.silenceThis(pl.getDataSet().getMasterTable());
	}
	
	/** Constructs a new instance with a new ProbeList, e.g. for importers */
	public MaydayDataObject(DataSet ds) {
		this(new ProbeList(ds, false));
		this.isCopy=true;
	}

	/** Returns a copy of the current MaydayDataObject.
	 * All Probes that had been added to this MaydayDataObject will be cloned into the duplicate to avoid interference between
	 * the two objects.
	 * @return the copy
	 */

	public MaydayDataObject duplicate() {
		MaydayDataObject nmdo = new MaydayDataObject(pl);
		nmdo.pl_name = this.pl_orig.getName();  // remove temporary name modifier!
		nmdo.pl_orig = this.pl_orig;
		for (Probe pb : newlyCreatedProbes) {
			nmdo.remove(pb);  // remove this probe in the new mdo, so we can add it with a proper name
			Probe pbx = (Probe)pb.clone();
			if (pbx.getProbeLists().contains(pl))
					pbx.removeProbeList(pl);
			String oldname = restoreNamePrefix(pbx.getName(),this.temporaryModifier);
			pbx.setName(oldname);
			pbx = nmdo.add(pbx);
			cloneMIOs(pb, pbx);
		}
		return nmdo;
	}

	/** Returns a MaydayDataObject with the same name, annotation etc but with no Probes inside.
	 * If you are using this function instead of OutputData[0]=InputData[0], make sure to call
	 * InputData[0].dismiss() to clean up memory used by InputData[0];
	 * @return the new MaydayDataObject
	 * @deprecated because memory cleanup is not guaranteed with this function
	 */
	@Deprecated
	public MaydayDataObject empty_copy() {
        ProbeList l_probeList = new ProbeList( pl.getDataSet(), pl.isSticky() );
        l_probeList.setAnnotation( pl.getAnnotation().clone() );
        l_probeList.setColor( new Color( pl.getColor().getRGB() ) );
        return new MaydayDataObject(l_probeList);
	}

	/** Sets the name modifier to append to the attached probelist's name upon reintegration
	 * into Mayday as well as to all probes that are added to this MaydayDataObject.
	 * @param nm the new name modifier to use
	 */
	public void setNameModifier(String nm) {
		// alle alten modifier entfernen und durch neue ersetzen und dabei alle namen auf uniqueness pr�fen
		String oldModifier = nameModifier;
		nameModifier = nm;
		for (Probe p : newlyCreatedProbes) {
			String oldname = restoreNamePrefix(p.getName(),oldModifier);
			String newname = generateUniqueName(oldname);
			// I HAVE TO remove the probe from the probelist before adding it again under the new name because
			// plugins like e.g. Profile Plot don't like it if the hashmap key != the hashmap value's name
			pl.removeProbe(p);
			p.setName(newname);
			pl.addProbe(p);
		}
	}
	
	/** Sets the slot name for the output slot that this MDO is finally returned from. The slotno will be shown in 
	 * the name modifier of the probe list but NOT in the name modifiers of probes created in this MDO. This is because
	 * a probe created in a MDO that is later MDO.duplicate()d will result in that probe being added twice with different
	 * modifiers to the global dataset, even when both probes are identical objects before reintegrate is called. 
	 * @param sn
	 */
	public void setSlotName(String sn) {
		this.slotName = sn;
	}

	/**  Returns a Probe specified by it's name in the wrapped ProbeList
	 * You MUST NOT change the returned Probe in any way! (Of coure, I could have ensured that by returning a clone,
	 * but I want the function to be fast)
	 * @param Name the name of the Probe to return
	 * @return the desired probe
	 * @throws RuntimeException if the operation fails
	 * @see ProbeList#getProbe(String)
	 */
	public Probe getProbe(String Name) throws RuntimeException {
		return pl.getProbe(Name);
	}

	/** adds a probe to the wrapped Probelist and creates a new unique name for the probe if neccessary.
	 * The added probe is a clone of the parameter pb.
	 * @param pb the probe to add
	 * @return the newly added probe
	 */
	public Probe add(Probe pb) {
		makeUnique();
		Probe pbx = (Probe)pb.clone();
		String uniqueName = generateUniqueName(pbx.getName());
		pbx.setName(uniqueName);
		cloneMIOs(pb,pbx);
		pl.addProbe(pbx);
		this.newlyCreatedProbes.add(pbx);
		return pbx;
	}

	/** Returns the number of probes in this MaydayDataObject
	 * @return the number of probes
	 */
	public int size() {
		return pl.getNumberOfProbes();
	}

	/* remember the last value that was successful in generating a unique name.
	 * This will help us speed up generateUniqueName() if many probes have name clashes
	 * and would thus require multiple runs through the while-loop in that function.
	 * By using the last known good value for "i", I can give a huge speed-up here. A small
	 * but acceptable tradeoff is that uniquified names don't always have the lowest "i" suffix.
	 */
	private int lastSuccessfulUniquifier = 0;

	/** Creates a unique probe name using a given prefix.
	 * The name is made unique by adding the name modifier as well as the
	 * lowest integer that creates a unique name
	 * @param prefix a prefix that the name will start with
	 * @return the unique name
	 */
	private String generateUniqueName(String prefix) {
		String suffix = " ["+this.nameModifier+"]";
		String name = prefix+suffix;
		if (pl.contains(name) || containedInGlobalList(name)) {
			int i=lastSuccessfulUniquifier-1;
			do {
				++i;
				name = prefix+suffix+"."+i;
			} while (pl.contains(name) || containedInGlobalList(name));
			lastSuccessfulUniquifier = i;
		}		
		return name;
	}

	private String restoreNamePrefix(String modifiedName, String modifier) {
		return modifiedName.substring(0,modifiedName.indexOf(" ["+modifier));
	}

	private boolean containedInGlobalList(String Name) {
		// There MUST be an easier way to do this, but I can't find it
		return pl.getDataSet().getMasterTable().getProbes().containsKey(Name);
	}

	/** Removes a probe from this MaydayDataObject
	 * @param pb The probe to remove
	 */
	public void remove(Probe pb) {
		makeUnique();
		pl.removeProbe(pb);
		newlyCreatedProbes.remove(pb);
	}

	/** Creates a new Probe in the wrapped ProbeList and returns it
	 * If the name is already taken, it is made unique (@see #generateUniqueName(String))
	 * @param uniqueName the new name for the Probe.
	 * @return the newly created Probe
	 */
	public Probe newProbe(String uniqueName) { // create a new probe in this probelist
		makeUnique();
		Probe new_ = new Probe(pl.getDataSet().getMasterTable());
		uniqueName = generateUniqueName(uniqueName);
		new_.setName(uniqueName);
		pl.addProbe(new_);
		newlyCreatedProbes.add(new_);
		return new_;
	}
	

	/** Copies MIO objects from one probe to another. The target probe is assumed to belong to the same DataSet as this MaydayDataObjects ProbeList	 *
	 * @param old the probe to copy MIOs from
	 * @param new_ the probe to copy MIOs to
	 */
	public void cloneMIOs(Object old, Object new_) {
		// now clone the MIOs. This is now possible due to changes in MIOGroup.java (marked //060507, fb:)
		//MIContainer oldC = old.getMIContainer();
		mayday.core.meta.MIManager mimanager = pl.getDataSet().getMIManager();

		for (MIGroup mg : mimanager.getGroupsForObject(old)) {
			// do NOT clone the annotation of probelists
			if ((old instanceof ProbeList) && mg.getMIOType()=="PAS.MIO.Annotation")
				continue;
			MIType oldMIO = mg.getMIO(old);
			mg.add(new_,oldMIO);
		}
	}

	/** Duplicates a given probe so that in the end we have two identical probes
	 * The input probe need not belong to this MaydayDataObject, yet it must be in the same mastertable
	 * (Of course, their names differ) @see #generateUniqueName(String)).
	 * @param pb the probe to clone
	 * @return the newly created probe
	 */
	public Probe cloneProbe(Probe pb) { // duplicates an existing probe => we have two probes now
		makeUnique();
		Probe old = pb;
		Probe new_ = (Probe)old.clone();
		// find new name
		String Name = generateUniqueName(new_.getName());
		new_.setName(Name);
		// old may be in pl_orig and definitely is in pl, same for new_
		//if (pl.contains(new_)) pl.removeProbe(new_); //for renaming
		if (pl_orig.contains(new_)) pl_orig.removeProbe(new_);
		// now new is in no probelist		
		cloneMIOs(old,new_); //must be called after the new probe has a unique name!
		pl.addProbe(new_); // now its there with a new name		
		newlyCreatedProbes.add(new_);
		return new_;
	}

	/** Safely replace all values of a Probe with new values.
	 * @param pb the Probe that will recieve the new values
	 * @param newValues the Probe that contains the new values to be set
	 * @throws RuntimeException if both Probes don't have the same number of experiments
	 * @return the changed probe. Could be the same object, could be a new one depending on whether this Probe was already
	 * detached from the input ProbeList. Always use the returned value for further changes to this probe
	 */
	public Probe replaceValues(Probe pb, Probe newValues) {
		if (pb.getNumberOfExperiments()!=newValues.getNumberOfExperiments())
			throw new RuntimeException("Number of experiments must be equal in MaydayDataObject.replaceValues!");
		Probe new_ = makeUniqueProbe(pb);
		for (int i=0; i!=newValues.getNumberOfExperiments(); ++i) {
			new_.setValue(newValues.getValue(i),i);
		}
		return new_;
	}

	/** Safely replace all values of a Probe with new values.
	 * @param pb the Probe that will recieve the new values
	 * @param newValues a vector containing the new values
	 * @throws RuntimeException if the vector doesn't contain the right number of values
	 * @return the changed probe. Could be the same object, could be a new one depending on whether this Probe was already
	 * detached from the input ProbeList. Always use the returned value for further changes to this probe
	 */
	public Probe replaceValues(Probe pb, Vector<Double> newValues) {
		Probe newV = new Probe(pl.getDataSet().getMasterTable()); // temporary probe
		for (Double d : newValues) newV.addExperiment(d);
		return replaceValues(pb, newV);
	}

	/** Safely set an experiment value of a probes.
	 * @param pb The probe to change
	 * @param experiment The experiment to change
	 * @param value The new value for the experiment
	 * @return the changed probe. Could be the same object, could be a new one depending on whether this Probe was already
	 * detached from the input ProbeList. Always use the returned value for further changes to this probe
	 */
	public Probe setProbeValue(Probe pb, int experiment, double value) {
		Probe new_ = makeUniqueProbe(pb);
		new_.setValue(value, experiment);
		return new_;
	}

	private void checkedRemove(ProbeList pl, Probe pb) {
		/* another nice thing: While ProbeList.removeProbe doesn't care whether the probe actually is in the ProbeList,
		 * Probe.removeProbeList throws an Exception of the ProbeList isn't in the Probe. I don't need to check the exception
		 * because I called removeProbe to make sure that no connection between Probe and ProbeList remains. If this was
		 * already the case when calling the function, just as well. */
		/* 060501: This should not happen anymore. If it does, the filter programmer made a mistake, i.e. makeUniqueProbe is being
		 * called on a Probe that no longer is in this ProbeList. */
		try {
			pl.removeProbe(pb);
		} catch (Exception e) {
			throw new RuntimeException("This module does not work properly (most probable cause: " +
					"it's not using the return value of replaceValues or setProbeValue). Please contact the module developer." +
					"If you ran out of memory during this Mayday session, that could also be the reason for this problem.");
		};

	}

	/** Make a given probe in the wrapped ProbeList a unique Probe,
	 * i.e. detach this Probe from it's original so that changes do not affect the original data.
	 * This function will mostly be called internally by MaydayDataObject
	 * @param pb the Probe to make unique
	 * @return the newly created Probe
	 */
	public Probe makeUniqueProbe(Probe pb) { // makes a unique copy of an existing probe (if not already done before)
		makeUnique();
		Probe old = pb;						  // Situation here: old in pl  and  in pl_orig

		if (newlyCreatedProbes.contains(old)) // nothing left to do
			return old;

		checkedRemove(pl,old);										// Situation here: old in pl_orig
		Probe new_ = (Probe)old.clone(); 							// Situation here: old in pl_orig, new in pl_orig
		/* As with probelists, probes are equal by NAME during probelist.removeProve().
		 * ==> First change the clone's name, then remove it. Else we'll remove the original probe
		 */
		String Name = generateUniqueName(new_.getName());
		new_.setName(Name);
		// checkedRemove(pl_orig,new_); 								// Situation here: old in pl_orig, new in no list
		cloneMIOs(old, new_); //		must be called after the new probe has a unique name!
		pl.addProbe(new_);											// Situation here: old in pl_orig, new in pl
		newlyCreatedProbes.add(new_);
		return new_;
	}
	
	/** Add a probe to this Probelist without cloning it, i.e. add a link to the supplied probe
	 * @param pb the Probe to add
	 */
	public void addProbeWithoutCloning(Probe pb) {
		makeUnique();
		pl.addProbe(pb);
	}

	/** Returns the wrapped ProbeList. Only use this if you know what you're doing. Corrupting Maydays
	 * data structures is easy with direct access to ProbeLists.
	 * @return the ProbeList
	 */
	@Deprecated
	public ProbeList getProbeList() {return pl;}

	/** Makes this a unique object, i.e. makes sure that changes to the wrapped ProbeList (addition/removal of Probes)
	 * do not affect the original data.
	 * Normally, this method will be called internally by the MaydayDataObject
	 */
	private void makeUnique() {
		if (!isCopy) {
			pl = (ProbeList)pl.cloneProperly();
			/* Must change the name of the new ProbeList, or desaster will ensue (see comment for pl_name) */
			pl.setName(pl_name+"~"+temporaryModifier+'~');
			cloneMIOs(pl_orig, pl);
			isCopy = true;
		}
	}

	/** returns a static snapshot of the current Probelist. This is useful, e.g. when iterating over the probelist
	 * and adding new probes: The newly added probes will not be iterated over, deleting probes doesn't break the iteration
	 * @return a snapshot of the wrapped probelist
	 */
	public LinkedList<Probe> getProbelistSnapshot() {
		LinkedList<Probe> ret = new LinkedList<Probe>();
		for (Probe pb : pl.getAllProbes())
			ret.add(pb);
		return ret;
	}


	/** Remove all probes
	 */
	public void clear() {
		makeUnique();
		pl.clearProbes();
		performCleanup();
	}

	/** Set the annotation for the wrapped ProbeList and newly created Probes
	 * This String will be added to the existing annotation
	 * @param ann the String to add to all new probes and to the wrapped probelist's annotation
	 */
	public void setAnnotation(Map<String, String> ann) {
		annotation = (ann!=null)? ann : annotation;
	}
	
	public void addToAnnotation(Map<String, String> ann ) {
		if (annotation!=null && ann!=null)
			annotation.putAll(ann);
		else if (annotation==null)
			annotation = ann;
	}
		
	public void addToAnnotation(String key, String value) {
		if (annotation==null)
			annotation = new TreeMap<String,String>();
		annotation.put(key, value);
	}

	/** Set the name for the wrapped ProbeList
	 * This name will be used when the ProbeList is reintegrated into Mayday
	 * @param name
	 */
	public void setName(String name) {
		pl_name=name;
	}


	/** Returns the original name of the probelist associated with this object
	 * @return the original name
	 */
	public String getName() {
		return pl_orig.getName();
	}

	private void performCleanup() {
		// remove all _new_ probes from the mastertable
		MasterTable mt = pl.getDataSet().getMasterTable();
		for (Probe pb : newlyCreatedProbes) {
			for (MIGroup mg : pl.getDataSet().getMIManager().getGroupsForObject(pb))
				mg.remove(pb);
			mt.removeProbe(pb.getName());
		}
		newlyCreatedProbes.clear();
		if (isCopy)
			pl.setAnnotation(null);
	}


	/** Removes all probes contained in this ProbeList from the MasterTable etc.
	 * This function is called in the event of an Exception occurring during Pipeline execution
	 */
	public void dismiss() {
		// remove all probes so that the probes no longer link to this object
		// but only if we don't destroy user data!
		if (isCopy) {
			pl.clearProbes();
			for (MIGroup mg : pl.getDataSet().getMIManager().getGroupsForObject(pl))
				mg.remove(pl);			
		}
		performCleanup();
	}

/*	private String generateUniqueMIOGroupName(String prefix) {
		TreeSet<String> usedNames = new TreeSet<String>();
		// Collect all names
		for (MIGroup mg : pl.getDataSet().getMIManager().getGroups())
			usedNames.add(mg.getName());
			
		String name = prefix;
		int i=0;
		while (usedNames.contains(name))
			name = prefix+" ("+(++i)+")";

		return name;
	}*/

	private void renameProbe(Probe pb, String newname) {
		makeUnique();
		pl.removeProbe(pb);
		pb.setName(newname); // expected to be unique
		pl.addProbe(pb);
	}

	/** Reintegrates the wrapped ProbeList and all new probes into Mayday
	 * Also sets annotations
	 * @param mainMasterTable the MasterTable to reintegrate into
	 */
	public void reintegrateIntoMayday(ProgressMeter pm) {
		makeUnique(); //spätestens hier!
		// Annotierung und Namen erweitern
		pl.setName(pl_name+" ["+nameModifier + (slotName!="" ? ":"+slotName : "")  +"]");
		if (pl.getAnnotation().getQuickInfo().equals("")) {
			pl.getAnnotation().setQuickInfo(
					"Probelist created by processing pipeline: "
					+ nameModifier);
		} else {
			pl.getAnnotation().setQuickInfo(
					"Probelist created by processing pipeline: "
					+ nameModifier +"\n"
					+"Previous annotation follows. \n\n"
					+ pl.getAnnotation().getQuickInfo()
					);
		}
		
		// mio for annotation
		MIManager mim = pl.getDataSet().getMIManager();
		String annotationGroupID = "Processing Parameters";
		MIGroup annotationGroup;
		MIGroupSelection<MIType> mgs = mim.getGroupsForName(annotationGroupID);
		if (mgs.size()>0)
			annotationGroup = mgs.get(0);
		else 
			annotationGroup = mim.newGroup("PAS.MIO.StringMap", annotationGroupID);
		
		//annotate probelist and new probes with processing parameters
		StringMapMIO annotationMIO = new StringMapMIO(annotation);
		annotationGroup.add(pl, annotationMIO);

		if (pm!=null) pm.initializeStepper(newlyCreatedProbes.size());

		AnnotationMIO annotation = new AnnotationMIO();
		annotation.setQuickInfo("[Probe created by processing pipeline: "
				+ nameModifier + "]");
		
		for (Probe p : newlyCreatedProbes) {
			
			if (p.getAnnotation()!=null && !p.getAnnotation().getQuickInfo().equals("")) {
				p.getAnnotation().setQuickInfo("[Probe created by processing pipeline: "
						+ nameModifier + "]\n"
					+"Original annotation follows. \n\n"
					+ p.getAnnotation().getQuickInfo());
			} else {
				//p.setAnnotation(annotation);  //no need for this annotation any more
			}
		
			Probe checkPb = pl.getDataSet().getMasterTable().getProbe(p.getName());
			boolean checkPb_equals_p = false;
			
			if (checkPb != null) {
				/* The probe could already be in the mastertable for two reasons:
				 * (1) two MDOs in the same filter created a probe of that name  [names are equal, values aren't]
				 * (2) a MDO was duplicated and the probe is in both resulting MDO's newlycreatedprobes list [names&values equal]
				 */
				checkPb_equals_p = ProbesEqualByValues(checkPb,p);
				if (!checkPb_equals_p) {
					// this is case (1) ==> I have to rename my own probe to a unique name
					renameProbe(p, generateUniqueName( restoreNamePrefix( p.getName(), this.nameModifier ) ) );
					// more work is done below the "checkPb!=null"-block
				} else {
					// this is case (2) ==> I must not add the probe to the main mastertable AGAIN. 
					// Instead, I have to remove MY clone of that probe and add the other one to my pl
					pl.removeProbe(p);					
					pl.addProbe(checkPb);
				}
			}
			
			MasterTable mainMasterTable = this.pl_orig.getDataSet().getMasterTable();
			
			if (!checkPb_equals_p) {
				// my probe is not in the mastertable yet ==> I must add it and provide metadata 
				mainMasterTable.addProbe(p);				
				annotationGroup.add(p, annotationMIO);				
			}

			if (pm!=null) pm.stepStepper(1);
		}
		
		silencedContainers.unsilenceThis(pl);
		silencedContainers.unsilenceThis(pl.getDataSet().getMasterTable());
	}

	
	private boolean ProbesEqualByValues(Probe p1, Probe p2) {
		boolean they_are_equal = true;		
		int nex = p1.getNumberOfExperiments();
		for (int i=0; i!=nex && they_are_equal; ++i)
			they_are_equal &= (p1.getValue(i).equals(p2.getValue(i)));
		return they_are_equal;
	}
	
	public int getNumberOfExperiments() {
		return (size()==0 ? 0 : pl.getProbe(0).getNumberOfExperiments());
	}

	public Iterator<Probe> iterator() {
		// first make a static copy of the probelist, we want to make sure that iteration can continue when objects are removed or added
		return this.getProbelistSnapshot().iterator();
	}
	

	// Following code is to work aroung the problem that visualizers slow down MPF processing enormously. Probelist and MasterTable
	// are silenced before they are worked on. To make sure that they are unsilenced at the end, reference counting has to be done
	// on every silenced thing.
	
	private static class ProbeContainerMap {
		
		private class referenceItem {
			boolean hasBeenSilenced = true;
			int referenced = 1;
		}
		
		private HashMap<Object,referenceItem> silencedThings = new HashMap<Object,referenceItem>();
		
		public void silenceThis(Object theThing) {
			referenceItem myReference = silencedThings.get(theThing);
			if ( myReference == null ) { // check current status and add reference count to my list
				myReference = new referenceItem();
				if (theThing instanceof MasterTable) {
					myReference.hasBeenSilenced = !((MasterTable)theThing).isSilent();
					((MasterTable)theThing).setSilent(true);
				} else
				if (theThing instanceof ProbeList) {
					myReference.hasBeenSilenced = !((ProbeList)theThing).isSilent();
					((ProbeList)theThing).setSilent(true);
				}
				silencedThings.put(theThing, myReference);
			} else {
				myReference.referenced++;
			}
		}
		
		public void unsilenceThis(Object theThing) {
			referenceItem myReference = silencedThings.get(theThing);
			if (myReference != null) { // should never be null!
				myReference.referenced--;
				if (myReference.referenced==0) {
					if (myReference.hasBeenSilenced) { //maybe it was silent before the MPF worked on it
						if (theThing instanceof MasterTable) {
							((MasterTable)theThing).setSilent(false);
						} else
						if (theThing instanceof ProbeList) {
							((ProbeList)theThing).setSilent(false);
						}
					}
					silencedThings.remove(theThing);
				}
			}
		}
	}
	
	
	
	
}
