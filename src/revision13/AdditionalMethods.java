package revision13;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.StatUtils;

import cc.mallet.util.Maths;

public class AdditionalMethods {
	
	private static boolean once = true;
	
	public double distortionHMeasure(HashMap<String, Double[]> sample1, ArrayList<String> ids, boolean klornot, BufferedWriter fr) {
		// think it in terms of share distortion.
		// for hardcutoff use index = 2.
		double[] initialc = new double[sample1.size()];
		double[] updated = new double[sample1.size()];
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		try {			
			Iterator<Entry<String, Double[]>> it = sample1.entrySet().iterator();
			int i = 0;
			while(it.hasNext()) {			
				String key = it.next().getKey(); 			
				boolean initial  = false;
				for(String id : ids) {
					if(id.equalsIgnoreCase(key)) {
						initial = true;
						break;
					}
				}
				
				if(initial) {
					initialc[i] = sample1.get(key)[0]; sb1.append(initialc[i] + ", ");
					updated[i] = sample1.get(key)[2];  sb2.append(updated[i] + ", ");  
				} else {
					initialc[i] = (double) 0; sb1.append(initialc[i] + ", "); 
					updated[i] = sample1.get(key)[2]; sb2.append(updated[i] + ", "); 
				}
				i++;
			}
			if(once) {
				//fr.write(sb1.toString());
				//fr.newLine();
				once = false;
			}
			
			//fr.write(sb2.toString());
			//fr.newLine();
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		double initialc1 = StatUtils.sum(initialc);
		double updated1 = StatUtils.sum(updated);
		
		for (int i = 0; i < updated.length; i++) {
			initialc[i] = initialc[i]/initialc1;
			updated[i] = updated[i]/updated1;
		}
		
		if(klornot) {
			return Maths.klDivergence(initialc, updated);
		} else {
			return Maths.jensenShannonDivergence(initialc, updated);
		}		
	}

}