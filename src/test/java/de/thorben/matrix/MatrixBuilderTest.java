package de.thorben.matrix;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import Jama.Matrix;
import de.thorben.imageimport.Image;
import de.thorben.imageimport.ImageImportTest;
import de.thorben.imageimport.ImageImporter;

public class MatrixBuilderTest {

	private static final double DELTA = 1e-15; 
	
	private Image image;
	
	@Before
	public void setUp() {
		ImageImporter importer = new ImageImporter(128, 120);
		image = importer.loadSingleImage(ImageImportTest.class.getResource("../../../face/test.pgm").getPath());
	}
	
	@Test
	public void testBuilder() {
		MatrixBuilder builder = new MatrixBuilder(128, 120);
		builder.addImage(image);
		builder.addImage(image);
		builder.addImage(image);
		
		Matrix m = builder.buildMatrix();
		// first image
		Assert.assertEquals("The last pixel of the first image should be black", 0.0, m.get(128 * 120 - 1, 0), DELTA);	
		Assert.assertEquals("The fourth last pixel of the first image should have value 21", 21.0, m.get(128 * 120 - 4, 0), DELTA);
		
		// second image
		Assert.assertEquals("The last pixel of the second image should be black", 0.0, m.get(128 * 120 - 1, 1), DELTA);	// last pixel
		Assert.assertEquals("The fourth last pixel of the second image should have value 21", 21.0, m.get(128 * 120 - 4, 1), DELTA);
		
		//third image
		Assert.assertEquals("The last pixel of the third image should be black", 0.0, m.get(128 * 120 - 1, 2), DELTA);	// last pixel
		Assert.assertEquals("The fourth last pixel of the third image should have value 21", 21.0, m.get(128 * 120 - 4, 2), DELTA);
	}
	

}
