\name{extract.expression.matrix}
\alias{extract.expression.matrix}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday user functions  }
\description{
  Extract the expression matrix from the DATA object.
}
\usage{
extract.expression.matrix(DATA, explicit = TRUE, implicit = FALSE)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{DATA}{ The DATA object. }
  \item{explicit}{ Indicates whether to return \emph{explicit} probes. }
  \item{implicit}{ Indicates whether to return \emph{implicit} probes. }
}
\details{
  Use this in your applicable function.
}
\value{
  A list of
  \item{data }{A matrix. Each row profiles the gene expression of a specific probe/gene/EST. }
  \item{probes }{A vector of probe ids. Each entry corresponds to the row in matrix with the same index.}
}

\author{ Matthias Zschunke }

%\examples{}
\keyword{ programming }% at least one, from doc/KEYWORDS
