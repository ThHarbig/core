package mayday.vis3.plots.genomeviz.genomeoverview;

public class Spaces {

    private double valuespace=1;  //value space
    private int userspace=1;   //user space
    
    
    public Integer getFunctionValueOf(Double x)
    {
        return (int)(userspace*(x/valuespace));
    }
    
    public void setValueSpace(double L)
    {
        this.valuespace = L;
    }
    
    public void setUserSpace(int w)
    {
        this.userspace = w;
    }

}
