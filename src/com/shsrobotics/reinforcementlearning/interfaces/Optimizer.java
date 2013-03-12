package com.shsrobotics.reinforcementlearning.interfaces;

/**
 * Optimizes a series of coordinates to either maximize or minimize a given 
 * function.
 * <p/>
 * @author team 2412.
 */
public abstract class Optimizer {	
	/**
	 * Minimize the function.
	 * @return the minimized coordinates
	 */
	public abstract double[] minimize();
	
	/**
	 * Minimize the function.
	 * @return the maximized coordinates.
	 */
	public abstract double[] maximize();
	
	/**
	 * The function to optimize.
	 * @param input the domain (input).
	 * @return the range (output).
	 */
	public abstract double f(double[] input);
}
