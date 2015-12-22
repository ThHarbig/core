package mayday.mpf.filters;

import mayday.core.math.stattest.StatTestResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.mpf.FilterBase;
import mayday.mpf.options.OptClasses;
import mayday.mpf.options.OptStatTest;
import mayday.mpf.options.OptString;

public class StatTest extends FilterBase{

	private OptClasses Groups = new OptClasses("Classes","Select classes for testing. Depending on the kind of test, one,two or more classes are allowed.","");
	private OptStatTest method = new OptStatTest(); 

	private OptString pmioName = new OptString("MIO group name for p-value", "the name of the Meta Information Object that later contains the p-value", "p-value");


	public StatTest() {
		super(1,1);

		pli.setName("Statistical Test");
		pli.setIdentifier("PAS.mpf.stattest");
		pli.setAuthor("Florian Battke");
		pli.setEmail("battke@informatik.uni-tuebingen.de");
		pli.setAbout("Wrapper for core statistical tests");
		pli.replaceCategory("Statistics");

		Options.add(method);
		Options.add(Groups);
		Options.add(pmioName);

		Groups.setEditable(false);
	}

	@SuppressWarnings({ "deprecation" })
	public void execute() throws Exception {
		OutputData[0] = InputData[0];

		String experimentnames="";
		for (int i=0; i!=InputData[0].getNumberOfExperiments(); ++i) {
			String ename= InputData[0].getProbeList().getDataSet().getMasterTable().getExperimentName(i);
			if (ename==null)
				ename="Exp. "+i;
			experimentnames += ","+ename ;
		}
		Groups.ValueFromString(experimentnames);
		Groups.showDialog();
		
		StatTestResult res = method.getStatTest().runTest(InputData[0].getProbeList().getAllProbes(), Groups.getModel());
		
		MIManager mim = OutputData[0].getProbeList().getDataSet().getMIManager();
		res.getPValues().setName(pmioName.Value);
		mim.addGroup(res.getPValues());
		
		if (res.getRawScore()!=null)
			mim.addGroupBelow(res.getRawScore(), res.getPValues());
		
		for (MIGroup mg : res.getAdditionalValues())
			mim.addGroupBelow(mg, res.getPValues());

	}
	
}
