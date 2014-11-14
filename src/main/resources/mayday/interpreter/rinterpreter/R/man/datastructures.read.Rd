\name{datastructures.read}
\alias{datastructures.read}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday private }
\description{
  Delegates the parsing of the different sections of the input file
  to the specific \code{read} functions.
}
\usage{
datastructures.read(filename)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{filename}{ The filename of the input file containing the Mayday datastructure. }
}
\details{
  Do not use this function directly in your R-code.
}
\value{
  The \emph{DATA object}. A list of
  \item{dataset }{A list as returned by \code{dataset.read()}.}
  \item{mastertable }{A list as returned by \code{mastertable.read()}.}
  \item{probelists }{A list as returned by \code{probelists.read()}.}
  \item{probes }{A list as returned by \code{probes.read()}.}
}
\author{ Matthias Zschunke }

\keyword{ programming }% at least one, from doc/KEYWORDS

