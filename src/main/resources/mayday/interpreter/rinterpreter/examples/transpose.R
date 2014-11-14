#
# Transpose the expression matrix, to handle the probes as
# experiments and vice versa
#

# --
# Matthias Zschunke, 06/2005
#

#-----------------------------------------------------------


run<-function(DATA)
{
	X<-extract.expression.matrix(DATA,explicit=TRUE,implicit=TRUE)
	
	return(list(
		dataset=list(
			annotation.name=paste(DATA$dataset$annotation.name," - transposed",sep="")  
		),
		mastertable=list(
			#data.mode=DATA$mastertable$data.mode,
			#transformation.mode=DATA$mastertable$transformation.mode,
			probes=DATA$mastertable$experiments,
			experiments=DATA$mastertable$probes
		),
		probes=list(
	        probes=DATA$mastertable$experiments,
	        data=t(X$data),
	        is.implicit=FALSE
		)
	))
}