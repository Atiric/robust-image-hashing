package hr.fer.zemris.image.algo;

import java.util.BitSet;

import hr.fer.zemris.image.model.IHashableImageAlgo;

public class AvgHashMock  implements IHashableImageAlgo{

	@Override
	public BitSet executeAlgo(HashableImage hImg) {
		for(int i = 0; i < hImg.getBlocks().size(); i++ ){
			//System.out.printf("Za blok %s srednja vrijednost je %s\n", hImg.getBlocks().get(i), hImg.getMeanForBlock(i));
			//HashableImage.showResult( hImg.getSubmatrixBlock(i) );
		}
		hImg.getBlockAsBitset(0);
		
		System.out.println("Jaccardova sličnost je " + Comparison.compareTwoBitsets(hImg.getBlockAsBitset(0), hImg.getBlockAsBitset(0)) );
		return null;
		
	}	

}
