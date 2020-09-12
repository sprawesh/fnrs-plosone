package revision13;

import java.util.Random;

import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.AbstractRandomGenerator;

import umontreal.iro.lecuyer.probdist.InverseGaussianDist;

public class Generator extends AbstractRandomGenerator {
	
	private long seed;
	
	@Override
	public void setSeed(long seed) {
		this.seed = seed;
		
	}

	@Override
	public double nextDouble() {
		Random rand = new Random(seed);
		return rand.nextDouble(); 
	}
	
	public long refreshRNG() {
		Random rand = new Random(seed);
		return rand.nextLong();
	}
	
	public static void main(String[] args) {
		Generator gr = new Generator();
		InverseGaussianDist inv = new InverseGaussianDist(4, 3);
		
		gr.setSeed(764545);		
		for(int i = 0; i < 10; i++) {
			gr.setSeed(gr.refreshRNG()); 
			ZipfDistribution zpf = new ZipfDistribution(gr, 1000, 1.4);
			System.out.println(zpf.sample() + "\t" + Math.floor(inv.inverseF(gr.nextDouble()) + 1));   
		}
	}

}
