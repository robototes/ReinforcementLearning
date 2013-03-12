package com.shsrobotics.reinforcementlearning.optimizers;

public class Test extends DefaultOptimizer {
	
	public Test(double[] minimums, double[] maximums) {
		super(24, minimums, maximums);
	}
	
	void run() {
		int errors = 0;
		for (int i = 0; i < 10000; i++) {
			double[] best = minimize();
			if (Math.abs(best[0]) > 0.005) {
				errors++;
			}
		}
		System.out.println("Errors: " + errors);
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		double[] minimums = {-10, -10};
		double[] maximums = {10, 10};
		new Test(minimums, maximums).run();
	}

	@Override
	public double f(double[] input) {
		double x = input[0];
		double y = input[1];
		return x * x + y * y;
	}
}