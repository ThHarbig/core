/*
 * Created on 20.03.2005
 */
package mayday.interpreter.rinterpreter.core.mi;

import mayday.interpreter.rinterpreter.RDefaults;

/**
 * Encapsulation of the mio tag from the description files (XML).
 * 
 * @author Matthias Zschunke
 *
 */
public class MIOGroupRequirement
{
    private static final int IN=1;
    private static final int OUT=2;
    
    private String id;
    private String mioType;
    private int direction=0;
    
    
    
    /**
     * @param id
     * @param className
     * @param direction a string, either IN or OUT
     */
    public MIOGroupRequirement(String id, String mioType, String direction)
    {
        super();
        this.id = (id==null? "" : id);
        this.mioType = mioType;
        
        if(direction!=null && direction.equalsIgnoreCase("IN"))
        {
            this.direction=IN;
        }else
        {
            this.direction = OUT;
        }
    }
    
    /**
     * The fully qualified classname intended to use with this group.
     * 
     * @return
     */
    public String getMIOType()
    {
        return mioType;
    }
    
    /**
     * The fully qualified classname intended to use with this group.
     * @param className
     */
    public void setMIOType(String mioType)
    {
        this.mioType = mioType;
    }
    /**
     * The id of this MIOGroup. May be a short descriptor
     * vor the class of values, such as "variance" or "mean"
     * 
     * @return
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * The id of this MIOGroup. May be a short descriptor
     * vor the class of values, such as "variance" or "mean"
     * 
     * @param id
     * 
     */
    public void setId(String id)
    {
        this.id = (id==null? "" : id);
    }
    
    /**
     * Indicates whether this group is used to get MIOs from Java to R.
     * @return
     */
    public boolean isIN()
    {
        return (direction & IN) != 0;
    }
    
    /**
     * Indicates whether this group is used to get MIOs from R to Java.
     * @return
     */
    public boolean isOUT()
    {
        return (direction & OUT) != 0;
    }
    
    public String getDir()
    {
        return isIN()? "IN" : "OUT" ;
    }
    
    public void setDir(String direction)
    {
        if(direction!=null && direction.equalsIgnoreCase("IN"))
        {
            this.direction = IN;
        }else
        {
            this.direction = OUT;
        }        
       
    }
    
    public String xmlString()
    {
        return 
            "<"+RDefaults.RSDesc.MIO_ELEM+" "+
            RDefaults.RSDesc.MIOATTRIB_id+"=\""+this.id+"\" "+
            RDefaults.RSDesc.MIOATTRIB_classname+"=\""+this.mioType+"\" "+
            RDefaults.RSDesc.MIOATTRIB_direction+"=\""+this.getDir()+"\"/>"
        ;
    }
}
