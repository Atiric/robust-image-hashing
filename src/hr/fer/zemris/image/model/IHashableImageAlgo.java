package hr.fer.zemris.image.model;

import java.util.BitSet;

import hr.fer.zemris.image.algo.HashableImage;
/**
 * Interface that defines the hash algorithm for image.
 * @author Armin
 *
 */
public interface IHashableImageAlgo {
	/**
	 * Method that does the algorithm on image.
	 * @param image Image that is processed.
	 * @return {@link BitSet} representation of hash.
	 */
	BitSet executeAlgo(HashableImage image);

}
