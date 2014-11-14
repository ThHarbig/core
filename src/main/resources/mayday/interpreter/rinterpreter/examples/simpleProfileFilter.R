#
#  simple filtering example
#
#  creates 3 partitions of the explicit probes contained
#  in the master table
#  the partitions contain the probes where the expression
#  values are:
#    above zero
#    below zero
#    above and below zero
#
#
#  see SimpleProfileFilter plugin
#

#  --
#  Matthias Zschunke, 06/2005
#

#------------------------------------------------------------------

run<-function(DATA)
{
     result<-sapply(
     	DATA$probes,
          function(L)
          {
               if(all(L$values>0))
               {
                    1 #all above zero
               }else if(all(L$values<0))
               {
                    2 #all below zero
               }else
               {
                    3 #above and below zero
               }
          },
          simplify=TRUE
     )
     probe.names<-sapply(
     	DATA$probes,
          function(L)
          {
               L$annotation.name
          },
          simplify=TRUE
     )

	    
     pl.names<-c("above zero","below zero","above and below zero")
     unique.CI<-c(1,2,3)
     
     return(list(
     	probelists=list(
               annotation.name=pl.names,
               cluster.indicator=result,
               unique.cluster.indicator=unique.CI,
               probes=probe.names
          )
     ))
}




