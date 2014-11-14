\name{expression.matrix.subset}
\alias{expression.matrix.subset}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday user functions }
\description{
  Get a subset of the expression matrix, by a list of probe ids.
}
\usage{
expression.matrix.subset(DATA, probe.names)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{DATA}{ The DATA object. }
  \item{probe.names}{ A vector of probe ids.}
}
\details{
  Use this in your applicable function.
}
\value{
  \item{data }{A matrix. Each row profiles the geneexpression of a specific probe/gene/EST.}
  \item{probes }{A vector of selected probe ids.}
}

\author{ Matthias Zschunke }
%\examples{}

\keyword{ programming }% at least one, from doc/KEYWORDS
