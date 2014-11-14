#  
#  PAM (Partitioning Around Medoids)
#
#  Cluster microarray data using different
#  dissimilarity measures.
#
#  This application makes use of the 'pam'
#  function from the 'cluster' package.
#

#  --
#  Matthias Zschunke, 06/2005
#

#----------------------------------------------------------------------


run<-function(DATA, k=2, metric=c("euclidean","maximum","manhattan","canberra","binary","minkowski"), p=NA , plot=TRUE) #etc.
{
  X<-extract.expression.matrix(DATA);
  
  #NA handling //TODO: do a better job
  if(any(is.na(X$data)))
  {
    mean<-apply(X$data,MARGIN=1,FUN=function(row){mean(row,na.rm=TRUE)});
    ind<-which(is.na(X$data),arr.ind=TRUE);
    
    A<-apply(ind,MARGIN=1,FUN=function(row)paste(row,collapse=","))
    A<-paste("(",A,")",sep="",collapse=";")    
    send.process.state(A);
    
    X$data[ind]<-mean[ind[,"row"]];
  }
  
  D<-dist(X$data,method=metric,p=p);  
  
  library(cluster);
  P<-pam(D,k,keep.diss=FALSE,keep.data=TRUE);

  if(plot)
  {
    #par(bg="white");
    plot(P);
  }  

  return(list(
    probelists  = list(
      annotation.name = "pam: ",          #vector of character; name for each probe list  (1)
                             # annotation.quickinfo = ...,     #vector of character; 1 entry for 1 probe list  (2)
                             # annotation.info = ...,          #vector of character; 1 entry for 1 probe list  (2)
      cluster.indicator = P$clustering,        #vector of any;                                 (5)
      probes = X$probes
     )
  ))
}






