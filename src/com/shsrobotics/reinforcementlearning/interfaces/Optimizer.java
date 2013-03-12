package com.shsrobotics.reinforcementlearning.interfaces;

import com.shsrobotics.reinforcementlearning.util.DataPoint;

public abstract class Optimizer {	
	/**
	 * Minimize the function.
	 * @return the minimized coordinates
	 */
	public abstract DataPoint[] minimize();
	
	/**
	 * Minimize the function.
	 * @return the maximized coordinates.
	 */
	public abstract DataPoint[] maximize();
	
	/**
	 * The function to optimize.
	 * @param input the domain (input).
	 * @return the range (output).
	 */
	public abstract double f(double[] input);
}
