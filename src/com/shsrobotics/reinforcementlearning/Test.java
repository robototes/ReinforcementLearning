package com.shsrobotics.reinforcementlearning;

import java.util.Scanner;

public class Test {
	public static void main(String args[]) {
		DataPoint[] data = new DataPoint[50];
		for (int i = 0; i < 50; i++) {
			double[] input = {Math.random(), Math.random()};
			double[] output = {(input[0] + input[1] > 1.0) ? 1.0 : 0.0};
			data[i] = new DataPoint(input, output);
		}	
		
		QEstimator learner = new QEstimator(2, 2, 1, 0.2, 0.0);
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Press 'Enter' to train neural network.");
		scanner.nextLine();
		learner.train(data);
		
		double[] input = {0.3, 0.75};
		System.out.println("Press 'Enter' to test the values " + input[0] + " and " + input[1] + ".");
		
		scanner.nextLine
			();
		System.out.println("Answer: " + ((Math.round(learner.runInput(input)[0]) == 1) ? "Yes" : "No"));
	}
}
