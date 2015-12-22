#  
#  'R for Mayday' interface.
#  template file for applicable functions
#
#  //TODO:
#  Insert your function description here.
#

# -------------------------------------------------------------------



# //TODO: insert function identifier
# //TODO: insert specific arguments (Do not omit the 
#         DATA-argument!)
fun.identifier<-function(DATA, arg1, arg2) #etc.
{
  # //TODO: insert commands to extract the necessary
  #         information from the DATA-object
  #         e.g.: X<-extract.expression.matrix(DATA)



  # //TODO: insert R-code for the data analysis and transformation
  
  # Hint: You can send information about the current process via
  #       the 
  #                    send.process.state() 
  #       function. 
  #       The given message will be displayed in the RProgressDialog. 
  #       The current-value can be used to give a sort of percentage 
  #       of completion. 
  #       This value must then be in the range [0,1000]. If it exceeds
  #       the range or is NULL, the JProgressBar is set to indeterminate
  #       mode.


  # //TODO: create the result structure
  #         un-comment the necessary structures
  return(list(
     # dataset     = list( 
                           # annotation.name = ...,      #character; use this if you want to create a new data set
                           # annotation.quickinfo = ..., #character;
                           # annotation.info = ...,      #character;
                           # is.silent = ...,            #logical;
     #                   ),
     # mastertable = list(
                           # data.mode = ...,            #character; 
                           # transformation.mode = ...,  #character;
                           # probes = ...,               #vector of character; creates a new MasterTable with the given probes
                           # experiments = ...,          #vector of character; creates a new MasterTable with the given experiments
     #                   ),
     # probelists  = list(
                           # either:
                              # a list with probe list entries

                           # or: (It is recommended to use the following!)
                             # annotation.name = ...,          #vector of character; name for each probe list  (1)
                             # annotation.quickinfo = ...,     #vector of character; 1 entry for 1 probe list  (2)
                             # annotation.info = ...,          #vector of character; 1 entry for 1 probe list  (2)
                             # color = ...,                    #vector of rgb colors; if omitted rainbow colors are created (3)
                             # is.silent = ...,                #vector of logicals;                            (4) 
                             # is.sticky = ...,                #vector of logicals;                            (4)
                             # cluster.indicator = ...,        #vector of any;                                 (5)
                             # probes = ...,                   #vector of character;                           (5)
                             # unique.cluster.indicator = ..., #vector of any;                                 (5a)

                             # -------------------------------------------------------------------------------------
                             # short information:
                             # (1) If the length of this vector is 1 the string is used as prefix for each class name
                             #     else each string is used for each class
                             #
                             # (2) Length must be either 0, 1 or number of clusters (n = length(unique(cluster.indicator))
                             #     if 0: empty string is used
                             #     if 1: each class gets this (quick)info
                             #     if n: each class gets the corresponding (quick)info
                             #
                             # (3) Any length (len > 0) is accepted:
                             #     if len > n : only the first n colors are used
                             #     if len < n : the colors are 'recycled'
                             #     if omitted : rainbow colors are created 
                             #
                             # (4) Length must be either 0, 1 or number of clusters
                             #     if 0: is.silent=FALSE, is.sticky=TRUE for each probe list
                             #     if 1: recycled for each probe list
                             #     if n: each probe list gets the corresponding value
                             #
                             # (5) cluster.indicator cannot be omitted!!!
                             #     length is number of probes
                             #     
                             #
                             #

     #                   ),
     # probes      = list(),
     # mios        = list(
	 #	                   miotypes   = ...,  # a vector of class names 
	 #	                   miogroups  = ...,  # a vector of MIO group names
	 #	                   probes     = ...,  # a vector of Probe names
	 #	                   probelists = ...,  # a vector of Probelist names
	 #	                   values     = ...   # a matrix or 
	 #	             )  
     
  ))
}






