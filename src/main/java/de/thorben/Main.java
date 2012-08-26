package de.thorben;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] args) throws IOException {
		ImageRecognizer recognizer = new ImageRecognizer();
		recognizer.setTrainingSetPath("src/main/resources/lessFaces/");
		recognizer.buildUp();
		
		InputStreamReader isr = new InputStreamReader(System.in);           
		BufferedReader br = new BufferedReader(isr);
		while(true) {
			System.out.println("Enter an image name to match:");
			String imageName = br.readLine();
			recognizer.findMostSimilarImages(imageName);
		}
	}
}
