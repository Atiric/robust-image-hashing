package hr.fer.zemris.image.dataset;

import hr.fer.zemris.image.model.IDatesetHashHolder;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasetHashHolder implements IDatesetHashHolder {
	
	
	private Map<String, List<BitSet>> hashHolder;

	public DatasetHashHolder() {
		this.hashHolder = new HashMap<String, List<BitSet>>();
	}

	@Override
	public List<BitSet> getHashesForConfiguration(String conf) {
		if( !isValidKey(conf) ){
			throw new IllegalArgumentException("Key of configuration must start with keys specified in interface IDataSetHolder");
		}
		return hashHolder.get(conf);

	}

	@Override
	public void setHashesForConfiguration(String key, List<BitSet> hashes) {
		if( !isValidKey(key) ){
			throw new IllegalArgumentException("Key of configuration must start with keys specified in interface IDataSetHolder");
		}
		hashHolder.put(key, hashes);
	}

	@Override
	public String formKeyForConfiguration(boolean isNeedles, String key) {
		if( isNeedles ) {
			return IDatesetHashHolder.NEEDLES_PREFIX + "-" + key;
		}
		
		return IDatesetHashHolder.MODIFIED_PREFIX + "-" + key;
	}
	
	private boolean isValidKey( String key ) {
		return key.startsWith(IDatesetHashHolder.MODIFIED_PREFIX) || key.startsWith(IDatesetHashHolder.NEEDLES_PREFIX);
	}
	

}
