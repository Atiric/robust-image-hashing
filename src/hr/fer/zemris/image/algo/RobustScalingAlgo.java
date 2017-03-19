package hr.fer.zemris.image.algo;

import java.util.BitSet;


import hr.fer.zemris.image.model.IHashableImageAlgo;

public class RobustScalingAlgo implements IHashableImageAlgo {

	@Override
	public BitSet executeAlgo(HashableImage image) {
		int numBlocks = image.getBlocks().size();
		int predictedSize = HashableImage.BITS_FOR_COMPONENT * numBlocks;
		BitSet imgBitset = new BitSet(predictedSize+1);
		int bitsetPos = 0;
		for(int blockIndex = 0; blockIndex < numBlocks; blockIndex++){
			double[] meanOfBlock =  image.getMeanForBlock(blockIndex).val;
			image.saveToBitset(imgBitset, meanOfBlock, bitsetPos);
			//shift BitSet position number of values times number of bits per component
			bitsetPos +=HashableImage.BITS_FOR_COMPONENT;
		}
		
		
		return imgBitset;
	}


}
