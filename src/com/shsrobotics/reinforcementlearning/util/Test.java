package com.shsrobotics.reinforcementlearning.util;

public class Test {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		int n = 100;
		DataPoint[] data = new DataPoint[n];
		String[] inputKeys = {"A", "B"};
		String[] outputKeys = {"Z"};
		double[] minimums = {0, 0};
		double[] maximums = {100, 100};
		for (int i = 0; i < n; i++) {
			double randA = Math.round(100 * Math.random());
			double randB = Math.round(100 * Math.random());
			double[] input = {randA, randB};
			double outputValue = (randA > 50) ? 20 : -5;
			double[] output = {outputValue};
			data[i] = new DataPoint(inputKeys, input, outputKeys, output);
		}
		RandomDecisionTree tree = new RandomDecisionTree(data, 2, minimums, maximums);
		double[] input = {10, 30};
		double output = tree.run(input);
		System.out.println("output = " + output);
	}
}
