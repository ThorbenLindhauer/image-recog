package de.thorben.imageimport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ImageImporter {
	private static final String PIXEL_DELIMITER = " ";
	private static final char FILE_NAME_DELIMITER = File.pathSeparatorChar;

	private int xSize;
	private int ySize;

	public ImageImporter(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
	}

	public Image loadSingleImage(String filePath) {
		File file = new File(filePath);
		ImageBuilder imgBuilder = new ImageBuilder();
		try {
			FileInputStream fileStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(
					fileStream);
			BufferedReader reader = new BufferedReader(inputStreamReader);

			// TODO: assumes unique file names!
			String imageName = filePath.substring(filePath
					.lastIndexOf(FILE_NAME_DELIMITER) + 1);
			imgBuilder.newImage(xSize, ySize).setName(imageName);

			System.out.println("Loading image " + imageName + "...");
			readPrefixes(reader, imgBuilder);
			readContent(reader, imgBuilder);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imgBuilder.buildImage();
	}

	private void readContent(BufferedReader reader, ImageBuilder imgBuilder) throws IOException {
		String line = reader.readLine(); 
		int xPos = 0;
		int yPos = 0;
		while(line != null) {
			line = trimLine(line);
			String[] values = line.trim().split(PIXEL_DELIMITER);
			for (int i = 0; i < values.length; i++) {
				short value = Short.parseShort(values[i]);
				imgBuilder.setPoint(xPos, yPos, value);
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

	/**
	 * Removes comments from lines
	 * @param line
	 * @return
	 */
	private String trimLine(String line) {
		int commentDelimiterPosition = line.indexOf("#");
		if (commentDelimiterPosition == -1) {
			return line.trim();
		} else {
			return line.substring(0, commentDelimiterPosition).trim();
		}
	}

	private void readPrefixes(BufferedReader reader, ImageBuilder imgBuilder)
			throws IOException {
		int tokenCounter = 1;

		while (tokenCounter < 5) {
			String line = trimLine(reader.readLine());
			if (line.equals("")) {
				continue;
			} else {
				String[] tokens = line.split(PIXEL_DELIMITER);
				for (int i = 0; i < tokens.length; i++, tokenCounter++) {
					if (tokenCounter == 4) {
						imgBuilder.setMaximumGreyScaleValue(Short
								.parseShort(tokens[i]));
					}
				}
			}
		}

		if (tokenCounter > 5) {
			System.out
					.println("File began on same line as the greyscale value. Import will not work properly!");
		}

	}
}
