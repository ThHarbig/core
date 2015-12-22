#  
#  R for Mayday, maintenance
#
#  This function goes on and on and ...
#

#  --
#  Matthias Zschunke; 06-2005

#---------------------------------------------------------

endlos<-function(DATA)
{
  X<-extract.expression.matrix(DATA)

  # multiplying matrices
  while(TRUE)
  {
    X$data<-X$data %*% t(X$data)
  }

  return(NULL);
}






