###########################################################
#
# "R for Mayday"
#
# Functions for reading/writing the data types given by 
# Mayday
#
#
# author: Matthias Zschunke, 22.02.2004
#
#
# last update: 2005-05-20
###########################################################



datastructures.read <- ########################################
#
#  read the data structures given by mayday
#
# INPUT:  filename, the filename of the R-input-file
# OUTPUT: a list containing the entries
#            $dataset
#            $mastertable
#            $probelists
#            $probes
# TODO: other pluggable data structures can be read here
##########################################################
function(filename)
{
     Lines<-readLines(filename)
     
     parts<-charmatch(c(
     	"%dataset",
     	"%mastertable",
     	"%miotypes",
     	"%miogroups",
     	"%probelists",
     	"%probes"
     ),Lines)
     
     DS<-dataset.read(Lines[(parts[1]+1):(parts[2]-1)])
     MT<-mastertable.read(Lines[(parts[2]+1):(parts[3]-1)])
     MIT<-miotypes.read(Lines[(parts[3]+1):(parts[4]-1)])
     MG<-miogroups.read(Lines[(parts[4]+1):(parts[5]-1)])
     PL<-probelists.read(Lines[(parts[5]+1):(parts[6]-1)],MIT,MG)
     PR<-probes.read(Lines[(parts[6]+1):length(Lines)],MT,MIT,MG)
     
     return(list(
     	  dataset=DS,
          mastertable=MT,
          miotypes=MIT,
          miogroups=MG,
          probelists=PL,
          probes=PR
     ))
}

dataset.read <- ###########################################
#
#  read the dataset like specified by "R for Mayday"
#
# INPUT:  Lines, the dataset lines from the R-input file
# OUTPUT: a list, containing the entries:
#            $annotation.name          (string)
#            $annotation.quickinfo     (string)
#            $annotation.info          (string)
#            $is.silent                (boolean)
###########################################################
function(Lines)
{
  if(length(Lines)!=4)
  {
       msg<-paste(
       	"Parsing error: the expected number of dataset lines",
          " does not match 4", sep="")
       stop(msg)
  }

  list(
	annotation.name=Lines[1],
     annotation.quickinfo=replace.control.chars(Lines[2]),
     annotation.info=replace.control.chars(Lines[3]),
     is.silent=as.logical(Lines[4])  
  )
}

mastertable.read <- #######################################
#
#  read a mastertable like specified by "R for Mayday"
#
# INPUT:  Lines, mastertable lines from the R-input file
# OUTPUT: a list containing the entries
#            $DataMode (string)
#            $TransformationMode (string)
#            $Probes (vector of strings)
#            $number.probes (integer)
#            $Experiments (vector of strings)
#            $number.experiments (integer)
###########################################################
function(Lines)
{
  if(length(Lines)!=4)
  {
       msg<-paste(
       	"Parsing error: the expected number of mastertable lines",
          " does not match 4", sep="")
       stop(msg)
  }
  
  Probes  =strsplit(Lines[3],"\t")[[1]];
  ExpNames=strsplit(Lines[4],"\t")[[1]];
  
  #return
  list(
   	data.mode=Lines[1],
     transformation.mode=Lines[2],
     probes=Probes,
     experiments=ExpNames     
   )
}

miotypes.read <- ##########################################
#
#  read one line of strings representing the fully
#  qualified Java class names of the MIOs.
#
#  INPUT: Lines, must be one line
#  OUTPUT: an array of strings
#
###########################################################
function(Lines)
{
  Lines<-Lines[Lines!=""] # cut out empty lines
  Lines<-strsplit(Lines,"\t")
  
  if(length(Lines)<=0)
  {
     return(NULL)
  }else
  {
  	return(Lines[[1]])
  }
}

miogroups.read <- ##########################################
#
#  read one line of strings representing the id of the given
#  mio groups. These ids are intended to be used for querying
#  the appropriate miogroup. 
#
#  INPUT: Lines, one or two lines, in the latter the first line
#                represents the given group ids, else it is 
#                empty
#  OUTPUT: an array of strings
#
###########################################################
function(Lines)
{
  Lines<-Lines[Lines!=""] # cut out empty lines
  Lines<-strsplit(Lines,"\t")
  
  if(length(Lines)<=0)
  {
     return(NULL)
  }else
  {
  	return(Lines[[1]])
  }
}

