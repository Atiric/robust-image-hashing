package hr.fer.zemris.image.model;
/**
 * Interface that specifies the calculation of {@link MetricType}.
 * @author Armin
 *
 */
public interface IMetricIncrementCalculator {
	
	
	double nextMetricIncrement();
	
	boolean hasNext();
	
	void setRange(double downLimit, double upLimit, double increment);
	
	void setRangePercentageIncrement(double downLimit, double upLimit, double percentageIncrement);
	
	MetricType getCurrentMetric();
	
	void setCurrentMetric(MetricType metricType);
	
	

}
