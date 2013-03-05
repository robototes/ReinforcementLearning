package com.shsrobotics.reinforcementlearning.util;

/**
 * For use in {@link RandomDecisionTree}. Minimizes entropy by changing split
 * parameters.
 */
class VarianceMinimizer extends Optimizer {
	
	/**
	 * Current data to use in minimization.
	 */
	private DataPoint[] data;
	/**
	 * How much data.
	 * Used to save array lookups.
	 */
	private int dataLength;
	/**
	 * The current variable to use in minimization.
	 */
	private int variable;
	
	/**
	 * The current best split value (cutoff).
	 */
	private double bestSplit = 0.0;

	/**
	 * Create an variance minimizer.
	 * <p/>
	 * @param minimums minimum variable values.
	 * @param maximums maximum variable values.
	 */
	protected VarianceMinimizer(double[] minimums, double[] maximums) {
		super(minimums.length, 8, minimums, maximums);
	}

	@Override
	protected double f(double[] input) {
		return nodeVariance(input[0]);
	}
	
	/**
	 * Calculate the best split for the given variable.
	 * @return the split to minimize variance of a node.
	 */
	protected double bestSplit(int variable) {
		this.variable = variable;
		bestSplit = minimize()[0];
		return bestSplit;
	}
	
	/**
	 * Get the entropy value associated with the best split.
	 * @return the lowest variance possible.
	 */
	protected double getMinimumVariance() {
		return nodeVariance(bestSplit);
	}
	
	/**
	 * Set the data to be used in the variance calculation.
	 * <p/>
	 * @param subset the data to use.
	 */
	protected void setDataSubset(DataPoint[] subset) {
		this.data = subset;
		this.dataLength = subset.length; // local scope is faster lookup
	}
	
	/**
	 * Calculate the variance of a node split.
	 * <p/>
	 * @param split the cutoff value to split the data with.
	 * @return the average of the variance for each branch.
	 */
	private double nodeVariance(double split) {
		DataPoint[] positiveSubset = getDataSubset(split, true); // yes node
		DataPoint[] negativeSubset = getDataSubset(split, false); // no node
		double positiveStdDev = individualSampleVariance(positiveSubset);
		double negativeStdDev = individualSampleVariance(negativeSubset);

		return (positiveStdDev + negativeStdDev) / 2; // average of standard devations
	}

	/**
	 * Calculate data variance.
	 * <p/>
	 * @param subset the data subset to calculate a variance for.
	 * @return the variance of the data subset.
	 */
	private double individualSampleVariance(DataPoint[] subset) {
		int subsetLength = subset.length;
		double sum = 0.0;

		//find average
		for (int i = 0; i < subsetLength; i++) {
			sum += subset[i].getInputs()[variable];
		}
		double mean = sum / subsetLength;

		//find sample standard deviation
		sum = 0.0;
		for (int i = 0; i < subsetLength; i++) {
			sum += Math.pow(subset[i].getOutputs()[0] - mean, 2);
		}

		return sum / (subsetLength - 1);
	}

	/**
	 * Find the data that would follow a branch after a decision node.
	 * <p/>
	 * @param cutoff the cutoff value.
	 * @param positiveNode whether or not to test for successes ({@code true})
	 * or failures ({@code false}).
	 * @return the isolated data.
	 */
	private DataPoint[] getDataSubset(double cutoff, boolean positiveNode) {
		DataPoint[] toReturn = null;
		for (int i = 0; i < dataLength; i++) {
			double value = data[i].getInputs()[variable];
			if (value > cutoff && positiveNode) {
				toReturn[i] = data[i];
			}
			if (value <= cutoff && !positiveNode) {
				toReturn[i] = data[i];
			}
		}
		return toReturn;
	}
}
