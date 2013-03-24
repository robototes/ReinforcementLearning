package com.shsrobotics.reinforcementlearning.supervisedlearners;

import com.shsrobotics.reinforcementlearning.util.DataPoint;
import java.util.ArrayList;

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
	protected ArrayList<DataPoint> data;
	
	
	/**
	 * Create a supervised learner.
	 * @param minimums see {@link #minimums}
	 * @param maximums see {@link #maximums}
	 */
	public SupervisedLearner(double[] minimums, double[] maximums) {
		this.minimums = minimums;
		this.maximums = maximums;
		data = new ArrayList<>();
	}
	
	/**
	 * Update the model with new experiences.
	 * @param dataPoint the data point to add.	 * 
	 * @return the class, for chaining method calls.
	 */
	public abstract SupervisedLearner update(DataPoint dataPoint);
	
	/**
	 * Query the learner for a prediction.
	 * @param input the input values.
	 * @return the predicted output value.
	 */
	public abstract double query(DataPoint input);
}
