package hr.fer.zemris.image.model;

import hr.fer.zemris.image.dataset.Dataset;

import java.util.BitSet;
import java.util.List;
/**
 * Interface that specifies the {@link Dataset} holder of hashes.
 * @author Armin
 *
 */
public interface IDatesetHashHolder {
	/**
	 * Field that can be used for configuring key for needles(original) list of hashes.
	 */
	public static final String NEEDLES_PREFIX = "NEEDLES_PREFIX";
	/**
	 * Field that can be used for configuring key for modified list of hashes.
	 */
	public static final String MODIFIED_PREFIX = "MODIFIED_PREFIX";
	
	/**
	 * Specifies the format for creating base configuration key.
	 */
	public static final String BASE_KEY_FORMAT = "-bits-%d-blocks-%d";
	/**
	 * Getter for all hashes of one specified string that represents configuration defined by function formKeyForConfiguration;
	 * @param conf Key for hash, must start with specific prefix to be valid.
	 * @return List of hashes as {@link BitSet}
	 */
	List<BitSet> getHashesForConfiguration( String conf );
	/**
	 * Setter for hashes that are fetched by key.
	 * @param key Key for hash, must start with specific prefix to be valid.
	 * @param hashes list of hashes.
	 */
	void setHashesForConfiguration( String key, List<BitSet> hashes);
	/**
	 * Method that forms key that can be used in fetching of configuration.
	 * Key must be created by using this method because there can be different needles(originals)
	 * hashes for one configuration or it could be different hashes of modified images.
	 * @param isNeedles Flag that specifies weather key is used for fetching original hashes or modified hashes.
	 * @param key Key for configuration.
	 * @return String that represents key that will be used for fetching needles( originals) hashes or modified hashes.
	 */
	String formKeyForConfiguration(boolean isNeedles, String key);

}
