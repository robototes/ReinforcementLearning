package com.shsrobotics.reinforcementlearning.optimizers;

public class Test extends Optimizer {
	
	public Test(double[] minimums, double[] maximums) {
		super(32, minimums, maximums);
	}
	
	void run() {
		for (int i = 0; i < 10000; i++) {
			double[] best = minimize();
			if (Math.abs(best[0]) > 0.0001) {
				System.out.println("ERROR: " + best[0]);
			} else {
				System.out.println("WORKED: " + best[0]);
			}
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		double[] minimums = {-10, -10};
		double[] maximums = {10, 10};
		Optimizer.class.getCanonicalName();
		Optimizer.Point.class.getCanonicalName();
		Math.class.getCanonicalName();
		System.class.getCanonicalName();
		StringBuilder.class.getCanonicalName();
		String.class.getCanonicalName();
		new Test(minimums, maximums).run();
	}

	@Override
	public double f(double[] input) {
		double x = input[0];
		double y = input[1];
		return x * x + y * y;
	}
}
