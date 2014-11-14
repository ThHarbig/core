package mayday.core.io.dataset.geosoft;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.io.EfficientBufferedReader;
import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.tasks.AbstractTask;

public class GeoSoft {

	protected DataSet ds;
	protected TreeMap<String, MIGroup> globalAnnotation = new TreeMap<String, MIGroup>();
	protected TreeMap<String, MIGroup> sampleAnnotation = new TreeMap<String, MIGroup>();
	protected TreeMap<String, MIGroup> probeAnnotation = new TreeMap<String, MIGroup>();
	protected ParsedLine plKeyVal, plTab;
	protected String usedPlatform;
	protected final static Pattern geo_next_element = Pattern.compile("//");
	protected final static Pattern geo_next_record = Pattern.compile("///");
	
	protected AbstractTask task;
	
	public DataSet parseFile(String filename, AbstractTask _task) throws IOException {

		task = _task;
		
		EfficientBufferedReader ebr = new EfficientBufferedReader(new FileReader(filename));
		plTab = new ParsedLine("", new ParserSettings());
		ParserSettings psett = new ParserSettings();
		psett.separator="[\\s]]*=[\\s]*";
		plKeyVal = new ParsedLine("", psett);
		
		ds = new DataSet(filename);
		
		String line;
		
		while (ebr.ready()) {
			line=ebr.readLine();
			plKeyVal.replaceLine(line);
			String type = plKeyVal.get(0).trim();
			if (type.equals("^DATABASE")) {
				parseDataBase(ebr, plKeyVal.get(1).trim());
			} else if (type.equals("^SERIES")) { // only one  
				parseSeries(ebr, plKeyVal.get(1).trim());
				// let the user select the platform they want
				selectPlatform();
			} else if (type.equals("^PLATFORM")) { // can be several
				if (usedPlatform==null)
					throw new RuntimeException("SERIES section missing");
				// check if this is what i want
				String platform = plKeyVal.get(1).trim();
				if (platform.equals(usedPlatform))
					parsePlatform(ebr, platform);
				else
					skipSection(ebr);
			} else if (line.startsWith("^SAMPLE")) {
				if (usedPlatform==null)
					throw new RuntimeException("PLATFORM section missing");				
				parseExperiment(ebr, plKeyVal.get(1).trim());
			} else if (line.startsWith("^DATASET")) {
				log("\nThis appears to be a GEO Soft __DataSet__ file. \n" +
						"Currently, only GEO SOFT __Series__ files are supported.\n\n");
				throw new RuntimeException(
						"This appears to be a GEO Soft __DataSet__ file. \n" +
						"Currently, only GEO SOFT __Series__ files are supported.");
			}
			else {
				log("Unexpected SOFT section: "+line);
				skipSection(ebr);
			}
		}
		
		return ds;
	}
	
	protected void log(String s) {
		if (task==null)
			System.out.println(s);
		else
			task.writeLog(s);
	}
	
	protected void selectPlatform() {
		MIGroup platform = globalAnnotation.get("Series_platform_id");
		StringMIO sm = (StringMIO)platform.getMIO(ds);
		String[] platforms = sm.getValue().split("; ");
		if (platforms.length>1) {
			RestrictedStringSetting rss = new RestrictedStringSetting("Platform", 
					"Multi-Platform SOFT import is not supported.\nPlease select which platform data to import",
					0, platforms);
			SettingDialog sd = new SettingDialog(null, "GEO SOFT import: Platform selection", rss);
			sd.showAsInputDialog();
			if (!sd.closedWithOK()) 
				throw new RuntimeException("User cancelled import");
			usedPlatform = rss.getObjectValue();
		} else {
			usedPlatform = platforms[0];
		}
	}
	
	protected void parseDataBase(BufferedReader br, String name) throws IOException {
		parseSection(br, "DataBase", name, '^');
	}
	
	protected void parseSeries(BufferedReader br, String name) throws IOException {
		parseSection(br, "Series", name, '^');
	}
	
