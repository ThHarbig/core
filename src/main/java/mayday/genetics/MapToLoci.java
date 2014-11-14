/*
 * Created on Jan 24, 2005
 *
 */
package mayday.genetics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import mayday.core.Probe;
import mayday.core.gui.PreferencePane;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.plugins.AbstractMetaInfoPlugin;
import mayday.core.meta.plugins.MetaInfoPlugin;
import mayday.core.meta.types.IntegerMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.tasks.AbstractTask;
import mayday.genetics.advanced.StrandFilterIterator;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate.DistanceAnchor;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapSetting;

public class MapToLoci
extends AbstractMetaInfoPlugin
{

	protected LocusMapSetting targetSet;
	protected IntSetting maxUpstream, maxDownstream;
	protected RestrictedStringSetting referenceAnchor, candidateAnchor;
	protected BooleanSetting strandSpecific;
	protected HierarchicalSetting setting;
	
	protected final static String DIST_UPSTREAM="upstream coordinate (e.g. gene start)";
	protected final static String DIST_DOWNSTREAM="downstream coordinate (e.g. gene end)";
	protected final static String DIST_CENTER="middle coordinate (average of start and end)";
	protected final static String DIST_CLOSEST="closest coordinate (gene distance)";

	public Setting getSetting() {
		if (setting == null) {
			targetSet = new LocusMapSetting();
			
			referenceAnchor = new RestrictedStringSetting("Compute distance relative to the reference's",
					"Loci from the selected target locus map are \"reference\" loci.",
				2, new String[]{DIST_UPSTREAM, DIST_DOWNSTREAM, DIST_CENTER, DIST_CLOSEST}
			);
			referenceAnchor.setLayoutStyle(LayoutStyle.RADIOBUTTONS);
			
			candidateAnchor = new RestrictedStringSetting("Compute distance relative to the candidate's",
					"Loci from the selected MI Group are \"candidate\" loci.",
					2, new String[]{DIST_UPSTREAM, DIST_DOWNSTREAM, DIST_CENTER, DIST_CLOSEST}
				);
			candidateAnchor.setLayoutStyle(LayoutStyle.RADIOBUTTONS);
			
			HierarchicalSetting maxdist = new HierarchicalSetting("Maximal distances")
			.addSetting(
					maxUpstream = new IntSetting("Maximal distance to upstream reference",null, 1500, 0, null, true, false)
			).addSetting(
					maxDownstream = new IntSetting("Maximal distance to downstream reference",null, 1500, 0, null, true, false)				
			);
			
			strandSpecific = new BooleanSetting("Strand specific search", null, true);
			
			setting = new HierarchicalSetting("Map to nearest locus")
			.addSetting(targetSet)
			.addSetting(referenceAnchor)
			.addSetting(candidateAnchor)
			.addSetting(maxdist)
			.addSetting(strandSpecific);
		}
		return setting;
	}
	
	public PreferencePane getPreferencePane() {
		return null;
	}

	public void init() {
		pli.getProperties().put(MetaInfoPlugin.MULTISELECT_HANDLING, MetaInfoPlugin.MULTISELECT_HANDLE_INTERNAL);
		registerAcceptableClass(LocusMIO.class);
	}

	protected long distance(AbstractGeneticCoordinate ref, AbstractGeneticCoordinate candidate) {
//		if (candidate==null)
//			System.out.println("WTF");
		if (candidate==null || ref==null)
			return Long.MAX_VALUE;
		boolean fwd = candidate.getStrand().similar(Strand.PLUS);
	
		DistanceAnchor upstream_coordinate_in_candidate_view = fwd ? DistanceAnchor.FROM : DistanceAnchor.TO; 
		DistanceAnchor downstream_coordinate_in_candidate_view = fwd ? DistanceAnchor.TO : DistanceAnchor.FROM;
			
		DistanceAnchor ref_anchor = DistanceAnchor.CLOSEST;
		switch (referenceAnchor.getSelectedIndex()) {
		case 0: ref_anchor = upstream_coordinate_in_candidate_view; break;
		case 1:	ref_anchor = downstream_coordinate_in_candidate_view; break;
		case 2:	ref_anchor = DistanceAnchor.CENTER; break;
		}

		DistanceAnchor cand_anchor = DistanceAnchor.CLOSEST;
		switch (candidateAnchor.getSelectedIndex()) {
		case 0: cand_anchor = upstream_coordinate_in_candidate_view; break;
		case 1: cand_anchor = downstream_coordinate_in_candidate_view; break;
		case 2: cand_anchor = DistanceAnchor.CENTER; break;
		}
		
		long ref_pos = ref.getAnchor(ref_anchor);
		long cand_pos = candidate.getAnchor(cand_anchor);

		// if cand=closest, we compute overlap-distance to ref_pos
		// if ref=closest, we compute overlap-distance to cand_pos
		// if both=closest, we compute overlap distance between loci
		if (ref_anchor == DistanceAnchor.CLOSEST && cand_anchor == DistanceAnchor.CLOSEST) {
			return candidate.getDistanceTo(ref, true);
		} else if (ref_anchor == DistanceAnchor.CLOSEST) {
			return ref.getDistanceTo(cand_pos, ref_anchor);
		} else if (cand_anchor == DistanceAnchor.CLOSEST) {
			long rawDist = candidate.getDistanceTo(ref_pos, cand_anchor);
			// now if ref is on the same strand as cand, we have to take the negative distance, because 
			// the upstream and downstream roles are reversed
			if (candidate.getStrand()==ref.getStrand())
				rawDist *= -1;
			// if, however, ref is on the opposite strand, then ref's notion of "upstream" will be cand's 
			// "downstream" and all is well.
			return rawDist;
		}
		
		// distance must be negative if cand is upstream of ref, else positive
		long rawDist = (cand_pos - ref_pos);
		// and this is exactly the other way round on the backward strand
		if (!fwd)
			rawDist *= -1;
		return rawDist;
	}
		
	public void run(final MIGroupSelection<MIType> selection, final MIManager miManager) {
		getSetting();
		SettingDialog sdl = new SettingDialog(null, "Map to nearest loci", setting);
		sdl.showAsInputDialog();
		if (!sdl.canceled()) {
			AbstractTask at = new AbstractTask("Mapping loci") {
				
				@SuppressWarnings("unchecked")
				protected void doWork() throws Exception {
					LocusMap map = targetSet.getLocusMap();
					
					ChromosomeSetContainer tcsc = map.asChromosomeSetContainer();
					
					for (MIGroup mg : selection) {
						
						// three children are added: distance, overlap and name
						String path = mg.getPath()+"/"+mg.getName()+"/Mapped to "+map.getName().replace("/", "|");
						MIGroup distance = miManager.newGroup("PAS.MIO.Integer", "Distance", path);
						MIGroup overlap = miManager.newGroup("PAS.MIO.Integer", "Overlap", path);
						MIGroup closest = miManager.newGroup("PAS.MIO.String", "Nearest", path);
						
						LocusMap sourcemap = new LocusMap(mg);
						ChromosomeSetContainer scsc = sourcemap.asChromosomeSetContainer();
						
						for (Chromosome sc : scsc.getAllChromosomes()) {
							LocusChromosomeObject<String> tc = 
								(LocusChromosomeObject<String>)tcsc.getChromosome(sc);
							
							for (Strand s : new Strand[]{Strand.PLUS, Strand.MINUS}) {

								Iterator<LocusGeneticCoordinateObject<String>> titer = tc.iterateByStartPosition(true);
								Iterator<LocusGeneticCoordinateObject<String>> siter = 
									((LocusChromosomeObject<String>)sc).iterateByStartPosition(true);

								siter = new StrandFilterIterator<LocusGeneticCoordinateObject<String>>(siter, s);

								if (strandSpecific.getBooleanValue()) {
									titer = new StrandFilterIterator<LocusGeneticCoordinateObject<String>>(titer, s);
								}

								map(distance, overlap, closest, siter, titer);
								
							} // strand
							
						} // chrome
						
						// now replace strings by actual probes
						for (Entry<Object,MIType> e : new HashSet<Entry<Object, MIType>>(distance.getMIOs())) {
							Object key = e.getKey();
							MIType val1 = e.getValue();							
							if (key instanceof String) {
								distance.remove(key);
								MIType val2 = closest.getMIO(key);
								MIType val3 = overlap.getMIO(key);
								closest.remove(key);
								overlap.remove(key);
								Probe pb = miManager.getDataSet().getMasterTable().getProbe((String)key);
								if (pb!=null) {
									distance.add(pb, val1);
									closest.add(pb, val2);
									overlap.add(pb, val3);
								}
							}
						}
						
					} // migroup
				}

				protected void initialize() {
				}

			};
			at.start();
		}

	}
	
	protected void map(
			MIGroup distance, MIGroup overlap, MIGroup closest,
			Iterator<LocusGeneticCoordinateObject<String>> siter,
			Iterator<LocusGeneticCoordinateObject<String>> titer
			) {
		
		// I always need to keep three reference loci in memory. if the current candidate
		// is closer to the third than to the second reference locus, drop the first, shift the others and get a new third
		
		LocusGeneticCoordinateObject<String> left = null;
		LocusGeneticCoordinateObject<String> middle = null;
		LocusGeneticCoordinateObject<String> right = null;
		
		int maxUp = maxUpstream.getIntValue();
		int maxDown = maxDownstream.getIntValue();
		boolean strand = strandSpecific.getBooleanValue();
		
		while(siter.hasNext()) {
			LocusGeneticCoordinateObject<String> candidate = siter.next();			
			// contract: RIGHT is always downstream of candidate
			if (right!=null && candidate.isDownstreamOf(right)) {
				right = null;
			}
			
			// fill the neighbors on the reference
			while (right==null && titer.hasNext()) {
				LocusGeneticCoordinateObject<String> tmp = titer.next();
				if (strand && !(tmp.getStrand().similar(candidate.getStrand())))
					tmp = null;
				if (tmp!=null && candidate.isDownstreamOf(tmp))
					tmp = null;
				right = tmp;
				if (left==null && right!=null) { // first filling situation
					left=middle;
					middle = right;
					right = null;
				}
			}
			
			// compute distances to all neighbors, depending on distance settings, treat NULL correctly. All distances are ABSOLUTE
			long dleft = Math.abs(distance( left, candidate));
			long dmiddle = Math.abs(distance( middle, candidate ));
			long dright = Math.abs(distance( right, candidate ));

			LocusGeneticCoordinateObject<String> partner;
			long dpartner;
			
			// select partner by overlap if two distance are equally small
			long minDist = Math.min( dleft, Math.min (dright, dmiddle));
			boolean chooseLeft = dleft==minDist;
			boolean chooseMiddle = dmiddle==minDist;
			boolean chooseRight = dright==minDist;
			if ((chooseLeft && (chooseMiddle || chooseRight)) || (chooseMiddle && chooseRight))  {
				// select partner by overlap if two distance are equally small
				long overLeft = safeOverlap(candidate, left);
				long overMiddle = safeOverlap(candidate, middle);
				long overRight = safeOverlap(candidate, right);
				long maxOver = Math.max(overLeft, Math.max(overRight, overMiddle));
				chooseLeft = overLeft==maxOver;
				chooseMiddle = overMiddle==maxOver;
				chooseRight = overRight==maxOver;
				// if still not resolved, we always pick "middle", then "left", then "right"
			}
			
			if (chooseMiddle) {
				dpartner = dmiddle;
				partner = middle;
			} else if (chooseLeft) {
				dpartner = dleft;
				partner = left;
			} else {
				dpartner = dright;
				partner = right;
			}

//			if (left!=null) System.out.print(left.getFrom());
//			if (middle!=null) System.out.print("\t"+middle.getFrom());
//			if (right!=null) System.out.print("\t"+right.getFrom());
//			System.out.println("\t\t"+candidate.getFrom());
			
			boolean isOK = true;
			
			if (partner==null)
				return;
			
			// check minima, maxima correctly for upstream/downstream. Must use candidate's coordinate system as reference!
			if (candidate.isDownstreamOf(partner)) { 
				if (dpartner>maxUp)		
					isOK = false;
			} else {
				if (dpartner>maxDown) {
					isOK = false;
				}
			}
			
			if (isOK) {
				// compute final distance values, these can now also be negative
				long realDistance = distance(partner, candidate);
				long realOverlap = safeOverlap(candidate, partner); 

				Object o = candidate.getObject();
				((IntegerMIO)distance.add(o)).setValue((int)realDistance);
				((IntegerMIO)overlap.add(o)).setValue((int)realOverlap);
				((StringMIO)closest.add(o)).setValue(partner.getObject());				
			}
			
			// if right is closer than middle, we can discard left
			if (chooseRight) {
				left = middle;
				middle = right;
				right = null;
			}
			
		}
		
	}
	
	protected long safeOverlap(AbstractGeneticCoordinate candidate, AbstractGeneticCoordinate partnerOrNull) {
		if (partnerOrNull==null)
			return -1;
		return partnerOrNull.getOverlappingBaseCount(candidate.getFrom(), candidate.getTo()); //MUST NOT be strand specific!
	}
	
	public PluginInfo register() throws PluginManagerException {
		pli= new PluginInfo(
				this.getClass(),
				"PAS.mio.maploci",
				new String[0], 
				Constants.MC_METAINFO_PROCESS,
				(HashMap<String,Object>)null,
				"Florian Battke",
				"battke@informatik.uni-tuebingen.de",
				"Map locus values to the nearest locus of another set of loci",
		"Map to nearest loci in other set");
		pli.addCategory("Locus Data");
		return pli;
	}

}
