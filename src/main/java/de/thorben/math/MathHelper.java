package de.thorben.math;

public class MathHelper {

	public static double minimalComponent(double[] vector) {
		if (vector.length == 0) {
			throw new RuntimeException("Cannot find minimal component: vector has 0 elements.");
		}
		
		double minimal = vector[0];
		
		for (int i = 0; i < vector.length; i++) {
			if (vector[i] < minimal) {
				minimal = vector[i];
			}
		}
		
		return minimal;
	}
}
