package mayday.interpreter.rinterpreter.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import mayday.interpreter.rinterpreter.RDefaults;

//IMPROVEMENT: rewrite this class using a syntax-tree mechanism 
/**
 * The RFunctionParser takes an R source file. Via the parse() function
 * one gets an ArrayList of all the function declarations found in the file.
 * See the documentation of the parse() function for further information.
 * 
 * The underlying grammar is a bit more strict than the syntax of R.
 * Each function body needs to be enclosed in braces '{' and '}'.
 * 
 * @author Matthias
 */
public class RFunctionParser
{
	private File srcFile;
	private String source;
	private String description;
	
	private static final String FUNCTIONID="function";
	
	public RFunctionParser(File f) throws IOException
	{
		srcFile=f;
		
		//read the source file		
		try
		{
			BufferedReader reader=new BufferedReader(new FileReader(srcFile));
			
			StringBuffer buf=new StringBuffer();
			StringBuffer descriptionBuf=new StringBuffer();
			
			String line=reader.readLine();
			if(line.trim().startsWith("#"))
			{
				do
				{
					descriptionBuf.append(line+"\n");
					line=reader.readLine();
				}while(line.trim().startsWith("#"));
			}
			
			do
			{
				//all comments will be deleted
				if(line.trim().startsWith("#")) continue;
				if(line.indexOf("#")!=-1)
				{
					if(line.indexOf("\"")==-1) 
					{
						line=line.split("#",2)[0];
					}else
					{
						String[] parts=line.split("\"");
						StringBuffer lineBuf=new StringBuffer();
						for(int i=0;i!=parts.length;++i)
						{	
							if(i%2==1)
							{
								lineBuf.append("\""+parts[i]+"\"");
							}else
							{
								if(parts[i].indexOf("#")!=-1)
								{
									lineBuf.append(parts[i].split("#",2)[0]);
									break;
								}else
								{
									lineBuf.append(parts[i]);
								}
							}
						}
						line=lineBuf.toString();
					}
				}
				
				
				buf.append(line+"\n");
			}while((line=reader.readLine())!=null);
			
			this.source=buf.toString();
			this.description=descriptionBuf.toString();
			
		} catch(Exception e)
		{
			throw new IOException(e.getMessage());
		}
	}
	
	/**
	 * @return an ArrayList containing all the top level functions found in the
	 * R source file. The Objects in the list are of type RFunction
	 * @throws RuntimeException
	 */
	public ArrayList<RFunction> parse() throws RuntimeException
	{
		int[] funPos=this.findFunDecl();
		
		if(funPos.length==0)
		  throw new RuntimeException(
			"RFunctionParser: Could not find any R function declaration \n"+
			"                 in file '"+srcFile.getAbsolutePath()+"'.");
		
		ArrayList<RFunction> functions =new ArrayList<RFunction>();
		for(int i=0; i!=funPos.length; ++i)
		{
			String id=funIdentifier(funPos[i]);
			
			if(id!=null)
			{
				RFunction fun=new RFunction();
				fun.setId(id);
				fun.setParamList(parListOf(funPos[i]));
				functions.add(fun);
			}
		}

		if(functions.size()==0) 
			throw new RuntimeException(
				"RFunctionParser: Could not find any R function declaration \n"+
				"                 in file '"+srcFile.getAbsolutePath()+"'."
			);
		
		return functions;
	}
	
	/**
	 * Parse the Description for the given R source.
	 * <br>
	 * <i>Definition</i>: The Description of an R source
	 * is the comment in front of the related R source file.
	 * The comment starts in the first line of the file and
	 * ends before the first line that does not begin with
	 * an R comment char ('<tt>#</tt>'). Leading whitespace
	 * characters are ignored.
	 *   
	 * @return null, if there is no comment with the given
	 * definition or the comment if it exists
	 */
	public String parseDescription()
	{
		return description;
	}
	/*
	public String parseDescription()
	{
		StringBuffer buf=new StringBuffer();
		try
		{			
			BufferedReader r=new BufferedReader(new StringReader(source));
			String line;
			while((line=r.readLine())!=null)
			{
				if(line.trim().startsWith("#"))
				{
					buf.append(line+"\n");
				}else
				{
					break;
				}
			}
		}catch(IOException ex)
		{
			return null;
		}		
		String result=buf.toString().trim();
		return (result.equals("")?null:result);
	}//*/
	
	
	/**
	 * Get the function name from the function declaration at position pos.
	 * 
	 * @param pos, the position of the string "<tt>function</tt>"
	 * @return name of the function declared at pos
	 */
	private String funIdentifier(int pos)
	{
		int i=pos-1;
		StringBuffer buf=new StringBuffer();
		
		char c=source.charAt(i);
		//find the assignment op "<-"
		while(c!='-')
		{
			if(!Character.isWhitespace(c)) return null;
			//if(i<0) return null;
			
			c=source.charAt(--i);
		}	
		--i; //this is the char '<' of "<-"
		if(source.charAt(i)!='<') return null;
		
		c=source.charAt(--i);
		while(Character.isWhitespace(c))
		{
			c=source.charAt(--i);
		}
		//now we found the last character of a word
		while(!Character.isWhitespace(c))
		{
			buf.append(c);
			c=source.charAt(--i);
			if(i==0) 
			{
				buf.append(source.charAt(i));
				break;
			}
		}
		//now we got the function identifier in reverse order
		return buf.reverse().toString().trim();
	}
	
