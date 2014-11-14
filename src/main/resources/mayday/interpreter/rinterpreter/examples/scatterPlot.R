#
#  Scatterplot of the probes contained in the given 
#  probelists
#
#  Plots 2 or 3 experiments of all explicit probes, the
#  experiments can be given either as integers or
#  as experiment identifiers
#

#  --
#  Matthias Zschunke, 06/2005
#

#--------------------------------------------------------

run<-function(DATA,exp1=1,exp2=2,exp3=NULL)
{
     # get the expression matrix and the corresponding 
     # probe identifier
     M<-extract.expression.matrix(DATA,implicit=TRUE);
     
     # get the probe identifier contained in the 
     # probelists
     probenames<-extract.probe.names(DATA);
     
     # get the indices of the probes in the expression
     # matrix
     indices<-match(probenames,M$probes)
     
     # remove NAs (this can occur if the probelists
     # contain implicit probes, because the expression
     # matrix does not)
     indices<-indices[!is.na(indices)]
     
     # get the probelists probes subset of the 
     # expression matrix
     M$probes<-M$probes[indices]
     M$data<-M$data[indices,]

     # get the index of the experiments
     # if the experiments are given as characters
     # we need to extract the indices
     # give an error message and stop if the experiment
     # identifier is neither numeric nor character
     if(is.character(exp1)) 
     {
         exp1<-match(exp1,DATA$mastertable$experiments)[1] #only the first if more are given 
     }
     else if(!is.numeric(exp1))
     {
       msg<-"Parameter 'exp1' neither numeric nor character."
       stop(msg)
     }
     if(is.character(exp2))
     {
       exp2<-match(exp2,DATA$mastertable$experiments)[1] #only the first if more are given
     }else if(!is.numeric(exp2))
     {
       msg<-"Parameter 'exp2' neither numeric nor character."
       stop(msg)
     }
     if(is.character(exp3))
     {
       exp3<-match(exp3,DATA$mastertable$experiments)[1] #only the first if more are given
     }else if(!is.numeric(exp3) && !is.null(exp3))
     {
       msg<-"Parameter 'exp3' neither numeric nor character."
       stop(msg)
     }
     
     # extract the probelists names
     pl.names<-sapply(DATA$probelists,function(L) L$annotation.name,simplify=TRUE)

     # get the color of the probes top priority probelists
     Col<-extract.colors(probenames,DATA$probelists)
     
     par(bg="white")
     if(is.null(exp3))
     {
       # 2d plot:
       plot(
         M$data[,exp1],
         M$data[,exp2],
         xlab=DATA$mastertable$experiments[exp1],
         ylab=DATA$mastertable$experiments[exp2],
         main=DATA$dataset$annotation.name,       
         #IMPROVEMENT: colorize the probelist names (function: [m]text(...))
         sub=paste(
             "ProbeLists [",
             paste(pl.names,collapse=","),
             "]",
             collapse=""
             ),
          col=Col
       )
       }else
       {       
         library(scatterplot3d)
         
         # 3d plot
         scatterplot3d(
           M$data[,exp1],
           M$data[,exp2],
           M$data[,exp3],
           xlab=DATA$mastertable$experiments[exp1],
           ylab=DATA$mastertable$experiments[exp2],
           zlab=DATA$mastertable$experiments[exp3],
           main=DATA$dataset$annotation.name,
           sub=paste(
             "ProbeLists [",
             paste(pl.names,collapse=","),
             "]",
             collapse=""
           ),
           color=as.character(Col),
           bg="black"
         )
         
         #second 3d plot
         scatterplot3d(
           M$data[,exp2],
           M$data[,exp3],
           M$data[,exp1],
           xlab=DATA$mastertable$experiments[exp2],
           ylab=DATA$mastertable$experiments[exp3],
           zlab=DATA$mastertable$experiments[exp1],
           main=DATA$dataset$annotation.name,
           sub=paste(
             "ProbeLists [",
             paste(pl.names,collapse=","),
             "]",
             collapse=""
           ),
           color=as.character(Col),
           bg="black"
         )
         
         #third 3d plot
         scatterplot3d(
           M$data[,exp3],
           M$data[,exp1],
           M$data[,exp2],
           xlab=DATA$mastertable$experiments[exp3],
           ylab=DATA$mastertable$experiments[exp1],
           zlab=DATA$mastertable$experiments[exp2],
           main=DATA$dataset$annotation.name,
           sub=paste(
             "ProbeLists [",
             paste(pl.names,collapse=","),
             "]",
             collapse=""
           ),
           color=as.character(Col),
           bg="black"
         )
       }     

     #no need to give something back, cause we only wanted to plot    
     return(NULL)
}

