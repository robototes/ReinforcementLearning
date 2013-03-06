package com.shsrobotics.reinforcementlearning.optimizers;

public class Test {
	
	public static void main(String[] args) {
		new Test().run();
	}
	
	public void run() {
		double[] minimums = {-10, -10};
		double[] maximums = {10, 10};
		double[] best = (new Optimizer(2, 8, minimums, maximums) {
			@Override
			public double f(double[] input) {
				double x = input[0];
				double y = input[1];
				return x * x + y * y;
			}
		}).minimize();
		System.out.println(best[0]);
		System.out.println(best[1]);
	}
	
}
