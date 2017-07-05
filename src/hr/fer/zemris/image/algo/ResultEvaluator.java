package hr.fer.zemris.image.algo;

import hr.fer.zemris.image.config.Configuration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

public class ResultEvaluator {
	
	private Path resultFolderPath;
	private Map<String, Integer> truePositiveCounter;
	private Map<String, Result> results;
	private Path originalFolderPath;
	private Set<String> needles;
	private Path modifiedFolderPath;
	
	public static BufferedWriter bw;
	/**
	 * TOTAL_FILES are calculated dynamically while initializing this object. 
	 */
	public static int TOTAL_FILES = 1093;
	
	/**
	 * TOTAL_TP is used as legacy, when i had only one needle, now {@link Map} is used to hold TOTAL_TP for needle signature.
	 * when result is calculated for one picture, its number of all possible TP is stored in TOTAL_TP.
	 */
	public static int TOTAL_TP = 32;
	
	public static String evaluationFile = "evaluation_folder/results_precision_recall_f1.txt";
	
	
	/**
	 * Initializes the result evaluator for calculating the final results for plotting the graph that are written in path defined by evaluationFile.
	 * @param resultFolder String that represents directory where all results as files are written by {@link Comparison} class.
	 * @param originalFolder String that represents directory where all needles are, original data set.
	 * @param modifiedFolder String that represents directory where all pictures that are modified from original data set are.
	 */
	public ResultEvaluator(String resultFolder, String originalFolder, String modifiedFolder) {
		
		this.resultFolderPath = Paths.get(resultFolder);
		this.originalFolderPath = Paths.get(originalFolder);
		this.modifiedFolderPath = Paths.get(modifiedFolder);
		//visit every file in original data set and add its true positive number in hash 
		
		this.truePositiveCounter = new HashMap<String, Integer>();
		this.needles = new HashSet<String>();
		
		try {
			Files.walkFileTree(originalFolderPath, new SimpleFileVisitor<Path>() {
				
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					
					String fileName = file.getFileName().toString();
					needles.add(fileName.substring(0, fileName.indexOf(".")));
					
					return FileVisitResult.CONTINUE;
				};
			});
				
		} catch (IOException e) {
			System.err.println("Can't visit all files!");
			System.exit(101);
		}
		TOTAL_FILES = 0;
		try {
			Files.walkFileTree(this.modifiedFolderPath, new SimpleFileVisitor<Path>() {
				
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
							
					String modifiedFileName = file.getFileName().toString();
					String needleSignature = modifiedFileName.substring(0, modifiedFileName.indexOf("_"));
					TOTAL_FILES++;
					if( needles.contains(needleSignature) ){
						// found modified picture that is a true positive match of one original picture
						if( truePositiveCounter.containsKey(needleSignature) ){
							int numTP = truePositiveCounter.get(needleSignature);
							truePositiveCounter.put(needleSignature, numTP+1);
						} else {
							truePositiveCounter.put(needleSignature, 1);
						}
					}
					return FileVisitResult.CONTINUE;
					
				}
			});
		} catch (IOException e) {
			System.err.println("Can't visit all files!");
			System.exit(100);
		}
		
		results = new LinkedHashMap<String, Result>();
		System.out.println("Done preprocessing dataset");
	
	
	}
	
	public Map<String, Result> getResults() {
		return results;
	}
	
	/**
	 * Begin the evaluation by visiting the result folder,
	 * finding all needles so that they can be evaluated by they result file.
	 * One {@link Result} is defined for one configuration by its threshold.
	 * EXAMPLE OF ONE CONFIGURATION : bits 4 blocks 4 threshold 0.2, it means that there are 4 blocks in row, 4 blocks in column for threshold 0.2 .
	 * If the needle signature is found in file that represents one configuration for threshold than 
	 * TP++ else FP++, FN is evaluated as TOTAL_TP - TP 
	 * while TN is all images in set B ( modified_images ) - TP - FN - FP .
	 * If we have multiple needles the {@link Result} object is retrieved and its values are updated.
	 */
	public void beginEvaluation(){
		
		
		
		try {
			Files.walkFileTree(resultFolderPath, new SimpleFileVisitor<Path>() {
				
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					List<String> lines = Files.readAllLines(file);
					
					int truePositive = 0;
					int falsePositive = 0;
					int falseNegative = 0;
					int trueNegative = 0;
					
					String resultFileName = file.getFileName().toString();
					String confThreshDesc = resultFileName.substring(resultFileName.indexOf("-")+1);
					String needleSignature = resultFileName.substring(0, resultFileName.indexOf("-"));
					
					TOTAL_TP = truePositiveCounter.get(needleSignature);
					
					for(String line : lines ){
						
						if( line.contains(needleSignature) ){
							truePositive++;
						} else {
							falsePositive++;
						}
					}
					falseNegative = TOTAL_TP - truePositive;
					//all the others
					trueNegative = TOTAL_FILES - truePositive - falsePositive - falseNegative;
					
					
					double threshold = Double.parseDouble(
							resultFileName.substring(resultFileName.lastIndexOf("-")+1,
							resultFileName.lastIndexOf("."))
							);
					if( results.containsKey(confThreshDesc) ){
						Result result = results.get(confThreshDesc);
						result.addResult(truePositive, trueNegative, falseNegative, falsePositive);
					} else {
						Result result = new Result(truePositive, falsePositive, falseNegative, trueNegative, threshold);
						results.put(confThreshDesc, result);
					}
					return FileVisitResult.CONTINUE;
				}
				
			});
		} catch (IOException e) {
			System.err.println("Can't visit all files!");
			System.exit(100);
		}
		
	}
	
	public static void main(String[] args) {
		Yaml yaml = new Yaml();
		Configuration configuration = null;
		try( InputStream in = Files.newInputStream(Paths.get("config/config.yml"))){
			configuration = yaml.loadAs(in, Configuration.class);
		} catch (IOException e) {
			System.err.println("Sth went wrong: " + e.toString());
		}

		ResultEvaluator re = new ResultEvaluator("results", configuration.getNeedlesPath(), configuration.getModifiedImages());
		re.beginEvaluation();
		List<String> keys = new ArrayList<> ( re.getResults().keySet());
		keys.sort( (key1, key2) -> {
//			String conf1 = key1.substring(0, key1.lastIndexOf("-"));
//			String conf2 = key2.substring(0, key2.lastIndexOf("-"));
			
			int indexBlock1 = key1.indexOf("-blo");
			int indexBlock2 = key2.indexOf("-blo");
			
			int block1 = Integer.parseInt(
					key1.substring( 
							indexBlock1 + 8,//after the "-blocks-" is num of blocks
							key1.indexOf("-thr")));
			int block2 = Integer.parseInt(
					key2.substring( 
							indexBlock2 + 8,//after the "-blocks-" is num of blocks
							key2.indexOf("-thr")));
			int thresh1 = (int) Double.parseDouble( key1.substring(key1.lastIndexOf("-") + 1, key1.lastIndexOf(".")));
			int thresh2 = (int) Double.parseDouble( key2.substring(key2.lastIndexOf("-") + 1, key2.lastIndexOf(".")));
			
			int bits1 = Integer.parseInt( key1.substring( 
					key1.indexOf("-") + 1,
					indexBlock1));
			
			int bits2 = Integer.parseInt( key2.substring( 
					key2.indexOf("-") + 1,
					indexBlock2));
			
			if( bits1 == bits2 ){
				if( block1 == block2 ){
					return thresh1 - thresh2;
				}
				return block1 - block2;
			}
			return bits1 - bits2;
		});
		//Sorted by its configurations and thresholds, important for plotting the data.
		//Arrays.sort(keys);
		
		String confWithoutThresh = "";
		
		try {
			ResultEvaluator.bw = Files.newBufferedWriter(
					Paths.get(ResultEvaluator.evaluationFile),
					StandardOpenOption.CREATE,
					StandardOpenOption.WRITE,
					StandardOpenOption.TRUNCATE_EXISTING);

			for( Object key: keys){
				String confWithThresh = ((String)key);
				String tempConf = confWithThresh.substring(0, confWithThresh.lastIndexOf("-"));
				System.out.println("Calculating parameters for :"+confWithoutThresh);
				//write starting coordinate for Jaccard distance(! not similarity)
				//graph is represented as Jaccard distance on x axis and precision, recall and F1 on y axis
				if( !confWithoutThresh.equals(tempConf) ){
					
//					if( !confWithoutThresh.isEmpty() ){
//						bw.write(confWithoutThresh + "	0.0	1.00	0.00	0.00\n");
//						
//					}
					bw.write("\n" + tempConf+"\n");
					confWithoutThresh = tempConf;
				}
				Result result = re.getResults().get(key);
				// this num must be  NUMBER_OF_NEEDLES * NUMBER_OF_MODIFIED_IMAGES
				result.calculateParams();
				bw.write(confWithThresh + "\t" + result + "\n" );
				
			}
			//bw.write(confWithoutThresh + "	0.0	1.00	0.00	0.00\n");
			
			ResultEvaluator.bw.close();
		} catch (IOException e) {
			// do nothing
			e.printStackTrace();
		}
	}

}
