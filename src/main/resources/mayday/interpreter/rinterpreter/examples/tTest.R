#  
#  t-test application for 'R for Mayday'.
#  
#  A simple two-sample, two-sided t-test returning the call and
#  the p-value as MIOs.
#
#  2 probelists are created for up-regulated and down-regulated
#  probes.
#
#  The p-value, the call {-1,1} and if requested the mean of the
#  groups.
#
#  The t-test will always be performed over the whole mastertable.
#
#  NA values are ignored.
#

# ---
#  Matthias Zschunke, 2005-09-27
#  zschunke@informatik.uni-tuebingen.de
#

# -------------------------------------------------------------------

run<-function(DATA, 
	group.name.test, cols.test, 
	group.name.reference, cols.reference, 
	group.means=c("both","test","reference","none"), 
	sig.level=0.05) #etc.
{
	# get the data
	X <- extract.expression.matrix(DATA)

	#group indexes
	if(is.character(cols.test)) {
	   cols.test.m <- match(cols.test,DATA$mastertable$experiments)
	   if(any(is.na(cols.test.m))) {
	      stop(paste(
	         "Some of the given column names are invalid (",
	         paste(cols.test[is.na(cols.test.m)],collapse=", "),
	         ").",
	      sep=""))
	   }
	    
	   cols.test <- cols.test.m
	}
	i.A <- as.numeric(cols.test)
	

	if(is.character(cols.reference)) {
	   cols.reference.m <- match(cols.reference,DATA$mastertable$experiments)
	   if(any(is.na(cols.reference.m))) {
	      stop(paste(
	        "Some of the given column names are invalid (",
	         paste(cols.reference[is.na(cols.reference.m)],collapse=", "),
	         ").",
	      sep=""))
	   }
	   
	   cols.reference <- cols.reference.m
	}
	i.B <- as.numeric(cols.reference)
	
	#check for group mean request
	group.means = charmatch(group.means,c("both","test","reference"))[1]
	
	#take the test
	P<-student(X$data,i.A,i.B,sig.level)
	
	#the indexes for the up- and down-regulated probes
	i.down <- intersect(P$signif.index, which(P$call <= 0))
	i.up   <- intersect(P$signif.index, which(P$call >= 0))
	
	call<-array(0,length(DATA$mastertable$probes))
	call[i.down]<- -1
	call[i.up]<- 1
	
	indicator <- array(0,length(DATA$mastertable$probes))
	indicator[i.up]<-1
	indicator[i.down]<-2
	
  	 miotypes   = c("PAS.MIO.Double", "PAS.MIO.Double")
	 miogroups  = c( paste("p.value of t-test (",group.name.test," vs. ",group.name.reference,")", sep=""),
	                 paste("call of t-test (",group.name.test," vs. ",group.name.reference,")", sep=""))
	values = cbind(P$p.value,P$call)

	mean.apply<-function(cols){
		apply(X$data[,cols],MARGIN=1,FUN=function(row){
			m<-mean(row,na.rm=TRUE)
			if(is.na(m)) { return(NA) }
			else { return(m) }
		})
	}
	
	if(group.means==1) { #both
    	miotypes  = c(miotypes,  "PAS.MIO.Double","PAS.MIO.Double")
    	miogroups = c(miogroups, group.name.test, group.name.reference)
    	values = cbind(values,mean.apply(i.A),mean.apply(i.B))
    	
  	}else if(group.means==2) { #test
    	miotypes  = c(miotypes,  "PAS.MIO.Double")  		
    	miogroups = c(miogroups, group.name.test)
    	values = cbind(values,mean.apply(i.A))
    	
  	}else if(group.means==3) { #reference
  		miotypes  = c(miotypes,  "PAS.MIO.Double")
    	miogroups = c(miogroups, group.name.reference)
    	values = cbind(values,mean.apply(i.B))
    	
  	}else { #none
  		;
  	}
  
	    
  return(list(
     probelists  = list(
       annotation.name = c(
         paste("up-regulated ("  , group.name.test, "<->", group.name.reference, ")", sep=""), 
         paste("down-regulated (", group.name.test, "<->", group.name.reference, ")", sep="")
       ),
       annotation.quickinfo = c(
         paste("up-regualted probes in t-test of Group '"  ,group.name.test,"' vs. Group '",group.name.reference,"' (level: ",sig.level,").",sep=""),
         paste("down-regulated probes in t-test of Group '",group.name.test,"' vs. Group '",group.name.reference,"' (level: ",sig.level,").",sep="")
       ), 
       cluster.indicator = indicator,     
       probes = DATA$mastertable$probes,                
       unique.cluster.indicator = c(1,2)
     ),
     mios = list(
	   miotypes = miotypes,
	   miogroups = miogroups,
	   probes     = DATA$mastertable$probes,
	   probelists = NULL,
	   values     = values
     )
  ))
}


# perform the tTest ------------------------------------------------------------
# 
# Input: X, a matrix where the groups are taken 
#           columnwise
#        classT, an index vector representing the test
#                group
#        classR, an index vector representing the reference
#                group
#        p.sig, the significance level, p-values larger than
#               p.sig are rejected
# 
# Output: a list with
#          $p.value, a vector of the p-values for each row
#          $call, in {-1, 0, 1} where -1 : mean-difference is negative ("down regulated")
#                                      0 : both estimated means are equal
#                                      1 : mean-diff. is positive ("up regulated")
#          $signif.index, a vector with the indexes of the sig. diff. expressed genes
#
student<-function(X,classT,classR,p.sig=0.05) #----------------------------
{
  P<-apply(X,MARGIN=1,FUN=function(row){
    x<-row[classT]  #test
    y<-row[classR]  #controll
  
    if(any(is.na(x)) || any(is.na(y))) {
      return(c(NA,NA))
    } else {
      t<-t.test(x,y)
      return( c(t$p.value, t$estimate[1]-t$estimate[2]) )
    }
  })
  
  return(
    list(p.value=P[1,],call=sign(P[2,]),signif.index=which(P[1,]<p.sig))
  )
}




