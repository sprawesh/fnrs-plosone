package extension;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.math3.distribution.ZipfDistribution;

public class CExponent extends CUtilities implements CProjectVariables {
	
	double[] clickcounts = new double[np];
	double[] amodified = new double[na];
	double[] hshare = new double[na];
	
	public void fileupdate(String[] abp, Double[] hcp, String[] hix) throws IOException {
		double sum = 0;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < abp.length; i++) {
			sum += hcp[i];
		}
		for(int i = 0; i < abp.length; i++) {
			boolean done = true;
			int j = 0;
			while(done) {
				if(hix[j].equals(abp[i])) {
					done = false;
					filewriter.append(Double.toString(hcp[j]) + ",");
					hshare[i] = hcp[j]/sum;
					sb.append(Double.toString(hcp[j]) + ",");
				}
				j++;
			}
		}
		filewriter.append('\n'); 
		System.out.println(sb); 
	}
	
	public int ZipfReader(int top, String[] a) {
		
	int index = top + new ZipfDistribution(a.length - top, ZEXPONENT).sample();
	return index;
		
    }
	
	public void read(String[] abp, String[] dd, int ps, Double[] cc, double[] newclicks, double reading_prob, double pu) throws IOException {
		
		if (pu < reading_prob) {
			int index = readerIndex(CProjectVariables.randomModel, ps);
			cc[index] = cc[index]+1;
			//singleUpdate(dd[index], abp, newclicks);
		} else {
			//int prandomIndex = pGenerator.nextInt(abp.length - ps)+ ps;
			int ZipfIndex = ZipfReader(ps, dd);
			while(ZipfIndex == cc.length) {
				ZipfIndex = ZipfReader(ps, dd);
			}			
			System.out.println(ZipfIndex);
			cc[ZipfIndex] = cc[ZipfIndex] + 1;
			//cc[prandomIndex] = cc[prandomIndex] + 1;
			//singleUpdate(dd[ZipfIndex], abp, newclicks);
		}
		
	}
	
	public double[] topCounts(String[] abp, String[] art, Double[] hcp, int ps) {
		
		double[] sum = new double[ps];
		for(int i = 0; i < ps; i++) {
			sum[i] = hcp[searchIndex(abp[ps], art)];
		}
		return sum;		
	}

	public void read(String[] abp, int pitr, double[] cinewclicks,
			Double[] chcp, int ps, String[] chix, double reading_prob, double pu) {
		if(pu < reading_prob) {
			int index = readerIndex(CProjectVariables.randomModel, ps);
			int found = searchIndex(chix[index], abp);
			cinewclicks[found] = cinewclicks[found] + 1;
			double reputation = discount(cinewclicks[found], pitr, reading_prob);
			double count = 1*reputation;
			chcp[index] = chcp[index] + count;
			//System.out.println(a[index] +"," + art[index] + "," + found);
			
		} else {
			int ZipfIndex = ZipfReader(ps, chix);
			int found = searchIndex(chix[ZipfIndex], abp);
			cinewclicks[found] = cinewclicks[found] + 1;
			double reputation = discount(cinewclicks[found], pitr, reading_prob);
			double count = 1*reputation;
			chcp[ZipfIndex] = chcp[ZipfIndex] + count;			
		}
		
	}

	public Double[] feedback(int index, double reading_prob, int pitr,
			double[] pnewclicks, double exp, String[] article, Double[] counts) {		
		
		Double[] expcounts = new Double[na];
		Double[] nexpcounts = new Double[na];
		double modr = 0;
		
		for(int i=0; i<amodified.length; i++) {
			amodified[i]=exp;
		}
		// commented the section below to run simulation without influence limiter algorithm.
		/*if(pitr <= inspection_in) {
				amodified[index] = discount(pnewclicks[index], pitr, reading_prob, exp);
				modr = amodified[index];
				} else {
					amodified[index] = modr; 
				}*/	
		int done = 0;
		for(int j=0; j<counts.length; j++) {
			double power;
			int j1 = 0;
			while(done == 0){
				if(trackarticles[j].equals(article[j1])) {
					done = 1;
					power = amodified[j];
					expcounts[j] = Math.pow(counts[j1], power);
				}
				j1++;
			}
			done = 0;			
		}
		//now get in current format
		done = 0;
		for(int i=0; i<counts.length;i++) {
			int j = 0;
			while(done == 0) {
				if(article[i].equals(trackarticles[j])) { 
					done = 1;
					nexpcounts[i] = expcounts[j];
				}
				j++;
			}
			done = 0;
		}
		
		return nexpcounts;
	}
	
	public Double[] revFeedBack(Double[] modcount, String[] indices, double a) {
		
		Double[] rexpcounts = new Double[na];
		int done = 0;
		
		for(int i=0; i<modcount.length;i++) {
			int j = 0;
			while (done == 0) {
				if(indices[i].equals(trackarticles[j])){
					done = 1;
					//rexpcounts[i]=Math.pow(modcount[i], 1/amodified[j]);
					rexpcounts[i]=Math.pow(modcount[i], 1/a);
				}
				j++;
			}
			done = 0;
		}
		return rexpcounts;
	}
	
	public Double[] maintainPrevious(String[] abp, String[] dd, Double[] iCounts, Vector<Object> vector) {
		Double[] cc = new Double[na];
		for(int i = 0; i < na; i++){
			cc[i] = (Double) vector.get(i);
			if (cc[i] == 0) {
				boolean done = true;
				int j = 0;
				while (done) {
					if(dd[i].equals(abp[j])){
						done = false;
						cc[i] = iCounts[j];
					}
					j++;
				}
			}
			}					
		// code for keeping the earlier count if 0		
		return cc;		
	}
	

}
