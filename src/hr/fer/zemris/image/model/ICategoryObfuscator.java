package hr.fer.zemris.image.model;

import java.io.File;
import java.util.Random;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;

public interface ICategoryObfuscator {
	static int RANGE_PIXEL = 255;
	
	void obfuscateImage(Mat imageMat, String imageName);
	
	
	
	public static class StohasticObfuscator implements ICategoryObfuscator {

		private double probability;
		private String categoryPath;
		private static Random random;
		
		
		public StohasticObfuscator(double probability, String categoryPath) {
			this.probability = probability;
			this.categoryPath = categoryPath;
			StohasticObfuscator.random = new Random();
		}
		
		
		
		@Override
		public void obfuscateImage(Mat imgMat, String imageName) {
			String name = categoryPath + File.separator + imageName;
			for ( int y=0; y <imgMat.rows(); y+= 1){
				for( int x = 0; x < imgMat.cols(); x += 1){	
					if( Double.compare( probability, random.nextDouble() ) > 0 ){
						imgMat.put(y, x, random.nextInt(RANGE_PIXEL));
					}
				}
			}
			Highgui.imwrite(name, imgMat);
		}
		
	}
	
	public static class RandomBlockObfuscator implements ICategoryObfuscator {
		
		private double probability;
		private String categoryPath;
		
		
		public RandomBlockObfuscator(double probability, String categoryPath) {
			this.probability = probability;
			this.categoryPath = categoryPath;
		}

		private static int BLOCK_SIZE = 40;
		
		@Override
		public void obfuscateImage(Mat imageMat, String imageName) {
			
			String name = categoryPath + File.separator + imageName;
			
			int yBlock = (int) ( Math.random() * imageMat.rows());
			int xBlock = (int)(Math.random() * imageMat.cols());
			
			if( yBlock + BLOCK_SIZE > imageMat.rows() ) {
				yBlock = yBlock - BLOCK_SIZE;
			}
			
			if( xBlock + BLOCK_SIZE > imageMat.cols() ) {
				xBlock = xBlock - BLOCK_SIZE;
			}
			Mat blockMat = imageMat.submat(new Rect(xBlock, yBlock, BLOCK_SIZE, BLOCK_SIZE));
			for ( int y=0; y <blockMat.rows(); y+= 1){
				for( int x = 0; x < blockMat.cols(); x += 1){	
					if( Double.compare( probability, Math.random() ) > 0 ){
						blockMat.put(y, x, (int)Math.random() * RANGE_PIXEL);
					}
				}
			}
			
			Highgui.imwrite(name, imageMat);
			
			
		}
		
	}

}