probelists.read <- ########################################
#
#  read a probelists file like specified by "R for Mayday"
#
# INPUT:  Lines, the probelists lines of the R-input file
#         miotypes,
#         miogroups,
# OUTPUT: a list containing <number of probelists> entries
#         each entry is a list as follows:
#            $annotation.name (string)
#            $color (string)
#            $is.sticky (booleans)
#            $is.silent (booleans)
#            $probes (vector of doubles)
###########################################################
function(Lines,miotypes,miogroups)
{
  Lines<-Lines[Lines!=""] # cut out empty lines
  
  Lines<-strsplit(Lines,"\t")
  Lines<-lapply(Lines,function(L) #pl.split
                      {
                        list(
                          annotation.name=L[1],
                          annotation.quickinfo=replace.control.chars(L[2]),
                          annotation.info=replace.control.chars(L[3]),
                          color=L[4],
                          is.sticky=as.logical(L[5]),
                          is.silent=as.logical(L[6]),
                          mios=if(!is.null(miogroups)) {mio.split(L[7:(7+length(miogroups)-1)],miotypes)} 
                               else {NULL},
                          probes=L[(7+length(miogroups)):length(L)] 
                        )
                      }
  );
  
  return(Lines);
}

mio.split <- ##############################################
#
#  split the mios by ':', the number before ':' indicates
#  the type, that will be used for parsing the rest
#
#  INPUT: entries, a vector of strings like "i:xyz"
#  OUTPUT: a list containing all mio values for this 
#          MIOExtendable (=probe/probelist)
#
###########################################################
function(entries,miotypes)
{
	apply(
	  as.matrix(entries), 
	  MARGIN=1,
	  FUN=function(x)
	  {
	    s<-strsplit(x,":")
	    i<-as.numeric(s[[1]][1])
	    if(is.na(i))
	    {
	    	return(NA)
	    }else
	    {
	        s<-paste(s[[1]][2:length(s[[1]])],sep="",collapse=":")
	        func<-paste(miotypes[i],".parse",sep="")
	        
	        return(eval(          #evaluate expression
	    	   call(func,s)      #create function call
	        ))
	     }
	  }
	)
}


#pl.split #################################################
#
#  helper function to make lapply possible
#
# INPUT:  one list entry
# OUTPUT: the list entry, splitted into the list entries:
#            $annotation.name (string)
#            $color (string)
#            $is.sticky (booleans)
#            $is.silent (booleans)
#            $probes (vector of doubles)
###########################################################



probes.read <- ############################################
#
#  read a probes file like specified by "R for Mayday"
#
# INPUT:  filename, the filename of the probes file
#         mastertab, list as returned by mastertable.read()
# OUTPUT: a list containing <number of probes> entrys
#            $annotation.name (string)
#            $is.implicit (boolean)
#            $values (vector of doubles)
#            $probe.lists (vector of strings)
###########################################################
function(Lines, mastertable, miotypes, miogroups)
{
  Lines<-Lines[Lines!=""] # cut out empty lines
  
  Lines<-strsplit(Lines,"\t")
  
  #return
  lapply(
    Lines,
    function(L,n) #probe.split
    {
      list(
        annotation.name=L[1],
        annotation.quickinfo=replace.control.chars(L[2]),
        annotation.info=replace.control.chars(L[3]),
        is.implicit=as.logical(L[4]),
        values=as.numeric(L[5:(n+4)]),
        mios=if(!is.null(miogroups)) 
             {mio.split(L[(n+5):(n+4+length(miogroups))],miotypes)} 
             else {NULL},
        probelists=L[(n+5+length(miogroups)):length(L)]
      )
    }, 
    n=length(mastertable$experiments)
  )
}


# probe.split #############################################
#
#  helper function to split the probe lines into lists
#
# INPUT:  L, one list entry
#         n, number of values
# OUTPUT: the list entry, splitted into the list entries:
#            $annotation.name (string)
#            $is.implicit (boolean)
#            $values (vector of doubles)
#            $probe.lists (vector of strings)
###########################################################



