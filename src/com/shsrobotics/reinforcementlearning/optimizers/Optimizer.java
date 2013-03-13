package com.shsrobotics.reinforcementlearning.optimizers;

/**
 * Optimizes a series of coordinates to either maximize or minimize a given 
 * function.
 * <p/>
 * @author team 2412.
 */
public interface Optimizer {	
	/**
	 * Minimize the function.
	 * Implementations should use {@code final} keyword.
	 * @return the minimized coordinates
	 */
	public double[] minimize();
	
	/**
	 * Minimize the function.
	 * Implementations should use {@code final} keyword.
	 * @return the maximized coordinates.
	 */
	public double[] maximize();
	
	/**
	 * The function to optimize.
	 * Implementations should use {@code abstract} keyword.
	 * @param input the domain (input).
	 * @return the range (output).
	 */
	public double f(double[] input);
}
