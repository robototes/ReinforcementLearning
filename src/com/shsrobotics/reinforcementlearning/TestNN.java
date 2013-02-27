package com.shsrobotics.reinforcementlearning;

import java.util.Random;

public class TestNN {
    public static void main(String[] args) {
        NeuralNetworkQEstimator estimator = new NeuralNetworkQEstimator(2, 1, 1, 0.2);
        estimator.setShortTermMemory(20);
        estimator.setIterations(15);
		Random random = new Random(1234567890);
        for (int i = 0; i < 20; i++) {
            double[] input = {random.nextInt(6), random.nextInt(6)};
            double[] output = {(input[0] + input[1] > 5) ? 1 : 0};
            estimator.addDataPoint(new DataPoint(input, output));
            estimator.train();
        }		
		double[] input = {3, 3};
		System.out.println(Math.round(estimator.runInput(input)[0]) == 1 ? "Yes" : "No");
    }
}
