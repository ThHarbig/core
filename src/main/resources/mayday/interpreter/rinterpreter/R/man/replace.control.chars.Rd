\name{replace.control.chars}
\alias{replace.control.chars}
%- Also NEED an '\alias' for EACH other topic documented here.
\title{ R for Mayday private }
\description{
  Replace controll characters as linefeed and tabulator.
}
\usage{
replace.control.chars(s, is.input = TRUE)
}
%- maybe also 'usage' for other objects documented here.
\arguments{
  \item{s}{ A string.}
  \item{is.input}{ Logical indicating which direction should be use to
  convert. If \code{TRUE}, e.g. an escaped linefeed ("\\n") will be transposed by a normal linefeed.
  If \code{FALSE}, the other direction is used. }
}
\details{
  You need not to use this in your code! Conversions are done 
  automatically in the read and output functions.
}
\value{
	The converted string.
}
%\references{ ~put references to the literature/web site here ~ }
\author{ Matthias Zschunke }

\keyword{ programming }% at least one, from doc/KEYWORDS
