package mayday.interpreter.rinterpreter.core;

import mayday.interpreter.rinterpreter.core.RFunctionParser.FunParam;

/**
 * Representation of one parameter of the applicable function.
 * 
 * @author Matthias
 *
 */
public class RSourceParam 
implements Comparable<RSourceParam>
{
    private String name;            //exact R-Parametername, as in the function-header
    private ParameterType type;     //\in {default, boolean, list,}
    private String defaultvalue;    //default value of this parameter, maybe null
    private String description;  	// short description for showing in a tooltip
    private String value;			//the value to give to the function


    RSourceParam(String name, String defaultvalue,String desc)
    {
        this.name=name;
        this.type=null; //type;
        this.defaultvalue = defaultvalue;
        this.description=desc;
        this.value=null;
    }
    
    RSourceParam(FunParam p)
    {
    	this.name=p.getId();
    	this.defaultvalue=p.getDefault();
    	this.description=p.getDescription();
    	this.value=null;
    }

    public String getName()
    {
        return this.name;
    }

    public ParameterType getType()
    {
        return this.type;
    }
    
    public void setType(ParameterType type)
    {
        this.type=type;
    }

    public String getDefault()
    {
        return this.defaultvalue;
    }
    public void setDefault(String s)
    {
    	this.defaultvalue=s;
    }

	public String getDescription()
	{
		return this.description;
	}
	public void setDescription(String s)
	{
		this.description=s;
	}
	
	public String getValue()
	{
		return this.value;
	}
	public void setValue(String value)
	{
		this.value=value;
	}
	
    public String paramString()
	{
		return name+(value!=null && !value.trim().equals("")?
										"="+value:"="+defaultvalue);
	}
	
    public String toString()
    {
        return this.name;
    }
    
	public boolean equals(Object o)
	{
        return (o instanceof RSourceParam) 
            && this.compareTo((RSourceParam)o)==0;
	}

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(RSourceParam o)
    {
        return this.name.compareTo(o.name);
    }
}
