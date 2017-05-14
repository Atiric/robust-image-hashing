package hr.fer.zemris.image.dataset;

import hr.fer.zemris.image.model.IDatesetHashHolder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasetHashHolder implements IDatesetHashHolder {
	
	
	private Map<String, List<BitSet>> hashHolder;

	public DatasetHashHolder() {
		this.hashHolder = new HashMap<String, List<BitSet>>();
	}
	
	public DatasetHashHolder(Map<String, List<BitSet>> hashHolder) {
		this.hashHolder = hashHolder;
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
	

	public boolean serialize(String cachePath) {
		Path path = Paths.get(cachePath);
		try {
			FileOutputStream fos = new FileOutputStream(path.toFile());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(hashHolder);
			oos.close();
		} catch (Exception e) {
			System.out.println("Can't serialize, error while accesing filesystem or writing object");
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public static IDatesetHashHolder deSerialize( String cachePath ){
		Path path = Paths.get(cachePath);
		Map<String, List<BitSet>> hashHolder;
        try {
			FileInputStream fis = new FileInputStream(path.toFile());
			ObjectInputStream ois = new ObjectInputStream(fis);
			hashHolder = (Map<String, List<BitSet>>) ois.readObject();
	        ois.close();
        } catch (Exception e) {
        	System.out.println("Can't deserialize, error while accesing filesystem or writing object");
        	return null;
		}
        return new DatasetHashHolder(hashHolder);

	}
	

}
