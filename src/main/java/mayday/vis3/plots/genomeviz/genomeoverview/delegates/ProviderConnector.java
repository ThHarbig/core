package mayday.vis3.plots.genomeviz.genomeoverview.delegates;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.Strand;
import mayday.vis3.ValueProvider;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.genomeviz.AbstractLogixVizModel;
import mayday.vis3.plots.genomeviz.EnumManagerGHM.SplitView;
import mayday.vis3.plots.genomeviz.genomeoverview.GenomeOverviewModel;
import mayday.vis3.plots.genomeviz.genomeoverview.LogixVizColorProvider;

public abstract class ProviderConnector {

	/**
	 * returns an double array which contains the maximum, minimum or mean of values from colorprovider for all probes
	 * @param last
	 * @param split
	 * @param exp
	 * @return value (mean/max/min) or NaN.
	 */
	public static void getBothValues_exp_transp(long first, long last, SplitView split,int exp, ValueProvider transparencyProvider, double[] valArray, AbstractLogixVizModel model) {
		/* transparency for forward strand*/
		double transparencyMeanPlus = Double.NaN;
		double transparencyMinPlus = Double.NaN;
		double transparencyMaxPlus = Double.NaN;
		/* transparency for backward strand*/
		double transparencyMeanMinus = Double.NaN;
		double transparencyMinMinus = Double.NaN;
		double transparencyMaxMinus = Double.NaN;

		/* experiment value for forward strand*/
		double minPlus = Double.NaN;
		double maxPlus = Double.NaN;
		double meanPlus = Double.NaN;		
		/* experiment value for backward strand*/
		double minMinus=Double.NaN;
		double maxMinus=Double.NaN;
		double meanMinus=Double.NaN;

		List<LocusGeneticCoordinateObject<Probe>> lolgcp = model.getData().getProbes(first, last, Strand.UNSPECIFIED);
		if (!lolgcp.isEmpty()) {

			transparencyMinPlus = Double.POSITIVE_INFINITY;
			transparencyMaxPlus = Double.NEGATIVE_INFINITY;
			transparencyMeanPlus=0.;
			/* transparency for backward strand*/
			transparencyMinMinus = Double.POSITIVE_INFINITY;
			transparencyMaxMinus = Double.NEGATIVE_INFINITY;
			transparencyMeanMinus=0.;
			/* experiment value for forward strand*/
			minPlus = Double.POSITIVE_INFINITY;
			maxPlus = Double.NEGATIVE_INFINITY;
			meanPlus=0.;
			/* experiment value for backward strand*/
			minMinus=Double.POSITIVE_INFINITY;
			maxMinus=Double.NEGATIVE_INFINITY;
			meanMinus=0.;

			int probeNumberPlus=0;
			int probeNumberMinus=0;
			ViewModel vm = transparencyProvider.getViewModel();

			for (LocusGeneticCoordinateObject<Probe> olgcp : lolgcp) {
				Probe pb = olgcp.getObject();
				double vals[] = vm.getProbeValues(pb);
				double expVal=vals[exp];
				double transparencyValue=transparencyProvider.getValue(pb);
				
				if (olgcp.getStrand().similar(Strand.PLUS)){
					
					if(minPlus>expVal) minPlus=expVal;
					if(maxPlus<expVal) maxPlus=expVal;
					meanPlus+=expVal;

					if(transparencyMinPlus>transparencyValue)transparencyMinPlus=transparencyValue;
					if(transparencyMaxPlus<transparencyValue)transparencyMaxPlus=transparencyValue;
					transparencyMeanPlus+=transparencyValue;
					probeNumberPlus++;
					
				};
				if(olgcp.getStrand().similar(Strand.MINUS)){					

					if(minMinus>expVal)minMinus=expVal;
					if(maxMinus<expVal)maxMinus=expVal;
					meanMinus+=expVal;

					transparencyMeanMinus+=transparencyValue;
					if(transparencyMinMinus>transparencyValue)transparencyMinMinus=transparencyValue;
					if(transparencyMaxMinus<transparencyValue)transparencyMaxMinus=transparencyValue;
					probeNumberMinus++;
				}
			}

			if(probeNumberMinus>0){
				transparencyMeanMinus=transparencyMeanMinus/(double)probeNumberMinus;
				meanMinus=meanMinus/(double)probeNumberMinus;
			} else {
				transparencyMeanMinus=Double.NaN;
				transparencyMinMinus=Double.NaN;
				transparencyMaxMinus=Double.NaN;
				meanMinus=Double.NaN;
				maxMinus=Double.NaN;
				minMinus=Double.NaN;
			}

			if(probeNumberPlus>0){
				transparencyMeanPlus=transparencyMeanPlus/(double)probeNumberPlus;
				meanPlus=meanPlus/(double)probeNumberPlus;
			} else {
				transparencyMeanPlus=Double.NaN;
				transparencyMinPlus=Double.NaN;
				transparencyMaxPlus=Double.NaN;
				meanPlus=Double.NaN;
				maxPlus=Double.NaN;
				minPlus=Double.NaN;
			}

		}


		if(split.equals(SplitView.mean)){
			valArray[0]=meanPlus;
			valArray[1]=meanMinus;
			valArray[2]=transparencyMeanPlus;
			valArray[3]=transparencyMeanMinus;
		} else if(split.equals(SplitView.min)){
			valArray[0]=minPlus;
			valArray[1]=minMinus;
			valArray[2]=transparencyMinPlus;
			valArray[3]=transparencyMinMinus;
		} else if(split.equals(SplitView.max)){
			valArray[0]=maxPlus;
			valArray[1]=maxMinus;
			valArray[2]=transparencyMaxPlus;
			valArray[3]=transparencyMaxMinus;
		}
	}

