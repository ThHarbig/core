/*
 * Created on 03.02.2005
 */
package mayday.interpreter.rinterpreter.core;

import java.util.Arrays;

import mayday.interpreter.rinterpreter.RDefaults;


public class ParameterType
{
    public static final int TEXTFIELD=0;
    public static final int COMBOBOX=1;
    public static final int FILECHOOSER=2;
    public static final int FILELIST=5;
    
    
    public static final int DEFAULT=0;
    public static final int BOOLEAN=1;
    public static final int STRICTBOOLEAN=2;
    public static final int LIST=3;
    public static final int FILENAME=4;
    public static final int FILENAMES=5;
    public static final int STRING =6;
    
        
    private String id;
    private boolean suppressEditing=true;
    private String[] values;
    private int editor;
    
    private static final ParameterType __BOOLEAN=
        new ParameterType(
            RDefaults.RSDesc.TYPENAME_BOOLEAN,
            new String[] {"TRUE","FALSE","NA"},
            COMBOBOX
        );
    
    private static final ParameterType __DEFAULT=
        new ParameterType(
            RDefaults.RSDesc.TYPENAME_DEFAULT,
            null,
            TEXTFIELD
        );
    
    private static final ParameterType __STRICTBOOLEAN=
        new ParameterType(
            RDefaults.RSDesc.TYPENAME_STRICTBOOLEAN,
            new String[] {"TRUE","FALSE"},
            COMBOBOX
        );
    
    @SuppressWarnings("unused")
	private static final ParameterType __LIST=
        new ParameterType(
            RDefaults.RSDesc.TYPENAME_LIST,
            null,
            COMBOBOX,
            false
        );
    
    private static final ParameterType __FILENAME=
    	new ParameterType(
    		RDefaults.RSDesc.TYPENAME_FILENAME,
    		null,
    		FILECHOOSER,
    		false);

    private static final ParameterType __FILENAMES=
    	new ParameterType(
    		RDefaults.RSDesc.TYPENAME_FILENAMES,
    		null,
    		FILELIST,
    		false);
    //added 08.10.08 by NT
    private static final ParameterType __STRING=
    	new ParameterType(
    			RDefaults.RSDesc.TYPENAME_STRING,
    			null,
    			TEXTFIELD
    );
    
    public static final ParameterType[] KNOWN_TYPES=
    {
        __DEFAULT,
        __BOOLEAN,
        __STRICTBOOLEAN,
        null,
        __FILENAME,
        __FILENAMES,//must be null, placeholder for LIST
        __STRING
    };
    
    private ParameterType(String id, String[] values, int editor, boolean suppressEditing)
    {
        this.id=id;
        this.values=values;
        this.editor=editor;
        this.suppressEditing=suppressEditing;
    }

    private ParameterType(String id, String[] values, int editor)
    {
        this(id,values,editor,true);
    }
    
    public static ParameterType createInstance(String id)
    {
//        ParameterType t;
        if(id.equalsIgnoreCase(RDefaults.RSDesc.TYPENAME_BOOLEAN))
        {
            return KNOWN_TYPES[BOOLEAN];            
        }else if(id.equalsIgnoreCase(RDefaults.RSDesc.TYPENAME_STRICTBOOLEAN))
        {
            return KNOWN_TYPES[STRICTBOOLEAN];
        }else if(id.equalsIgnoreCase(RDefaults.RSDesc.TYPENAME_LIST))        
        {
            return new ParameterType(
                RDefaults.RSDesc.TYPENAME_LIST,
                null,
                COMBOBOX,
                false);
        }
        else if(id.equalsIgnoreCase(RDefaults.RSDesc.TYPENAME_FILENAME))
        {
            return KNOWN_TYPES[FILENAME];
        }else if(id.equalsIgnoreCase(RDefaults.RSDesc.TYPENAME_FILENAMES))
        {
            return KNOWN_TYPES[FILENAMES];
        }
        //added 06.10.08 nastasja
        else if(id.equalsIgnoreCase(RDefaults.RSDesc.TYPENAME_STRING))
        {
            return KNOWN_TYPES[STRING];
        }
        return KNOWN_TYPES[DEFAULT];
    }
    
    public static ParameterType createInstance(int id)
    {
        switch(id)
        {
            case BOOLEAN:
                return KNOWN_TYPES[BOOLEAN];
            case STRICTBOOLEAN:
                return KNOWN_TYPES[STRICTBOOLEAN];
            case LIST:
                return new ParameterType(
                    RDefaults.RSDesc.TYPENAME_LIST,
                    null,
                    COMBOBOX,
                    false);
            case FILENAME:
            	return KNOWN_TYPES[FILENAME];
            case FILENAMES:
            	return KNOWN_TYPES[FILENAMES];
            	//added 06.10.08 nastasja
            case STRING:
            	return KNOWN_TYPES[STRING];
            default:
                return KNOWN_TYPES[DEFAULT];
        }
    }
    
    public String[] values()
    {
        return this.values;
    }
    
    public void setValues(String[] values)
    {
        if(this.id.equalsIgnoreCase(RDefaults.RSDesc.TYPENAME_LIST))
        {
            this.values=values;
        }
    }
    public boolean equals(Object cmp) //do not change, the TypeCellEditors relies on it
    {
        return (cmp instanceof ParameterType) 
                && this.id.equals(((ParameterType)cmp).id);
    }
    
    public int editor()
    {
        return this.editor;
    }
    
    public boolean suppressEditing()
    {
        return this.suppressEditing &&
               !this.id.equals(RDefaults.RSDesc.TYPENAME_LIST);
    }
    public void setSuppressEditing(boolean suppEdit)
    {
        if(this.id.equals(RDefaults.RSDesc.TYPENAME_LIST))
        {
            this.suppressEditing=suppEdit;
        }
    }
    
    public String id()
    {
        return this.id;
    }
    
    public String toString() //do not change, some Renderer relies on it
    {
        return this.id;
    }
    
    public String xmlString(int indent)
    {
        char[] cindent=new char[indent];
        Arrays.fill(cindent,'\t');
        
        StringBuffer buf=new StringBuffer();
        buf.append(cindent);
        buf.append("<"+RDefaults.RSDesc.TYPE_ELEM+" name=\""+
            RSourceDescriptionParser.escapeEntity(this.id)
            +"\""+
            (!this.suppressEditing()?
                    "":
                    " "+RDefaults.RSDesc.TYPEATTRIB_NOEDIT+"=\"false"+
                    "\""
             ));
        
        if(id.equalsIgnoreCase(RDefaults.RSDesc.TYPENAME_LIST)
                && values!=null && values.length!=0)
        {
            buf.append(">\n");
            for(int i=0; i!=values.length; ++i)
            {
                buf.append(cindent);
                buf.append(
                    "\t<"+RDefaults.RSDesc.ENTRY_ELEM+" value=\""+
                    RSourceDescriptionParser.escapeEntity(values[i])+
                    "\"/>\n"
                );
            }
            
            buf.append(cindent);
            buf.append("</type>");
        }else
        {
            buf.append("/>");
        }
        return buf.toString();
    }
}