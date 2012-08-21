package de.thorben.imageimport;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;


public class ImageImportTest {

	@Test
	public void testImport() {
		ImageImporter importer = new ImageImporter(128, 120);
		Image image = importer.loadSingleImage(ImageImportTest.class.getResource("../../../face/test.pgm").getPath());
		
		Assert.assertEquals(148, image.getMaximumGreyScale());
		
		short[] lastLine = image.getData()[119];
		Assert.assertEquals(21, lastLine[124]);
		Assert.assertEquals(0, lastLine[125]);
		Assert.assertEquals(0, lastLine[126]);
		Assert.assertEquals(0, lastLine[127]);
	}
	
	@Test
	public void testExport() throws IOException {
		ImageImporter importer = new ImageImporter(128, 120);
		Image image = importer.loadSingleImage(ImageImportTest.class.getResource("../../../face/test.pgm").getPath());
		image.exportToPgm("unprocessed.pgm");
		
		Preprocessor preprocessor = new Preprocessor();
		preprocessor.interpolateNewMaximumGrey(image, (short) 255);
		image.exportToPgm("processed.pgm");
	}

}
