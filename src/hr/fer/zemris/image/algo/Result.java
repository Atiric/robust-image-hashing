package hr.fer.zemris.image.algo;
/**
 * Class that represents one configuration for threshold
 * It is used for calculation of precision, recall and F1 mesures.
 * EXAMPLE : bits 4 blocks 4 blocks in row, 4 blocks in column for threshold 0.2 .
 * @author Armin
 *
 */
public class Result {
	
	private int truePositive;
	private int falsePositive;
	private int falseNegative;
	private int trueNegative;
	
	private double thresh;
	private double precision;
	private double recall;
	private double f1;
	
	
	public Result(int truePositive, int falsePositive, int falseNegative,
			int trueNegative, double thresh) {
		
		this.truePositive = truePositive;
		this.falsePositive = falsePositive;
		this.falseNegative = falseNegative;
		this.trueNegative = trueNegative;
		this.thresh = thresh;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(thresh);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Result other = (Result) obj;
		if (Double.doubleToLongBits(thresh) != Double
				.doubleToLongBits(other.thresh))
			return false;
		return true;
	}


	public void calculateParams(){
		precision = (double)truePositive/ ( truePositive + falsePositive);
		recall = (double)truePositive/ (truePositive + falseNegative);
		f1 = (double)2*(precision * recall)/ ( precision + recall);
	}
	
	public int getTruePositive() {
		return truePositive;
	}
	
	public int getFalsePositive() {
		return falsePositive;
	}
	
	public int getFalseNegative() {
		return falseNegative;
	}
	
	public int getTrueNegative() {
		return trueNegative;
	}
	
	
	@Override
	public String toString() {
		//show result for Jaccard distance
		return (1-thresh) + "\t" + precision + "\t" + recall + "\t" + f1 ;
	}


	public void addResult(int truePositive2, int trueNegative2,
			int falseNegative2, int falsePositive2) {
		truePositive += truePositive2;
		trueNegative += trueNegative2;
		falseNegative += falseNegative2;
		falsePositive += falsePositive2;
		
	}
	
	
	
	
	
	

}