	protected void parsePlatform(BufferedReader br, String name) throws IOException {
		addMeta("Platform", name, ds, "/GEO", globalAnnotation);
		parseSection(br, "Platform", name, '#');
		String line = "";		
		// first parse annotation columns
		ArrayList<MIGroup> columns = new ArrayList<MIGroup>();
		while (br.ready()) {
			line = br.readLine().trim();
			if (!line.startsWith("#"))
				break;
			plKeyVal.replaceLine(line.substring(1));
			String meta_id = plKeyVal.get(0).trim();
			String meta_annot = plKeyVal.size()>1?geosplit(plKeyVal.get(1).trim()):null;
			MIGroup theNewGroup = addMeta(meta_id, null, null, "/GEO/Sample", sampleAnnotation);
			// annotate the migroup with the extra info
			if (meta_annot!=null) {
				AnnotationMIO am = (AnnotationMIO)ds.getMIManager().getGroupsForType("PAS.MIO.Annotation").get(0).add(theNewGroup);
				am.setInfo(meta_annot);
				am.setQuickInfo("Annotation imported from GEO SOFT format");
			}
			columns.add(theNewGroup);
		}
		//now parse the probe annotations
		if (!line.equals("!platform_table_begin"))
				throw new RuntimeException("Expecting platform_table_begin element, but found: "+line);
		while (br.ready()) {
			line = br.readLine().trim();
			if (line.equals("!platform_table_end"))
				return;
			plTab.replaceLine(line);
			String probeID = plTab.get(0).trim();
			if (probeID.equals("ID"))
				continue;
			Probe pb = new Probe(ds.getMasterTable());
			pb.setName(probeID);
			ds.getMasterTable().addProbe(pb);
			for (int column=1; column!=plTab.size(); ++column) {
				MIGroup mg = columns.get(column);
				String colval = plTab.get(column);
				if (colval!=null) {
					String value = geosplit(colval.trim());
					StringMIO sm = (StringMIO)mg.add(pb);
					sm.setValue(value);
				}
			}
		}
	}
	
	
	protected void parseExperiment(BufferedReader br, String name) throws IOException {
		String line=null;
		char peek;
		
		Experiment ex = new Experiment(ds.getMasterTable(), name );
		
		do {
			br.mark(1);
			peek = (char)br.read();
			br.reset();
			if (peek=='^')
				break; // done with this sample
			if (peek=='!') {
				//meta data
				line = br.readLine().substring(1).trim(); // discard "!"
				if (line.equals("sample_table_end"))
					break;
				plKeyVal.replaceLine(line);
				String meta_id = plKeyVal.get(0).trim();
				String content = plKeyVal.size()>1?plKeyVal.get(1).trim():"";
				/* Oh thank you very much, we can only check the sample platform here and
				 * have to rollback any changes if the platform doesn't fit 
				 */
				if (meta_id.equals("Sample_platform_id") && !(content.equals(usedPlatform))) {
					log("Sample "+name+" based on platform "+content+" is skipped");
					skipSection(br);
					for (MIGroup mg : sampleAnnotation.values())
						mg.remove(ex);
					return;
				}
				if (!meta_id.equals("sample_table_begin"))
					addMeta(meta_id, content, ex, "/GEO/Sample", sampleAnnotation);
			} else if (peek!='#'){ // experimental data
				line = br.readLine().trim();
				plTab.replaceLine(line);
				String probe_id = plTab.get(0).trim();
				Probe pb = ds.getMasterTable().getProbe(probe_id);
				if (pb!=null) {
					String value = plTab.size()>1?plTab.get(1).trim():null;
					Double dvalue = value==null?null:Double.parseDouble(value);
					pb.addExperiment(dvalue);
				}
			} else {
				br.readLine();
			}
		} while (br.ready());
			
		ds.getMasterTable().addExperiment(ex);
	}
	
	protected String geosplit(String in) {
		if (!in.contains("//"))
			return in;
		in = geo_next_record.matcher(in).replaceAll("\n");
		in = geo_next_element.matcher(in).replaceAll("\t");
		return in;
	}
	
	
	protected void parseSection(BufferedReader br, String Section, String name, char stop) throws IOException {
		MIGroup mg = addMeta(Section, name, ds, "/GEO", globalAnnotation);
		String path = mg.getPath()+"/"+mg.getName();
		
		String line=null;
		char peek;
		
		while (br.ready()) {
			line = br.readLine();
			do {
				br.mark(1);
				peek = (char)br.read();
				br.reset();
				if (peek==stop)
					return; // done with this part
				if (peek=='!') {
					//meta data
					line = br.readLine().substring(1).trim(); // discard "!"
					plKeyVal.replaceLine(line);
					String meta_id = plKeyVal.get(0).trim();
					String content = plKeyVal.size()>1?plKeyVal.get(1).trim():"";
					addMeta(meta_id, content, ds, path, globalAnnotation);
				}				
			} while (br.ready());
		}
	}
	
	protected void skipSection(BufferedReader br) throws IOException {
		char peek;
		do {
			br.mark(1);
			peek = (char)br.read();
			br.reset();
			if (peek=='^')
				return; // done with this part
			br.readLine();
		} while (br.ready());
	}
	
	protected MIGroup addMeta(String meta_id, String content, Object annotatedObject, String mioPath, Map<String,MIGroup> cache) {
		MIGroup mg = cache.get(meta_id);				
		if (mg==null)
			cache.put( meta_id, mg = ds.getMIManager().newGroup("PAS.MIO.String", meta_id, mioPath ) );
		// check if this mg already contains data, and append if so
		if (annotatedObject==null)
			return mg;
		StringMIO sm = (StringMIO)mg.getMIO(annotatedObject);
		if (sm==null)
			sm = (StringMIO)mg.add(annotatedObject);
		String mioval = sm.getValue();
		if (mioval==null)
			mioval = "";
		else 
			mioval +="; ";
		mioval = mioval+content;
		sm.setValue(mioval);
		return mg;
	}


		
	
}