	public static void getValues_exp_transp(long first, long last,
			SplitView split, int exp,
			ValueProvider transparencyProvider, double[] expVals,
			GenomeOverviewModel model, Strand strand) {

		/* transparency for forward strand*/
		double transparencyMean = Double.NaN;
		double transparencyMin = Double.NaN;
		double transparencyMax = Double.NaN;

		/* experiment value for forward strand*/
		double experimentMin = Double.NaN;
		double experimentMax = Double.NaN;
		double experimentMean = Double.NaN;

		List<LocusGeneticCoordinateObject<Probe>> lolgcp = model.getData().getProbes(first, last, strand);		
		
		if (!lolgcp.isEmpty()) {
			transparencyMin = Double.POSITIVE_INFINITY;
			transparencyMax = Double.NEGATIVE_INFINITY;
			transparencyMean=0.;

			/* experiment value for forward strand*/
			experimentMin = Double.POSITIVE_INFINITY;
			experimentMax = Double.NEGATIVE_INFINITY;
			experimentMean=0.;

			ViewModel vm =  transparencyProvider.getViewModel();

			for (LocusGeneticCoordinateObject<Probe> olgcp : lolgcp) {
				Probe pb = olgcp.getObject();
				double vals[] = vm.getProbeValues(pb);
				double expVal=vals[exp];

				if(experimentMin>expVal)experimentMin=expVal;
				if(experimentMax<expVal)experimentMax=expVal;
				experimentMean+=expVal;

				double transparencyValue=transparencyProvider.getValue(pb);
				transparencyMean+=transparencyValue;
				if(transparencyMin>transparencyValue)transparencyMin=transparencyValue;
				if(transparencyMax<transparencyValue)transparencyMax=transparencyValue;
			}

			if(lolgcp.size()>0){
				transparencyMean=transparencyMean/(double)lolgcp.size();
				experimentMean=experimentMean/(double)lolgcp.size();
			} else {
				transparencyMean=Double.NaN;
				transparencyMin=Double.NaN;
				transparencyMax=Double.NaN;
				experimentMean=Double.NaN;
				experimentMax=Double.NaN;
				experimentMin=Double.NaN;
			}
		}
		
		if(split.equals(SplitView.mean)){
			expVals[0]=experimentMean;
			expVals[1]=transparencyMean;
		} else if(split.equals(SplitView.min)){
			expVals[0]=experimentMin;
			expVals[1]=transparencyMin;
		} else if(split.equals(SplitView.max)){
			expVals[0]=experimentMax;
			expVals[1]=transparencyMax;
		}
	}

	public static double getTransparencyValue(Set<Probe> list, ValueProvider transparencyProvider, SplitView split) {
		/* transparency for forward strand*/
		double transparencyMean = Double.NaN;
		double transparencyMin = Double.NaN;
		double transparencyMax = Double.NaN;

		if(!list.isEmpty()){
			transparencyMin = Double.POSITIVE_INFINITY;
			transparencyMax = Double.NEGATIVE_INFINITY;
			transparencyMean=0.;

			for (Probe pb : list) {
				double transparencyValue=transparencyProvider.getValue(pb);
				transparencyMean+=transparencyValue;
				if(transparencyMin>transparencyValue)transparencyMin=transparencyValue;
				if(transparencyMax<transparencyValue)transparencyMax=transparencyValue;
			}

			transparencyMean=transparencyMean/(double)list.size();
		}

		if(split.equals(SplitView.mean)){
			return transparencyMean;
		} else if(split.equals(SplitView.min)){
			return transparencyMin;
		} else if(split.equals(SplitView.max)){
			return transparencyMax;
		}

		return Double.NaN;
	}

	public static Color getTransparencyColorForExperiment(Graphics2D g2D, Double colVal, Double transpVal, LogixVizColorProvider colProv,
			double minTransparency, double maxTransparency, boolean isTransparent, boolean invert) {
		if(!colVal.isNaN()){
			Color col = colProv.getColor(colVal);
			Color transpCol = col;
			if(isTransparent){
				if(!transpVal.isNaN()){
					int alpha = (int)DataMapper.mapValue(minTransparency, maxTransparency, transpVal, 0, 255);
					if(invert)alpha = 255-alpha;
					transpCol = new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
				}
			}
			return transpCol;
		}
		return null;
	}

	public static Color getTransparentColor(Set<Probe> set, Color colorValue, Color transparencyColor, double minTransparency, double maxTransparency,
			ValueProvider transparencyProvider, SplitView split, boolean invert){
		double transparencyValue = Double.NaN;
		transparencyValue = getTransparencyValue(set, transparencyProvider, split);
		if (transparencyValue != Double.NaN) {
			int alpha = (int) DataMapper.mapValue(minTransparency,
					maxTransparency, transparencyValue, 0, 255);

			if (invert)
				alpha = 255-alpha;

			transparencyColor = new Color(colorValue.getRed(), colorValue
					.getGreen(), colorValue.getBlue(), alpha);
		}
		return transparencyColor;
	}

	public static Color getTransparentColor(int alpha, Color colorValue, boolean invert){
		if(alpha>=0){
			if(invert)alpha = 255-alpha;
			return new Color(colorValue.getRed(), colorValue.getGreen(), colorValue.getBlue(), alpha);
		}
		return null;
	}

	public static int getAlpha(Set<Probe> set, double minTransparency, double maxTransparency, SplitView split, ValueProvider transparencyProvider) {
		double transparencyValue = Double.NaN;
		transparencyValue = getTransparencyValue(set, transparencyProvider, split);
		if (transparencyValue != Double.NaN) {
			int alpha = (int) DataMapper.mapValue(minTransparency, maxTransparency, transparencyValue, 0, 255);
			return alpha;
		}
		return -1;
	}
}
