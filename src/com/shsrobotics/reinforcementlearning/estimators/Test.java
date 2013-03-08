package com.shsrobotics.reinforcementlearning.estimators;

import com.shsrobotics.reinforcementlearning.util.DataPoint;
import com.shsrobotics.reinforcementlearning.util.RandomDecisionTree;

public class Test {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		int n = 150;
		DataPoint[] data = new DataPoint[n];
		String[] inputKeys = {"A", "B"};
		String[] outputKeys = {"Z"};
		double[] minimums = {0, 0};
		double[] maximums = {1, 20};
		for (int i = 0; i < n; i++) {
			double randA = Math.round(Math.random() * 20);
			double randB = Math.round(Math.random());
			double[] input = {randA, randB};
			double z;
			if (randB == 1) {
				if (randA > 10) {
					z = 10;
				} else {
					z = -1;
				}
			} else {
				if (randA > 10) {
					z = -4;
				} else {
					z = 0;
				}
			}
			double[] output = {z};
			data[i] = new DataPoint(inputKeys, input, outputKeys, output);
		}		
		double[] input = {5, 0};
		//*
		RandomForestQEstimator forest = new RandomForestQEstimator(5, minimums, maximums, 4);
		forest.addInitialData(data);
		System.out.println("output: " + forest.run(input));
		/*/
		RandomDecisionTree tree = new RandomDecisionTree(data, 2, minimums, maximums, 4);
		System.out.println("outupt: " + tree.run(input));
		//*/
	}
}
