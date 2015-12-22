\name{probes.read}
\alias{probes.read}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday private }
\description{
  Parse the probes section from the input file.
}
\usage{
probes.read(Lines, mastertable, miotypes, miogroups)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{Lines}{ The lines as given in the probes section in the input file.}
  \item{mastertable}{ DATA\verb+!+mastertable}
  \item{miotypes}{A vector of strings giving the MIOTypes.}
  \item{miogroups}{A vector of string giving the ids of the MIO groups.}
}
\details{
  Do not use this function in your code!
}
\value{
	A list of probes, as defined in the DATA object.
}
%\references{ ~put references to the literature/web site here ~ }
\author{ Matthias Zschunke }

\keyword{ programming }% at least one, from doc/KEYWORDS
