package extension;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class CUtilities implements CProjectVariables {

	protected FileWriter filewriter;
	protected static String[] trackarticles = new String[na];
	private static ArrayList<String> listids= new ArrayList<String>();
	
	public static void setIds() {
		String istr;
		for (int i = 0; i < 200; i++) 
		{
			istr = Integer.toString(i + 1);
			trackarticles[i] = "A".concat(istr);
			listids.add(trackarticles[i]);

		}
	}
	
	public ArrayList<String> getIds() {		
		return listids;
		
	}

	public void makecsv(String[] abp, String file) throws IOException {
		
		filewriter = new FileWriter(file);
		for(int j = 0; j < abp.length; j++)
		{
			filewriter.append(abp[j]);
			filewriter.append(',');
		}	    
		filewriter.append('\n');
	}
	
	public void writeRow(String[] abp, String[] chix, Double[] chcp) throws IOException {
		
		for(int i = 0; i < abp.length; i++) {
			boolean done = true;
			int j = 0;
			if(i==chix.length){
				break;
			}
			while(done) {
				if(abp[i].equals(chix[j])) {
					done =  false;
					filewriter.append(Double.toString(chcp[j]));
					filewriter.append(',');
				}
				j++;
			}
		}
		filewriter.append('\n');
	}

	public void closefile() {
		try	{
			filewriter.append('\n');
			filewriter.flush();
			filewriter.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}	
	}

	public static String[] readFile(String file_path)throws IOException {
		FileReader fr = new FileReader(file_path);
		BufferedReader txtReader = new BufferedReader(fr);
		
		int numberofLines = readLines(file_path);
		String[] txtData = new String[numberofLines];

		for(int i=0; i<numberofLines; i++)
		{
			txtData[i] = txtReader.readLine();
		}

		txtReader.close();
		return txtData;
	}
	
	static int readLines(String file_path) throws IOException {
		FileReader file_to_read = new FileReader(file_path);
		BufferedReader bf = new BufferedReader(file_to_read);

		int numberofLines = 0;
		while((bf.readLine()) !=null)
		{
			numberofLines++;
		}
		bf.close();
		return numberofLines;		
	}
	
	public void match(String[] original, String[] article, int index, Double[] dccx, double[] newclicks) {		
		int i = 0;
		int found = 0;
		while (found == 0) 
		{
			if (article[i].equals(original[index])) 
			{
				found = 1;				
				newclicks[index] = newclicks[index] + 1; // newclicks are in initial orders
				//double discount = modifiedCount(newclicks[index], pitr, reading_prob);
				double incount = 1;
				dccx[i] = dccx[i] + incount; // this is updated one.
			}
			i = i + 1;
		}
	}
	
	public void match(String[] abp, String[] chix, int index, Double[] chcp, double[] cinewclicks, int pitr, double reading_prob) 
			throws IOException {
		int i = 0;
		int found = 0;
		while (found == 0) {
			if (chix[i].equalsIgnoreCase(abp[index].toString())) {
				found = 1;
				cinewclicks[index] = cinewclicks[index] + 1;
				double disc = discount(cinewclicks[index], pitr, reading_prob);
				double incount = disc*1;
				chcp[i] = chcp[i] + incount;
			}
			i = i + 1;
		}
		count_sort(chcp, chcp.length, chix);
	}

	protected double discount(double revCount, int pitr, double reading_prob) {
		double ureputation;
		if(revCount>=2) {
			double reputation;
			reputation = new Double((pitr+1)*reading_prob/(10*(revCount-1))).doubleValue();
			ureputation = Math.min(1, reputation);
		} else {
			ureputation = 1;
		}			
		return ureputation;
	}
	
	public double discount(double RevCount, int Itr, double reading_prob, double exponent) {
		double ureputation; // = 3.9;
		// reputation score should not fall below 3
		if(RevCount>=1)
		{
			double reputation;
			reputation = new Double((Itr+1)*reading_prob/(10*RevCount)).doubleValue();
			//modified exponent
			ureputation = exponent*Math.min(1, reputation);								
		}
		else
			ureputation = exponent;
		//System.out.println(ureputation);
		return ureputation;			
	}
	
	//sorting of articles
		public void count_sort(Double[] count, int n, String b[]) {
			int temp;

			for(int i=0; i<n-1; i++)
			{
				for(int j=0; j<n-1-i; j++)
				{
					if(count[j]<count[j+1])
					{
						temp = count[j+1].intValue();
						count[j+1] = count[j];
						count[j] = (double) temp;
						// transforming corresponding articles
						String switchId = b[j+1];
						b[j+1] = b[j];
						b[j] = switchId;
					}			

				}
			}
		}
		
		public int readerIndex(boolean option, int m) {
			int index = 0;
			Random generator = new Random();			

			if(option) {
				// use randomIndex
				index = generator.nextInt(m);			
			}		
			else {
				// readerIndex
				index = readPattern(m);			
			}		
			return index;
		}

		public int readPattern(int n) {
			double[] d1 = new double[n];
			double[] d2 = new double[n];
			double[] sumd = new double[n+1];
			sumd[0] = -1;

			for(int j=0; j<n; j++) {
				d2[j] = j+1;
			}

			double temp;
			for(int j=1; j<=n-1; j++) {
				temp = d2[j-1];
				d2[j] = d2[j]+temp;

			}

			for(int i=0; i<n; i++) {
				d1[i]= (n-i)/d2[n-1];
				if(i>0)
				{
					temp = d1[i-1];
					d1[i] = d1[i]+temp;

				}
				sumd[i+1] = d1[i];
			}
			//selection of articles
			Random random = new Random();
			double rn = random.nextDouble();
			int done = 0;
			int i = 0;
			while(done==0) {
				if(rn>sumd[i]&&rn<sumd[i+1]) {
					done=1;
					break;
				}
				i=i+1;
			}
			return i;
		}
		
		public void singleUpdate(String id, String[] abp, double[] newclicks) {
			boolean notfound = true;
			int i = 0;
			while(notfound) {
				if(id.equals(abp[i].toString())){
					notfound = false;
					newclicks[i] = newclicks[i]+1;
				}
				i++;
			}
		}
		
		public int searchIndex(String trace, String[] target) {
			boolean notfound = true;
			int i = 0;
			while(notfound) {
				if(trace.equals(target[i])){
					notfound = false;					
				}
				i++;
			}
			return i;
		}
		
		public double psuccess(String article, double pappear, String[] pappeared)
		{
			int i=0;
			while (i<10)
			{
				if(article.equals(pappeared[i].toString()))
				{
					pappear = pappear + 1;
					break;
				}
				i=i+1;		
			}
			return pappear;
		}
		
		public Double[] numbers(Vector<Object> vector1) {
			Double[] rvector = new Double[vector1.capacity()];
			for (int i = 0; i < vector1.capacity(); i++) {
	    		rvector[i] = (Double) vector1.get(i);
	    	}
			return rvector;
		}

}
