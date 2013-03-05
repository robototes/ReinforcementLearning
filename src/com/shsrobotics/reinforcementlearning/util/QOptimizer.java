package com.shsrobotics.reinforcementlearning.util;

/**
 * Optimizes Q-Values.
 */
class QOptimizer extends Optimizer {
	
	/**
	 * Environment parameter values.
	 */
	private double[] environment;

	/**
	 * Create a Q-Value optimizer.
	 * @param iterations the number of iterations to use.
	 * @param minimums the minimum variable values.
	 * @param maximums the maximum variable values.
	 */
	protected QOptimizer(int iterations, double[] minimums, double[] maximums) {
		super(minimums.length, iterations, minimums, maximums);
	}
	
	@Override
	protected double f(double[] input) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * Set the state values to use in the optimizer.
	 * @param state the current environment state.
	 */
	protected void setState(double[] state) {
		this.environment = state;
	}
	
}
