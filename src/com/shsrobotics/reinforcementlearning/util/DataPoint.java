package com.shsrobotics.reinforcementlearning.util;

/**
 * A data point, to be used with a {@link QEstimator}.
 */
public class DataPoint {
	public double[] input;
	public double[] output;
	
	/**
     * Create a data point.
     * @param in array of inputs (domain).
     * @param out array of outputs (range).
     */
    public DataPoint(double[] in, double[] out) {
		input = in;
		output = out;
	}
}
