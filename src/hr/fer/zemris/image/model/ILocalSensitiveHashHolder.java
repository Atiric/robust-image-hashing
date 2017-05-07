package hr.fer.zemris.image.model;

import hr.fer.zemris.image.dataset.LSHResult;

/**
 * Interface that specifies the locality sensitive hashing holder for one Dataset holder 
 * @author Armin
 *
 */
public interface ILocalSensitiveHashHolder extends IDatesetHashHolder {
	
	void findCandidatesFromCache(int bits, int blocks);

	LSHResult getLocalitySensitiveCandidates(int bits,
			int blocks, int needleIndex);
	

}
