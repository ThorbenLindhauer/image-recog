package de.thorben;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;

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
	private List<Image> images;
	private SingularValueDecomposition svd;
	private Map<String, Matrix> mappedImages;
	
	public ImageRecognizer() {
		mappedImages = new HashMap<String, Matrix>();
	}
	
	public void setTrainingSetPath(String path) {
		trainingSetPath = path;
	}
	
	public void buildUp() {
		images = loadImages();
		Matrix m = buildMatrix();
		System.out.println("Image loading successful.");
		
		svd = m.svd();
		System.out.println("SVD complete");
		
		exportFirstEigenfaces(svd, 20);		
		System.out.println("Exported eigenfaces");
		
		mapImagesToLatentSpace();
		
	}
	
	private void mapImagesToLatentSpace() {
		for (Image image : images) {
			Matrix vector = image.getDataAsSingleDimensionalVector();
			Matrix mappedImage = null;
			Matrix u = svd.getU();
			// TODO fixme
			mappedImage = u.transpose().times(vector);
//			try {
//				System.out.println("U transposed: " + u.transpose().getRowDimension() + " x " + u.transpose().getColumnDimension());
//				System.out.println("Image Vector: " + vector.getRowDimension() + " x " + vector.getColumnDimension());
//				mappedImage = u.transpose().arrayTimes(vector);
//				
//			} catch (Exception e) {
//				mappedImage = u.arrayTimes(vector);
//			}
			mappedImages.put(image.getName(), mappedImage);
		}		
	}
	
	public void findMostSimilarImages(String imageName) {
		Matrix imageVector = mappedImages.get(imageName);
		NavigableMap<Double, List<String>> largestSimilarities = new TreeMap<Double, List<String>>();
		
		for (Entry<String, Matrix> mappedImage : mappedImages.entrySet()) {
			double similarity = cosineSimilarity(mappedImage.getValue(), imageVector);
			
			List<String> similarities = largestSimilarities.get(similarity);
			if (similarities == null) {
				largestSimilarities.put(similarity, new ArrayList<String>());
			}
			largestSimilarities.get(similarity).add(mappedImage.getKey());
		}
		
		System.out.println("Most similar images: ");
		NavigableSet<Double> similarities = largestSimilarities.descendingKeySet();
		Iterator<Double> it = similarities.iterator();
		for (int i = 0; i < 5; i++) {
			Double similarity = it.next();
			List<String> similarImages = largestSimilarities.get(similarity);
			for (String similarImageName : similarImages) {
				System.out.println("Similarity " + similarity + " for image " + similarImageName);
			}
			
		}
	}
	
	private double cosineSimilarity(Matrix vector1, Matrix vector2) {
		double dotProduct = vector1.arrayTimes(vector2).norm1();
	    double eucledianDist = vector1.normF() * vector2.normF();
	    return dotProduct / eucledianDist;

	}

	/**
	 * Exports the top k eigenfaces.
	 * @param svd
	 * @param k
	 */
	private void exportFirstEigenfaces(SingularValueDecomposition svd, int k) {
		EigenfaceCreator creator = new EigenfaceCreator(svd, IMAGE_X_SIZE, IMAGE_Y_SIZE, MAXIMUM_GREY_VALUE);
		List<Image> eigenfaces = creator.getFirstEigenfaces(k);
		
		int i = 0;
		for (Image eigenface : eigenfaces) {
			try {
				eigenface.exportToPgm("eigenfaces/eigenface" + i + ".pgm");
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}

	/**
	 * Loads all pgm images from the specified path and all subfolders
	 * @return
	 */
	private List<Image> loadImages() {
		ImageImporter importer = new ImageImporter(IMAGE_X_SIZE, IMAGE_Y_SIZE);
		ImagePreprocessor preprocessor = new ImagePreprocessor();
		
		File trainingSetDirectory = new File(trainingSetPath);
		return loadImages(importer, preprocessor, trainingSetDirectory);
	}
	
	/**
	 * puts all loaded images in a matrix
	 * @return
	 */
	private Matrix buildMatrix() {
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
