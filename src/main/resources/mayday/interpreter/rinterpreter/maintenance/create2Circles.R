#
#  R for Mayday, maintenance
#
#  Create a dataset with two circles
#
#  showing how to create a new DataSet
#

#  --
#  Matthias Zschunke, 06/2005
#

#------------------------------------------------------------



run <- ####################################################
#
#  
# INPUT:  DATA, the mayday data structure
#         n, number of point per circle
#         radius.innercircle, 
#         radius.outercircle,
#         sd, standard deviation för radius computation
# OUTPUT: 
###########################################################
function(DATA,n,radius.innercircle=1,radius.outercircle=2,sd=0.1)
{
	#angle and radius of the inner circle
     alpha1<-runif(n,min=0,max=1)*2*pi
     radius1<-rnorm(n,mean=radius.innercircle,sd=sd)
     
    #angle and radius of the outer circle
     alpha2<-runif(n,min=0,max=1)*2*pi
     radius2<-rnorm(n,mean=radius.outercircle,sd=sd)
    
     R<-c(radius1,radius2)
     A<-c(alpha1,alpha2)
    
    #transform: polar coordinates to kartesian coordinates 
     X<-R*cos(A)
     Y<-R*sin(A)
     
    #create names for the points
     Names<-matrix("",length(R),2)
     Names[,1]<-array("Point",length(R))
     Names[,2]<-c(1:length(R))
     
     names<-apply(
     	     Names,
               MARGIN=1,
               function(row,max)
               {
                 fill<-array(" ",max-nchar(row[2]))
                 return(paste(row[1],row[2],sep=paste(fill,collapse="")))
               },
               max=nchar(Names[length(R),2]))
    
    #create and return the result with respect to the RForMayday result specifications
     list(
     	dataset=list(
          	annotation.name="2 Circles",
               annotation.quickinfo="Created by 'create2Circles.R'",
          ),
          mastertable=list(
          	data.mode="absolute",
               transformation.mode="No Transformation",
               probes=names,
               experiments=c("X","Y")               	
          ),
          probelists=list(
          	annotation.name=c("innerCircle","outerCircle"),
               probes=names,
               cluster.indicator=c(array(1,n),array(2,n))
          ),
          probes=list(
          	probes=names,
               data=cbind(X,Y),
               is.implicit=FALSE
          )
     )   
}