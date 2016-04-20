package mayday.core.plugins.dataset.biomart;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.DatasetPlugin;
import mayday.core.tasks.AbstractTask;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A plugin that used the rest API of genenames.org to map gene symbols
 * to the current approved symbol. In addition, some meta-information
 * about the gene family and the entrez_id are loaded.
 *
 * Created by adrian on 4/18/16.
 */
public class GenenamesMapperPlugin extends AbstractPlugin implements DatasetPlugin {

    public void init() {
    }

    @SuppressWarnings("unchecked")
    public PluginInfo register() throws PluginManagerException {
        PluginInfo pli = new PluginInfo(
                (Class)this.getClass(),
                "PAS.core.biomart.GenenamesMapperPlugin",
                new String[0],
                Constants.MC_DATASET,
                new HashMap<String, Object>(),
                "Adrian Geissler",
                "adrian.geissler@uni-tuebingen.de",
                "Set the Probe display names from a MIO group.",
                "lookup IDs on genenames.org"
        );
        pli.addCategory("Probe Names");
        pli.setMenuName("map IDs with genenames.org");
        return pli;
    }

    /**
     * Selection of fields that are
     */
    public static final String[] FIELDS = new String[] {
            "hgnc_id",
            "symbol",
            "name",
            "status",
            "locus_type",
            "alias_symbol",
            "location",
            "entrez_id",
            "refseq_accession",
            "ensemble_gene_id",
            "uniprot_ids",
    };

    /**
     * Default value to fill in for missing entries.
     */
    public static final String MISSING = "N/A";

    @Override
    public List<DataSet> run(final List<DataSet> datasets) {
        AbstractTask job = new AbstractTask("genenames.org") {
            @Override
            protected void initialize() {         }
            @Override
            protected void doWork() throws Exception {
                // run plugin on each dataset individually
                for(DataSet ds : datasets) {
                    // create MI group to store results in
                    MIManager mi = ds.getMIManager();
                    MIGroup mig = mi.newGroup("PAS.MIO.String", "genenames.org");
                    // Create MIGroups per field
                    HashMap<String, MIGroup> groups = new HashMap<>();
                    for (String f : FIELDS) {
                        groups.put(f, mi.newGroup("PAS.MIO.String", f, mig));
                    }
                    // for each probe
                    ProbeList global = ds.getMasterTable().getGlobalProbeList();
                    for (Probe p : global.getAllProbes()){
                        // lookup genenames.org entry
                        String hgnc = search(p.getDisplayName());
                        JSONObject entry = hgnc != null ? fetch(hgnc) : null;
                        // fill fields
                        for (String f : FIELDS) {
                            StringMIO mio = new StringMIO(stringify(entry, f));
                            groups.get(f).add(p, mio);
                        }
                    }

                }
            }
        };
        // Let lookup run in background
        job.start();
        return null;
    }

    /**
     * Return the value in j for the corresponding key as a String, or if
     * j==null the missing value.
     * @param j
     * @param key
     * @return
     */
    public static String stringify(JSONObject j, String key) {
        if (j == null) {
            return MISSING;
        }
        if (!j.has(key)) {
            return MISSING;
        }
        Object v = j.get(key);
        if (v instanceof String) {
            return (String) v;
        } else {
            return v.toString();
        }
    }

    /**
     * Request http://rest.genenames.org/search/'q' for unique
     * hgnc_id.
     * @param q
     * @return hgnc_id
     */
    public static  String search(String q) {
        // http request
        JSONObject jz = httpRequest("http://rest.genenames.org/search/" + q);
        // were there any results?
        int numFound = jz.getJSONObject("response").getInt("numFound");
        if (numFound == 0) {
            return null;
        }
        // find entry with highest score
        double maxScore = jz.getJSONObject("response").getDouble("maxScore");
        JSONArray ja = jz.getJSONObject("response").getJSONArray("docs");
        final double EPS = 10e-7;
        for(Object o : ja) {
            JSONObject jo = (JSONObject) o;
            if (Math.abs(jo.getDouble("score") - maxScore) < EPS) {
                // found max entry
                return jo.getString("hgnc_id");
            }
        }
        throw new RuntimeException("This code should never be reached");
    }

    /**
     * Fetch JSONObject for uniq hgnc_id
     * @param hgnc_id
     * @return
     */
    public static JSONObject fetch(String hgnc_id) {
        JSONObject jz = httpRequest("http://rest.genenames.org/fetch/hgnc_id/" + hgnc_id);
        // assert id was unique
        assert jz.getJSONObject("response").getInt("numFound") == 1;
        // return that only entry
        return jz.getJSONObject("response").getJSONArray("docs").getJSONObject(0);
    }


    /**
     * Helper Method for querying the specified url and parsing the response
     * as a Json object.
     * @param url
     * @return
     */
    public static JSONObject httpRequest(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            // set genenames.org specific header
            con.setRequestProperty("Accept", "application/json");
            con.setDoInput(true);
            if (con.getResponseCode() != 200) {
                System.err.println("[HTTP/" + con.getResponseCode() + "] " + url);
                throw new RuntimeException("Can't connect with genesnames.org");
            }
            // read response
            BufferedReader buffin = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            StringBuilder txt = new StringBuilder();
            String line;
            while ((line = buffin.readLine()) != null) {
                txt.append(line);
            }
            return new JSONObject(txt.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't connect with genesnames.org");
        }
    }


    public static void main(String[] args) {
        // Some dynamic tests for this class and its methods.
        // The genenames are possibly (however unlikely) subjected to change
        // -> double check before complaining

        // <str name="symbol">ZNF3</str>
        assert search("ZNF3").equals("HGNC:13089");
        assert fetch("HGNC:13089").getString("name").equals("zinc finder protein 3");
        //alias_symbol":["A8-51","KOX25","PP838","FLJ20216","HF.12","Zfp113"]
        assert stringify(fetch("HGNC:13089"), "alias_symbol").equals(
                "A8-51, KOX25, PP838, FLJ20216, HF.12, Zfp113"
        );

    }
}
