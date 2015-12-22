\name{extract.probe.names}
\alias{extract.probe.names}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday user functions  }
\description{
  Extract the probe ids from the given DATA object.
}
\usage{
extract.probe.names(DATA)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{DATA}{ The DATA substructure. }
}
\details{
  Use this in your applicable function to get all probe names of
  the Probes contained in the MasterTable.
}
\value{
  A vector of probe ids.
}

\author{ Matthias Zschunke }

\seealso{\code{\link{expression.matrix.subset()}}}

\keyword{ programming }% at least one, from doc/KEYWORDS
