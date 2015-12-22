\name{mio.split}
\alias{mio.split}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday private }
\description{
	Split the entries of a vector of mio strings as given in the 
	input file. Each entry is either NA or
	of the form \code{type.id : value}. The corresponding MioType
	is selected from the \code{miotypes} vector via the \code{type.id}.
	Each such type corresponds to a parse function that will be
	applied to the value.
	  
}
\usage{
mio.split(entries, miotypes)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{entries}{ A vector of strings. Each string is of the form
  \code{type.id : value}.}
  \item{miotypes}{ A vector of strings. Each string is a fully 
  qualified class name of a MIOType. }
}
\details{
  Do not use this function in your code directly!
}
\value{
	A vector or list (depending on the parse functions of the MIOTypes)
	of mio values.
}
%\references{ ~put references to the literature/web site here ~ }
\author{ Matthias Zschunke }

\keyword{ programming }% at least one, from doc/KEYWORDS
