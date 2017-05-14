package hr.fer.zemris.image.metric;

import hr.fer.zemris.image.model.IMetricIncrementCalculator;
import hr.fer.zemris.image.model.MetricType;

public class MetricIncrementCalculator implements IMetricIncrementCalculator {
	
	private MetricType metricType;
	@SuppressWarnings("unused")
	private double downLimit;
	private double upLimit;
	private double increment;
	private double current;
	
	public MetricIncrementCalculator(MetricType metricType, double downLimit, double upLimit, double increment) {
		this.metricType = metricType;
		setRange(downLimit, upLimit, increment);
	}

	public MetricIncrementCalculator(MetricType metricType) {
		this.metricType = metricType;
	}

	@Override
	public double nextMetricIncrement() {
		if( !hasNext() ){
			System.err.println("No next increment");
			throw new IllegalStateException();
		} 
		this.current = current + increment;
		return  current;
	}

	@Override
	public boolean hasNext() {
		if ( Double.compare(current + increment, upLimit) > 0 || metricType == null) {
			return false;
		}
		return true;
	}

	@Override
	public void setRange(double downLimit, double upLimit, double increment) {
		this.current = downLimit;
		this.downLimit = downLimit;
		this.upLimit = upLimit;
		this.increment = increment;
	}

	@Override
	public void setRangePercentageIncrement(double downLimit, double upLimit,
			double percentageIncrement) {
		this.current = downLimit;
		this.increment = percentageIncrement * ( upLimit - downLimit);
		if( Double.compare( this.increment, 0.0d ) < 0 ) {
			throw new IllegalArgumentException("Check your limits and percentage increment");
		}
		this.downLimit = downLimit;
		this.upLimit = upLimit;
		
	}

	@Override
	public MetricType getCurrentMetric() {
		return this.metricType;
	}

	@Override
	public void setCurrentMetric(MetricType metricType) {
		this.metricType = metricType;
		
	}

	public double getCurrentValue() {
		return current;
	}

}
