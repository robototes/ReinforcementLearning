package com.shsrobotics.reinforcementlearning;

public class DataPoint {
	public final double[] input;
	public final double[] output;

	public DataPoint(double[] input, double[] output) {
		this.input = input;
		this.output = output;
	}
}