extract.probe.names <- ####################################
#
#  get all probe names from a list of probes
#
# INPUT:  list of probes
# OUTPUT: vector of names
###########################################################
function(DATA)
{
  as.character(lapply(DATA$probes,(function(L) L$annotation.name)));
}

extract.expression.matrix <- ##############################
#
#  get the data as expression matrix, so that algorithms
#  are easily applicable
#  Note: only the probes which are explicit will be 
#        extracted 
#
# INPUT:  DATA, the mayday data structure
#         explicit, logical indicating whether explicit
#                   probes are returned (default is TRUE)
#         implicit, logical indication whether implicit
#                   probes are returned (default is FALSE)
# OUTPUT: a list containing the entries
#            $probes (vector of probe names)
#            $data   (matrix of length $probes, where each
#                     row contains the expression values
#                     of the corresponding probe)
###########################################################
function(DATA,explicit=TRUE,implicit=FALSE)
{
  probes<-DATA$probes;
  pr<-extract.probe.names(DATA);
  D<-sapply(probes,FUN=(function(L) L$values),simplify=TRUE,USE.NAMES=FALSE)
  D<-if(is.vector(D)) {as.matrix(D)} else {t(D)}
  explicit.index<-! (sapply(probes, FUN=(function(L) L$is.implicit),simplify=TRUE,USE.NAMES=FALSE))
  
  index<-(explicit & explicit.index) | (implicit & !explicit.index)  
  list(probes=pr[index],data=as.matrix(D[index,]));
}

extract.probelist.names <- ###############################
#
# given the DATA object, extract the names (ids) if all
# probelists 
#
##########################################################
function(DATA)
{
  as.character(lapply(DATA$probelists, FUN=function(L){L$annotation.name}))
}

extract.mios <- ###########################################
#
#  extract specific mios from given object ids. Objects are
#  either probes or probelists. The mios returned are
#  specified by their ids.
#
#  INPUT: DATA, the DATA-object
#         miogroups, a subset of DATA$miogroups
#         probes, logical indicating whether to get the probes' mios
#                 if FALSE the probelists' mios will be computed
#
#  OUTPUT: a list with entries:
#            objects, the given ids
#            values, if possible a matrix of values, 
#                    else a list of values
#
###########################################################
function(DATA,miogroups,probes=TRUE)
{
	objects=sapply(
       if(probes){DATA$probes}else{DATA$probelists},
       FUN=function(e)
       {
         e$annotation.name
       },
       simplify=TRUE
	)
	
	indexes=match(miogroups,DATA$miogroups)
	indexes=indexes[!is.na(indexes)]
	
	values=sapply(
		if(probes){DATA$probes}else{DATA$probelists},
		FUN=function(e)
		{
		  sapply(
		    indexes,
		    FUN=function(i)
		    {
		      e$mios[[i]]
		    },
		    simplify=TRUE
		  )		  
		},
		simplify=TRUE
	)
	
	values = if(!is.vector(values)) {t(values)}
	         else {as.matrix(values)}
	
	return(list(objects=objects, values=values))
}




expression.matrix.subset <- ###############################
#
#  get a subset of the expression matrix
#
# INPUT:  DATA, the mayday data structure
#         probe.names, a vector of probe identifiers, that should
#                be extracted
#         
# OUTPUT: a list containing the entries
#            $probes (vector of probe names, maybe the 
#                     order will be changed; the order is
#                     taken from names)
#            $data (matrix of length $probes, where each
#                   row contains the expression values of
#                   the corresponding probe
###########################################################
function(DATA, probe.names)
{
  #getting the whole expression matrix
  X<-extract.expression.matrix(DATA);
          
  #make the identifier unique
  names.unique<-unique(probe.names);
  indices<-match(names.unique,X$probes)
  
  #Error handling, if indices contains NAs: message and 
  #                go on deleting the NAs
  if(any(is.na(indices)))
  {
     msg<-paste(     
       c("These probes could not be found, and are ignored:\n",
       names.unique[is.na(indices)],
       "\n"),
       collapse=" "
     )
     names.unique<-names.unique[!is.na(indices)]
     warning(msg);
  }

  #Error handling, any duplicated?
  dupl<-duplicated(probe.names);
  if(any(dupl))
  {
    msg<-paste(
      c("These probes occured more than once and have been unified:\n",
      probe.names[dupl],
      "\n"),
      collapse=" "
    )    
    warning(msg);
  }
  
  # go on with unique probe identifiers that are really contained 
  #   in the probes
  indices<-match(names.unique,X$probes)
  data<-X$data[indices,]
  
  #return
  list(probes=names.unique,data=data);
}

