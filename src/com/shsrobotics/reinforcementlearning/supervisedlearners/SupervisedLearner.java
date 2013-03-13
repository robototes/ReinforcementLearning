package com.shsrobotics.reinforcementlearning.supervisedlearners;

import com.shsrobotics.reinforcementlearning.util.DataPoint;

/**
 * A supervised learner.
 */
public abstract class SupervisedLearner {
	/**
	 * Minimum values for each input variable.
	 */
	protected double[] minimums;
	/**
	 * Maximum values for each input variable.
	 */
	protected double[] maximums;
	
	/**
	 * The model data.
	 */
	protected DataPoint[] data;
	
	
	/**
	 * Create a supervised learner.
	 * @param minimums see {@link #minimums}
	 * @param maximums see {@link #maximums}
	 */
	public SupervisedLearner(double[] minimums, double[] maximums) {
		this.minimums = minimums;
		this.maximums = maximums;
	}
	
	/**
	 * Update the model with new experiences.
	 * @param input the input values.
	 * @param output the correct output value.
	 */
	public abstract void update(double[] input, double output);
	
	/**
	 * Query the learner for a prediction.
	 * @param input the input values.
	 * @return the predicted output value.
	 */
	public abstract double query(double[] input);
}
