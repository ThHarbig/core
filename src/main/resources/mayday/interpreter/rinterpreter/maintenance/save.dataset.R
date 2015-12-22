#
#  R for Mayday, maintenance
#
#  Save the current data set in RData format.
#  This can be used to write and test applicable
#  functions outside Mayday.
#

#  --
#  Matthias Zschunke, 2005-02-26
#

#----------------------------------------------------

run<-function(DATA)
{
  f<-file.choose();
  
  data<-DATA; # need this to resolve reference
  
  save(data,file=f,ascii=TRUE);
}
