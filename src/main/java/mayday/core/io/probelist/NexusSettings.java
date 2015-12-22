/*
 *  Created on Sep 19, 2004
 *
 */
package mayday.core.io.probelist;

/**
 * Setting for the Nexus Export Plugin
 * <p>
 * 
 * @author Markus Riester
 * @version 0.1
 */
public class NexusSettings {
    private String[] alphabet;
    private boolean addAssumptionsBlock = true;
    private boolean addPaupBlock = true;
    /*
     * addPaupBlock options:
     */
    private int PB_BandB = 2;  // 0 = no branch & bound, 1 =  b & b, 2 = auto (b&b if < 16 taxas)
    private int PB_BandB_auto_taxalimit = 16;
    private boolean PB_NJ = true;
    
    private boolean createPhylipTree = true;
    
    /**
     * @return Returns the alphabet.
     */
    public String[] getAlphabet() {
        return alphabet;
    }
    
    /**
     * @param alphabet The alphabet to set.
     */
    public void setAlphabet(String[] alphabet) {
        this.alphabet = alphabet;
    }
    
    /**
     * Returns a the symbols string for the Paup data block
     * @return a string containing the alphabet
     */
    public String getAlphabetString() {
        String result = "";
        for (int i = 0; i != alphabet.length; ++i) {
            result += alphabet[i];
        }
        return result;
    }
    
    /**
     * @return Returns the addAssumptionsBlock.
     */
    public boolean getAddAssumptionsBlock() {
        return addAssumptionsBlock;
    }
    
    /**
     * @param addAssumptionsBlock The addAssumptionsBlock to set.
     */
    public void setAddAssumptionsBlock(boolean addAssumptionsBlock) {
        this.addAssumptionsBlock = addAssumptionsBlock;
    }
    
    /**
     * @return Returns addPaupBlock.
     */
    public boolean getAddPaupBlock() {
        return addPaupBlock;
    }
    /**
     * @param addPaupBlock The addPaupBlock to set.
     */
    public void setAddPaupBlock(boolean addPaupBlock) {
        this.addPaupBlock = addPaupBlock;
    }
    /**
     * @return Returns the pB_BandB.
     */
    public int getPB_BandB() {
        return PB_BandB;
    }
    /**
     * @param bandB The pB_BandB to set.
     */
    public void setPB_BandB(int bandB) {
        PB_BandB = bandB;
    }
    /**
     * @return Returns the pB_BandB_auto_taxalimit.
     */
    public int getPB_BandB_auto_taxalimit() {
        return PB_BandB_auto_taxalimit;
    }
    /**
     * @param bandB_auto_taxalimit The pB_BandB_auto_taxalimit to set.
     */
    public void setPB_BandB_auto_taxalimit(int bandB_auto_taxalimit) {
        PB_BandB_auto_taxalimit = bandB_auto_taxalimit;
    }
    /**
     * @return Returns the pB_NJ.
     */
    public boolean getPB_NJ() {
        return PB_NJ;
    }
    /**
     * @param pb_nj The pB_NJ to set.
     */
    public void setPB_NJ(boolean pb_nj) {
        PB_NJ = pb_nj;
    }
    /**
     * @return Returns the createPhylipTree.
     */
    public boolean getCreatePhylipTree() {
        return createPhylipTree;
    }
    /**
     * @param createPhylipTree The createPhylipTree to set.
     */
    public void setCreatePhylipTree(boolean createPhylipTree) {
        this.createPhylipTree = createPhylipTree;
    }
}
