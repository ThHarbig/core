#  
#  GCRMA-Normalization 'R for Mayday'.
#  
# annotation.name='GCRMA-Normalized Data'
# ---
#  Anna Jasper, 2007-03-28
#  Anna.ivic@gmail.com
#
# -------------------------------------------------------------------

library(gcrma) 
run<-function(DATA,files, Compress, CelfilePath, SampleNames, PhenoData, Description, Notes, Normalize, Bgversion, Type, K, Stretch, Correction, Rho, OpticalCorrect, Verbose, Fast, Minimum, OptimizeBy, Cdfname) #etc.
{
	# get the data and make a filelist for RMA
	file <- splitstr(files)

	# make GCRMA
	eset <- justGCRMA(filenames=file, compress=Compress, celfile.path="", sampleNames=SampleNames, phenoData=PhenoData, description=Description, notes=Notes, normalize=Normalize, bgversion=Bgversion, type=Type, k=K, stretch=Stretch, correction=Correction, rho=Rho, optical.correct=OpticalCorrect, verbose=Verbose, fast=Fast, minimum=Minimum, optimize.by=OptimizeBy, cdfname=Cdfname)
     
 return(list(
          dataset=list(
            annotation.name='GCRMA-Normalized Data'
            ),
          mastertable=list(
            probes=featureNames(eset),
	    experiments=sampleNames(eset)
            ), 
          probes=list(
            data=exprs(eset),
            probes=featureNames(eset),
            is.implicit=FALSE
	    )
        ));
}


splitstr <- function (x) 
{
    x <- as.character(x)
    s <- strsplit(x = x, ", ")
    return(s[[1]])
}

if (Sys.info()[1]=="Windows") {
  file.path <- function (..., fsep = .Platform$file.sep) {
    if (list(...)[[1]]=="") fsep="";
    .Internal(file.path(list(...), fsep))
  }
}
