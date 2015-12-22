#
#  Simple k-means algorithm (see pckg. {mva})
#
#  "R for Mayday" example showing how to return 
#  only probe lists 
#

#  --
#  Matthias, 06/2005
#

#-------------------------------------------------------------

if(as.numeric(version$major) < 2)
{
  require(mva)
}else
{
  require(stats)
}

run<-function(DATA, k=10, iter.max=10)
{
  #get the expression matrix of the explicit probes
  X<-extract.expression.matrix(DATA);
 
  #invoke kmeans clusterting 
  CL<-kmeans(X$data,k,iter.max)
  
  #create result probelists
  PL<-list(
    annotation.name=paste("kmeans{mva}:k=",k,", iter.max=",iter.max,sep=""),  
    annotation.quickinfo=paste("within-cluster sum of squares: ",CL$withinss,sep=""),
    cluster.indicator=c(CL$cluster),
    probes=c(X$probes)
  )  
  
  #return the structures
  return(list(probelists=PL))
}