package com.shsrobotics.reinforcementlearning;

public class DataPoint {
	public double[] input;
	public double[] output;
	
	public DataPoint(double[] in, double[] out) {
		input = in;
		output = out;
	}
}
