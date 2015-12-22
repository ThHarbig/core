package mayday.interpreter.rinterpreter.core;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.interpreter.rinterpreter.RDefaults;
import mayday.interpreter.rinterpreter.core.mi.MIParserFactory;
import mayday.interpreter.rinterpreter.core.mi.MITypeParser;

/**
 * Parser for the results of R.
 * 
 * @author Matthias
 *
 */
public class RResultParser
{
	private RSettings settings;
	private File inputFile;
	private LineNumberReader reader;
	
	private MasterTable oldMasterTable;
	
	private DataSet dataSet;
	private MasterTable masterTable;
	
	
	private boolean isnewDataset=false;
	private boolean isnewMasterTable=false;
	//private boolean uninitializedProbes=false;
	
	private String line;
	private List<ProbeList> returnProbeLists=null;

	
	public RResultParser(File f,RSettings settings) 
	throws IOException
	{
		System.out.println("RResultParser::Ctor");
		this.settings=settings;
		this.inputFile=f;
		this.oldMasterTable=settings.getMasterTable();

		if(!f.exists()) 
		{
			throw new RuntimeException(
				RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
				RDefaults.Messages.RResultParser.IN_FILE+f.getName()+"\n\n"+
				"The file does not exist."
			);
		}
		
		reader=new LineNumberReader(new FileReader(f));
		//reader.setLineNumber(1); //begin the counting at 1 (not 0)
		
		//go up to the result mark
		//String line;
		
		do{
			line=reader.readLine();
			if(line==null)
			{
				throw new RuntimeException(
					RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
					RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
					RDefaults.Messages.RResultParser.AT_LINE+reader.getLineNumber()+"\n\n"+
					"invalid file format"
				);
			}
		}while(!(line.trim()).endsWith(RDefaults.RSrcComponents.RESULT_COMMENT));
	}
	
