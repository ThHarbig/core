\name{probelists.read}
\alias{probelists.read}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday private }
\description{
  Parse the probelists section of the input file.
}
\usage{
probelists.read(Lines, miotypes, miogroups)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{Lines}{ The lines of the probelists section from the input file.
  Each line corresponds directly to one ProbeList. }
    \item{miotypes}{A vector of strings giving the MIOTypes.}
  \item{miogroups}{A vector of string giving the ids of the MIO groups.}
  
}
\details{
  Do not use this function in your code!
}
\value{
	A list of probelists. (Definition is a part of the DATA object)
}
%\references{ ~put references to the literature/web site here ~ }
\author{ Matthias Zschunke}

\keyword{ programming }% at least one, from doc/KEYWORDS
