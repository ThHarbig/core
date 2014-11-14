/**
 * File GridTopology.java
 * Created on 03.03.2004
 * As part of package clustering.SOM
 * By Janko Dietzsch
 *
 */

package mayday.clustering.som;


/**
 * This class implements the grid type selector for 
 * the used unit map.
 * 
 * @author  Janko Dietzsch
 * @version 0.1
 * 
 */
public enum GridTopology {
    RECTANGULAR("rectangular"),
    HEXAGONAL("hexagonal");
    
    private final String type;
    
    private GridTopology(String type) {this.type = type;};
    public String getName() {return this.type;};
    public String toString() {return this.type;};
}
