package com.shsrobotics.reinforcementlearning.estimators;

import com.shsrobotics.reinforcementlearning.util.DataPoint;
import com.shsrobotics.reinforcementlearning.util.RandomDecisionTree;

/**
 * Estimates Q Values using a random forest ensemble classifier for use in a
 * {@link QLearner}.
 * <p/>
 * @author Team 2412.
 */
public class RandomForest {

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
	 * The maximum depth of each tree.
	 */
	private int maxDepth;
	
	/**
	 * The probability of adding a given data point to a tree.
	 */
	private double w = 0.4;

	/**
	 * Create a Random Forest machine learner to estimate values.
	 * <p/>
	 * @param trees the number of trees in the forest.
	 */
	public RandomForest(int trees, double[] minimums, double[] maximums, int maxDepth) {
		this.forest = new RandomDecisionTree[trees];
		this.maxDepth = maxDepth;
		this.minimums = minimums;
		this.maximums = maximums;
	}

	/**
	 * Add sample to the forest. Will erase existing sample and re-train forest.
	 * There should be at least twenty examples per variable in the set.
	 * <p/>
	 * @param sample the sample to use to train the trees in the forest.
	 */
	public void addInitialData(DataPoint[] data) {
		int sampleSize = data.length;
		int variableSize = minimums.length;
		int numberOfTrees = forest.length;
		int subsetSize = (int) Math.ceil(variableSize * w);
		if (subsetSize > variableSize) { // happens
			subsetSize = variableSize;
		}
		BootstrapSample[] samples = new BootstrapSample[numberOfTrees];
		for (int i = 0; i < numberOfTrees; i++) {
			samples[i] = takeBootstrapSample(sampleSize, data);
			// add new tree and train it
			forest[i] = new RandomDecisionTree(samples[i].sample,
				subsetSize, minimums, maximums, maxDepth);
		}
	}

	/**
	 * Get the random forest prediction.
	 * <p/>
	 * @param input the input value.
	 * @return the random forest output.
	 */
	public double run(double[] input) {
		double treeAverageSum = 0.0;
		int numberOfTrees = forest.length;
		for (int i = 0; i < numberOfTrees; i++) {
			treeAverageSum += forest[i].run(input);
		}
		return treeAverageSum / numberOfTrees; // average each tree's output
	}

	/**
	 * Take a bootstrap sample of the sample. Data is sampled with replacement
	 * {@code size} number of times.
	 * <p/>
	 * @param size the size of the sample.
	 * @param sample the sample to sample.
	 * @return the sample of sample. Index 1 is the sample, index 2 is the OOB
	 * sample.
	 */
	private BootstrapSample takeBootstrapSample(int size, DataPoint[] data) {
		DataPoint[] sample = new DataPoint[size]; // n
		int cases = data.length;
		boolean[] used = new boolean[cases];
		for (int i = 0; i < size; i++) { // sample n times with replacement
			int index = (int) (cases * Math.random());
			sample[i] = data[index];
			used[index] = true;
		}
		return new BootstrapSample(sample, used);
	}

	/**
	 * Holds result of a bootstrap sample.
	 */
	public class BootstrapSample {

		/**
		 * The sample.
		 */
		public DataPoint[] sample;

		/**
		 * List of which data points were used.
		 */
		public boolean[] used;

		/**
		 * Create a bootstrap sample results list.
		 * <p/>
		 * @param sample the data sample.
		 * @param used a list of which data points were used.
		 */
		public BootstrapSample(DataPoint[] sample, boolean[] used) {
			this.sample = sample;
			this.used = used;
		}
	}
}
