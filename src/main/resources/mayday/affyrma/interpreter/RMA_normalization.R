#  
#  RMA-Normalization 'R for Mayday'.
#  
# annotation.name='RMA-Normalized Data'
# ---
#  Anna Jasper, 2007-03-28
#  Anna.ivic@gmail.com
#
# -------------------------------------------------------------------

library(affy) 
run<-function(DATA,files, CelfilePath, SampleNames, PhenoData, Description, Notes, rmMask, rmOutliers, rmExtra, Hdf5, Hdf5FilePath, Verbose, Normalize, Background, BGversion, Destructive, CDFname)
{
	# get the data and make a filelist for RMA
	file <- splitstr(files)
	
	# make RMA
	eset <- justRMA(filenames=file, celfile.path = "", phenoData= PhenoData, description = Description, notes = Notes, rm.mask = rmMask, rm.outliers = rmOutliers, rm.extra = rmExtra, hdf5 = Hdf5, hdf5FilePath = Hdf5FilePath, verbose = Verbose, normalize = Normalize, background = Background, bgversion = BGversion, destructive = Destructive, cdfname = CDFname )
     
 return(list(
          dataset=list(
            annotation.name='RMA-Normalized Data'
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



