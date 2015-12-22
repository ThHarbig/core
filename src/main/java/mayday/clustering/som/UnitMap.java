/*
 * File UnitMap.java
 * Created on 09.02.2004
 * As part of package clustering.SOM
 * By Janko Dietzsch
 *
 */
package mayday.clustering.som;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.structures.linalg.vector.AbstractVector;


class UnitCoordinates {
	public int x;
	public int y;	
	
	public UnitCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
}


/**
 * This class implements the layer of SOM-neurons.
 * 
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 * 
 */
public class UnitMap {
		
	/**
	 * @serial row dimension
	 * @serial column dimension
	 */
	private int rows,cols;	
	
	/**
	 * @serial dimension of the weight vector
	 */
	private int dim;
	
	/**
	 * Topology type of the used grid
	 * 
	 * @serial save topology of the used grid
	 */	
	private GridTopology TopologyType;
    
	/**
	 * @serial internal structure for the map
	 */
	private double [] [] [] Weights;
    
    /**
     * @serial Decision field for the use of caching mechanisms.
     */
    private boolean useCaching;
    
    /**
     * @serial Internal cache of the neighborhood method
     */
	private List<List<Integer>>[] cachedNeighborhood;
    
	/* ----------------
		Constructor
	   ----------------*/
	   
	/**
	 * @param rows of the grid
	 * @param columns of the grid
	 * @param dimension of the data and weight vectors
	 * @param topology type of the grid
	 */
	public UnitMap(int Rows, int Columns, int Dimension, GridTopology GridTop) {
     this(Rows, Columns, Dimension, GridTop,true);   
    }
    
    
    /**
     * @param rows of the grid
     * @param columns of the grid
     * @param dimension of the data and weight vectors
     * @param topology type of the grid
     * @param should caching mechanisms be used
     */
    public UnitMap(int Rows, int Columns, int Dimension, GridTopology GridTop, boolean useCaching) {
		// Check range of Rows and Columns ---> Rows * Columns must fit in the range of Integer !!!!!!!!
		this.rows=Rows;
		this.cols=Columns;
		this.dim=Dimension;
		this.TopologyType=GridTop;
        this.useCaching = useCaching;
		this.Weights=new double[Rows][Columns][Dimension];
	}

/*
	public Matrix (double[][] Field) {
		 rows = Field.length;
		 cols = Field[0].length;
		 for (int k = 0; k < rows ; k++) {
			if (Field[k].length != cols) {
			   throw new IllegalArgumentException("The rows have differetn lengths.");
			}
		 }
		 this.M = Field;
	}
*/
	
	public double[] getWeightsOfUnit(int row,int col) {
		return this.Weights[row][col];
	}
	
	public double[] getWeightsOfUnit(int UnitID) {
		return this.getWeightsOfUnit(this.getRowOfUnitID(UnitID), this.getColOfUnitID(UnitID));
	}
	
	public void setWeightsOfUnit(double[] Weights, int row, int col) {
		if (Weights.length != this.dim) {
					   throw new IllegalArgumentException("The weight vectors have different dimensions.");
		};
		for (int i = 0; i < this.dim; i++)	this.Weights[row][col][i]=Weights[i];
	}
	
	public void setWeightsOfUnit(double[] Weights, int UnitID) {
		this.setWeightsOfUnit(Weights, this.getRowOfUnitID(UnitID), this.getColOfUnitID(UnitID));	
	}
	
	public int getCols() {
		return this.cols;
	}
	
	public int getRows() {
		return this.rows;
	}

	public int getDimensonOfWeights() {
		return this.dim;
	}

	public int getIDofUnit(int x, int y) {
		return  x*this.cols+y;
	}
	
	public int getAmountOfUnits() {
		return (this.cols*this.rows);
	}
	
	public int getRowOfUnitID(int UnitID) {
		return (int) UnitID / this.cols;
	}
	
	public int getColOfUnitID(int UnitID) {
		return (int) UnitID % this.cols;
	}
    
    public void useCaching() {
        this.useCaching = true;
    }
    
    public void noCaching() {
        this.useCaching = false;
        this.cachedNeighborhood = null;
    }
    
    public int[] getSequenceOfUnitIDs() {
        int maxUnitID = this.getAmountOfUnits();
        int[] UnitIDs = new int[maxUnitID];
        for (int i = 0; i < maxUnitID; i++) UnitIDs[i] = i;
        return UnitIDs;
    }