create.output <- ##########################################
#
#  create the output easyly parseable by Java
#
# INPUT: result, a list containing the result following the
#                result specification of "R for Mayday"
# OUTPUT: none, used for its side effects
###########################################################
function(result)
{
   cat("%dataset\n")
   #if(is.null(result$dataset))
   #{
   #     cat("NULL\n")
   #}else
   #{
        cat(paste(
          if(is.null(result$dataset$annotation.name)) 
            "NULL" 
          else 
            result$dataset$annotation.name,
          paste(
            replace.control.chars(result$dataset$annotation.quickinfo,FALSE),
            collapse="\\n"),
          paste(
            replace.control.chars(result$dataset$annotation.info,FALSE),
            collapse="\\n"),
          if(is.null(result$dataset$is.silent))
            "NULL"
          else
            result$dataset$is.silent,
          sep="\n"),
          "\n",
          sep="")      
   #}
   
   
   cat("%mastertable\n")
   if(is.null(result$mastertable))
   {
        cat("NULL\n")
   }else
   {
        cat(paste(
          if(is.null(result$mastertable$data.mode)) #if null: the handling is done in Java
            "NULL"
          else
            result$mastertable$data.mode,
          if(is.null(result$mastertable$transformation.mode)) #if null: the handling is done in Java
            "NULL"
          else
            result$mastertable$transformation.mode,
          if(is.null(result$mastertable$probes)) #if null: the handling is done in Java
            "NULL"
          else
            paste(result$mastertable$probes,collapse="\t"), 
          if(is.null(result$mastertable$experiments)) #if null: the handling is done in Java
            "NULL"
          else
            paste(result$mastertable$experiments,collapse="\t"),
          sep="\n"),
          "\n",
          sep="")          
   }
   
   cat("%probelists\n")
   if(is.null(result$probelists))
   {
        cat("NULL\n")
   }else
   {
        if(is.null(result$probelists$cluster.indicator))
        {
             #normal probelists
             vec<-as.character(
             	lapply(
                 result$probelists,
                 (function(L) paste(
                     if(is.null(L$annotation.name)) "NULL" else L$annotation.name, 
                     replace.control.chars(L$annotation.quickinfo,FALSE),
                     replace.control.chars(L$annotation.info,FALSE),
                     if(is.null(L$color)) rgb(0,0,0) else L$color,
                     if(is.null(L$is.sticky)) TRUE else L$is.sticky,
                     if(is.null(L$is.silent)) FALSE else L$is.silent,
                     if(is.null(L$probes)) "" else paste(L$probes,collapse="\t"),
                     sep="\t")                
                 )#(function(L)               
               )#lapply
             )#as.character
             cat(paste(
               vec,
               collapse="\n"),
               "\n",
               sep=""
             )
        }else
        {
             PL<-result$probelists            
             
             #create nice probelists
             
             if(!is.null(PL$unique.cluster.indicator))
             {# if a unique cluster indcator vector is given
                  unique.CI<-PL$unique.cluster.indicator
             }else
             {
             	unique.CI<-unique(PL$cluster.indicator)
             	unique.CI<-sort(unique.CI,decreasing=TRUE)
             }
             
             Probes<-apply(
               as.matrix(unique.CI),
               MARGIN=1,
               FUN=(function(ci,pl) 
               {
                  paste(pl$probes[pl$cluster.indicator==ci],collapse="\t")
               }),
               pl=PL
             )
             

             #TEST for annotation.name
             if(is.null(PL$annotation.name))
             {
                  PL$annotation.name<-paste(
                      "R for Mayday", #maybe set this to "%REPLACE"
                      unique.CI,
                      sep=" "
                  )
             }else if(length(PL$annotation.name)==1)
             {
                  PL$annotation.name<-paste(
                    PL$annotation.name,
                    unique.CI,
                    sep=" "
                  )
             }else if(length(PL$annotation.name)!=length(unique.CI))
             {
                  msg=paste(
                    "The length of annotation.name does not match the expected number of probelists.\n",
                    "length: ",length(PL$annotation.name),"; ",
                    "expected: 0,1 or ",length(unique.CI),
                    sep=""
                  )
                  stop(msg)
             }
             
             #TEST for annotation.quickinfo
             if(is.null(PL$annotation.quickinfo))
             {
                  PL$annotation.quickinfo<-"Created by 'R for Mayday'."
             }else if(length(PL$annotation.quickinfo)!=1
                     && length(PL$annotation.quickinfo)!=length(unique.CI))
             {
                  msg=paste(
                    "The length of annotation.quickinfo does not match the expected number of probelists.\n",
                    "length: ",length(PL$annotation.quickinfo),"; ",
                    "expected: 0 (NULL),1 or ",length(unique.CI),
                    sep=""
                  )
                  stop(msg)
             }
             
             #TEST for annotation.info
             if(is.null(PL$annotation.info))
             {
                  PL$annotation.info<-""
             }else if( length(PL$annotation.info)!=1
                     && length(PL$annotation.info)!=length(unique.CI))
             {
                  msg=paste(
                    "The length of annotation.info does not match the expected number of probelists.\n",
                    "length: ",length(PL$annotation.info),"; ",
                    "expected: 0 (NULL),1 or ",length(unique.CI),
                    sep=""
                  )
                  stop(msg)   
             }
             
             #TEST for color (only if longer than uC or color==NULL)
             if(length(PL$color)>length(unique.CI))
             {
                  PL$color<-PL$color[1:length(unique.CI)]
             }else if(is.null(PL$color))
             {
                  PL$color<-rainbow(length(unique.CI))
             }
             
             #TEST for is.sticky
             if(is.null(PL$is.sticky))
             {
                 PL$is.sticky<-TRUE 
             }else if(length(PL$is.sticky)!=1
               && length(PL$is.sticky)!=length(unique.CI))
             {
                  msg=paste(
                    "The length of is.sticky does not match the expected number of probelists.\n",
                    "length: ",length(PL$is.sticky),"; ",
                    "expected: 0(NULL),1 or ",length(unique.CI),
                    sep=""
                  )
                  stop(msg)                    
             }
             
             #TEST for is.silent
             if(is.null(PL$is.silent))
             {
                 PL$is.silent<-FALSE 
             }else if(length(PL$is.silent)!=1
               && length(PL$is.silent)!=length(unique.CI))
             {
                  msg=paste(
                    "The length of is.silent does not match the expected number of probelists.\n",
                    "length: ",length(PL$is.silent),"; ",
                    "expected: 0(NULL),1 or ",length(unique.CI),
                    sep=""
                  )
                  stop(msg)                    
             }             
             
             cat(paste(
               PL$annotation.name,
               replace.control.chars(PL$annotation.quickinfo,FALSE),
               replace.control.chars(PL$annotation.info,FALSE),
               PL$color,
               PL$is.sticky,
               PL$is.silent,
               Probes,
               sep="\t"),
               sep="\n"
             )
             cat("\n")
        }
   }
   
   cat("%probes\n")
   if(is.null(result$probes))
   {
        cat("NULL\n")
   }else
   {
        if(is.null(result$probes$data))
        {
             #normal list of probes
             vec<-as.character(
             	lapply(
                 result$probes,
                 (function(L) paste(
                     L$annotation.name,
                     replace.control.chars(L$annotation.quickinfo,FALSE),
                     replace.control.chars(L$annotation.info,FALSE),
                     if(is.null(L$is.implicit)) "NULL" else L$is.implicit,
                     paste(L$values,collapse="\t"),
                     paste(L$probelists,collapse="\t"),
                     sep="\t")                
                 )#(function(L)               
               )#lapply
             )#as.character
             cat(paste(
               vec,
               collapse="\n"),
               "\n",
               sep=""
             )             
        }else
        {
             PR<-result$probes
             #create nice probes
             #some TESTS:
             
             #TEST annotation.name
             if(is.null(PR$probes))
             {
                if(is.null(PR$annotation.name))
                {
                  msg<-paste(
                    "No probe identifiers given.",
                    sep=""
                  )
                  stop(msg)
                }else
                {
                  PR$probes<-PR$annotation.name;
                }
             }else if(!is.null(PR$annotation.name))
             {
                msg<-paste(
                  "The components 'probes' and 'annotation.name' of the ",
                  "probes return definition cannot occur at the same time."
                )
                stop(msg)
             }
             
             if(length(PR$probes)!=length(PR$data[,1]))
             {
                  msg<-paste(
                    "The number of probe identifiers does not match the expected ",
                    "number of probes. \n",
                    "length: ",length(PR$probes),"; ",
                    "expected: ",length(PR$data[,1]),
                    sep=""
                  )
                  stop(msg)
             }
             
             #TEST annotation.quickinfo
             if(is.null(PR$annotation.quickinfo))
             {
                  PR$annotation.quickinfo<-"Created by 'R for Mayday'."
             }else if(length(PR$annotation.quickinfo)!=1
                     && length(PR$annotation.quickinfo)!=length(PR$data[,1]))
             {
                  msg<-paste(
                    "The length of annotation.quickinfo does not match the expected ",
                    "number of probes.\n",
                    "length: ",length(PR$annotation.quickinfo),"; ",
                    "expected: 0 (NULL),1 or ",length(PR$data[,1]),
                    sep=""
                  )
                  stop(msg)
             }
             
             #TEST annotation.info
             if(is.null(PR$annotation.info))
             {
                  PR$annotation.info<-""
             }else if(length(PR$annotation.info)!=length(PR$data[,1])
                     && length(PR$annotation.info)!=1)
             {
                  msg<-paste(
                    "The length of annotation.info does not match the expected ",
                    "number of probes.\n",
                    "length: ",length(PR$annotation.info),"; ",
                    "expected: 0 (NULL), 1 or ",length(PR$data[,1]),
                    sep=""
                  )
                  stop(msg)
             }
             
             #TEST is.implicit
             if(!is.null(PR$is.imlicit)
                && length(PR$is.implict)!=length(PR$data[,1])
                && length(PR$is.implicit!=1))
             {
                msg<-paste(
                  "The length of is.implicit does not match the expected ",
                  "number of probes.\n",
                  "length: ",lenght(PR$is.implicit),"; ",
                  "expected: 0 (NULL), 1 or ", length(PR$data[,1]),
                  sep=""
                )
                stop(msg)
             }
             
             #TEST probelists
             if(is.null(PR$probelists))
             {
                PR$probelists<-""
             }else if(is.list(PR$probelists))
             {
                if(length(PR$probelists)!=length(PR$data[,1]))
                {
                   msg<-paste(
                     "The length of the list structure 'probelists' does not ",
                     "match the expected number of probes.\n",
                     "length: ",length(PR$probelists),"; ",
                     "expected: ",length(PR$data[,1]),
                     sep=""
                   )
                   stop(msg)
                }else
                {
                   PR$probelists<-sapply(
                     PR$probelists,
                     function(L) paste(L,collapse="\t")
                   )
                }
             }else if(is.matrix(PR$probelists))
             {
               if(length(PR$probelists[,1])!=length(PR$data[,1]))
               {
                 msg<-paste(
                   "The length of the matrix structure 'probelists' does not ",
                   "match the expected number of probes.\n",
                   "length: ",length(PR$probelists[,1]),"; ",
                   "expected: ",length(PR$data[,1]),
                   sep=""
                 )
                 stop(msg)
               }else
               {
                 PR$probelists<-apply(
                   PR$probelists,
                   MARGIN=1,
                   function(row) paste(row,collapse="\t")
                 )
               }
             }else if(is.vector(PR$probelists))
             {
                if(length(PR$probelists)!=length(PR$data[,1]))
                {
                  msg<-paste(
                    "The length of the vector structure 'probelists' does not ",
                    "match the expected number of probes.\n",
                    "length: ", length(PR$probelists),"; ",
                    "expected: ",length(PR$data[,1]),
                    sep=""                    
                  )
                  stop(msg)
                }
             }else
             {
               msg<-paste(
                "The type of the structure 'probelists' does not match ",
                "one of the expected types that are either list, matrix or vector.",
                sep=""
               )
               stop(msg)
             }
             
             cat(paste(
                   PR$probes,
                   replace.control.chars(PR$annotation.quickinfo,FALSE),
                   replace.control.chars(PR$annotation.info,FALSE),
                   if(is.null(PR$is.implicit)) "NULL" else PR$is.implicit, #if null: the handling is done in Java
                   apply(PR$data,1,FUN=(function(row)paste(row,collapse="\t"))), #paste all rows together
                   PR$probelists,
                   sep="\t"),
                sep="\n"
             )
             cat("\n")              
        }        
   }
   
   
   cat("%mios\n")
   MIOS<-result$mios
   if(is.null(MIOS))
   {
        cat("NULL\n")
   }else
   {
      if(is.null(MIOS$miogroups) || is.null(MIOS$miotypes)
         || length(MIOS$miogroups)!=length(MIOS$miotypes))
      {
        cat("NULL\n")
        warning(paste(
          "There was an mios section but it has been ignored due to errors.",
          "(Lengths of miogroups and miotypes did not match!)",
          sep="\n"
          )
          ,call.=FALSE)
      }else if(is.null(MIOS$probes) && is.null(MIOS$probelists))
      {
      	cat("NULL\n")
      	warning(paste(
      	  "There was an mios section but it has been ignored due to errors.",
      	  "(Either probes or probelists must be given!)",
      	  ,sep="\n"
      	),call.=FALSE)
      }else if(is.null(MIOS$values))
      {
      	cat("NULL\n")
      	warning(paste(
      	  "There was an mios section but it has been ignored due to errors.",
      	  "(No values given!)",
      	  ,sep="\n"
      	),call.=FALSE)
      }else if(is.matrix(MIOS$values) 
         && length(MIOS$values[,1])!=(length(MIOS$probes)+length(MIOS$probelists))
         && length(MIOS$values[1,])!=length(MIOS$miogroups)
         )
      {
      	cat("NULL\n")
      	warning(paste(
      	  "There was an mios section but it has been ignored due to errors.",
      	  "(Values matrix with wrong dimensions!)",
      	  sep="\n"
      	),call.=FALSE)         
      }else if(is.list(MIOS$values)
         && length(MIOS$values)!=length(MIOS$miogroups)
         && any(sapply(
                MIOS$values,
                FUN=function(e)
                {
                  length(e)!=(length(MIOS$probes)+length(MIOS$probelists))
                }
            ))
      )
      {
      	cat("NULL\n")
      	warning(paste(
      	  "There was an mios section but it has been ignored due to errors.",
      	  "(Values list with wrong dimensions!)",
      	  ,sep="\n"
      	),call.=FALSE)              
      }else if(!is.matrix(MIOS$values) && !is.list(MIOS$values))
      {
      	cat("NULL\n")
      	warning(paste(
      	  "There was an mios section but it has been ignored due to errors.",
      	  "(Values must either be a matrix or a list!)",
      	  ,sep="\n"
      	))                   
      }else
      {
        #output
   		cat(paste(MIOS$miogroups,sep="",collapse="\t"))
        cat("\n")
        cat(paste(MIOS$miotypes,sep="",collapse="\t"))
        cat("\n")        
        cat(paste(MIOS$probes,sep="",collapse="\t"))
        cat("\n")
        cat(paste(MIOS$probelists,sep="",collapse="\t"))
        cat("\n")
        if(is.matrix(MIOS$values))
        {
          cat(
            paste(
              apply(MIOS$values,MARGIN=1,FUN=function(row){paste(row,sep="",collapse="\t")}),
              sep="",collapse="\n")
          )
          cat("\n")
          
        }else #MIOS$values are a list
        {
          cat(paste(sapply(
              MIOS$values,
              FUN=function(e)
              {
                paste( 
                   sapply(
                     1:length(MIOS$miotypes),
                     FUN=function(i)
                     {
                       foo<-paste(MIOS$miotypes[i],".output",sep="")
	                    
	                   return(
	                      replace.control.chars(eval(call(
	    	                func,
	    	                if(is.list(e))
	    	                {
	    	                  e[[i]]
	    	                }else
	    	                {
	    	                  e[i]
	    	                }     
	    	           )),is.input=FALSE))                       
                     }                   
                   ),
                   sep="",collapse="\t"
                 )
              }, # FUN=function(e)
              sep="",collapse="\n"
            ))) # sapply, paste, cat        
        } # MIOS$values is a list
      } # all entries are ok 
   } # mios is not null  
   
   cat("\n\n\n")   
}


