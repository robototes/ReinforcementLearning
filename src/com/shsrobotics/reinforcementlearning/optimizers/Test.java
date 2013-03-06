package com.shsrobotics.reinforcementlearning.optimizers;

public class Test {
	double[] minimums = {-10, -10};
	double[] maximums = {10, 10};
	FunctionOptimizer optimizer = new FunctionOptimizer(32, minimums, maximums);
	
	public static void main(String[] args) {
		new Test().run();
	}
	
	public void run() {
		double[] best = optimizer.minimize();
		System.out.println(best[0]);
		System.out.println(best[1]);
	}
	
	public class FunctionOptimizer extends Optimizer {
		
		public FunctionOptimizer(int iterations, double[] minimums, double[] maximums) {
			super(iterations, minimums, maximums);
		}

		@Override
		public double f(double[] input) {
			double x = input[0] - 2;
			double y = input[1] + 1;
			return x * x + y * y;
		}			
	}	
}