	public int getBestMatchingUnit(AbstractVector DataVec, DistanceMeasurePlugin DistanceObj) {
		int BMU_x = 0;
		int BMU_y = 0;
		double MinDistance = Double.MAX_VALUE;
		double distance = 0.0;
		for (int x = 0; x < this.rows; x++ )
			for (int y = 0; y < this.cols; y++) {
				distance = DistanceObj.getDistance(DataVec,this.Weights[x][y]);
				if (MinDistance > distance) {
					MinDistance = distance;
					BMU_x = x; BMU_y = y; 
				};
			};
		return this.getIDofUnit(BMU_x, BMU_y);
	}
	
//	public int getMaximalDistanceInMapForUnit(int x, int y) {
//		switch (this.TopologyType) {
//			case 0: // maximal distance in a rectangular grid
//						int MaxDistance1=  ( x > ( (this.rows - 1) - x) ) ?  x : ((this.rows - 1) - x);
//						int MaxDistance2=  ( y > ( (this.cols - 1) - y) ) ? y : ( (this.cols - 1) - y);
//						return ( MaxDistance1 + MaxDistance2 ); 					
//						
////			case 1: // maximal distance in a hexagonal grid
////						
////						return ;
////						break;
//
//			default: return 0;
//		}
//	}
//
//
//	private boolean OutsideMap(int x, int y) {
//		boolean Out = false;
//		if ( ( x <  0) || ( x > this.rows ) ) Out = true;
//		if ( ( y < 0 ) || ( y > this.cols ) ) Out = true;
//		return Out;
//	}
//	
//	private List getHexagonalNeighborsInDistance(int x, int y, int dist) {
//		List ListOfNeighbors= new LinkedList();
//		if ( dist != 0) {
//			
//			 
//		} else {
//			ListOfNeighbors.add( new Long( this.getIDofUnit(x, y)  ) );
//		}
//		return ListOfNeighbors;
//	}
//	
//	private List getRectangularNeighborsInDistance(int x, int y, int dist) {
//		List ListOfNeighbors= new LinkedList();
//		if ( dist != 0) {
//			int k;
//			int nu_x = x - dist;  // begin with the leftmost neighbor
//			if (!this.OutsideMap(nu_x, y)) ListOfNeighbors.add( new Long( this.getIDofUnit( nu_x, y) ));
//			for (k = 1; k <= dist; k++) {
//				--nu_x;
//				if (!this.OutsideMap(nu_x, y + k)) ListOfNeighbors.add( new Long( this.getIDofUnit( nu_x, y + k) ));
//				if (!this.OutsideMap(nu_x, y - k )) ListOfNeighbors.add( new Long( this.getIDofUnit( nu_x, y - k) ));
//			};
//			nu_x = x + dist;  // begin with the rightmost neighbor
//			if (!this.OutsideMap(nu_x, y)) ListOfNeighbors.add( new Long( this.getIDofUnit( nu_x, y) ));
//			for (k = 1; k < dist; k++) {
//				--nu_x;
//				if (!this.OutsideMap(nu_x, y + k)) ListOfNeighbors.add( new Long( this.getIDofUnit( nu_x, y + k) ));
//				if (!this.OutsideMap(nu_x, y - k )) ListOfNeighbors.add( new Long( this.getIDofUnit( nu_x, y - k) ));
//			};			
//		} else {
//			ListOfNeighbors.add( new Long( this.getIDofUnit(x, y)  ) );
//		}
//		return ListOfNeighbors;
//	}
//	
//	public List getNeighborsInDistance(int x, int y, int Distance) {
//		switch (this.TopologyType) {
//			case 0: return getRectangularNeighborsInDistance( x, y, Distance);
//			case 1: return getHexagonalNeighborsInDistance( x, y, Distance);
//			default: return new LinkedList();
//		}
//	}
	
	private List<UnitCoordinates> getNextRectangularNeighborsOfUnit(UnitCoordinates Unit) {
		LinkedList<UnitCoordinates> NextNeighbors = new LinkedList<UnitCoordinates>();
		if (Unit.x > 0) NextNeighbors.addLast( new UnitCoordinates( (Unit.x - 1) , Unit.y ) ); // next upper neighbor
		if (Unit.x < (this.rows - 1) ) NextNeighbors.addLast( new UnitCoordinates( (Unit.x + 1), Unit.y ) ); // next lower neighbor
		if (Unit.y > 0) NextNeighbors.addLast( new UnitCoordinates( Unit.x , (Unit.y  - 1) )); // next left neighbor
		if (Unit.y < (this.cols - 1) ) NextNeighbors.addLast( new UnitCoordinates ( Unit.x , (Unit.y + 1) ) ); // next right neighbor
		return (List<UnitCoordinates>) NextNeighbors;
	}
	
