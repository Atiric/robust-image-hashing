package hr.fer.zemris.image.algo;

import java.util.BitSet;

import hr.fer.zemris.image.model.IHashableImageAlgo;

public class NaiveSearchAlgo implements IHashableImageAlgo {

	@Override
	public BitSet executeAlgo(HashableImage image) {
		return image.getBitSetFromMat(image.getImgMat());
	}
	

}
