#  
#  Imputation. (k-nearest neighbor)
#  
#  Guess missing values with the function
#  impute.knn() from the package {impute}
#
#  Requires package {impute}!
#

#  --
#  Matthias Zschunke, 06/2005
#

# -------------------------------------------------------------------



run<-function(DATA, k=10, rowmax=0.5, colmax=0.8, maxp=1500)
{
  X<-extract.expression.matrix(DATA)

  library(impute)
  
  #need to transpose, 'cause impute expects the genes as cols
  X$data<-t(impute.knn(t(X$data),k=k,rowmax=rowmax,colmax=colmax,maxp=maxp))

  return(list(probes=X))
}






