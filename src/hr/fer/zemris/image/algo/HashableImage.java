package hr.fer.zemris.image.algo;

import hr.fer.zemris.image.dataset.Dataset;
import hr.fer.zemris.image.model.IHashableImageAlgo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
/**
 * Abstraction of image that can be hashed using {@link IHashableImageAlgo}.
 * @author Armin
 *
 */

public class HashableImage {
	/**
	 * Height of one block.
	 */
	private int nBlockRow;
	/**
	 * Width of one block.
	 */
	private int nBlockCol;
	/**
	 * Height of image.
	 */
	private int imgSizeRows;
	/**
	 * Width of image.
	 */
	private int imgSizeCols;
	/**
	 * If true, image is loaded as gray image, otherwise color image.
	 */
	private boolean isGray;
	/**
	 * OpenCV container of this image.
	 */
	private Mat imgMat;
	/**
	 * All calculated blocks.
	 */
	private List<Rect> blocks = new ArrayList<>();	
	/**
	 * Resized width length of every image that is processed
	 */
	private int RESIZED_WIDTH = 320;
	/**
	 * Resized height length of every image that is processed
	 */
	private int RESIZED_HEIGHT = 180;
	/**
	 * Number of bits reserved for one pixel component when converting it to {@link BitSet}.
	 */
	public static int BITS_FOR_COMPONENT = 5;

	/**
	 * Number of ranges for one pixel.
	 */
	public static int NUM_PIXEL_RANGES = (int) Math.pow(2, BITS_FOR_COMPONENT);
	/**
	 * Defines a number of blocks for row.
	 */
	public static int NUM_BLOCK_ROW;
	/**
	 * Defines a number of blocks for column.
	 */
	public static int NUM_BLOCK_COL;
	/**
	 * Class that represents the image that can be hashed using {@link IHashableImageAlgo}. 
	 * @param nBlockRow number of blocks for row.
	 * @param nBlockCol number of blocks for column.
	 * @param isGray Flag, if set image is loaded as gray-scale image.
	 * @param imgPath string that represents path to image.
	 */
	public HashableImage(boolean isGray, String imgPath) {
		super();
		this.isGray = isGray;
		this.initImg(imgPath, NUM_BLOCK_ROW, NUM_BLOCK_COL);
	}
	
	public int getImgSizeRows() {
		return imgSizeRows;
	}


	public int getImgSizeCols() {
		return imgSizeCols;
	}


	public boolean isGray() {
		return isGray;
	}


	public Mat getImgMat() {
		return imgMat;
	}

