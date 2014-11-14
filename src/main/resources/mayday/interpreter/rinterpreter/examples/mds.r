#
# multidimensional scaling, using 
#    cmdscale {mva} (classical metric multidim. scaling), 
#    isoMDS {MASS} (Kruskal's non-metric MDS) or
#    sammon {MASS} (Sammon's non-linear mapping)
#
#  R for Mayday example showing the creation of a new
#  MasterTable and DataSet
#
#  Afterwards, a clustering is made.
# 
 
#  --
#  Matthias Zschunke, 06/2005
#

#-----------------------------------------------------------------------

run<-function(DATA,method="cmdscale",k=2, n.clust=2)
{
     #get the expression matrix with all probes (explicit AND implicit ones)
     M<-extract.expression.matrix(DATA,explicit=TRUE,implicit=TRUE)
     
     if(!is.na(charmatch(method,"cmdscale")))
     {
          #use cmdscale
          if(as.numeric(version$major) < 2)
          {
            require(mva)
	      }else
	      {
			  require(stats)
		  }
	      result<-cmdscale(dist(M$data),k=k)
          
     }else if(!is.na(charmatch(method,"isoMDS")))
     {
          #use isoMDS
          library(MASS)
          result<-isoMDS(dist(M$data),k=k)$points          
     }else if(!is.na(charmatch(method,"sammon")))
     {
          #use sammon
          library(MASS)
          result<-sammon(dist(M$data),k=k)$points
     }else
     {
          #error
          msg<-paste(method," is not a valid method identifier!")
          stop(msg)
     }
     
     # create names for the columns containing the result
     new.exp.names<-paste("MDS_",c(1:k),sep="")
     
     # if the names already exist, create other names
     i<-1
     while(!all(is.na(match(new.exp.names,DATA$mastertable$experiments))))
     {
       indices<-c(1:k)
       indices<-indices+(k*i)
       new.exp.names<-paste("MDS_",indices,sep="")
       i<-i+1
     }
     
     #kmeans clustering: kmeans{mva}
     if(as.numeric(version$major) < 2)
     {
       require(mva)
     }else
     {
       require(stats)
     }
      
     
     Cluster<-kmeans(result,n.clust,iter.max=10)
     
     #create probe lists of clusters:
     PL<-list(
     	annotation.name=paste("MDS clustering k=",n.clust,sep=""),
     	cluster.indicator=Cluster$cluster,
     	probes=M$probes
     )
     
     #result specification
     return(list(
     	# create a new DataSet
          dataset=list(
            annotation.name=paste(
              DATA$dataset$annotation.name,"_MDS",sep=""
            ),
            annotation.quickinfo=paste(
              "Created by Multidimensional Scaling",method,sep="; "
            )             
          ),     
     	# to add the result as new Experiment vectors, you need
          # to paste them to the end
     	  mastertable=list(
            experiments=c(DATA$mastertable$experiments,new.exp.names)
          ),
        # give all probes and the new Matrix containing the old
        # values and the new ones
          probes=list(
            probes=DATA$mastertable$probes,
            data=cbind(M$data,result)          
          ),
        # the probelists
        probelists=PL
     ))     
}
