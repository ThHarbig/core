#
# R for Mayday, maintenance
#
# Export the expression matrix to a tab
# separated file.
#
# NAs will be inserted whenever there's a null
# value.
#

# --
# Matthias Zschunke, 2005-03-16
# 

#------------------------------------------------------------------------------

run<-function(DATA)
{
  send.process.state("Please choose a filename!");

  file<-file.choose();
  X<-extract.expression.matrix(DATA);
  
  lines<-apply(X$data,MARGIN=1,FUN=function(row){paste(row, collapse="\t")});
  
  lines<-paste(X$probes,lines,sep="\t");
  cat(c("",DATA$mastertable$experiments),sep="\t", file=file)
  cat("\n",sep="",file=file,append=TRUE)
  cat(lines,sep="\n",file=file,append=TRUE);
  
  return(NULL);
}


