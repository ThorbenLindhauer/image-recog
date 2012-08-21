package de.thorben.imageimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ImageImporter {
	private static final String DELIMITER = " ";
	
	private int xSize;
	private int ySize;
	
	public ImageImporter(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public Image loadSingleImage(String filePath){
		File file = new File(filePath);
		Image image = null;
		try {
			FileInputStream fileStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);
			
			image = new Image(xSize, ySize);
			
			readPrefixes(reader, image);
			readContent(reader, image);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return image;
	}

	private void readContent(BufferedReader reader, Image image) throws IOException {
		String line = reader.readLine(); 
		int xPos = 0;
		int yPos = 0;
		while(line != null) {
			String[] values = line.trim().split(DELIMITER);
			for (int i = 0; i < values.length; i++) {
				short value = Short.parseShort(values[i]);
				image.setPoint(xPos, yPos, value);
				xPos++;
				if (xPos == xSize) {
					// go to next line
					xPos = 0;
					yPos++;
				}				
			}
			line = reader.readLine(); 
		}
	}


	private void readPrefixes(BufferedReader reader, Image image) throws IOException {
		int tokenCounter = 1;
		
		while(tokenCounter < 5) {
			String line = reader.readLine();
			String[] tokens = line.split(DELIMITER);
			for (int i = 0; i < tokens.length; i++, tokenCounter++) {
				if (tokenCounter == 4) {
					image.setMaximumGreyScale(Short.parseShort(tokens[i]));
				}
			}
		}
		
		if (tokenCounter > 5) {
			System.out.println("File began on same line as the greyscale value. Import will not work properly!");
		}
		
		
	}
}
