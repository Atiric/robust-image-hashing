package hr.fer.zemris.image.dataset;

import hr.fer.zemris.image.algo.HashableImage;
import hr.fer.zemris.image.model.IDatesetHashHolder;
import hr.fer.zemris.image.model.ILocalSensitiveHashHolder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class LocalSensitiveHashHolder  implements ILocalSensitiveHashHolder{
	
	private IDatesetHashHolder datasetHashHolder;
	private HashMap<Integer, Set<Integer>> currentFoundCandidates;
	private String lastSearched;

	public LocalSensitiveHashHolder(IDatesetHashHolder datesetHashHolder) {
		this.datasetHashHolder = datesetHashHolder;
	}

	@Override
	public List<BitSet> getHashesForConfiguration(String conf) {
		return datasetHashHolder.getHashesForConfiguration(conf);
	}

	@Override
	public void setHashesForConfiguration(String key, List<BitSet> hashes) {
		datasetHashHolder.setHashesForConfiguration(key, hashes);
		
	}

	@Override
	public String formKeyForConfiguration(boolean isNeedles, String key) {
		return datasetHashHolder.formKeyForConfiguration(isNeedles, key);
	}

	
	
	public HashMap<Integer, Set<Integer>> startLSH(int bits, int blocks){
		String modifiedKey = formKeyForConfiguration(false, String.format(BASE_KEY_FORMAT, bits, blocks));
		String needleKey = formKeyForConfiguration(true, String.format(BASE_KEY_FORMAT, bits, blocks));
		List<BitSet> modifiedHashes = getHashesForConfiguration(modifiedKey);
		//first we add all hashes from original set with needles
		List<BitSet> searchHashes = new ArrayList<BitSet>( getHashesForConfiguration(needleKey) );
		int sizeModified = modifiedHashes.size();
		int sizeNeedles = searchHashes.size();
		//then we add all modified hashes for O(N) pass of LSH
		searchHashes.addAll(modifiedHashes);
		
		HashMap<Integer, Set<Integer>> candidates = new HashMap<Integer, Set<Integer>>();
		
		for (int wantedBand = 0; wantedBand < bits*blocks; wantedBand++) {
			HashMap<Integer, Set<Integer>> buckets = new HashMap<Integer, Set<Integer>>();
			//System.out.println("Band "+trazeniPojas+" je obradjen!" );
			for ( int searchId = 0; searchId < searchHashes.size(); searchId++){
				BitSet hash = searchHashes.get(searchId);
				int bandHash = getHashedPartOfBand(hash, wantedBand, bits, blocks);
				
				Set<Integer> similaryHashed;
				if ( buckets.containsKey(bandHash) ){
					similaryHashed = buckets.get(bandHash);
					for (Integer tempId : similaryHashed) {
						//we are interested only for candidates of modified hashes
						if( tempId >= sizeNeedles && searchId < sizeNeedles ) {
							initializeSetForHash(candidates, searchId);
							candidates.get(searchId).add(tempId - sizeNeedles);
						}
						//we are interested only for candidates of modified hashes
						if( searchId >= sizeNeedles && tempId < sizeNeedles ) {
							initializeSetForHash(candidates, tempId);
							candidates.get(tempId).add(searchId - sizeNeedles);
						}
						
					}
				} else {
					similaryHashed = new TreeSet<Integer>();
				}
				
				similaryHashed.add(searchId);
				
				
				buckets.put(bandHash, similaryHashed);
			}
			buckets.clear();
		}
		return candidates;
	}
	/**
	 * Metoda koja preracunava hash za zadani pojas odnosno za raspon indexa
	 *  u K bitnom sazetku na nacin
	 * <p>[wantedBand * BROJ_POJASEVA, (wantedBand+1) * BROJ_POJASEVA></p>
	 * @param fingerprint {@link BitSet} prezentacija trazenog sazetka
	 * @param wantedBand index trazenog banda, krece od 0.
	 * @return vraca hash prezentaciju trazenog banda.
	 */
	private int getHashedPartOfBand(BitSet fingerprint,int wantedBand, int bits, int blocks){
		int hash = 17;
		if ( wantedBand < 0 || wantedBand >= bits*blocks){
			throw new IllegalArgumentException("Broj trazenog pojasa za hash nije ispravan.");
		}
		int NUM_BITS = HashableImage.BITS_FOR_COMPONENT;
		for (int i = wantedBand * NUM_BITS; i < (wantedBand+1) * NUM_BITS ; i++) {
			if(fingerprint.get(i)){
				hash = hash*31*i + "1".hashCode();
			} else {
				hash = hash*31*i + "0".hashCode();
			}
		}
		
		return hash;
	}
	
	private void initializeSetForHash(HashMap<Integer, Set<Integer>> candidates, Integer id ){
		if(!candidates.containsKey(id) ){
			candidates.put(id, new TreeSet<Integer>());
		}
	}

	@Override
	public void findCandidatesFromCache(int bits, int blocks) {
		this.currentFoundCandidates = startLSH(bits, blocks);
		this.lastSearched = String.format(BASE_KEY_FORMAT, bits, blocks);
		
	}

	@Override
	public LSHResult getLocalitySensitiveCandidates(int bits, int blocks,
			int needleIndex) {
		String searchingFor = String.format(BASE_KEY_FORMAT, bits, blocks);
		
		if( ! searchingFor.equals(lastSearched) ) {
			System.out.println("Candidates not searched yet, starting search for : " + searchingFor);
			findCandidatesFromCache(bits, blocks);
		}
		List<Integer> needleCandidates = new ArrayList<Integer>( currentFoundCandidates.get(needleIndex));

		String modifiedKey = formKeyForConfiguration(false, String.format(BASE_KEY_FORMAT, bits, blocks));
		List<BitSet> modifiedHashes = getHashesForConfiguration(modifiedKey);
		List<BitSet> lshCandidates = new ArrayList<BitSet>();
		for( Integer index: needleCandidates ){
			lshCandidates.add( modifiedHashes.get(index));
		}
		
		return new LSHResult(lshCandidates, needleCandidates);
	}

}
