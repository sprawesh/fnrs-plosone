package revision13;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TemporaryControl {
	
	private static final double numberOfReaders = 50000;//000; //lambda = .005	
	private static double[] exponent = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };	
	private static double[] exponentD = {0, 2.5, 2.7, 2.9, 3.1, 3.3, 3.5};	
	private static DecimalFormat format = new DecimalFormat("0.000");
	private static int idsForManipulation = 20;
	
	private static ArrayList<double[]> rsensitivity() {	
		ArrayList<double[]> probs = new ArrayList<double[]>();
		double[] prob1  = { 0.3, 0.4, 0.3 };// most popular, breaking news,  5 articles on the front page for each category
		double[] prob2  = { 0.3, 0.5, 0.2 };
		double[] prob3  = { 0.4, 0.3, 0.3 };
		double[] prob4  = { 0.4, 0.4, 0.2 };		
		double[] prob5 =  { 0.5, 0.3, 0.2 };
		double[] prob6  = { 0.6, 0.2, 0.2 };
		double[] prob7  = { 0.7, 0.2, 0.1 };
		double[] prob8 =  { 0.8, 0.1, 0.1 };
		probs.add(prob1); probs.add(prob2); probs.add(prob3); probs.add(prob4); probs.add(prob5);
		probs.add(prob6); probs.add(prob7); probs.add(prob8);
		
		return probs;
	}
	
	
	private static void runProgram(double[] prob, int it, int seed, StringBuilder gini_test) {
		ArrayList<ArrayList<ArrayList<Double>>> repdatapoints = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<ArrayList<Double>>> m2datapoints = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<ArrayList<Double>>> accLosses = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<ArrayList<Double>>> rentropies = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<ArrayList<Double>>> giniAll = new ArrayList<ArrayList<ArrayList<Double>>>();
		ArrayList<ArrayList<Double>> acclosses = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> ncclosses = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> jsds = new ArrayList<Double>();
		ArrayList<Double> hvalues = new ArrayList<Double>();
		double lambda = 0.0003;			
		
		Generator gr = new Generator();		
		ArrayList<Double> rexp = new ArrayList<Double>(20); 
		
		for(int i = 1; i <= 10; i+=2) {
			gr.setSeed(seed); 
			
			List<DynamicArticleProperties> articleList = new LinkedList<DynamicArticleProperties>();
			List<DynamicArticleProperties> initialTimeSort = new LinkedList<DynamicArticleProperties>();
			List<DynamicArticleProperties> countSort = new ArrayList<DynamicArticleProperties>();
			ArrayList<Double> losses = new ArrayList<Double>();				
			//ArrivalProcess arrivals = new ArrivalProcess((int) numberOfReaders, lambda, prob);
			
			ArrivalProcess arrivals = new ArrivalProcess(gr, (int) numberOfReaders, lambda, prob);	
			arrivals.seedArticles(articleList,  initialTimeSort, countSort, idsForManipulation);
			arrivals.updateArticles(countSort, exponent[i-1], idsForManipulation); 
			rexp.add(exponent[i-1]); 
			
			if( i == 1) {
				ArrayList<ArrayList<Double>> hard = arrivals.getHSimulationPoints(); /// arraylist with iteration and m1 value.
				ArrayList<ArrayList<Double>> m2hard = arrivals.gethm2Plot();
				ArrayList<ArrayList<Double>> accLoss = arrivals.getaccLosses();
				ArrayList<ArrayList<Double>> giniMetricH = arrivals.getGiniValH(); 
				
				repdatapoints.add(hard); m2datapoints.add(m2hard); //accLosses.add(accLoss);
				giniAll.add(giniMetricH);
				writesingColumn(arrivals.getJHSDistortion(), it); //we can skip distortion for hardcutoff
			}
			
			ArrayList<ArrayList<Double>> prob1 = arrivals.getPSimulationPoints();				
			ArrayList<ArrayList<Double>> m2prob = arrivals.getpm2Plot();
			
			//ArrayList<ArrayList<Double>> paccLoss = arrivals.getaccLosses();
			ArrayList<ArrayList<Double>> paccLoss = arrivals.getExactLosses();
			
			//ArrayList<ArrayList<Double>> pjsdmetric = arrivals.getJSDistortion();
			ArrayList<ArrayList<Double>> pjsdmetric = arrivals.getExactJSD();
			ArrayList<ArrayList<Double>> giniMetric = arrivals.getGiniVal();
			
			repdatapoints.add(prob1); m2datapoints.add(m2prob); 
			accLosses.add(paccLoss); rentropies.add(pjsdmetric); 
			giniAll.add(giniMetric);
			
			double loss = 0; //arrivals.getAverageAccuracyLoss(arrivals.getaccLosses()); // CHANGE IT
			
			//System.out.println("loss : " + format.format(loss)); 				
			losses.add((double) i-1); losses.add(loss);
			// we are printing both previous loss metric and normalized loss metric, git test
			acclosses.add(losses);
			
			double distortion = arrivals.getAverageJSD();
			jsds.add(distortion);
			System.out.println("-----------new exponent " + (i-1) + "----------"); 
		}
		writeFile(repdatapoints, "M1" + "-" + it + ".csv", rexp, null);
		writeFile(m2datapoints, "M2" + "-" + it + ".csv", rexp, null);
		
		writeFile(accLosses, "accLossMetric" + "-" + it + ".csv", rexp, null); 		
		writeFile(rentropies, "JSDMetric" + "-" + it + ".csv", rexp, null); 
		writeFile(giniAll, "Gini" + "-" + it + ".csv", rexp, gini_test);
		
		writeMetric(acclosses, jsds, "Metrics" + "-" + it + ".csv");
		//writeMetric(ncclosses, jsds, "nMetrics" + "-" + it + ".csv");
	}	
	

	//Main method to execute multi-threading implementation of simulation
	public static void main(String[] args) {	
		
		ArrayList<double[]> probs = rsensitivity();
		int len = probs.size()-1;
		
		StringBuilder gini_test = new StringBuilder(); //Gini test data
		Random seedGn = new Random();
		
		//we only need multiple loops here to generate data for t-test.
		int pr = 0;
		for(int it = 0; it < 50; it++) {
			
			System.out.println("iteration : " + (it+1)); 
			int seed = seedGn.nextInt();		
			//for experimenting with multiple reading probabilities we will have to insert another character after the completion of simulation for..
			//..a reading probability
			for(int i = len-1; i >= len-1; i--) {		//int i = len; i >= 0; i-- for all reading probabilities
				runProgram(probs.get(i), i+1, seed, gini_test);			
				pr = i+1;
			}			
		}
		writeTest(gini_test, pr);
		System.out.println("done!");
		
	}
	
	private static void writesingColumn(ArrayList<Double> data, int it) {
		try {
			
			BufferedWriter bw  = new BufferedWriter(new FileWriter("hardDistortion"+"-"+it+".csv")); 
			for(int i = 0; i < data.size(); i++) {
				bw.write(Double.toString(data.get(i)));	
				bw.newLine();
			}
			bw.flush(); bw.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}
	

	private static void writeMetric(ArrayList<ArrayList<Double>> acclosses, ArrayList<Double> jsds,
			String path) {		
		
		try {
			BufferedWriter bw  = new BufferedWriter(new FileWriter(path));			
			bw.write("exponent" + "," + "b0"+"," + "b0.1"+"," + "b0.2"+"," + "b0.3"+"," + "b0.4"+"," + "b0.5"+"," + "b0.6"+"," + "b0.7"+"," + "b0.8" + "," + "b0.9"+"," + "b1"+","); 
			bw.newLine();
			
			for(int i = 0; i < acclosses.size(); i++) {
				double exponent = acclosses.get(i).get(0);
				double accloss = acclosses.get(i).get(1);
				double jsd = jsds.get(i);
				
				StringBuilder sb = new StringBuilder();
				for(int j = 0; j <= 10; j++) {
					double beta = (double)j/(double)10;
					double metric = (beta)*accloss + (1-beta)*jsd;
					sb.append(metric + ","); 
				}
				
				bw.write(exponent + "," + sb); 
				bw.newLine();
			}
			
			bw.flush(); bw.close();
			
		} catch (IOException e) {			
			e.printStackTrace();
		}		
		
	}
	
	private static void updateGini(StringBuilder all, StringBuilder gini_last) {
		
		//Get the last row
		//append it to gini_last
		int temp = all.lastIndexOf("\n");
		int last = all.length()-1;
		//char s = all.charAt(last);
		all.deleteCharAt(last);
		
		temp = all.lastIndexOf("\n");
		String val = all.substring(temp+1);
		String x = val.concat("\n");
		gini_last.append(x);
		
	}
	
	public static void writeTest(StringBuilder sb, int it) {
		//write Gini test file here.
		String path = "Gini-" + it + "-t-test.csv";
		try {
			BufferedWriter bw  = new BufferedWriter(new FileWriter(path));
			//header
			bw.write("run,hard,exp0,exp2,exp4,exp6,exp8\n");
			bw.write(sb.toString());
			bw.flush(); bw.close(); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void writeFile(ArrayList<ArrayList<ArrayList<Double>>> repdatapoints, String path, ArrayList<Double> rexp, StringBuilder gini) {
		try {
			StringBuilder sb = new StringBuilder();
			BufferedWriter bw  = new BufferedWriter(new FileWriter(path));
			int l = repdatapoints.size();
			
			if(path.contains("Metric")) {				
				sb.append("iteration" + ","); 				
				for(int i = 0; i <= l-1; i++) {					
					sb.append("exp" + rexp.get(i) + ",");
				}
			}
			else {				
				sb.append("iteration" + "," + "hard"+",");
				for(int i = 0; i < l-1; i++) {					
					sb.append("exp"+ rexp.get(i) + ","); 
				}
			}						
			sb.append('\n');
			
			
			for(int i = 0; i < repdatapoints.get(0).size()-1; i++) {
				
				for( int j = 0; j < l; j++) { //hard + exp1 + exp2 +...
					if( j == 0) {
						sb.append(repdatapoints.get(j).get(i).get(0) + ",");  
					}
					sb.append(repdatapoints.get(j).get(i).get(1) + ","); 
				}
				sb.append('\n');
			}
			
			if(gini != null) {
				//get the last row for Gini coefficient. 
				updateGini(sb, gini);
			}
			
			bw.write(sb.toString());
			bw.flush(); bw.close(); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
}
