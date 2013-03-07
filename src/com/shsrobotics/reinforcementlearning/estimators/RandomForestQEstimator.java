package com.shsrobotics.reinforcementlearning.estimators;

import com.shsrobotics.reinforcementlearning.util.DataPoint;
import com.shsrobotics.reinforcementlearning.util.RandomDecisionTree;

/**
 * Estimates Q Values using a random forest ensemble classifier for use in a {@link QLearner}.
 * @author Team 2412.
 */
public class RandomForestQEstimator {
	
	/**
	 * The actual random forest.
	 */
	private RandomDecisionTree[] forest;
	
	
	/**
	 * Minimum input values.
	 */
	private double[] minimums;
	/**
	 * Maximum input values.
	 */
	private double[] maximums;
	
	/**
	 * Create a Random Forest machine learner to estimate values.
	 * @param trees the number of trees in the forest.
	 */
	public RandomForestQEstimator(int trees, double[] minimums, double[] maximums) {
		this.forest = new RandomDecisionTree[trees];
	}
	
	/**
	 * Add data to the forest.
	 * Will erase existing data and re-train forest. There should be at least
	 * twenty examples per variable in the set.
	 * @param data the data to use to train the trees in the forest.
	 */
	public void addInitialData(DataPoint[] data) {
		int sampleSize = (int) (0.666 * data.length);
		int variableSubsetSize = data[0].getInputs().length;
		int numberOfTrees = forest.length;
		for (int i = 0; i < numberOfTrees; i++) {
			// add new tree and train it
			forest[i] = new RandomDecisionTree(bootstrapSample(sampleSize, data), 
				variableSubsetSize, minimums, maximums);
		}
	}
	
	/**
	 * Add a single data point. 
	 * The data point will be used to adjust cutoff values in each tree.
	 * @param data the data point to add.
	 */
	public void addNewData(DataPoint data) {
		
	}
	
	public double run(double[] input) {
		double sum = 0.0;
		int numberOfTrees = forest.length;
		for (int i = 0; i < numberOfTrees; i++) {
			sum += forest[i].run(input);
		}
		return sum / numberOfTrees;
	}
	
	
	
	/**
	 * Take a bootstrap sample of the data. 
	 * Data is sampled without replacement {@code size} number of times.
	 * @param size the size of the sample.
	 * @param data the data to sample.
	 * @return the sample of data.
	 */
	private DataPoint[] bootstrapSample(int size, DataPoint[] data) {
		DataPoint[] sample = new DataPoint[size]; // n
		int cases = data.length;
		for (int i = 0; i < size; i++) { // sample n times with replacement
			sample[i] = data[(int) Math.round(cases * Math.random())];
		}
		return sample;
	}
}
