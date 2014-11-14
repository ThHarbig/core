package mayday.vis3.graph.layout;

public abstract class CanvasLayouterFactory
{
	public static final String GRID_LAYOUTER="Grid Layout";
	public static final String HORIZONTAL_LAYOUTER="Horizontal Layout";
	public static final String VERTICAL_LAYOUTER="Vertical Layout";
//	public static final String FR_LAYOUTER="Fruchterman-Reingold-Layout";
	public static final String FR_LAYOUTER="Force Based";
	public static final String CIRCULAR_LAYOUTER="Circular Layout";
//	public static final String SUGIYAMA_LAYOUTER="Sugiyama Layout";
	public static final String SUGIYAMA_LAYOUTER="Hierarchical Layout";
	public static final String RANDOM_LAYOUTER="Random Layout";
	public static final String SNAKE_LAYOUTER="Snake Layout";
	
	public static final String[] LAYOUTERS={
		GRID_LAYOUTER, 
		HORIZONTAL_LAYOUTER, 
		VERTICAL_LAYOUTER,
		FR_LAYOUTER,
		CIRCULAR_LAYOUTER,
		SUGIYAMA_LAYOUTER,
		SNAKE_LAYOUTER,
	};
	
	public static CanvasLayouter createLayouter(String layouter)
	{
		if(layouter.equals(GRID_LAYOUTER))
			return new GridLayouter();
		if(layouter.equals(HORIZONTAL_LAYOUTER))
			return new GridLayouter(GridLayouter.HORIZONTAL);
		if(layouter.equals(VERTICAL_LAYOUTER))
			return new GridLayouter(GridLayouter.VERTICAL);
		if(layouter.equals(FR_LAYOUTER))
			return new FruchtermanReingoldLayout();
		if(layouter.equals(CIRCULAR_LAYOUTER))
			return new SimpleCircularLayout();
		if(layouter.equals(SUGIYAMA_LAYOUTER))
			return new SugiyamaLayout();
		if(layouter.equals(RANDOM_LAYOUTER))
			return new RandomLayout();
		if(layouter.equals(SNAKE_LAYOUTER))
			return new SnakeLayout();
		throw new IllegalArgumentException(layouter+": no such layouter.");
	}
}
