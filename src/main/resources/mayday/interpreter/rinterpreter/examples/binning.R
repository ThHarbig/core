#
# bins the expression matrix
#
# transform.method (transform data BEFORE binning):
#          - none    
#          - log (log transformation)
#          - sqrt
#
# binning.method: 
#          - scale (simply scales the intensities to the
#              range min-max.)
#          - rank  (same as scale, but uses the rank of
#              the intensities instead of intensities
#              directly)
#
# rounding.method:
#          - ceiling (returns the smallest integer not 
#               less than the float value)
#          - floor   (returns the largest integer not 
#               greater than the float value)
#          - round   (rounds the value.  Note that for 
#               rounding off a 5, the IEEE standard is 
#               used, "_go to the even digit_")
#
# min:    - n  (the smallest allowed binned value)
#
# max:    - n  (the greatest allowed binned value)
#
#

#----------------------------------------------------------------

run<-function(DATA,transform.method="none", binning.method="scale",rounding.method="round",min=0,max=3)
{
   #todo check params
   
	M<-extract.expression.matrix(DATA,explicit=TRUE,implicit=TRUE)
   
   if (transform.method == "log") {
      M$data<-apply(
         M$data, 2, function(col)
            {
               # was ist sinnvoll hier? log10, log2 oder alles als option?
               col <-log(col)
            }
            )
   }
   if (transform.method == "sqrt") {
      M$data<-apply(
         M$data, 2, function(col)
            {
               col <-sqrt(col)
            }
            )
   }
   
   # es werden min / max der experimente (spalten) berechnet. option zur 
   # bestimmung von min/max der gesammten matrix???
   
	if (binning.method == "rank") {
      M$data<-apply(
         M$data, 2, function(col)
            {
               col <-rank(col)

               col <- col - min(col)
               # scale to min-max
               col <- col / max(col) * (max - min) + min
            }
            )
   } else if (binning.method == "scale") {
      M$data<-apply(
         M$data,2,function(col)
            {
               col <- col - min(col)
               # scale to min-max
               col <- col / max(col) * (max - min) + min
            }
            )
   }
   
   if (rounding.method == "ceiling") {
      M$data<-apply(M$data, 2, ceiling)
   } else if (rounding.method == "floor") {
      M$data<-apply(M$data, 2, floor)
   } else {
      M$data<-apply(M$data, 2, round)
   }
   
	return(list(
		dataset=list(
			annotation.name=paste(DATA$dataset$annotation.name, " - binned", sep="")
	       ),

		mastertable=list(
			#data.mode=DATA$mastertable$data.mode,
			#transformation.mode=DATA$mastertable$transformation.mode,
			probes=DATA$mastertable$probes,
			experiments=DATA$mastertable$experiments
		),
 		probes=list(
 	        probes=DATA$mastertable$probes,
 	        data=M$data,
 	        is.implicit=FALSE
 		)
 	))
}
