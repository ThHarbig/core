package mayday.core.structures.graph.nodes;

public class Nodes 
{
	public static class Roles
	{
		public static final String NODE_ROLE="Node";
		
		public static final String PROBE_ROLE="Probe";
		public static final String PROBES_ROLE="Probes";
		public static final String PROBELIST_ROLE="Probelist";
		public static final String DATASET_ROLE="Dataset";
		public static final String MIO_ROLE="MIO";
		public static final String EXPERIMENT_ROLE="Experiment";
		
		public static final String NOTE_ROLE="Note";
		
		public static final String OTHER_ROLE="Other";
		
		public static final String[] ROLES={NODE_ROLE,PROBE_ROLE,PROBES_ROLE,PROBELIST_ROLE,
			DATASET_ROLE,MIO_ROLE,EXPERIMENT_ROLE,NOTE_ROLE,OTHER_ROLE};

		public static boolean isDefaultRole(String role)
		{
			for(int i=0; i!=  ROLES.length; ++i)
			{
				if(ROLES[i].equals(role)) return true;
			}
			return false;
		}
	}
	public static final String NOTE_FONT_NAME="Font Name";
	public static final String NOTE_FONT_STYLE="Font Style";
	public static final String NOTE_FONT_SIZE="Font Size";
	public static final String NOTE_BACKGROUND_COLOR="Background";
	public static final String NOTE_TEXT_COLOR="Text color";
	public static final String NOTE_TEXT="Text";
	
	public static final String URL_KEY="url";
}
