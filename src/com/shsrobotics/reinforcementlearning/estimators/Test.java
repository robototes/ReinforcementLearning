package com.shsrobotics.reinforcementlearning.estimators;

import com.shsrobotics.reinforcementlearning.util.DataPoint;

public class Test {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		int n = 50;
		DataPoint[] data = new DataPoint[n];
		String[] inputKeys = {"A", "B", "C"};
		String[] outputKeys = {"Z"};
		double[] minimums = {0, 0};
		double[] maximums = {100, 100};
		for (int i = 0; i < n; i++) {
			double randA = Math.round(Math.random() * 100);
			double randB = Math.round(Math.random() * 100);
			double[] input = {randA, randB};
			double z = 0;
			if (randA > 60) {
				z += 1;
			}
			double[] output = {z};
			data[i] = new DataPoint(inputKeys, input, outputKeys, output);
		}
		RandomForestQEstimator forest = new RandomForestQEstimator(50, minimums, maximums, 1);
		forest.addInitialData(data);
		double[] input = {90, 00};
		System.out.println("output: " + forest.run(input));
	}
}
