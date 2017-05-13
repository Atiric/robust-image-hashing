package hr.fer.zemris.image.algo;

import hr.fer.zemris.image.dataset.Dataset;
import hr.fer.zemris.image.dataset.DatasetHashHolder;
import hr.fer.zemris.image.dataset.LSHResult;
import hr.fer.zemris.image.dataset.LocalSensitiveHashHolder;
import hr.fer.zemris.image.metric.MetricIncrementCalculator;
import hr.fer.zemris.image.model.IDatesetHashHolder;
import hr.fer.zemris.image.model.IHashableImageAlgo;
import hr.fer.zemris.image.model.ILocalSensitiveHashHolder;
import hr.fer.zemris.image.model.MetricType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.opencv.core.Core;


public class Comparison {
	
	private static double THRESHOLD = 0.0;
	/**
	 * Map that contains indexes of images as keys and similarity as value, first key is current image that is being compared.
	 */
	private static Map<Integer, Double> results;
	
	private static List<Path> imagePaths;
	private static ArrayList<HashableImage> images;
	private static MetricIncrementCalculator metricIncrementCalculator;
	/**
	 * Computes Jaccard similarity of two bitsets that represents hashes.
	 * @param imgBitSet1 First {@link BitSet} 
	 * @param imgBitSet2 First {@link BitSet} 
	 * @return Similarity of two image bit sets.
	 */
	public static double compareTwoBitsetsJaccard( BitSet imgBitSet1, BitSet imgBitSet2){
		// length is logical size of bitset which means index of highest set index of bit
//		if( imgBitSet1.size() != imgBitSet2.size() ){
//			throw new IllegalArgumentException("BitSets not the same size");
//		}
		int counterX = 0;
		int counterY = 0;
		
		//TODO what about Z rows? combination 0-0
		for(int i = 0; i < imgBitSet1.size(); i++){
			if( imgBitSet1.get(i)  && imgBitSet2.get(i) ){
				counterX++;
			} else if( !imgBitSet1.get(i) && !imgBitSet2.get(i) ){
				continue;
			}
			counterY++;
		}
		
		return (double) counterX/counterY;
		
	}
	/**
	 * Method that compares two bitsets and calculates the number of differencing bits in two {@link BitSet}.
	  * @param imgBitSet1 First {@link BitSet} 
	 * @param imgBitSet2 First {@link BitSet} 
	 * @return Hamming distance of two image bit sets.
	 */
	public static int compareTwoBitsetsHamming( BitSet imgBitSet1, BitSet imgBitSet2){
		int numDifferentBits = 0;
		//TODO check for changes in number of RGB components of one pixel
		//size of hash
		int size = HashableImage.NUM_BLOCK_COL * HashableImage.NUM_BLOCK_ROW * HashableImage.BITS_FOR_COMPONENT;
		
		for(int bitIndex = 0; bitIndex < size; bitIndex++){
			if(imgBitSet1.get(bitIndex) != imgBitSet2.get(bitIndex) ){
				numDifferentBits++;
			}
		}
		return numDifferentBits;
	}
	
	
	/**
	 * Computes Jaccard similarity of two {@link BitSet} that are grouped with length  defined in {@link HashableImage} BITS_FOR_COMPONENT.
	 * @param imgBitSet1 First {@link BitSet} that represents hash.
	 * @param imgBitSet2 Second {@link BitSet}  that represents hash.
	 * @return Similarity of two image bit sets.
	 */
	public static double compareTwoBitsetsGroup( BitSet imgBitSet1, BitSet imgBitSet2){
		
		int counterX = 0;
		int counterY = 0;
		
		int nBitComp = HashableImage.BITS_FOR_COMPONENT;
		int predictedGroupSize =  HashableImage.NUM_BLOCK_COL * HashableImage.NUM_BLOCK_ROW;
//		System.out.println("PredictedSize : " + predictedSize);
//		int bitsize = imgBitSet1.size();
//		System.out.println("Predicted Size:" + predictedSize + "bitsetSize" + bitsize);
		for(int groupIndex = 0; groupIndex < predictedGroupSize; groupIndex++){
			boolean validComparison = true;//if the value of pixel is the same in defined length then the pixel has the same value  and is valid
			for(int i = groupIndex*nBitComp; i < groupIndex*nBitComp + nBitComp  ;i++ ){
				if( !( imgBitSet1.get(i)  == imgBitSet2.get(i) ) ){
					validComparison = false;
					break;
				}
					
			}
			counterY++;
			if( validComparison ){
				counterX++;
			}
		}
		
		
		return (double) counterX/counterY;
		
	}
	
