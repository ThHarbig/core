#
#  Spectral Clustering
#
#  Example for "R for Mayday"
#
#  Spectral clustering:
#  Ref: A. Y. Ng, M. I. Jordan, Yair Weiss. 
#       On Spectral Clustering: Analysis and an algorithm. 
#       In NIPS 14. 2002
#

#  --
#  Matthias Zschunke, 06/2005
#

#-------------------------------------------------------------


##### Hint: ###############################################
#
#  The clustering result depends extremly on the
#  sigma parameter. It is not very robust!
#
#  e.g. on 2Circle data set: (radii=1,2; sd=0.1)
#        => sigma in [0.04,0.28]=I; does well for these
#           settings
#           best results with 0.04 <= sigma <= 0.1
#             (try to plot the Y-matrix)
#        
#           if sigma > I:
#                the clustering is similar to kmeans 
#           if sigma < I:
#                the clustering is similar to random
#                cluster assignment
#
###########################################################



# loading libraries #######################################
if(as.numeric(version$major) < 2)
{
  require(mva)
}else
{
  require(stats)
}
###########################################################



# run #####################################################
#
# the applicable function, following the input function
# specification of "R for Mayday"
#
# Input: DATA, Mayday data structure
#        k, number of clusters
#        sigma, scaling factor for the similarity measure
#        kmeans.iter.max, number of max. iterations in 
#                         kmeans
#        plot.Y, boolean indicating whether to print the
#                Y matrix (only the first 2 dimensions are
#                plotted)
#
# Output: a Vector of length <number of Probes>, containing
#		  the cluster numbers
###########################################################        
run<-function(DATA,k=2, sigma=sqrt(0.5), kmeans.iter.max=10, 
              plot.Y=FALSE)
{
     # process state with given message and indeterminate
     send.process.state("Preprocessing: ",cur=0)
		
     #get the expression matrix
     M<-extract.expression.matrix(DATA)
     
     #get the selected probelists
     probeNames<-extract.probe.names(DATA)
  
  	 #select the probes from the expression matrix
     indices<-match(probeNames,M$probes)
  	 M$probes<-M$probes[indices]
     M$data<-M$data[indices,]
     
     # process state with given message and max,current
     send.process.state("Clustering: ",cur=100);
     
     # Spectral Clustering:
     CL<-specClust(M$data,k,sigma,kmeans.iter.max)
     Clust=CL$cluster
     Y<-CL$Y



     if(plot.Y)
     {
       # process state with given message and max,current
       send.process.state("Creating the plot: ",cur=900);

	   par(bg="white")
       plot(Y)
       unique.CI<-unique(Clust)
       colors<-rainbow(length(unique.CI))[Clust]
       points(Y[,1],Y[,2],col=colors)
       
       
       #for(i in 1:length(Y[,1]))
       #{
       #   color<-if(Clust[i]==1) "#ff0000" else "#0000ff"
       #   points(Y[i,1],Y[i,2],col=color)
       #}
     }
     
     #plot(M$data)     
     #for(i in 1:length(Y[,1]))
     #{
     #     color<-if(Clust[i]==1)"#ff0000" else "#0000ff"
     #     points(M$data[i,1],M$data[i,2],col=color)
     #}   


     # process state with given message and max,current
     send.process.state("Returning to Mayday: ",cur=1000);

     return(list(
       probelists=list(
         annotation.name=paste("Spectral Clustering; k=",k," sigma=",sigma,sep=""),
         annotation.quickinfo=paste("Within-cluster sum-of-square: ",CL$withinss,sep=""),
         # annotation.info
         # color
         # is.silent 
         # is.sticky
         cluster.indicator=Clust,
         probes=M$probes
         # unique.cluster.indicator
       )
     ));
}



# specClust ###############################################
#
#  spectral clustering algorithm 
#                    of A. Y. Ng, M. I. Jordan, Yair Weiss
#
#  INPUT: M; matrix where each row is a point
#         k; number of clusters
#         sigma; scaling factor
#         kmeans.iter.max; max iterations in kmeans
#  OUTPU: a list with components:
#             cluster (cluster indicator vector)
#             withinss (within-cluster sum-of-squares)
#             Y (the eigenvector (n x k)-matrix)
#
###########################################################
specClust<-function(M,k=2,sigma=0.1,kmeans.iter.max=10)
{
     pstate.cur=200;     
	 send.process.state("Computing affinity map.",cur=pstate.cur)
	
     # 1.) compute the affinity matrix     
     A<- -(as.matrix(dist(M,method="euclidean"))^2) # A= -||s_i - s_j||^2
     A<- A/(2*sigma^2)     
     A<- exp(A)
     diag(A)<- 0 # set diagonal elements to 0



     pstate.cur=300;     
	 send.process.state("Computing 'D'.",cur=pstate.cur)
    
     # 2.a) Define D, where D_ii is the sum of the i-th row of A
     D<-matrix(0,length(A[1,]),length(A[,1]))
     diag(D)<-(apply(A,MARGIN=1,function(row) sum(row)))^-0.5



     #pstate.cur=400;     
	 send.process.state("Computing 'L'.")
     
     # 2.b) Define L := DxAxD
     L<-D %*% A %*% D
 
 
 
     #pstate.cur=500;     
	 send.process.state("Computing eigen-vectors.") 
 
     # 3.) get the k largest eigenvectors of L and form X
     #     this must be improved if there are multiple eigenvalues
     #     in the range 1:k
     Eig<-eigen(L)     
     X<-Eig$vectors[,1:k]
          
          
     pstate.cur=600;     
	 send.process.state("Renormalize X.",cur=pstate.cur)           

     # 4.) renormalize X
     Y<-X/apply(X,MARGIN=1,FUN=function(row) sqrt(sum(row^2)))


     pstate.cur=700;     
	 send.process.state("Applying k-means.",cur=pstate.cur)           

     # 5.0) prepare the initial cluster centers:
     #      as described in the reference; choose
     #      k rows of Y to be as much as possible
     #      orthogonal to each other
     #      ...

     # 5.) each row in Y is a point in R^k => cluster them!    
     CL<-kmeans(Y,k,kmeans.iter.max)
    
     return(list(
       cluster=CL$cluster,
       withinss=CL$withinss,
       Y=Y
     ))
}

