\name{send.process.state}
\alias{send.process.state}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday user function }
\description{
  Send information about the running process to the Java part
  of the R for Mayday interface.
}
\usage{
send.process.state(msg = NULL, cur = NULL)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{msg}{ A short message what the programm is doing. }
  \item{cur}{ An integer indicating the progress. Must be in [0,1000] }
}
\details{
   The message and the current values are
 printed to the COMAREA-file that is initialized
 by the RProcessStateMonitor it monitors.

 The RProcessStateMonitor is looking at the
 file each 1000 ms. If the content has 
 changed it will read the file and update the
 JProgressBar in the RProgressDialog
 
 Use \code{cur=NULL} to indicate that
 the progress bar should be set to
 "indeterminate" mode.
}
\value{
  none
}
%\references{ ~put references to the literature/web site here ~ }
\author{ Matthias Zschunke }

\keyword{ programming }% at least one, from doc/KEYWORDS