	/**
	 * Parse the parameter list of the function declaration starting
	 * at the given position.
	 * 
	 * @param pos, the position of the string "function"
	 * @return An ArrayList containing the parameters of this function, the Objects
	 * contained in the list are of type FunParam.
	 */
	private ArrayList<FunParam> parListOf(int pos) throws RuntimeException
	{
		//get substring "(....)"
		int i=pos+RFunctionParser.FUNCTIONID.length();
		//now i points to the first char after "function"
		
		//find the opening bracket
		int begin=0;
		int end=0;
		
		char c=0;
		do
		{
			c=source.charAt(i++);
		}while(c!='(');
		//now i points to one past the opening bracket
		begin=i;
		
		//find the corresponding closing bracket
		int bracketcounter=1;
		while(bracketcounter!=0)
		{
			if(i>=source.length() || bracketcounter<0)
			  throw new RuntimeException("RFunctionParser: Parsing error at "+i+"!");
			  
			 c=source.charAt(i++);
			 if(c=='(') ++bracketcounter;
			 if(c==')') --bracketcounter;
		}
		//now i points to one past the closing bracket
		end=i-1;
		
		String parameterlist=source.substring(begin,end);
		
		//iterate over the plist
		//
		//int posOfId=0;
		int posOfAssign=-1;
		int posOfKomma=-1;
		bracketcounter=0;
		ArrayList<FunParam> result=new ArrayList<FunParam>();
		for(int j=0;j!=parameterlist.length();++j)
		{
		    c=parameterlist.charAt(j);
		    if(c=='(') 
		    {
		        ++bracketcounter;
		        continue;
		    }
		    else if(c==')')
		    {
		        --bracketcounter;
		        continue;
		    }
		    
		    if(bracketcounter==0)
		    {
		        if(c==',' || j==parameterlist.length()-1)
		        {
		            int epsilon=(j==parameterlist.length()-1)?1:0;
		            
		            FunParam p;
		            if(posOfAssign!=-1)
		            {
		                //with default value given
		                p=new FunParam(
		                    parameterlist.substring(posOfKomma+1,posOfAssign).trim(), //id
		                    parameterlist.substring(posOfAssign+1,j+epsilon)    //default value
		                );
		            }else
		            {
		                //without default value given
		                p=new FunParam(
		                    parameterlist.substring(posOfKomma+1,j+epsilon),
		                    null
		                );
		            }
		            
		            if(posOfKomma==-1) //add the Description for DATA
		            {
		                p.setDescription(RDefaults.RSDesc.PARAMDESC_FIRST_DEFAULT);
		            }
		            
		            result.add(p);
		            
		            posOfKomma=j;
		            posOfAssign=-1;
		        }else if(c=='=')
		        {
		            posOfAssign=j;
		        }
		    }		    
		}
		
		
		return result;
		
		//String[] parListStrings=source.substring(begin,end).split(",");
		//ArrayList result=new ArrayList();
		
		// the first parameter is reserved for the
		// mayday data structure, but read it to
		// show it in the function chooser dialog		
//		for(i=0; i!=parListStrings.length; ++i)
//		{
//			String[] parStrings=splitParamString(parListStrings[i]);
//			
//			FunParam p=new FunParam(parStrings[0],parStrings[1]);
//			if(i==0)
//			{
//			  p.setDescription(RDefaults.RSDesc.PARAMDESC_FIRST_DEFAULT);
//			}
//			result.add(p);
//		}
//		
//		return result;
	}
	
//	/**
//	 * Split one paramter into identifier and default value.
//	 * The given String comes in the form:
//	 * "<i>identifier</i>[=<i>default</i>]".
//	 * 
//	 * @param s, an R function parameter string, e.g. "m=0" or "x"
//	 * @return new String array of length 2 containing the parameter identifier
//	 *         and the default value if it exists, else null
//	 */
//	private String[] splitParamString(String s)
//	{
//		String[] tmp=s.split("=",2);
//		String[] result=new String[2];
//		
//		result[0]=new String(tmp[0].trim());
//		result[1]=(tmp.length==1)?null:new String(tmp[1].trim());
//		
//		return result;
//	}
	
