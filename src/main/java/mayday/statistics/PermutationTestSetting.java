package mayday.statistics;

import mayday.core.settings.typed.IntSetting;

public class PermutationTestSetting extends IntSetting 
{
//	protected IntSetting numberOfPermutations;

	public PermutationTestSetting() 
	{
		super("Number of Permutations","The number of permutations to be computed during testing",100,1,1000000,true,true);
//		super("Permutation Test");
//		setLayoutStyle(LayoutStyle.PANEL_HORIZONTAL);
//		addSetting(numberOfPermutations = new IntSetting("Number of Permutations","The number of permutations to be computed during testing",100));
	}

	public int getNumberOfPermutations()
	{
		return getIntValue();
//		return numberOfPermutations.getIntValue();
	}

	public PermutationTestSetting clone() {
		PermutationTestSetting cs = new PermutationTestSetting();
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}

}