	private List<UnitCoordinates> getNextHexagonalNeighborsOfUnit(UnitCoordinates Unit) {
		LinkedList<UnitCoordinates> NextNeighbors = new LinkedList<UnitCoordinates>();
		
		//		the neighbors in one row:  * + *
		if ( Unit.y > 0 ) NextNeighbors.addLast(new UnitCoordinates( Unit.x, (Unit.y - 1) ) );
		if ( Unit.y < (this.cols - 1) ) NextNeighbors.addLast(new UnitCoordinates( Unit.x, (Unit.y ) + 1) );
		
		if ( (Unit.y % 2) == 0) { // all even columns have the cell    	 *-*-*
											//													(* + *)
											// 												    *
			if ( Unit.x > 0) { // the upper "baseline" neighbors *-*-*
				NextNeighbors.addLast(new UnitCoordinates( (Unit.x - 1), Unit.y ) );
				if ( Unit.y > 0 ) NextNeighbors.addLast(new UnitCoordinates( (Unit.x - 1), (Unit.y - 1) ) );
				if ( Unit.y < (this.cols - 1) ) NextNeighbors.addLast(new UnitCoordinates( (Unit.x - 1), (Unit.y ) + 1) );
			};
			// the lower cusp neighbor
			if ( Unit.x < (this.rows - 1) ) NextNeighbors.addLast(new UnitCoordinates( (Unit.x + 1), Unit.y ) );
		} else { // all even columns have the cell    	    *
					//													(* + *)
					// 												 *-*-*
			//	the upper cusp neighbor
			if ( Unit.x > 0 ) NextNeighbors.addLast(new UnitCoordinates( (Unit.x - 1), Unit.y ) );
			if ( Unit.x < (this.rows - 1) ) { // the lower "baseline" neighbors *-*-*
				NextNeighbors.addLast(new UnitCoordinates( (Unit.x + 1), Unit.y ) );
				if ( Unit.y > 0 ) NextNeighbors.addLast(new UnitCoordinates( (Unit.x + 1), (Unit.y - 1) ) );
				if ( Unit.y < (this.cols - 1) ) NextNeighbors.addLast(new UnitCoordinates( (Unit.x + 1), (Unit.y ) + 1) );
			};	
		};
		return (List<UnitCoordinates>) NextNeighbors;
	}
    
    @SuppressWarnings("unchecked")
	private void calcCacheNeighborhood() {
        int countOfUnits = this.getAmountOfUnits();
        this.cachedNeighborhood = new List[countOfUnits];
        for (int i = 0; i < countOfUnits; i++) {
            this.cachedNeighborhood[i] = this.calcNeighborhoodOfUnit(this.getRowOfUnitID(i),this.getColOfUnitID(i));
        }   
    }
    
    
    /**
     * This method calculates a linked list of all neighbors of the given unit.
     *  
     * @param x supplies the row of the unit of interest
     * @param y supplies the column of the unit of interest
     * @return the returned List-structure contains lists (type is List) that collects units which lay in the same distance. 
     * The list is ordered by distances, that means the first element has distance 0 and contains only the unit itself. The 
     * next nested list contains the next neighboring units of the unit of interested. They have a distance of 1.   
     */
    @SuppressWarnings("unchecked")
	private List<List<Integer>> calcNeighborhoodOfUnit(int x, int y) {
        LinkedList<List<Integer>> ListOfCompleteNeighborhood = new LinkedList<List<Integer>>(); // list of the complete neighborhood
        
        int [][] UnitMap = new int[this.rows][this.cols]; // helping structure to find the neighbors
        int k, i; 
        for (k=0; k < this.rows; k++)  // initialize the helping structure with a value < 0
                    for (i=0; i < this.cols; i++) UnitMap[k] [i] = -1;
                    
        int dist = 0;
        UnitMap[x][y] = 0; // write the distance of the neighbor in the helping unit map
        LinkedList<UnitCoordinates> NewNeighbors = null;
        LinkedList<UnitCoordinates> OldNeighbors = new LinkedList<UnitCoordinates>();
        OldNeighbors.add( new UnitCoordinates(x, y) ); 
        LinkedList<Integer> NeighborIDs = new LinkedList<Integer>(); // first list for the distance 0 
        NeighborIDs.addLast( new Integer(this.getIDofUnit(x, y)) ); // in distance 0 we have only the unit itself
        ListOfCompleteNeighborhood.addLast( NeighborIDs ); // add the neighborhood of distance 0 to the complete list of neighbors
        
        do {
            ++dist; // neighbors of the next distance 
            NeighborIDs = new LinkedList<Integer>();
            NewNeighbors = new LinkedList<UnitCoordinates>();
            for ( Iterator OldN_it = OldNeighbors.iterator(); OldN_it.hasNext(); ) { // cycle over all neighbors of the distance "dist" - 1
                List ElementaryCell = null;
                switch (this.TopologyType) { // get the elementary cell of the unit according to the current topology
                    case RECTANGULAR: ElementaryCell = getNextRectangularNeighborsOfUnit( (UnitCoordinates) OldN_it.next() );
                                break;
                    case HEXAGONAL: ElementaryCell = getNextHexagonalNeighborsOfUnit( (UnitCoordinates) OldN_it.next() );
                                break;
                };
                if (ElementaryCell==null)
                	continue;
                for ( Iterator ElCell_it = ElementaryCell.iterator(); ElCell_it.hasNext();  ) { // test the members of the elementary cell to lay in distance "dist"
                    UnitCoordinates Unit = (UnitCoordinates) ElCell_it.next();
                    if ( UnitMap[Unit.x][Unit.y] < 0 ) {
                        UnitMap[Unit.x][Unit.y] = dist;
                        NewNeighbors.addLast( new UnitCoordinates(Unit.x , Unit.y) );
                        NeighborIDs.addLast( new Integer(this.getIDofUnit(Unit.x, Unit.y)) );
                    };
                };
            }
            if (!NeighborIDs.isEmpty()) ListOfCompleteNeighborhood.addLast(NeighborIDs); // add the neighborhood of distance "dist" to the complete list of neighbors 
            OldNeighbors.clear(); // to save memory, but not necessary - and - maybe connected with performance costs 
            OldNeighbors = NewNeighbors;
        } while (!OldNeighbors.isEmpty());
        
        return ((List<List<Integer>>) ListOfCompleteNeighborhood);      
    }
	
