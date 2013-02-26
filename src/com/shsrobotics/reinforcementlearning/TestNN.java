package com.shsrobotics.reinforcementlearning;

import java.util.Random;

public class TestNN {
    public static void main(String[] args) {
        QEstimator estimator = new QEstimator(2, 2, 1, 0.2);
        estimator.setShortTermMemory(20);
        estimator.setIterations(10);
		Random random = new Random(1234567890);
        for (int i = 0; i < 50; i++) {
            double[] input = {random.nextInt(6), random.nextInt(6)};
            double[] output = {(input[0] + input[1] > 5) ? 1 : 0};
            estimator.addDataPoint(new DataPoint(input, output));
            estimator.train();
			System.out.println(estimator.runInput(input)[0]);
        }		
		double[] input = {3, 4};
		System.out.println(estimator.runInput(input)[0]);
    }
	
	public static void main2(String[] args) {
		Double a = null;
		double b = 1;
		System.out.println(a + b);
	}
}