replace.control.chars <- ##################################
# 
#  replace the control characters in the string s
#
# INPUT:  s, string
#            if s is a vector of strings: the replacement
#            is done element-wise
#         is.input, boolean, if TRUE: the other replacement
#                                     direction is used
# OUTPUT: s, with \t replaced by \\t 
###########################################################
function(s,is.input=TRUE)
{
  if(is.input)
  {
    s<-paste(strsplit(s,"\\\\n")[[1]],collapse="\n");
    s<-paste(strsplit(s,"\\\\t")[[1]],collapse="\t");
  }else
  { 
    if(is.null(s))
    {
         s="NULL"
    }else
    {
      split<-strsplit(s,"\n")
      s<-as.character(lapply(split,function(L)paste(L,collapse="\\n")))
      split<-strsplit(s,"\t")
      s<-as.character(lapply(split,function(L)paste(L,collapse="\\t")))
    }
  }
  s
} 

extract.colors <- #########################################
#
#   get the color of the top priority probelist of the
#   pobes identifier
#
#   TODO: improve this function
#
#  INPUT:  probes, vector of probe identifiers
#  OUTPUT: vector of colors with the same length as probes
###########################################################
function(probes,PL)
{
     color<-array(NA,length(probes))
     for(i in 1:length(probes))
     {
          tempCols<-array(NA,length(PL))
          for(j in 1:length(PL))
          {
               if(!is.na(match(probes[i],PL[[j]]$probes)))
               {
                    tempCols[j]<-PL[[j]]$color
               }else
               {
                    tempCols[j]<-NA
               }
          }
          #cat(paste(tempCols,collapse=" "),"\n",sep="")
          index<-is.na(tempCols)
          #cat(paste(index,collapse=" "),"\n",sep="")
          color[i]<-(tempCols[!index])[1]
     }
     
     return(color)
}