	/**
	 * This method is the same like @see public List getNeighborhoodOfUnit(int x, int y). The unit of 
	 * interest is specified by giving the ID of it.  
	 * @param UnitID ID of the unit of interest
	 * @return the returned List-structure contains lists (type is List) that collects units which lay in the same distance. 
	 * The list is ordered by distances, that means the first element has distance 0 and contains only the unit itself. The 
	 * next nested list contains the next neighboring units of the unit of interested. They have a distance of 1.   
	 */
	@SuppressWarnings("unchecked")
	public List getNeighborhoodOfUnit(int UnitID) {
		return this.getNeighborhoodOfUnit(this.getRowOfUnitID(UnitID), this.getColOfUnitID(UnitID));
	}
	
	/**
	 * This method delivers a linked list of all neighbors of the given unit. 
     * It uses either a reference to the precalculated cached structure or 
     * calculates a new extra list if caching is disabled. 
	 *  
	 * @param x supplies the row of the unit of interest
	 * @param y supplies the column of the unit of interest
	 * @return the returned List-structure contains lists (type is List) that collects units which lay in the same distance. 
	 * The list is ordered by distances, that means the first element has distance 0 and contains only the unit itself. The 
	 * next nested list contains the next neighboring units of the unit of interested. They have a distance of 1.   
	 */
	public List<List<Integer>> getNeighborhoodOfUnit(int x, int y) {
        
        List<List<Integer>> ListOfCompleteNeighborhood;      
        
        if (this.useCaching) {
            if (this.cachedNeighborhood == null) this.calcCacheNeighborhood(); // first cache access 
            ListOfCompleteNeighborhood = this.cachedNeighborhood[this.getIDofUnit(x,y)];
        } else {
            ListOfCompleteNeighborhood = this.calcNeighborhoodOfUnit(x,y);
        }
		
		return ((List<List<Integer>>) ListOfCompleteNeighborhood);  	
	}
		
    /**
     * This method returns the maximal distance that can be observed inside this unit map
     * @return The maximal distance of units that can be appear.
     */
    public int getMaximalDistance() {
        int maxDist = 0;
        if (this.useCaching) {
            if (this.cachedNeighborhood == null) this.calcCacheNeighborhood(); // first cache access 
            for (List<List<Integer>> Neighbors : this.cachedNeighborhood) {
                if (maxDist < Neighbors.size()) maxDist = Neighbors.size();
            }
        } else {
            List<List<Integer>> Neighbors;
            for (int x = 0; x < this.rows; x++)
                for (int y = 0; y < this.cols; y++) {
                    Neighbors = this.calcNeighborhoodOfUnit(x,y);
                    if (maxDist < Neighbors.size()) maxDist = Neighbors.size();
                }
        }
        maxDist--; // Map count to maximal value that can be observed -> range: 0, ... , size - 1
        return maxDist;
    }
    
}