	/**
	 * Show the image that is represented by OpenCV Mat class
	 * @param img Image that is represented with Mat.
	 */
	public static void showResult(Mat img) {
	    //Imgproc.resize(img, img, new Size(640, 480));
	    MatOfByte matOfByte = new MatOfByte();
	    Highgui.imencode(".jpg", img, matOfByte);
	    byte[] byteArray = matOfByte.toArray();
	    BufferedImage bufImage = null;
	    try {
	        InputStream in = new ByteArrayInputStream(byteArray);
	        bufImage = ImageIO.read(in);
	        JFrame frame = new JFrame();
	        frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	/**
	 * Resize the {@link Mat}
	 * @param image image that is being resized.
	 * @return resized image.
	 */
	@SuppressWarnings("unused")
	private Mat resizeImage(Mat image){
		Mat resizeImage = new Mat();
		Size sz = new Size(RESIZED_WIDTH,RESIZED_HEIGHT);
		if( !imgMat.empty()){
			Imgproc.resize(imgMat, resizeImage, sz );
			imgMat.release();
			imgMat = resizeImage;
			//generate blocks
			imgSizeCols = RESIZED_WIDTH;
			imgSizeRows = RESIZED_HEIGHT;
		}
		return resizeImage;
	}
	/**
	 * Method that reads image at path and initializes it with given arguments.
	 * @param path String that represents path to image, OpenCV loads images from {@link String} not from {@link Path}.
	 * @param nRow Number of blocks in row.
	 * @param nCol Number of blocks in column.
	 */
	private void initImg(String path,int nRow, int nCol){
		if ( isGray ){
			imgMat = Highgui.imread(path, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		} else {
			imgMat =  Highgui.imread(path, Highgui.CV_LOAD_IMAGE_COLOR); 
		}
		
		if( imgMat.empty() ){
			System.out.println("BUG in DATASET");
			return;
		}
		
		imgSizeCols = imgMat.cols();
		imgSizeRows = imgMat.rows();
		if( imgSizeRows == 0 || imgSizeCols == 0){
			throw new IllegalArgumentException("Invalid image in dataset, it has no defined size!");
		}
		nBlockRow = imgSizeRows/nRow;
		nBlockCol = imgSizeCols/nCol;
		//resize the image for uniform width and height(or not)
		//resizeImage(imgMat);
		generateRect();
		
	}
	/**
	 * Method that generates blocks for next evaluation without loading the image.
	 * @param nRow Number of blocks in row of this image.
	 * @param nCol Number of blocks in column of this image.
	 */
	public void refreshParamsForEvaluation(int nRow, int nCol){
		nBlockRow = imgSizeRows/nRow;
		nBlockCol = imgSizeCols/nCol;
		
		
		blocks.clear();
		for ( int y=0; y + nBlockRow < imgSizeRows; y+= nBlockRow){
			for( int x = 0; x + nBlockCol < imgSizeCols; x += nBlockCol){	
				blocks.add(new Rect(x, y, nBlockCol, nBlockRow));
			}
		}
		
	}

	/**
	 * Getter for Mat object that is represents image block on specified position with  {@link Rect} class of OpenCV.
	 * @param index Index of rectangle.
	 * @return Image Mat specified with {@link Rect} class at given index.
	 */
	public Mat getSubmatrixBlock(int index){
		return imgMat.submat( blocks.get(index) );
	}
	/**
	 * Method that calculates mean values of this block at index.
	 * @param index Index of block of this image that can be hashed.
	 * @return Array of mean values of specified block.
	 */
	public Scalar getMeanForBlock(int index){
		Mat submatrix = getSubmatrixBlock(index);
		return Core.mean(submatrix);
	}
	/**
	 * Generate all rectangles that are specifying all blocks in image.
	 */
	private void generateRect() {
		if( blocks.isEmpty()){
			for ( int y=0; y + nBlockRow < imgSizeRows; y+= nBlockRow){
				for( int x = 0; x + nBlockCol  < imgSizeCols; x += nBlockCol){	
					blocks.add(new Rect(x, y, nBlockCol, nBlockRow));
				}
			}
		}
		
	}
	
	/**
	 * Method that executes arbitrary algorithm on instance of {@link HashableImage}.
	 * @param algo Algorithm that makes arbitrary calculation on image.
	 * @param hImg Image that is being processed in algorithm.
	 * @return Hash or arbitrary calculation on image.
	 */
	public static BitSet executeAlgorithm(IHashableImageAlgo algo, HashableImage hImg){
		return algo.executeAlgo(hImg);
	}
	
	/**
	 * Getter for blocks.
	 * @return Return all rectangles that represents all rectangles in this image.
	 */
	public List<Rect> getBlocks() {
		if( imgMat.empty() ) return null;
		return blocks;
	}
	
	/**
	 * Getter for {@link BitSet} representation of this image that is defined by pixel classes
	 *  and one specific component of pixel has BITS_FOR_COMPONENT bits.
	 * @param mat Matrix that represents image, or reduced image.
	 * @return {@link BitSet} representation of {@link Mat}, defined by BITS_FOR_COMPONENTS,
	 *  pixels in mat and number of components in pixel.
	 */
	public BitSet getBitSetFromMat(Mat mat){
		BitSet imgBitset = new BitSet();
		int bitsetPos = 0;
		
		for ( int i = 0; i < mat.rows(); i++ ){
			for ( int j = 0; j < mat.cols(); j++){
				double [] pixel =  mat.get(i, j);
				saveToBitset(imgBitset, pixel, bitsetPos);
				//number of components times bits reserved 
				bitsetPos += pixel.length * BITS_FOR_COMPONENT; 
			}
		}
		
		return imgBitset;
	}
	
	/**
	 * Getter for block of this HashableImage represented by a {@link BitSet}.
	 * Pixel values are reduced to 16 ranges of pixel values.
	 * For example 0-15 integer value of pixel is range 0, 16-32 is range 2... 238-255 is range 15.
	 * This is done so one pixel can be represented as 4 bits.
	 * @param index of block to be represented. 
	 * @return {@link BitSet} representation of this HashableImage.
	 */
	public BitSet getBlockAsBitset(int block){
		Mat blockMat = getSubmatrixBlock(block);
		
		return getBitSetFromMat(blockMat);
		
	}
	
	

	/**
	 * Helper method for saving pixel value or mean value of pixel that is represented by array of doubles in {@link BitSet}.
	 * @param imgBitset {@link BitSet} where processed array of doubles will be saved.
	 * @param pixel Array of doubles that represents pixel values or mean values, currently only one component is being processed at index 0
	 * @param bitsetPos
	 */
	void saveToBitset(BitSet imgBitset, double[] pixel, int bitsetPos) {
		int NUM_COMPONENTS = 1;
		for( int pixIndex=0; pixIndex < NUM_COMPONENTS; pixIndex++){
			// this is where all magic is happening, pixel is represented by one class of pixel
			int number = (int) ( pixel[pixIndex]/( 256/ NUM_PIXEL_RANGES));
			
			for(int i = BITS_FOR_COMPONENT - 1; i >= 0 ; i--) {
			        int mask = 1 << i;
			        int comparison = (number & mask ); 
			        if (  comparison == mask) {
			        	imgBitset.set( bitsetPos + pixIndex * BITS_FOR_COMPONENT + i);
			        }
			 }
			
		}
		
	}
	/**
	 * Image name is a full path to image that is loaded. block_size_row specifies the number of block in rows
	 * @param args Array of parameters as string : image_name block_size_row block_size_cols is_gray_scale
	 */
	static void exampleExecution(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//suppose we have 4 args
		if ( args.length != 4 ){
			System.err.println("Invalid number of arguments");
			System.err.println("Expected arguments: image_name block_num_row block_num_cols is_gray_scale");
			System.exit(0);
		}
		//img path 
		//it can represent one image like this example, or folder of images like in Comparison
		String imgPath = null;
		boolean isGray = false ;
		
		try {
			imgPath = args[0];
			HashableImage.NUM_BLOCK_ROW = Integer.parseInt( args[1] );
			HashableImage.NUM_BLOCK_COL = Integer.parseInt( args[2] );
			isGray = Boolean.parseBoolean(args[3]);
		} catch (Exception e) {
			System.err.println("Invalid arguments provided");
			System.exit(1);
		}
		
		
		Dataset caltech256Dataset = new Dataset("256_ObjectCategories");
		//load the needle
		HashableImage needle = new HashableImage(isGray, imgPath);
		List<Path> imagePaths = caltech256Dataset.getImagePaths();
		
		//IHashableImageAlgo algo = new AvgHashMock();
		//int i=0;
		for( Path pathImg : imagePaths ){
			//Because it was needed to change configuration, definition of rows and columns is static.
			HashableImage hImg = new HashableImage(isGray, pathImg.toString());
			//show the image if you want
			//HashableImage.showResult(hImg.getImgMat());
			//you can calculate hash
			//BitSet hash = HashableImage.executeAlgorithm(algo, hImg);
			
			//do something with hashes, for example...
			Comparison.compareTwoHasable(needle, hImg);
			
			//System.out.printf("Obrađena %d slika ...\n", i);
			//i++;
			
			
			
		}
	}
	
}