#  
#  Remove columns from the expression matrix.
#

#  --
#  Matthias Zschunke, 2005-02-25
#

#------------------------------------------------------

run<-function(DATA, v)
{
    #get data values
	X<-extract.expression.matrix(DATA);

    #get the experiments to delete
	cols<-DATA$mastertable$experiments;
	
	
	if(is.null(v) || is.na(v)) # remove columns containing only NAs
	{
	  v<-which(apply(X$data,
	                 MARGIN=2,
	                 FUN=function(col){ !all(is.na(col)) } 
	          ))
	}else # remove the given columns
	{	
	  if(is.character(v))
	  {
	    v<-match(v,cols);
	  }	
	  v<-setdiff(1:length(cols),v)
    }
    
    if(!is.numeric(v))
	{
	  stop("v must be either NULL, of type character or numeric.");
	}
	  
    return(list(
      mastertable = list(
        experiments = cols[v],
      ),
      probes = list(
        data = X$data[,v]
        ,probes = X$probes
      )  
    ))
}