	/**
	 * Not ready for comparison yet. Work in progress.
	 * @param candidates List of candidates for comparison.
	 * @param indexLine define the current hash that is compared.
	 * @param hammingDistance define the maximum Hamming  
	 * distance.
	 * @return Currently returns number of hashes within distance.
	 */
	public static List<Integer> checkWithinHammingDistance(List<BitSet> candidates,
			int indexLine, int hammingDistance) {
		//TODO adapt for algorithm
		int counter = 0;
		BitSet current = candidates.get(indexLine);
		List<Integer> similar = new ArrayList<Integer>();
		
		for (int i = 0; i < candidates.size(); i++) {
			
			if( i == indexLine ) continue;
			BitSet testHash = candidates.get(i);
			int numDifferentBits = compareTwoBitsetsHamming(current, testHash);
			
			
			if( numDifferentBits >= 0 && numDifferentBits <= hammingDistance ){
				counter++;
				similar.add(i);
				Comparison.results.put(i, (double) numDifferentBits);
			}
			
		}
		System.out.println("Pronađeno " + counter + " kandidata!");
		return similar;
	}
	
	public static List<Integer> checkWithinHammingDistance(LSHResult lshResult,
		int hammingDistance, BitSet current) {
		int counter = 0;
		List<Integer> similar = new ArrayList<Integer>();
		List<BitSet> candidates = lshResult.getLocalitySensitiveCandidates();
		List<Integer> candidateIndexes = lshResult.getLocalitySensitiveCandidateIndexes();
		
		for (int i = 0; i < candidates.size(); i++) {
			BitSet testHash = candidates.get(i);
			int numDifferentBits = compareTwoBitsetsHamming(current, testHash);
			
			
			if( numDifferentBits >= 0 && numDifferentBits <= hammingDistance ){
				counter++;
				similar.add(candidateIndexes.get(i));
				Comparison.results.put(candidateIndexes.get(i), (double) numDifferentBits);
			}
			
		}
		System.out.println("Pronađeno " + counter + " kandidata!");
		return similar;
	}
	
	
	/**
	 * Method that checks list of candidates. All candidates are compared to hash
	 * that is defined by indexLine, and are greater than specified threshold.
	 * @param candidates List of all hashes that are being compared,
	 *  		including the fixed that will be compared to all others.
	 * @param indexLine Index of fixed hash in list of candidates.
	 * @param threshold Threshold that defines similar hashes.
	 * 		 Hashes that have greater similarity than defined threshold are similar to fixed hash.
	 * @return List of hashes that are similar in respect to fixed hash in list of candidates and
	 * 		are Jaccard similarity is greater than specified threshold.
	 */
	public static List<Integer> checkWithinJaccardDistance(List<BitSet> candidates,
			int indexLine, double threshold) {
		int counter = 0;
		BitSet current = candidates.get(indexLine);
		List<Integer> similar = new ArrayList<Integer>();
		for (int i = 0; i < candidates.size(); i++) {
			
			if( i == indexLine ) continue;
			BitSet test = candidates.get(i);
			double similarity = compareTwoBitsetsGroup(current, test);
			if ( Double.compare(threshold, similarity)  < 0 ){
				counter++;
				similar.add(i);
				Comparison.results.put(i, similarity);
			}
		}
		
		System.out.println("Pronađeno " + counter + " kandidata!");
		
		return similar;
	}
	
	
	
