package mayday.core.structures.graph.edges;

import mayday.vis3.graph.arrows.ArrowSettings;

public class Edges 
{
	public static class Roles
	{
		public static final String EDGE_ROLE="Edge";
		public static final String NO_ARROW_EDGE="undirected";
		public static final String REVERSE_EDGE="reverse";
		public static final String BIDIRECTIONAL_EDGE="bidirectional";
		
		public static final String INTERSECT_EDGE="Intersect";
		
		public static final String[] ROLES={EDGE_ROLE, NO_ARROW_EDGE, REVERSE_EDGE, BIDIRECTIONAL_EDGE, INTERSECT_EDGE};
	}
	
	public static ArrowSettings getArrowSettings(String role)
	{
		ArrowSettings s=new ArrowSettings();
		if(role.equals(Roles.EDGE_ROLE))
		{
			return s;
		}
		
		if(role.equals(Roles.NO_ARROW_EDGE) || role.equals(Roles.INTERSECT_EDGE))
		{
			s.setRenderTarget(false);
			return s;
		}
		if(role.equals(Roles.REVERSE_EDGE))
		{
			s.setRenderTarget(false);
			s.setRenderSource(false);
			return s;
		}
		if(role.equals(Roles.BIDIRECTIONAL_EDGE))
		{
			s.setRenderTarget(true);
			s.setRenderSource(true);
			return s;
		}
		return s;
	}



}