	@SuppressWarnings("unchecked")
	public List<ProbeList> parse() throws IOException
	{
		System.out.println("RResultParser::parse");
		//parsing the dataset
		int baselineNo=reader.getLineNumber();
		while(!(line=reader.readLine().trim()).startsWith(RDefaults.RResults.DS))
		{
			if(line==null)
			{
				throw new RuntimeException(
					RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
					RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
					RDefaults.Messages.RResultParser.AT_LINE+baselineNo+"\n\n"+
					"invalid file format, "+RDefaults.RResults.DS+" expected"
				);
			}
		}
		baselineNo=reader.getLineNumber();
		
		String[] dataset=
		{
			reader.readLine().trim(),
			reader.readLine().trim(),
			reader.readLine().trim(),
			reader.readLine().trim()
		};
		
		if(dataset[0].equals("NULL"))
		{
			//no new dataset
			this.isnewDataset=false;
			this.dataSet=this.oldMasterTable.getDataSet();
			
			
		}else
		{
			//new dataset
			this.isnewDataset=true;
			this.dataSet=new DataSet();
			this.dataSet.setName(dataset[0]);
			
			
			//add the dataset to the DSMV
			//=> at the end of the parsing process			
		}
		this.masterTable=this.dataSet.getMasterTable();
		
		if(!dataset[1].equals("NULL")&& !dataset[1].equals(""))
		{
			this.dataSet.getAnnotation().setQuickInfo(
				replaceControlChars(dataset[1]));
		}else
		{
			//if a new dataSet has been created, we create a nice
			// quickinfo
			if(this.isnewDataset)
			{
				this.dataSet.getAnnotation().setQuickInfo(
					RDefaults.RResults.DS_QINFO_NEW
						.replaceAll(
							RDefaults.TIME_REPLACE,
							""+new Date(System.currentTimeMillis())
						)
						.replaceAll(
							RDefaults.RResults.SETTINGS_REPLACE,
							settings.toString()
						)
				);
			}else
			{
				//do nothing
			}
		}
		
		if(!dataset[2].equals("NULL") && !dataset[2].equals(""))
		{
			this.dataSet.getAnnotation().setInfo(
				replaceControlChars(dataset[2]));
		}else
		{
			//do nothing
		}
		
		if(!dataset[3].equals("NULL"))
		{
			try
			{ 
				this.dataSet.setSilent(Boolean.getBoolean(dataset[3]));
			}catch(Exception ex)
			{
				int lineNo=baselineNo+3;
				throw new RuntimeException(
					RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
					RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
					RDefaults.Messages.RResultParser.AT_LINE+lineNo+"\n\n"+
					ex.getMessage()
				);
			}
		}else	//if no is.silent is given
		{			
			if(this.isnewDataset)
			{
				this.dataSet.setSilent(
					oldMasterTable.getDataSet().isSilent());
			}else
			{
				//do nothing
			}
		}

		while(!(line=reader.readLine().trim()).startsWith(RDefaults.RResults.MT))
		{
			if(line==null)
			{
				throw new RuntimeException(
					RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
					RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
					RDefaults.Messages.RResultParser.AT_LINE+reader.getLineNumber()+"\n\n"+
					"invalid file format"
				);
			}		
		}
		baselineNo=reader.getLineNumber();
		
		//reading the mastertable
		String[] mastertab={null,null,null,null};
		int i=0;
		while(!(line=reader.readLine().trim()).startsWith(RDefaults.RResults.PL))
		{
			if(line==null)
			{
				throw new RuntimeException(
					RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
					RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
					RDefaults.Messages.RResultParser.AT_LINE+(baselineNo+i)+"\n\n"+
					"invalid file format" 					
				);
			}
			mastertab[i++]=new String(line);	
		}
		
		if(mastertab[0].equals("NULL") && mastertab[1]==null)
		{
			//no new mastertable
			this.isnewMasterTable=false;
			
			//if new DataSet: its mastertable must be initialized
			if(this.isnewDataset)
			{
//				this.masterTable.setDataMode(
//					new DataMode(
//						new String(this.oldMasterTable.getDataMode().getName()),
//						this.oldMasterTable.getDataMode().getId()
//					)
//				);
//				this.masterTable.setTransformationMode(
//					new TransformationMode(
//						new String(this.oldMasterTable.getTransformationMode().getName()),
//						this.oldMasterTable.getTransformationMode().getId()
//					)
//				);
				//set the experiments of the MasterTable
				this.masterTable.setNumberOfExperiments(
					this.oldMasterTable.getNumberOfExperiments()
				);
				for(i=0;i!=this.masterTable.getNumberOfExperiments();++i)
				{
					this.masterTable.setExperimentName(
						i,
						new String(this.oldMasterTable.getExperimentName(i))
					);
				}

				//add the probes of the old MasterTable to the new one
				Probe[] oldProbes=(Probe[])this.oldMasterTable.getProbes().values().toArray(new Probe[0]);
				for(i=0;i!=oldProbes.length;++i)
				{
					Probe p=new Probe(this.masterTable);
					p.setName(oldProbes[i].getName());
					if (oldProbes[i].getAnnotation()!=null) {
						p.setAnnotation(oldProbes[i].getAnnotation().clone());
						//AnnotationMIO a=p.getAnnotation();					
					}
					p.setImplicitProbe(oldProbes[i].isImplicitProbe());
					
					for(int j=0; j!=this.masterTable.getNumberOfExperiments();++j)
					{
						p.setValue(oldProbes[i].getValue(j),j);
					}
					this.masterTable.addProbe(p);
				}
				
				//adding the ProbeLists
				ProbeListManager oldPLM=this.oldMasterTable.getDataSet().getProbeListManager();
				ProbeList[] oldPLs=(ProbeList[])oldPLM.getObjects().toArray(new ProbeList[0]);
				for(i=0;i!=oldPLs.length;++i)
				{
					ProbeList p=new ProbeList(this.dataSet,oldPLs[i].isSticky());
					p.setAnnotation(oldPLs[i].getAnnotation().clone());
					p.setColor(new Color(oldPLs[i].getColor().getRGB()));
					
					//ArrayList probeNames=new ArrayList();
					for(int j=0; j!=oldPLs[i].getNumberOfProbes();++j)
					{
						String probeName=oldPLs[i].getProbe(j).getName();
						p.addProbe(this.masterTable.getProbe(probeName));
					}				
					this.dataSet.getProbeListManager().addObjectAtTop(p);
				}									
			}else
			{
				//do nothing
			}
		}else  //we create a new MasterTable
		{
			this.isnewMasterTable=true;
			
			if(!this.isnewDataset)
			{
				this.masterTable=new MasterTable(this.dataSet);	
							
			}else
			{
				//do nothing (a new MasterTable has already been created)
			}
			
			//set the datamode
//			if(mastertab[0].equals("NULL"))
//			{
//				this.masterTable.setDataMode(
//					new DataMode(
//						new String(this.oldMasterTable.getDataMode().getName()),
//						this.oldMasterTable.getDataMode().getId()
//					)
//				);
//			}else
//			{
//				DataMode dM=(DataMode)parseModeName(mastertab[0],"DataMode");
//				if(dM==null)
//				{
//					throw new RuntimeException(
//						RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
//						RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
//						RDefaults.Messages.RResultParser.AT_LINE+(baselineNo)+"\n\n"+
//						"invalid DataMode"
//					);
//				}
//				this.masterTable.setDataMode(dM);
//			}
			//set the transformation mode
//			if(mastertab[1].equals("NULL"))
//			{
//				this.masterTable.setTransformationMode(
//					new TransformationMode(
//						new String(this.oldMasterTable.getTransformationMode().getName()),
//						this.oldMasterTable.getTransformationMode().getId()
//					)
//				);			
//			}else
//			{
//				TransformationMode tM=(TransformationMode)parseModeName(mastertab[1],"TransformationMode");
//				if(tM==null)
//				{
//					throw new RuntimeException(
//						RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
//						RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
//						RDefaults.Messages.RResultParser.AT_LINE+(baselineNo+1)+"\n\n"+
//						"invalid TransformationMode"
//					);
//				}
//				this.masterTable.setTransformationMode(tM);
//			}
			
			//set the experiment list
			if(mastertab[3].equals("NULL"))
			{
				//get the experiments from the old mastertable
				this.masterTable.setNumberOfExperiments(
					this.oldMasterTable.getNumberOfExperiments()
				);
				for(i=0; i!=this.masterTable.getNumberOfExperiments();++i)
				{
					this.masterTable.setExperimentName(
						i,
						new String(this.oldMasterTable.getExperimentName(i))
					);
				}
			}else
			{				
				String[] experiments=mastertab[3].split("\t");
				this.masterTable.setNumberOfExperiments(experiments.length);
				for(i=0; i!=this.masterTable.getNumberOfExperiments();++i)
				{
					this.masterTable.setExperimentName(
						i,
						experiments[i]
					);				
				}
			}
			//add probes to the mastertable
			if(mastertab[2].equals("NULL"))
			{
				//create a mapping of new names to old indices
				ArrayList l_expMap=new ArrayList();
				if(!mastertab[3].equals("NULL"))
				{
					Map l_expMapOld=new HashMap();
					for(i=0;i!=this.oldMasterTable.getNumberOfExperiments();++i)
					{
						l_expMapOld.put(
							this.oldMasterTable.getExperimentName(i),
							new Integer(i)
						);
					}
					
					for(i=0;i!=this.masterTable.getNumberOfExperiments();++i)
					{
						String l_name=this.masterTable.getExperimentName(i);
						l_expMap.add(
							l_expMapOld.get(l_name) //null if this experiment is not contained in the old MT
						);
					}					
				}
				
				//we take the old probes
				Probe[] probes=(Probe[])oldMasterTable.getProbes().values().toArray(new Probe[0]);
				for(i=0; i!=oldMasterTable.getNumberOfProbes();++i)
				{
					Probe p=new Probe(this.masterTable);
					
					p.setName(probes[i].getName());
					if (probes[i].getAnnotation()!=null) {
						p.setAnnotation(p.getAnnotation().clone());
					}
					
					if(mastertab[3].equals("NULL")) //that means: old experiments
					{
						//we take the comlete probe
						for(int j=0;j!=probes[i].getNumberOfExperiments();++j)
						{
							p.addExperiment(new Double(
								probes[i].getValue(j).doubleValue()
							));
						}
					}else //new experiments
					{
						//add only the experiments to it that are contained in the new mastertable
						for(int j=0;j!=this.masterTable.getNumberOfExperiments();++j)
						{
							Double v=((Integer)l_expMap.get(j)==null)?
								null :
                                probes[i].getValue(
                                    ((Integer)l_expMap.get(j)).intValue())==null?
                                null :
								new Double(
									probes[i]
									.getValue(
										((Integer)l_expMap.get(j)).intValue()
									).doubleValue()
								)
							;

							p.addExperiment(
								v
							);
						}
					}
					
					
					this.masterTable.addProbe(p);
				}
			}else	//create empty probes
			{
				String[] sProbes=mastertab[2].split("\t");
				for(i=0;i!=sProbes.length;++i)
				{
					Probe p=new Probe(this.masterTable,true); //the probes are still system probes
					p.setName(sProbes[i]);
					
					/*
					//initialize experiment values by null
					for(int j=0;j!=this.masterTable.getNumberOfExperiments();++j)
					{
						p.addExperiment(null);
					}*/
					
					this.masterTable.addProbe(p);
				}
				
				//this.uninitializedProbes=true;
			}
		}
		

		//parse the probelists
		baselineNo=reader.getLineNumber();
		
		line=reader.readLine();
		
		//if(RDefaults.DEBUG) System.out.println(line);
		
		if(line.trim().equals("NULL"))
		{
			//copy the old Probelists
			if(!this.isnewDataset && this.isnewMasterTable)
			{
				//copy the probelists
				ProbeList[] l_probeLists=
					(ProbeList[])this.dataSet.getProbeListManager()
					.getObjects().toArray(new ProbeList[0]);
				
				ProbeListManager l_PLM=this.dataSet.getProbeListManager();
				
				Probe[] l_newProbes=(Probe[])this.masterTable.getProbes().values().toArray(new Probe[0]);
										
				for(i=0;i!=l_probeLists.length;++i)
				{
					ProbeList oldPL=l_probeLists[i];
					ProbeList pl=new ProbeList(this.dataSet,oldPL.isSticky());
					pl.setAnnotation(oldPL.getAnnotation().clone());
					pl.setColor(new Color(oldPL.getColor().getRGB()));
					pl.setSilent(oldPL.isSilent());
					
					for(int j=0;j!=l_newProbes.length;++j)
					{
						Probe p=l_newProbes[j];
						if(oldPL.contains(p))
						{
							pl.addProbe(p);
						}
					}					
					
					l_PLM.removeObject(oldPL);
					l_PLM.addObject(pl);
				}				
			}else
			{
				//do nothing
			}
		}else
		{
			//read the new Probelists
			List l_probeLists=new ArrayList();
			
			int lineNo=1;
			do
			{
				//if(RDefaults.DEBUG) System.out.println(line);
				
				if(line==null)
				{
					throw new RuntimeException(
						RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
						RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
						RDefaults.Messages.RResultParser.AT_LINE+(baselineNo+i)+"\n\n"+
						"invalid file format" 					
					);					
				}
				
				//overread empty lines
				//IMPROVEMENT: define, that this case is an error
				if(line.trim().equals("")) continue;
				
				//System.out.println("PL:"+line);
				
				try
				{
					String[] l_probeList=line.split("\t");
					
					//if(RDefaults.DEBUG) RDefaults.printResultArrays(l_probeList);
					
					ProbeList newPL=new ProbeList(this.dataSet,true);//null;
					
					//probelist identifier
					if(l_probeList[0].trim().equals("NULL"))
					{
						throw new RuntimeException("invalid probelist name");
					}else
					{
						newPL.setName(l_probeList[0]);
					}
					
					//quickinfo
					if(l_probeList[1].trim().equals("NULL"))
					{
						newPL.getAnnotation().setQuickInfo(
							RDefaults.RResults.DS_QINFO_NEW
							.replaceAll(
								RDefaults.RResults.SETTINGS_REPLACE,
								settings.toString()
							).replaceAll(
								RDefaults.TIME_REPLACE,
								""+new Date(System.currentTimeMillis())
							)
						);
					}else
					{
						newPL.getAnnotation().setQuickInfo(
							replaceControlChars(l_probeList[1])
						);
					}
					
					//info
					if(l_probeList[2].trim().equals("NULL"))
					{
						//do nothing
					}else
					{
						newPL.getAnnotation().setInfo(
							replaceControlChars(l_probeList[2])
						);
					}
					
					//color
					newPL.setColor(
						RDefaults.stringToColor(l_probeList[3])
					);
					
					//isSticky
					newPL.setSticky(
						RDefaults.parseBoolean(l_probeList[4],true)
					);
										
					//isSilent
					newPL.setSilent(
						RDefaults.parseBoolean(l_probeList[5],false)
					);
					
					//add the probes
					//System.out.println("\nPL: "+newPL.getAnnotation().getName());
					for(i=6;i<l_probeList.length;++i)
					{
						if(this.masterTable.getProbe(l_probeList[i])!=null)
						{
							Probe l_probe=this.masterTable.getProbe(l_probeList[i]);
							
							if(l_probe==null) //should not occur
								throw new RuntimeException("Could not find Probe: "+l_probeList[i]);
							
							newPL.addProbe(
						 		l_probe
							);
						}else
						{
							//*
							//the problem here is that the probelist can
							//contain a probe, that is not in the mastertable
							//yet, but will added during the parsing of the probes
							
							//maybe create a mapping of PL identifiers to
							// a list of Probe identifiers
							
							//...no, it will be better to create a new Probe with this name
							Probe l_probe=new Probe(this.masterTable,true);
							l_probe.setName(l_probeList[i]);
							
							//initialize the experiments:
							for(int j=0;j!=this.masterTable.getNumberOfExperiments();++j)
							{
								l_probe.addExperiment(null);
							}
							
							this.masterTable.addProbe(l_probe);
							newPL.addProbe(l_probe);
							//*/
							//throw new RuntimeException("invalid probe name");
						}
					}
					
					//System.out.println(newPL);
					//RDefaults.printResultArrays(newPL.toCollection().toArray());
					
					l_probeLists.add(newPL);
					//System.out.println(l_probeLists);
					
					
				}catch(RuntimeException ex)
				{
					if(RDefaults.DEBUG) ex.printStackTrace();
					
					throw new RuntimeException(
						RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
						RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
						RDefaults.Messages.RResultParser.AT_LINE+(baselineNo+lineNo)+"\n\n"+
						ex.getMessage()
					);					
				}	
						
				++lineNo;
			}while(!(line=reader.readLine()).equals(RDefaults.RResults.PR));
			
			//decide how to give the probelists back
			if(!this.isnewDataset && !this.isnewMasterTable)
			{
				this.returnProbeLists=l_probeLists;
			}else
			{
				Iterator iter=l_probeLists.iterator();
				while(iter.hasNext())
				{
					this.dataSet.getProbeListManager()
							.addObject((ProbeList)iter.next());
				}
			}
		}
		
		baselineNo=reader.getLineNumber();
		
		line=reader.readLine();
		if(line.trim().equals(RDefaults.RResults.PR))
		{
			baselineNo=reader.getLineNumber();
			line=reader.readLine();
		}
		
		if(line.trim().equals("NULL"))
		{
			//do nothing
		}else
		{			
			Object[] l_PL1=(Object[])this.dataSet.getProbeListManager().getObjects().toArray();
			
			List l_PL=new ArrayList();
			for(i=0;i!=l_PL1.length;++i)
			{
				l_PL.add(l_PL1[i]);
			}
			
			if(this.returnProbeLists!=null)
			{
				Object[] l_PL2=this.returnProbeLists.toArray();
				for(i=0;i!=l_PL2.length;++i)
				{
					l_PL.add(l_PL2[i]);
				}
			}
			
			//RDefaults.printResultArrays(l_PL.toArray());
			
			int lineNo=1;
			try
			{
				do{
					if(line==null)
					{
						throw new RuntimeException("probe expected");
					}
					
					if(line.trim().equals("")) continue;
					if(line.trim().startsWith(">")) continue;
					
					String[] l_probe=line.split("\t");
				
					Probe p=null;
				
					//probe identifier
					if(l_probe[0].trim().equals("NULL"))
					{
						throw new RuntimeException("invalid probe identifier");
					}else
					{
						if(this.masterTable.getProbe(l_probe[0].trim())==null)
						{
							//the probe does not exist and will be created and added
							p=new Probe(this.masterTable,true);
							p.setName(l_probe[0].trim());
				
							//initialize probes
							for(i=0;i!=this.masterTable.getNumberOfExperiments();++i)
							{
								p.addExperiment(null);
							}
							
							
							this.masterTable.addProbe(p);
						}else
						{
							p=this.masterTable.getProbe(l_probe[0].trim());
							if(p==null)
							{
								throw new RuntimeException(
									"Probe "+l_probe[0].trim()+"is not contained in this master table."
								);
							}
						}		
					}
				
					//quickinfo
					if(l_probe[1].trim().equals("NULL"))
					{
						//do nothing
					}else
					{
						if (p.getAnnotation()==null)
							p.setAnnotation(new AnnotationMIO());
						p.getAnnotation().setQuickInfo(
							replaceControlChars(l_probe[1])
						);
					}
					
					//info
					if(l_probe[2].trim().equals("NULL"))
					{
						//do nothing
					}else
					{
						if (p.getAnnotation()==null)
							p.setAnnotation(new AnnotationMIO());
						p.getAnnotation().setInfo(
							replaceControlChars(l_probe[2])
						);
					}
					
					//isImplicit
					if(l_probe[3].trim().equals("NULL"))
					{
						//do nothing
					}else
					{
						boolean b=p.isImplicitProbe();
						p.setImplicitProbe(
							RDefaults.parseBoolean(l_probe[3],b)
						);
					}
					
					//parse Values
					for(i=4;i!=4+this.masterTable.getNumberOfExperiments();++i)
					{
						Double v=null;
						try
						{
							v=new Double(Double.parseDouble(l_probe[i]));	
						}catch(Exception ex)
						{
							if(!l_probe[i].equals("NA") && !l_probe[i].equals(""))
							{
								throw new RuntimeException(
									"invalid value"
								);								
							}
						}
						
						p.setValue(v,i-4);
					}
					
					//parse Probelists
					int tmp_i=i;
					for(i=tmp_i;i!=l_probe.length;++i)
					{	
						if(l_probe[i].trim().equals("")) continue;
											
						for(int j=0; j!=l_PL.size();++j)
						{
							ProbeList pl=(ProbeList)l_PL.get(j);
							if(pl.getName().equals(l_probe[i]))
							{
								try
								{
									pl.addProbe(p);
								}catch(Exception ex)
								{;} 
								//IMPROVEMENT: add the ex.message()s to a warning vector that can be displayed as summary.
							}
						}
					}
					
					
					++lineNo;
				}while((line=reader.readLine())!=null && !line.startsWith("%mios"));
				
			}catch(RuntimeException ex)
			{
				if(RDefaults.DEBUG) ex.printStackTrace();
				
				throw new RuntimeException(
					RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
					RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
					RDefaults.Messages.RResultParser.AT_LINE+(baselineNo+lineNo)+"\n\n"+
					ex.getMessage()
				);
			}
			
		}
        
        int lineNo=0;
        try
        {
            if(line!=null)
            {
                //read the mios
                while(line!=null && !line.trim().equals("%mios"))
                {
                    line=reader.readLine();
                }
                baselineNo=reader.getLineNumber();
                
                if((line=reader.readLine())!=null)
                {
                    ++lineNo;
                    if(line.trim().equals("NULL"))
                    {
                        //do nothing
                    }else
                    {
                        //read the miogroups line
                        String[] miogroups=line.split("\\t");
                        //miotypes
                        List<String> miotypes=new ArrayList<String>();
                        ++lineNo;
                        for(String s:(line=reader.readLine()).split("\\t")){                            
                        	miotypes.add(s);              
                        }
                        
                        //probes
                        ++lineNo;
                        String[] pr=(line=reader.readLine()).split("\\t");
                        
                        //probelists
                        ++lineNo;
                        String[] pl=(line=reader.readLine()).split("\\t");
                        
                        List mioExtendables=new ArrayList();
                        for(String s:pr)
                        {
                            Probe p=this.dataSet.getMasterTable().getProbe(s);
                            mioExtendables.add(p);
                        }
                        
                        //collect the Probelists
                        Map<String, ProbeList> map=new HashMap<String, ProbeList>();
                        for(Object obj:this.dataSet.getProbeListManager().getObjects())
                        {
                            if(obj instanceof ProbeList)
                            {
                                ProbeList p=(ProbeList)obj;
                                map.put(p.getName(),p);
                            }
                        }
                        if(this.returnProbeLists!=null)
                        {
                            for(Object obj:this.returnProbeLists)
                            {
                                if(obj instanceof ProbeList)
                                {
                                    ProbeList p=(ProbeList)obj;
                                    map.put(p.getName(),p);
                                }                            
                            }
                        }
                        
                        for(String s:pl)
                        {
                            if(map.containsKey(s))
                            {
                                mioExtendables.add(map.get(s));
                            }                            
                        }  
                        
                        
                        //for each mioextendable read the lines
                        
                        MIGroup[] mioGroups=new MIGroup[miogroups.length];
                        for(int j=0; j!=mioGroups.length; ++j)
                        {
                            //IMPROVEMENT: MIOGroups should be parameterized
                            mioGroups[j]=new MIGroup(
                            		MIManager.getMIOPluginInfo(miotypes.get(j)),
                            		miogroups[j],
                            		this.dataSet.getMIManager()
                            );
                            this.dataSet.getMIManager().addGroup(mioGroups[j]);
                        }
                        
                        for(Object e:mioExtendables)
                        {
                            ++lineNo;
                            line=reader.readLine();
                            
                            //stop on empty lines
                            if(line.trim().equals("")) break;
                            
                            String[] split=line.split("\\t");
                            
                            //for each mio
                            for(int k=0;k!=split.length;++k)
                            {
                                MITypeParser parser=
                                	MIParserFactory.createParser(miotypes.get(k));
                                
                                if(!split[k].equalsIgnoreCase("NA"))
                                {
                                    try
                                    {
                                        mioGroups[k].add(
                                            e,
                                            parser.parse(                           
                                                replaceControlChars(split[k])
                                            )
                                        );
                                    }catch(Exception ex)
                                    {
                                        ex.printStackTrace();
                                        //and ignore
                                    }
                                }
                            }                            
                        }
                        //end of all mio lines
                        //now this f***ing sh** hopefully works
                        
                    }
                }
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
            throw new RuntimeException(
                RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
                RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
                RDefaults.Messages.RResultParser.AT_LINE+(baselineNo+lineNo)+"\n\n"+
                "Exceptions: "+ex.getMessage()
            );
        }
        
        
		/*
		if(this.uninitializedProbes)
		{
			throw new RuntimeException(
				RDefaults.Messages.RResultParser.PARSING_ERROR+"\n"+
				RDefaults.Messages.RResultParser.IN_FILE+this.inputFile.getName()+"\n"+
				"\n"+
				"uninitialized Probes found"
			);
		}//*/
		
		reader.close();
		
		return this.returnProbeLists;
	}
	
	private static String replaceControlChars(String s)
	{
		return s.replaceAll("\\\\n","\n").replaceAll("\\\\t","\t");
	}
	
	/**
	 * @return
	 */
	public MasterTable getMasterTable()
	{
		return masterTable;
	}
    
//    private static class MIOWarning extends Exception
//    {
//        public MIOWarning(String msg)
//        {
//            super(msg);
//        }
//    }

}
