\name{dataset.read}
\alias{dataset.read}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday private }
\description{
  Read the \code{\%dataset} part of the input file.
}
\usage{
dataset.read(Lines)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{Lines}{ The lines as read from the input file. }
}
\details{
  Do not use this function directly in your R-code.
}
\value{
  List of 
  \item{annotation.name }{The name of the dataset.}
  \item{annotation.quickinfo }{A short description of the dataset.}
  \item{annotation.info}{A longer description of the dataset.}
  \item{is.silent}{A flag. See the Mayday manual.} 
}

\author{ Matthias Zschunke }

\keyword{ programming }% at least one, from doc/KEYWORDS
