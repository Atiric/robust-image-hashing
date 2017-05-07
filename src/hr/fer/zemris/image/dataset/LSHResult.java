package hr.fer.zemris.image.dataset;

import java.util.BitSet;
import java.util.List;

public class LSHResult {
	
	private List<BitSet> localitySensitiveCandidates;
	private List<Integer> localitySensitiveCandidateIndexes;
	
	
	
	public LSHResult(List<BitSet> localitySensitiveCandidates,
			List<Integer> needleCandidates) {
		this.localitySensitiveCandidates = localitySensitiveCandidates;
		this.localitySensitiveCandidateIndexes = needleCandidates;
	}

	public List<BitSet> getLocalitySensitiveCandidates() {
		return localitySensitiveCandidates;
	}
	
	public List<Integer> getLocalitySensitiveCandidateIndexes() {
		return localitySensitiveCandidateIndexes;
	}
	

}
