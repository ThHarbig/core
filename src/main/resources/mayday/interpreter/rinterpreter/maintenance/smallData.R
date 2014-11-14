#
# R for Mayday, maintenance
#
#   create a small dataset with 
#     n probes 
#     m experiments 
#   
#   the output contains two mios:
#     mean
#     variance
#

# --
# Matthias Zschunke, 2005-05-19
#

#-----------------------------------------


run<-function(DATA, n=20, m=5)
{
    fill<-function(x,len=nchar(as.character(n))) {paste(paste(array(0,len-nchar(x)),sep="",collapse=""),x,sep="")}
    fill.m<-function(x) {fill(x,len=nchar(m))}
    id<-sapply(1:n,FUN=fill)
    
	X<-list(
		data=matrix(rnorm(n*m),n,m),
		probes=paste("Probe",id,sep="_"),
		is.implicit=FALSE		
	)
	
	CI<-array(1:2,dim=n);
	
	pl.a.mio<-c(
		mean(X$data[CI==1,]),
		var(X$data[CI==1])
	)
	pl.b.mio<-c(
	    mean(X$data[CI==2]),
	    var(X$data[CI==2])
	)
	
	pr.mios<-t(apply(X$data,MARGIN=1,FUN=function(row)
	{
	  c(mean(row),var(row))
	}))
	
	
	return(list(
		dataset=list(
		  annotation.name="Dataset"
		),
		mastertable=list(
		  probes=X$probes,
		  experiments=paste("Exp",sapply(1:m,FUN=fill.m),sep="_",collapse="\t")
		),	
		probelists=list(
		  annotation.name=c("list a","list b"),
		  cluster.indicator=CI,
		  probes=X$probes		  
		),
		probes=X,
		mios=list(
		  miotypes=c("PAS.MIO.Double","PAS.MIO.Double"),
		  miogroups=c("mean","variance"),
		  probes=X$probes,
		  probelists=c("list a","list b"),
		  values=rbind(pr.mios,pl.a.mio,pl.b.mio)
		)
	));
}