package com.shsrobotics.reinforcementlearning.estimators;

import com.shsrobotics.reinforcementlearning.util.DataPoint;
import com.shsrobotics.reinforcementlearning.util.RandomDecisionTree;

public class Test {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		int n = 250;
		
		DataPoint[] data = new DataPoint[n];
		String[] inputKeys = {"A"};
		String[] outputKeys = {"Z"};
		double[] minimums = {0, 0};
		double[] maximums = {1, 20};
		for (int i = 0; i < n; i++) {
			double randA = Math.random();
			double[] input = {randA};
			double z;
			if (randA >= 0.2) {
				z = 1;
			} else {
				z = 0;
			}
			double[] output = {z};
			data[i] = new DataPoint(inputKeys, input, outputKeys, output);
		}		
		double[] input = {0.95};
		double[] input2 = {0.05};
		//*
		RandomForest forest = new RandomForest(64, minimums, maximums, 8);
		forest.addInitialData(data);
		System.out.println("output: " + forest.run(input));
		System.out.println("output: " + forest.run(input2));
		/*/
		RandomDecisionTree tree = new RandomDecisionTree(data, 2, minimums, maximums, 4);
		System.out.println("outupt: " + tree.run(input));
		//*/
	}
}
