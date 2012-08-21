package de.thorben;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import de.thorben.imageimport.Image;
import de.thorben.imageimport.ImageImporter;
import de.thorben.imageimport.ImagePreprocessor;
import de.thorben.matrix.MatrixBuilder;

public class ImageRecognizer {

	private static final int IMAGE_X_SIZE = 128;
	private static final int IMAGE_Y_SIZE = 120;
	
	private static final short MAXIMUM_GREY_VALUE = 255;
	
	private String trainingSetPath;
	
	public void setTrainingSetPath(String path) {
		trainingSetPath = path;
	}
	
	public void buildUp() {
		Matrix m = loadImagesAndBuildMatrix();
		System.out.println("Image loading successful.");
		
		SingularValueDecomposition svd = m.svd();
		System.out.println("SVD complete");
	}
	
	private Matrix loadImagesAndBuildMatrix() {
		ImageImporter importer = new ImageImporter(IMAGE_X_SIZE, IMAGE_Y_SIZE);
		ImagePreprocessor preprocessor = new ImagePreprocessor();
		
		File trainingSetDirectory = new File(trainingSetPath);
		List<Image> images = loadImages(importer, preprocessor, trainingSetDirectory);
		
		MatrixBuilder builder = new MatrixBuilder(IMAGE_X_SIZE, IMAGE_Y_SIZE);
		for (Image image : images) {
			builder.addImage(image);
		}
		
		return builder.buildMatrix();		
	}
	
	/**
	 * Loads all images in the given directory recursively.
	 * @param importer
	 * @param preprocessor
	 * @param file
	 * @return
	 */
	private List<Image> loadImages(ImageImporter importer, ImagePreprocessor preprocessor, File file) {
		List<Image> images = new ArrayList<Image>();
		if (!file.isDirectory()) {	
			try {
				String fileName = file.getCanonicalPath();
				String fileEnding = file.getAbsolutePath().substring(fileName.length() - 4);
				if (!fileEnding.equals(".pgm")) {
					System.out.println("File named " + fileName + " is no pgm file.");
				} else {
					Image image = importer.loadSingleImage(fileName);
					preprocessor.interpolateNewMaximumGrey(image, MAXIMUM_GREY_VALUE);
					images.add(image);
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return images;
		} else {
			File[] contents = file.listFiles();
			for (int i = 0; i < contents.length; i++) {
				images.addAll(loadImages(importer, preprocessor, contents[i]));
			}
			return images;			
		}
	}
}