	public static double compareTwoHasable(HashableImage hashImg1, HashableImage hashImg2){
		BitSet bitSet1 =  hashImg1.getBitSetFromMat(hashImg1.getImgMat());
		BitSet bitSet2 =  hashImg2.getBitSetFromMat(hashImg2.getImgMat());
		return compareTwoBitsetsGroup(bitSet1, bitSet2);
		
	}
	
	public static void compareBlockSimilarity(HashableImage current, HashableImage mock){
		for(int i = 0; i < current.getBlocks().size(); i++){
			BitSet bitSetMockBlock = mock.getBlockAsBitset(i);
			BitSet bitSetRealBlock = current.getBlockAsBitset(i);
			if ( i == 4 ){
				HashableImage.showResult(current.getSubmatrixBlock(i));
				HashableImage.showResult(mock.getSubmatrixBlock(i));
			}
			
			double similarity = Comparison.compareTwoBitsetsGroup(bitSetMockBlock, bitSetRealBlock);
			System.out.println("Sličnost bloka " + i + " je " +  similarity );
		}
	}
	
	
	
	static void mockTestBlockComparison(int nBlockRow,int nBlockCol,boolean isGray){
		String realImgPath = "test1/003_0001-test.jpg";
		String mockImgPath = "test/003_0001.jpg";//
		HashableImage imgReal = new HashableImage( false, realImgPath);
		HashableImage imgMock = new HashableImage( false, mockImgPath);
		
		double whole = Comparison.compareTwoHasable(imgReal, imgMock);
		
		System.out.println("Ukupna jaccardova sličnost od " + realImgPath + " i " + mockImgPath +"je " + whole );
		System.out.println("______________________");
		
		
		compareBlockSimilarity(imgReal, imgMock);
	}
	
	
	
	/**
	 * Main method that is run.
	 * @param args Array of parameters as string
	 * 				 
	 */
	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Comparison.results = new HashMap<>();
		Comparison.metricIncrementCalculator = new MetricIncrementCalculator( MetricType.HAMMING_DISTANCE);
		
		String needlesPath = "test";
		boolean isGray = true ;
		
		int[] arrayOfBitsSize = new int[]{4, 6, 7};
		// define the blocks as 2 per row 2 per col, 3 per row 3 per col ... etc
		int[] blockNums = new int[] { 4, 16, 32};

		Dataset dataset = new Dataset("modified_images");//"TPobfuscated"
		Comparison.imagePaths = dataset.getImagePaths();
		Comparison.images = new ArrayList<HashableImage>();
		//mock definitions of block, needed for initializing mock HashableImages,
		//blocks are recalculated as specified by arrayOfBitsSize and blockNums
		HashableImage.NUM_BLOCK_COL = 1;
		HashableImage.NUM_BLOCK_ROW = 1;
		
		List<String> needles = findPathsOfFiles(needlesPath);
		for(int i = 0; i < imagePaths.size(); i++){
			images.add(new HashableImage(isGray, imagePaths.get(i).toString()));
		}
		IHashableImageAlgo algo = new RobustScalingAlgo();
		//without lsh IDatesetHashHolder datasetHashHolder = new DatasetHashHolder();
		IDatesetHashHolder datasetHashHolder = null;
		ILocalSensitiveHashHolder localSensitiveHashHolder = null;
		
