\name{set.default.plot.device}
\alias{set.default.plot.device}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday private }
\description{
  Set the plot device that should be used in this session.
  This function is applied automatically with the plot parameter 
  choosen in the R for Mayday GUI.
}
\usage{
set.default.plot.device(dev = "postscript")
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{dev}{ The plotting device name, e.g. "postscript", "pdf", "jpeg", "devSVGMulti" }
}
\details{
  Do not use this in your code!
  Furthermore it is not neccesary to load a plot device in your code,
  since this is done automatically by the R for Mayday interface.  
}
\value{
	none
}
%\references{ ~put references to the literature/web site here ~ }
\author{ Matthias Zschunke }

\keyword{ programming }% at least one, from doc/KEYWORDS
