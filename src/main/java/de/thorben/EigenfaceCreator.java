package de.thorben;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import de.thorben.imageimport.Image;
import de.thorben.imageimport.ImageBuilder;
import de.thorben.imageimport.ImagePreprocessor;
import de.thorben.math.MathHelper;

public class EigenfaceCreator {

	private static final int NORMALIZING_SCALAR = 5000;

	private SingularValueDecomposition svd;
	private int imageXSize;
	private int imageYSize;
	private short maximumGreyScaleValue;

	public EigenfaceCreator(SingularValueDecomposition svd, int imageXSize,
			int imageYSize, short maximumGreyScaleValue) {
		this.svd = svd;
		this.imageXSize = imageXSize;
		this.imageYSize = imageYSize;
		this.maximumGreyScaleValue = maximumGreyScaleValue;
	}

	/**
	 * Gets the first k Eigenfaces
	 * 
	 * @param k
	 * @return
	 */
	public List<Image> getFirstEigenfaces(int k) {
		List<Image> eigenfaces = new ArrayList<Image>();
		Matrix u = svd.getU();

		for (int i = 0; i < k; i++) {
			Matrix eigenfaceColumn = u.getMatrix(0, imageXSize * imageYSize - 1, i, i);
			double[] eigenface = eigenfaceColumn.getColumnPackedCopy();

			Image eigenfaceImage = buildImageFromVector(eigenface);
			eigenfaces.add(eigenfaceImage);
		}
		
		return eigenfaces;
	}
	
	private Image buildImageFromVector(double[] vector) {
		double minimalValue = MathHelper.minimalComponent(vector);
		ImageBuilder imgBuilder = new ImageBuilder();
		imgBuilder.newImage(imageXSize, imageYSize);
		
		int x = 0;
		int y = 0;
		for (int j = 0; j < vector.length; j++) {
			// normalize to value between 0 and maxValue*NORMALIZING_SCALAR
			Double matrixValue = new Double((vector[j] - minimalValue) * NORMALIZING_SCALAR);	
			short pixelValue = matrixValue.shortValue();
			imgBuilder.setPoint(x, y, pixelValue);

			if (x < imageXSize - 1) {
				x++;
			} else {
				x = 0;
				y++;
			}
		}
		
		
//		Image image = new Image(imageXSize, imageYSize);
//		image.setMaximumGreyScale(maximumGreyScaleValue);
//		
//		int x = 0;
//		int y = 0;
//		for (int j = 0; j < vector.length; j++) {
//			// normalize to value between 0 and maxValue*NORMALIZING_SCALAR
//			Double matrixValue = new Double((vector[j] - minimalValue) * NORMALIZING_SCALAR);	
//			short pixelValue = matrixValue.shortValue();
//			image.setPoint(x, y, pixelValue);
//
//			if (x < imageXSize - 1) {
//				x++;
//			} else {
//				x = 0;
//				y++;
//			}
//		}
		
		Image image = imgBuilder.buildImage();
		ImagePreprocessor preprocessor = new ImagePreprocessor();
		preprocessor.interpolateNewMaximumGrey(image, maximumGreyScaleValue);
		return image;
	}
}