		if( !Files.exists(Paths.get(IDatesetHashHolder.CACHE_PATH)) ){
			datasetHashHolder = new DatasetHashHolder();
			localSensitiveHashHolder = new LocalSensitiveHashHolder(datasetHashHolder);
			System.out.println("Calculating hashes...");
			// create hashes for cache
			for(int bitSizeIndex = 0; bitSizeIndex < arrayOfBitsSize.length; bitSizeIndex++){
				for( int blockIndex = 0; blockIndex < blockNums.length; blockIndex++){
					initCacheForConf(
							arrayOfBitsSize[bitSizeIndex],
							blockNums[blockIndex],
							dataset,
							algo,
							needles,
							datasetHashHolder);
				}
			}
			System.out.println("Saving hashes to cache...");
			((DatasetHashHolder) datasetHashHolder ).serialize(IDatesetHashHolder.CACHE_PATH);
		//deserialize from file system
		} else {
			System.out.println("Reusing calculated hashes from cache...");
			datasetHashHolder = DatasetHashHolder.deSerialize(IDatesetHashHolder.CACHE_PATH);
			localSensitiveHashHolder = new LocalSensitiveHashHolder(datasetHashHolder);
		}
		
		
		//at this point all hashes can be found in datasetHashHolder
		images.clear();
		
		
		for(int bitSizeIndex = 0; bitSizeIndex < arrayOfBitsSize.length; bitSizeIndex++){
			for( int blockIndex = 0; blockIndex < blockNums.length; blockIndex++){
				for(int needleIndex = 0; needleIndex < needles.size(); needleIndex++) {
					System.out.println("Finding candidates of " + needles.get(needleIndex));
					makeIterationForConfiguration(
							arrayOfBitsSize[bitSizeIndex],
							blockNums[blockIndex],
							needles,
							isGray,
							algo,
							needleIndex,
							localSensitiveHashHolder);
				}
			}
		}
		
	
	}

	
	
	private static void initCacheForConf(int bits, int blocks, Dataset dataset,
			IHashableImageAlgo algo, List<String> needlesPath, IDatesetHashHolder datasetHashHolder) {
		String baseFileName = String.format(IDatesetHashHolder.BASE_KEY_FORMAT, bits, blocks);
		System.out.println("Calculating and caching hashes for " + baseFileName);
		
		setParamsForMeasurement(bits, blocks, 0.0d);
		
		List<BitSet> needleHashes = new ArrayList<BitSet>(1500);
		for(int i=0; i < needlesPath.size(); i++){
			String path = needlesPath.get(i);
			HashableImage image = new HashableImage(true, path);
			needleHashes.add(HashableImage.executeAlgorithm(algo, image));
		}
		
		String needlesKey = datasetHashHolder.formKeyForConfiguration(true, baseFileName);
		datasetHashHolder.setHashesForConfiguration(needlesKey, needleHashes);
		
		
		List<BitSet> modifiedHashes = new ArrayList<BitSet>(9000);
		for( Path pathModified : dataset.getImagePaths()) {
			HashableImage image = new HashableImage(true, pathModified.toString());
			modifiedHashes.add(HashableImage.executeAlgorithm(algo, image));
		}
		
		String modifiedKey = datasetHashHolder.formKeyForConfiguration(false, baseFileName);
		datasetHashHolder.setHashesForConfiguration(modifiedKey, modifiedHashes);
		
		
		
		
	}
	private static void makeIterationForConfiguration(int numBits,int numBlock, List<String> needles, boolean isGray, IHashableImageAlgo algo, int needleIndex, ILocalSensitiveHashHolder localSensitiveHashHolder) {
		//initial starting point for threshold, 
		int threshold = (int) Comparison.THRESHOLD;
		int DEFAULT_SIZE = 50;
		String needle = needles.get(needleIndex);
		
		HashableImage.BITS_FOR_COMPONENT = numBits;
		HashableImage.NUM_PIXEL_RANGES =(int) Math.pow(2, numBits);
		HashableImage.NUM_BLOCK_COL = numBlock;
		HashableImage.NUM_BLOCK_ROW = numBlock;
		//after setting the params that are static calculate size of hash
		int SIZE_OF_HASH = HashableImage.BITS_FOR_COMPONENT * HashableImage.NUM_BLOCK_COL * HashableImage.NUM_BLOCK_ROW;//and times num of component which is 1
		int MAX_THRESH = SIZE_OF_HASH > DEFAULT_SIZE ? SIZE_OF_HASH: DEFAULT_SIZE;//on x axis of graph it is represented as Jaccard distance 1 - MAX_THRESH
		
		metricIncrementCalculator.setRangePercentageIncrement(0.0d, MAX_THRESH, 0.05);
		
		String baseKey = String.format(IDatesetHashHolder.BASE_KEY_FORMAT, numBits, numBlock);
		String needlesKey = localSensitiveHashHolder.formKeyForConfiguration(true, baseKey);
		//add to 0 index the search needle
		LSHResult lshResult = localSensitiveHashHolder.getLocalitySensitiveCandidates(numBits, numBlock, needleIndex);
		System.out.println("Found " + (lshResult.getLocalitySensitiveCandidates().size()) + " candidates for " + needle );

		while( metricIncrementCalculator.hasNext() ){
			
			String searchNeedleSignature = needle.substring(needle.lastIndexOf(File.separatorChar)+1, needle.lastIndexOf(".") );
			String threshValue = null;
			if( metricIncrementCalculator.getCurrentMetric() == MetricType.HAMMING_DISTANCE ){
				threshValue = String.format(Locale.UK, "%.2f", Math.floor( metricIncrementCalculator.getCurrentValue()));
			} else if ( metricIncrementCalculator.getCurrentMetric() == MetricType.JACCARD_DISTANCE ){
				threshValue = String.format(Locale.UK, "%.2f", metricIncrementCalculator.getCurrentValue());
			}
			String measurementFileName ="results/"+
					searchNeedleSignature +  
					"-bits-"+ numBits+
					"-blocks-"+ numBlock+ 
					"-thresh" + "-" + threshValue +".txt";
			BufferedWriter bw = null;
			try {
				bw= Files.newBufferedWriter(
						Paths.get(measurementFileName),
						StandardOpenOption.CREATE,
						StandardOpenOption.WRITE,
						StandardOpenOption.TRUNCATE_EXISTING);
//				//compare with JaccardDistance
//				List<Integer> results = checkWithinJaccardDistance(candidates, 0, threshold);
				List<Integer> results = checkWithinHammingDistance(
						lshResult,
						(int)metricIncrementCalculator.getCurrentValue(),
						localSensitiveHashHolder.getHashesForConfiguration(needlesKey).get(needleIndex));
						
				
				for(Integer indexResult : results){
					bw.write(
							imagePaths.get(indexResult).getFileName() +
							"\t" +
							Comparison.results.get(indexResult)+"\n");
				}
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			metricIncrementCalculator.nextMetricIncrement();
		}
		
	}


	private static List<String> findPathsOfFiles(String dirPath) {
		List<String> fileNames = new ArrayList<String>();
		
		try {
			Files.walkFileTree(Paths.get(dirPath), new SimpleFileVisitor<Path>() {
				
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					
					String fileName = file.toString();
					fileNames.add(fileName);
					
					return FileVisitResult.CONTINUE;
				};
			});
				
		} catch (IOException e) {
			System.err.println("Can't visit all files!");
			System.exit(101);
		}
		return fileNames;
	}

	private static void setParamsForMeasurement(int bits, int blocks, double thresh) {
		HashableImage.BITS_FOR_COMPONENT = bits;
		HashableImage.NUM_PIXEL_RANGES =(int) Math.pow(2, bits);
		HashableImage.NUM_BLOCK_COL = blocks;
		HashableImage.NUM_BLOCK_ROW = blocks;
		for( HashableImage img : Comparison.images){
			img.refreshParamsForEvaluation(blocks, blocks);
		}
	}
	
	
	
	
	

}
