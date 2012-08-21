package de.thorben.matrix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Jama.Matrix;

import de.thorben.imageimport.Image;

public class MatrixBuilder {

	private List<Image> images;
	private int imageXSize;
	private int imageYSize;
	
	public MatrixBuilder(int imageXSize, int imageYSize) {
		this.imageXSize = imageXSize;
		this.imageYSize = imageYSize;
		reset();
	}
	
	private void reset() {
		images = new ArrayList<Image>();
	}
	
	public void addImage(Image image) {
		if (image.getXSize() != imageXSize || image.getYSize() != imageYSize) {
			// TODO throw exception and test this behavior
		}
		images.add(image);
		
	}

	public Matrix buildMatrix() {
		Matrix m = new Matrix(imageXSize * imageYSize, images.size());
		int imageCounter = 0;
		for (Image image : images) {
			short[][] pixelData = image.getData();
			for (int y = 0; y < imageYSize; y ++) {
				for (int x = 0; x < imageXSize; x ++) {
					m.set(y * imageXSize + x, imageCounter, pixelData[y][x]);
				}
			}
			imageCounter++;
		}
		return m;
	}
	

	
}
