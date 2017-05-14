package hr.fer.zemris.image.config;

import hr.fer.zemris.image.model.MetricType;

import java.util.List;

public final class Configuration {
	private List<Integer> bitsSize;
	private List<Integer> blockSize;
	private MetricType metricType;
	private Boolean isGrey;
	private String needlesPath;
	private String modifiedImages;
	private String cachePath;
	private Boolean recalculateHashes ;
	
	
	
	

	public List<Integer> getBitsSize() {
		return bitsSize;
	}



	public void setBitsSize(List<Integer> bitsSize) {
		this.bitsSize = bitsSize;
	}



	public List<Integer> getBlockSize() {
		return blockSize;
	}



	public void setBlockSize(List<Integer> blockSize) {
		this.blockSize = blockSize;
	}



	public MetricType getMetricType() {
		return metricType;
	}



	public void setMetricType(MetricType metricType) {
		this.metricType = metricType;
	}



	public Boolean getIsGrey() {
		return isGrey;
	}



	public void setIsGrey(Boolean isGrey) {
		this.isGrey = isGrey;
	}



	public String getNeedlesPath() {
		return needlesPath;
	}



	public void setNeedlesPath(String needlesPath) {
		this.needlesPath = needlesPath;
	}



	public String getModifiedImages() {
		return modifiedImages;
	}



	public void setModifiedImages(String modifiedImages) {
		this.modifiedImages = modifiedImages;
	}



	public String getCachePath() {
		return cachePath;
	}



	public void setCachePath(String cachePath) {
		this.cachePath = cachePath;
	}



	public Boolean getRecalculateHashes() {
		return recalculateHashes;
	}



	public void setRecalculateHashes(Boolean recalculateHashes) {
		this.recalculateHashes = recalculateHashes;
	}
	
	
	

}
