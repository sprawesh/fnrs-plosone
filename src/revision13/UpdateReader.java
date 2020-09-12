package revision13;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

import extension.CProjectVariables;
import arrivalUtilities.BasicUtilities;
import umontreal.iro.lecuyer.probdist.InverseGaussianDist;

public class UpdateReader implements CProjectVariables {
	
	private static InverseGaussianDist inv = new InverseGaussianDist(meanArticles, 3); // newupdate = (3.2, 3) mu = (3,8) and lambda = (2,6.2)
	private int seed;
	Random rand;
	private BasicUtilities bsu = new BasicUtilities();
	private DynamicArticleProperties pdp;
	private int narticles;
	private static int totalCount;
	
	
	public UpdateReader(int seed) throws NotStrictlyPositiveException {		
		this.seed = seed;
	}
	
	// write method for reader behavior. it involves selection from breaking news, front page categorized and most popular articles.
	// number of articles are selected according to inverse guassian distribution, readers are also allowed to navigate through different sections.
	public void frontPageSelection(double[] fth, ArrayList<DynamicArticleProperties> mpa, LinkedList<DynamicArticleProperties> fda, 
			LinkedList<DynamicArticleProperties> ltempcat, ArrayList<LinkedList<DynamicArticleProperties>> allArticles, 
			ArrayList<DynamicArticleProperties> pmpa) {
		//ltempcat, all list of articles fda, front page list, mpa, most popular 
		rand = new Random(seed);		
		narticles = 1+ (int) Math.floor(inv.inverseF(rand.nextDouble())+1);
		totalCount += narticles;
		if( narticles > 10) {
			narticles = 10;
		}
		// first article from front page or most popular list.
		// generate probability.		
		for (int i = 0; i < narticles; i++) {
			double sp = rand.nextDouble();
			seed = rand.nextInt();
			if(i == 0){
				if(sp <= fth[0]){ 					 
					DynamicArticleProperties dp = updateCount(mpa, seed, true, 10, false, allArticles, pmpa); //random selection from most popular	
					updateInAll(dp, allArticles);
				} else if((sp > fth[0]) & (sp <= fth[0] + fth[1])) {					
					DynamicArticleProperties dp = updateCount(fda, seed, false, 20, false, allArticles, pmpa);
					updateInAll(dp, allArticles);
				}
				else {					
					updateCount(fda, seed, false, 5, true, allArticles, pmpa);
					//System.out.println("need to be done");
				}
			} else {
				// some probability for most popular
				// equal probability of selection other categories and then select in decreasing order.
				sp = rand.nextDouble();
				if( sp <= fth[0]) {
					
					DynamicArticleProperties dp = updateCount(mpa, seed, true, 10, false, allArticles, pmpa);
					updateInAll(dp, allArticles);
				} else {
					int catindex = rand.nextInt(BasicUtilities.getCategories().size());
					LinkedList<DynamicArticleProperties> scat = allArticles.get(catindex);
					seed = refreshRNG();
					int index = bsu.readPattern(10, seed); // 10 articles subsequently.
					//UPDATE OF THIS PAGE, as of now it appears no modification is needed.
					//Debug and print output
					DynamicArticleProperties dp = scat.get(index);					
					int count = dp.getCurrentClicks() + 1;
					double pCount = dp.getPcurrentClicks() + 1; // APPEARS TO BE PROBLEM
					dp.setCurrentClicks(count);
					dp.setPcurrentClicks(pCount); 
					//System.out.println(" exta previous : " + temp + "current : " + dp.getCurrentClicks());
				}
			}			
		}				
	}
	
	public void updateInAll(DynamicArticleProperties dp, ArrayList<LinkedList<DynamicArticleProperties>> allArticles) {
		
		int catindex = catnum(dp.getCategory()); 		
		LinkedList<DynamicArticleProperties> updating = allArticles.get(catindex);
		for(int j = 0; j < updating.size(); j++){
			if(dp.getID().equalsIgnoreCase(updating.get(j).getID())){
				updating.get(j).setCurrentClicks(dp.getCurrentClicks()); 
				updating.get(j).setPcurrentClicks(dp.getPcurrentClicks()); // new line of code for probabilistic update, this may be the reason for anomaly observed earlier
				//make sure it produces correct output.
			}
		}// Now for probabilistic
		
		if(pdp != null) {
			int pCatindex = catnum(pdp.getCategory());		
			LinkedList<DynamicArticleProperties> pUpdating = allArticles.get(pCatindex);
			for(int j = 0; j < pUpdating.size(); j++) {
				if(pdp.getID().equalsIgnoreCase(pUpdating.get(j).getID())) {
					pUpdating.get(j).setPcurrentClicks(pdp.getPcurrentClicks()); 
				}
			}
		}
		
	}
	
	private int catnum(String category) {
		int catindex = 0;
		ArrayList<String> categories = BasicUtilities.getCategories();	
		for (int i = 0; i < categories.size(); i++) {
			if(category.equalsIgnoreCase(categories.get(i))) {
				catindex = i;
				break;
			}
		}
		return catindex;
	}

	public DynamicArticleProperties updateCount(List<DynamicArticleProperties> dap, int seed, boolean mp, int displayed, 
			boolean categorized, ArrayList<LinkedList<DynamicArticleProperties>> allArticles, ArrayList<DynamicArticleProperties> pmpa) {
		
		DynamicArticleProperties dp = null;
		Random random = new Random(seed);
		if(mp) {
			int index = random.nextInt(displayed);
			dp = dap.get(index);
			pdp = pmpa.get(index);
			
			double pCount = pdp.getPcurrentClicks() + 1; // for probabilistic
			pdp.setPcurrentClicks(pCount); 
		} else if (categorized) 
		{
			pdp = null;
			int catindex = random.nextInt(BasicUtilities.getCategories().size());
			LinkedList<DynamicArticleProperties> scat = allArticles.get(catindex);
			int index = bsu.readPattern(displayed, seed);
			dp = scat.get(index);
			// update probabilistic count
			double pCount = dp.getPcurrentClicks() + 1;
			dp.setPcurrentClicks(pCount); 
		} else {
			pdp = null;
			int index = bsu.readPattern(displayed, seed); 
			dp = dap.get(index);
			
			double pCount = dp.getPcurrentClicks() + 1;
			dp.setPcurrentClicks(pCount); 
		}
				
		int count = dp.getCurrentClicks() + 1;	
		dp.setCurrentClicks(count);		
		return dp;				
		
	}
	
	private int refreshRNG() {
		int seed = rand.nextInt();
		return seed;
	}
	
	public int getNewClicks() {
		return narticles;
	}
	
	public void printTotal() {
		System.out.println("totalCount : " + totalCount);
	}

}
