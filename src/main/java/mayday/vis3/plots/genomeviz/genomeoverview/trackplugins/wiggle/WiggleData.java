package mayday.vis3.plots.genomeviz.genomeoverview.trackplugins.wiggle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import mayday.core.io.csv.ParsedLine;
import mayday.core.io.csv.ParserSettings;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.natives.LinkedDoubleArray;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.chromosome.Chromosome;

public class WiggleData implements SettingChangeListener {
	
	protected HashMap<Chromosome, File> wigData;
	protected ChromosomeSetContainer csc;
	protected FilesSetting src;
	protected StringSetting spc;
	protected LinkedDoubleArray cache;
	protected Chromosome cacheC;
	
		
	public WiggleData(FilesSetting input, StringSetting species, ChromosomeSetContainer csc) {
		input.addChangeListener(this);
		species.addChangeListener(this);
		this.csc=csc;
		this.src=input;
		this.spc=species;
		stateChanged(new SettingChangeEvent(input));
	}

	@Override
	public void stateChanged(SettingChangeEvent e) {
		List<String> fnames = src.getFileNames();
		if (fnames==null)
			return;
		wigData = new HashMap<Chromosome, File>();
		for (String fname : fnames) {
			File f = new File(fname);
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(f));
				String line = br.readLine();
				
				String[] header = line.split("[\\s]+");			
				
				for (String h : header) {
					if (h.startsWith("chrom")) {
						Chromosome c = csc.getChromosome(
								SpeciesContainer.getSpecies(spc.getStringValue()), 
								h.substring("chrom=".length()));
						wigData.put(c, f);
						break;
					}
				}

			} catch (FileNotFoundException e1) {
				System.err.println("Could not read wiggle file:");
				e1.printStackTrace();
			} catch (IOException e2) {
				System.err.println("Could not read wiggle file:");
				e2.printStackTrace();
			}
		}
	}
	
	public LinkedDoubleArray getWiggle(Chromosome c) {
		if (c==cacheC && cache!=null)
			return cache;
		
		cache=null;
		cacheC=c;
		
		File f = wigData.get(c);
		if (f==null)
			return null;
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			
			String[] header = line.split("[\\s]+");

			boolean isVariable = header[0].equals("variableStep");
			
			long start=0;
			int step=1;
			int span=1;
			
			for (String h : header) {
				if (h.startsWith("start")) {
					start = Long.parseLong(h.substring("start=".length()));
				} else if (h.startsWith("step")) {
					step = Integer.parseInt(h.substring("step=".length()));
				} else if (h.startsWith("span")) {
					span = Integer.parseInt(h.substring("span=".length()));
				}
			}
			
			ParsedLine pl = new ParsedLine(line, new ParserSettings());
			LinkedDoubleArray data = new LinkedDoubleArray(10000);
			data.ensureSize(c.getLength()+1); // +1 because wiggle files are 1 based
			
			if (isVariable) {
				while ((line=br.readLine())!=null) {
					pl.replaceLine(line);
					start = Long.parseLong(pl.get(0));
					double val = Double.NaN;
					try {
						val = Double.parseDouble(pl.get(1));
					} catch (Exception e) {}
					for (int i=span; i!=0; i--) 
						data.set(start+i-1, val);
				}
				cache=data;
			} else {				
				while ((line=br.readLine())!=null) {					
					pl.replaceLine(line);
					double val = Double.NaN;
					try {
						val = Double.parseDouble(pl.get(0));
					} catch (Exception e) {}
					for (int i=span; i!=0; i--) 
						data.set(start+i-1, val);
					start+=step;
				}
				cache=data;
			}

		
		} catch (FileNotFoundException e1) {
			System.err.println("Could not read wiggle file:");
			e1.printStackTrace();
		} catch (IOException e2) {
			System.err.println("Could not read wiggle file:");
			e2.printStackTrace();
		}

		return cache;
		
	}

}
