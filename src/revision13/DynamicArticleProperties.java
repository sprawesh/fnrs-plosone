package revision13;

public class DynamicArticleProperties {
	
	private String uid;
	private int initialClicks;
	private long creationTime;
	private String category;
	private boolean mostPopular = false;
	private boolean pmostPopular = false;
	private boolean frontMark = false;
	private boolean breakingNews = false;
	private int currentClicks;
	private double pCurrentClicks;
	private double exponent;
	
	public DynamicArticleProperties(String uid, int initialClicks, long timeStamp){
		this.uid = uid;
		this.initialClicks = initialClicks;
		this.creationTime = timeStamp;
	}
	
	public String getID(){
		return uid;
	}
	
	public int getClicks(){
		return initialClicks;
	}
	
	public long getCreationTime(){
		return creationTime;
	}
	
	public double getPcurrentClicks() {
		return pCurrentClicks;
	}
	public void setCategory(String category){
		this.category = category;
	}
	
	public String getCategory(){
		return category;
	}
	
	public boolean getPmostPopular() {
		return pmostPopular;
	}
	
	public void setHardPopular(boolean b){
		this.mostPopular = b;
	}
	
	public void setPmostPopular(boolean b) {
		this.pmostPopular = b;
	}
	
	public boolean getPopularMark(){
		return mostPopular;
	}
	
	public void setFrontcat(boolean b){
		this.frontMark = b;
	}
	
	public boolean getFrontcat(){
		return frontMark;
	}
	
	public void setCurrentClicks(int clicks){
		this.currentClicks = clicks;
	}
	
	public int getCurrentClicks() {
		return currentClicks;
	}
	
	public void setPcurrentClicks(double pcl) {
		this.pCurrentClicks = pcl;
	}
	
	public void setBreakingNews(boolean br){
		this.breakingNews = br;
	}
	
	public boolean getBreakingNews(){
		return breakingNews;
	}
	
	public void setInitialClicks(int initial) {
		this.initialClicks = initial;
	}
	
	public void setExponent(double exponent) {
		this.exponent = exponent;
	}
	
	public double getExponent() {
		return exponent;
	}

}
