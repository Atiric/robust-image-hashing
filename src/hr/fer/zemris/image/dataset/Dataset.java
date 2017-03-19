package hr.fer.zemris.image.dataset;

import hr.fer.zemris.image.model.ICategoryObfuscator;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class Dataset {
	
	Path startPath;
	List<Path> imagePaths;
	private static int EXPECTED_SIZE_IMAGES = 31000;
	
	public List<Path> getImagePaths() {
		return imagePaths;
	}


	public Path getStartPath() {
		return startPath;
	}


	public Dataset(String startPath){
		this.imagePaths = new ArrayList<>(EXPECTED_SIZE_IMAGES);
		this.startPath = Paths.get(startPath);
		//visit every file in data set and add its path 
		try {
			Files.walkFileTree(this.startPath, new SimpleFileVisitor<Path>() {
				
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					imagePaths.add(file);
					return FileVisitResult.CONTINUE;
				}
				
			});
		} catch (IOException e) {
			System.err.println("Can't visit all files!");
			System.exit(100);
		}
		
		System.out.println("Dataset visited...");
		
		 
	}
	
	public void saveCategoryObfuscated(ICategoryObfuscator obfuscator){
		
		for(Path path : imagePaths){
			String name = path.getFileName().toString();
			Mat mat = Highgui.imread(path.toString(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			obfuscator.obfuscateImage(mat, name);
		}
		
	}
	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String category =  "256_ObjectCategories/224.touring-bike";//"256_ObjectCategories/041.coffee-mug" 256_ObjectCategories\003.backpack
		
		double probability = 0.5;
		Dataset caltech256Dataset = new Dataset(category);
		ICategoryObfuscator obfuscator = new ICategoryObfuscator.StohasticObfuscator(probability, "test1");
		caltech256Dataset.saveCategoryObfuscated(obfuscator);
		
	}


}
