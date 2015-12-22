# 
#  kmeans, from the package mva
#
#  Example for "R for Mayday"
#  
#  Apply the k-means algorithm to the expression matrix.
#  
#

#  -- 
#  Matthias Zschunke, 06/2005
#

#---------------------------------------------------------

if(as.numeric(version$major) < 2)
{
  require(mva)
}else
{
  require(stats)
}
  

run<-function(DATA, k=10, iterations=10)
{  
  #get the expression matrix of the explicit probes
  X<-extract.expression.matrix(DATA);
 
  #invoke kmeans clusterting 
  CL<-kmeans(X$data,k,iterations)
  
  #create names for the clustercenters
  unique.CI<-unique(CL$cluster)
  CCenterNames<-paste("ClusterCenter_",unique.CI,sep="")
  
  #create result probelists
  PL<-list(
    annotation.name=paste("kmeans{mva}:k=",k,", iter.max=",iterations,sep=""),  
    annotation.quickinfo=paste("within-cluster sum of squares: ",CL$withinss,sep=""),
    cluster.indicator=c(CL$cluster,unique.CI),
    probes=c(X$probes,CCenterNames)
  )
  
  #create result probes
  # here only the cluster centers need to be returned (as implicit probes)
  PR<-list(
  	 probes=CCenterNames,
     data=CL$centers,
     is.implicit=TRUE
  )
  
  
  #return the structures
  return(
  	list(
       probelists=PL,
       probes=PR
	)
  )
}
