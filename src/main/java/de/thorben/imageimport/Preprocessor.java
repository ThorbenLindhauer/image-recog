package de.thorben.imageimport;

public class Preprocessor {

	public void interpolateNewMaximumGrey(Image image, short maximumGreyValue) {
		double scaleFactor = (double) maximumGreyValue / (double)image.getMaximumGreyScale();
		short[][] values = image.getData();
		for (int y = 0; y < values.length; y++) {
			short[] currentLine = values[y];
			for (int x = 0; x < currentLine.length; x++) {
				short value = currentLine[x];
				short newValue = (short) (value * scaleFactor);
				image.setPoint(x, y, newValue);
			}
		}
		
		image.setMaximumGreyScale(maximumGreyValue);
	}
}
