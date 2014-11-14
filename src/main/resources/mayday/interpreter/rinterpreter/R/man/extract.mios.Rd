\name{extract.mios}
\alias{extract.mios}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday user function }
\description{
  
}
\usage{
extract.mios(DATA, miogroups, probes = TRUE)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{DATA}{ The DATA object }
  \item{miogroups}{ A vector of strings representing the miogroups to extract. }
  \item{probes}{ A logical indicating whether to extract Probes. If \code{FALSE} Probelist MIOs will be extracted}
}
\details{
  You can use this function to extract a (couple of) MIO groups for further evaluation.
}

\value{
	A list with components:
  \item{ objects }{A vector of either Probe identifiers or Probelist identifiers.}
  \item{ values }{Normally a matrix of values. Each row belongs to one object and each column belongs to one MIO group.}
}

%\references{  }
\author{ Matthias Zschunke }
%\note{}

%\seealso{  }
%\examples{}

\keyword{ programming }% at least one, from doc/KEYWORDS