	/**
	 * Find all positions of R function declarations.
	 * <br>
	 * <i>Definition</i>: The position of an R function declaration
	 * is the position of the first character of
	 * the occurance of the String "<tt>function</tt>"
	 *  
	 * @return An integer array of all function declarations found in
	 * the file.
	 */
	private int[] findFunDecl()
	{
		ArrayList<Integer> functionPos=new ArrayList<Integer>();
		
		int i=0;
		while(i<source.length())
		{
			// ignore comments
			if(source.charAt(i)=='#')
			{
				while(source.charAt(i)!='\n') ++i;				
			}
			
			// get the source-code from this Position to end
			String tmp=source.substring(i);
			if(tmp.startsWith(RFunctionParser.FUNCTIONID))
			{
				functionPos.add(new Integer(i));
				i=i+RFunctionParser.FUNCTIONID.length();
				i=skipBody(i);
			}
			++i;
		}
		
		//casting the ArrayList to int-Array	
		int[] result=new int[functionPos.size()];		
		for(i=0; i!=functionPos.size();++i)
		{
			result[i]=((Integer)functionPos.get(i)).intValue();
		}
		
		return result;
	}
	
	/**
	 * Skips the function body, that is '{' R-statements '}'.
	 * Thus, only functions of the first level are found.
	 * 
	 * @param i
	 * @return
	 */
	private int skipBody(int i)
	{
	    int braceCounter=1;
	    char c=0;
	    while( i<source.length() && 
	           (c=this.source.charAt(i))!='{')
	    {
	        ++i;
	    }
	    if(i>=source.length()) 
	        throw new RuntimeException(
	            "RFunctionParser: Unexpected end of file: "+this.srcFile.getName()
	        );
	    
	    ++i;
	    while(braceCounter>0)
	    {
	        c=this.source.charAt(i);
	        if(c=='{') ++braceCounter;
	        else if(c=='}') --braceCounter;
	        ++i;
	    }
	 
	    return i;
	}
	
	/* (Kein Javadoc)
	 * 
	 */
	public void test_findFunDecl()
	{
		System.out.println(source);
		
		
		int[] pos=findFunDecl();
		
		System.out.print("Indices: [");
		for(int i=0; i!=pos.length;++i)
		{
			System.out.print(pos[i]+(i!=pos.length-1?", ":""));
		}
		System.out.println("]");
	}
	
	/**
	 * This class encapsulates RFunction information, means the function
	 * identifier and the paramter list, that is an ArrayList with Objects
	 * of type FunParam.
	 * 
	 * @author Matthias
	 *
	 */
	public class RFunction
	{
		private String id;
		private ArrayList<FunParam> parameterList=new ArrayList<FunParam>();
		
		public void setId(String id)
		{
			this.id=id;
		}
		public String getId()
		{
			return id;
		}
		public void clearParamList()
		{
			parameterList.clear();
		}
		public void addParam(String id, String def)
		{
			parameterList.add(new FunParam(id,def));
		}
		public void addParam(FunParam p)
		{
			parameterList.add(p);
		}
		public void setParamList(ArrayList<FunParam> l)
		{
			parameterList=l;
		}
		public Iterator<FunParam> getParamListIterator()
		{
			return parameterList.iterator();
		}
		public String toString()
		{
			return new String(id+"<-function"+parListString());
		}
		
		public String parListString()
		{
			StringBuffer buf=new StringBuffer();
			buf.append("(");
			
			for(int i=0; i!=parameterList.size(); ++i)
			{
				buf.append(((FunParam)parameterList.get(i)).toString());
				if(i!=parameterList.size()-1)
				  buf.append(",");
				else
				  buf.append(")");
			}
			
			return buf.toString();
		}
		
		public boolean equals(Object o)
		{
			return ((o instanceof String) && this.id.equals(o)) ||
			       ((o instanceof RFunction) &&  this.id.equals(((RFunction)o).getId())); 
		}
	}
	
	/**
	 * This class encapsulate the information for an R function parameter, means
	 * the parameter identifier and the default value, if it exists. The default value
	 * is null if it is not declared in the function declaration.
	 * <br><br>
	 * e.g.: "x=1" => id="x" and defaultValue="1"<br>
	 * or    "y"   => id="y" and defaultValue=null<br>
	 * 
	 * @author Matthias
	 * 
	 */
	public class FunParam
	{
		private String id;
		private String defaultValue;
		private String description;
		private String value=null;
		//private String type=null;
		

		public FunParam(String id, String def)
		{
			this.id=id;
			this.defaultValue=def;
		}
		
		public FunParam()
		{
			id=null;
			defaultValue=null;
		}

		public void setValue(String value)
		{
			this.value=value;
		}
		public String getValue()
		{
			return this.value;
		}
		/*
		public void setType(String type)
		{
			this.type=type;
		}
		public String getType()
		{
			return this.type;
		}//*/

		public void setId(String id)
		{
			this.id=id;
		}
		
		public void setDefault(String def)
		{
			this.defaultValue=def;
		}
		
		public String getId()
		{
			return id;
		}
		
		public String getDefault()
		{
			return this.defaultValue;
		}
		
		public String getDescription()
		{
			return this.description;
		}
		public void setDescription(String description)
		{
			this.description=description;
		}
		
		public String toString()
		{
			return new String(id+(defaultValue==null?"":("="+defaultValue)));
		}
	}
}
