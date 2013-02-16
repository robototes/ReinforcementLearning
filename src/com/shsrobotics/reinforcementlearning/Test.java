package com.shsrobotics.reinforcementlearning;

public class Test {
	private static QEstimator qEstimator = new QEstimator(1, 1, 1, 0.2, 0.0);
	
	public static void main(String args[]) {
		for (int i = 0; i < 10; i++) {
			int number = (int) (Math.random() * 100);
			double[] input = {number};
			double[] output = {number + 1};
			qEstimator.addDataPoint(input, output);
		}
	}
}
