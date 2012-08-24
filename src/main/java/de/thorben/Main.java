package de.thorben;

public class Main {

	public static void main(String[] args) {
		ImageRecognizer recognizer = new ImageRecognizer();
		recognizer.setTrainingSetPath("src/main/resources/faces/");
		recognizer.buildUp();
	}
}