probes.by.probelists <- ###################################
#
#  get a unique vector of probe identifiers that are 
#  contained in the given list of probelists
#
# INPUT:  probelists, list of probelists
#
# OUTPUT: vector of probe identifier, that are unique over
#         the probelists
###########################################################
function(DATA)
{
    as.character(sapply(
      DATA$probelists,
      FUN=function(pl)
      {
        pl$probes
      }
    ))    
}

devSVGMulti <-  ###########################################
#
# Wrapper function for devSVG to get this automatically
# loaded by the high level plots with the parameter 
# onefile=FALSE, indicating that there are more than one
# plots in the resulting svg-file.
#
###########################################################
function()
{
  library(RSvgDevice)
  if(exists("devSVG",mode="function"))
  {
    devSVG(onefile=FALSE,xmlHeader=TRUE)
  } else
  {
    postscript()
  }
}

set.default.plot.device <- ##################
#
#  set the default plot device and take 
#  postscript if no valid is given
#
#############################################
function(dev="postscript")
{
  if(!exists(dev,mode="function"))
  {
  	dev="postscript"
  }
  
  options(device=dev);
}

jpeg100 <- ##################################
#
# a wrapper for the jpeg device where the
# quality is set to 100
#
#############################################
function()
{
  jpeg(quality=100)
}

send.process.state <- ########################
#
# send the process state
#
# The message and the current values are
# printed to the COMAREA-file that is initialized
# by the RProcessStateMonitor it monitors.
#
# The RProcessStateMonitor is looking at the
# file each 1000 ms. If the content has 
# changed it will read the file and update the
# JProgressBar in the RProgressDialog
# 
# Use cur=NULL to indicate that
# the progress bar should be set to 
# "indeterminate" mode
#
##############################################
function(msg=NULL,cur=NULL)
{
  s<-paste(
    {if(is.null(cur)) -1 else cur}, 
    {if(is.null(msg)) "" else msg},
    sep=";"
  );
  
  # writing the process state to the status file
  # defined globally when starting the R process
  cat(s,"\n",sep="",file=COMAREA);
}


