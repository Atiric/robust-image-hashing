package hr.fer.zemris.image.metric;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricRangeEvaluator {
	
	private Path evaluationPath;
	private Path currentDir;
	/**
	 * Map that has key parameters description( bits-4-block ...) for hashing algorithm and F1 metrics for value
	 */
	private Map<String, Double> paramsMapper;
	/**
	 * Map that has key as parameters description( bits-4-block ...) for hashing algorithm and configuration for a value 
	 */
	private Map<String, String> configurationMapper;
	

	public MetricRangeEvaluator(Path path) {
		this.evaluationPath = path;
		paramsMapper = new HashMap<String, Double>();
		configurationMapper = new HashMap<String, String>();
	}
	
	public void evaluateMetrics() {
		try {
			Files.walkFileTree(evaluationPath, new SimpleFileVisitor<Path>() {
				
				@Override
				public FileVisitResult preVisitDirectory(Path dirPath,
						BasicFileAttributes arg1) throws IOException {
					currentDir = dirPath;
//					if( dirPath.getFileName().toString().contains("reduced") ){
//						return FileVisitResult.SKIP_SUBTREE;
//					}
					return super.preVisitDirectory(dirPath, arg1);
				}
				
				@Override
				public FileVisitResult visitFile(Path path,
						BasicFileAttributes arg1) throws IOException {
					
					if( path.getFileName().toString().equals( "results_precision_recall_f1.txt" )){
						System.out.println("Found file " + path.getFileName() + " current dir:" + currentDir.getFileName());
						processForMaxF1( path );
					}
					
					return  super.visitFile(path, arg1);
				}
				
			
		});
		}catch (Exception e) {
			e.printStackTrace();
		}	
		
		for( String paramsMapperKey : paramsMapper.keySet()){
			System.out.println(paramsMapperKey + " F1: "+ paramsMapper.get(paramsMapperKey) + "SearchType: " + configurationMapper.get(paramsMapperKey) );
		}
	}
	
	public void processForMaxF1(Path path) {
		List<String> lines = null;
		boolean flagForReadingParam = false; 
		try {
			lines = Files.readAllLines(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String currentParam = null;
		for( String line: lines ){
			if( currentParam == null){
				currentParam = line.trim();
				if( !paramsMapper.containsKey(currentParam) ){
					paramsMapper.put(currentParam, 0.0);
					if( !configurationMapper.containsKey(currentParam)){
						configurationMapper.put(currentParam, currentDir.getFileName().toString());
					}
				}
				continue;
			}
			if ( line.trim().isEmpty() || flagForReadingParam ){
				if( !line.trim().isEmpty() ){
					currentParam = line.trim();
					flagForReadingParam = false;
				} else {
					//next line is param line;
					flagForReadingParam = true;
				}
			} else {
				String[] contentLine = line.split("\t");
				if(!paramsMapper.containsKey(currentParam)){
					paramsMapper.put(currentParam, 0.0);
				}
				Double currentF1 = paramsMapper.get(currentParam);
				Double f1 = Double.parseDouble(contentLine[4]);
				if( f1.compareTo(currentF1) > 0 ){
					paramsMapper.put(currentParam, f1);
					configurationMapper.put(currentParam, currentDir.getFileName().toString());
				}
			}
		}
		
		
	}

	public static void main(String[] args) {
		Path evaluPath = Paths.get("E:\\FER\\Diplomski rad\\new dataset\\Evaluation\\reevaluation 6 mjesec");
		MetricRangeEvaluator metricRangeEvaluator = new MetricRangeEvaluator(evaluPath);
		metricRangeEvaluator.evaluateMetrics();
	}

}
