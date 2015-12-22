\name{mastertable.read}
\alias{mastertable.read}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday private }
\description{
  Read and parse the mastertable section from the input file.
}
\usage{
mastertable.read(Lines)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{Lines}{ The lines from the input file corresponding to the mastertable.}
}
\details{
  Do not use this function in your code!
}
\value{
	A list with components
  \item{ data.mode }{One out of "absolute", "log2 absolute", "log10 absolute", "ln absolute", 
  "ratio", "log2 ratio", "log10 ratio", "ln ratio"}
  \item{transformation.mode }{One out of "No transformation", "log2", "log10", "ln"}
  \item{probes}{The probe identifiers of the probes contained in the mastertable.}
  \item{experiments}{The experiment identifiers of the experiments from the mastertable.
  	Each experiment is a column in the expression matrix.
  }
}
\references{ Mayday Manual, Nils et.al.}
\author{ Matthias Zschunke }

\keyword{ programming }% at least one, from doc/KEYWORDS
