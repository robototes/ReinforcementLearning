package com.shsrobotics.reinforcementlearning.util;

/**
 * A Decision tree for use in random forests.
 * <p/>
 * @author Team 2412.
 */
public class RandomDecisionTree {

	/**
	 * The root tree node. Data is run through the tree starting here.
	 */
	private static Node root;
	
	/**
	 * A record of the last used node, for recursion purposes.
	 */
	private static Node lastUsedNode;
	
	/**
	 * A list of variable keys, in String form.
	 */
	private String[] variables;
	
	/**
	 * The number of variables to randomly select and use at each node.
	 */
	private int variableSubset;
	
	/**
	 * The total number of variables.
	 */
	private int numberOfVariables;
	
	/**
	 * A list of which variables have been used so far in the tree.
	 */
	boolean[] usedVariables = new boolean[numberOfVariables]; // list of used variables while building tree
	
	/**
	 * An optimizer/minimizer class for minimizing variance at a split. <p />
	 * {@link VarianceMinimizer}
	 */
	private VarianceMinimizer varianceMinimizer;

	/**
	 * Create a decision tree.
	 * <p/>
	 * @param data the decision tree data.
	 */
	public RandomDecisionTree(DataPoint[] data, int variableSubset, double[] minimums, double[] maximums) {
		this.variableSubset = variableSubset;
		this.numberOfVariables = data[0].getInputKeys().length;

		varianceMinimizer = new VarianceMinimizer(minimums, maximums);

		root = new Node(0.0);
		lastUsedNode = root;
		buildTree(data); // start building tree
	}

	/**
	 * Build the tree.
	 * <p/>
	 * @param dataSubset the data to build the tree with.
	 */
	private void buildTree(DataPoint[] dataSubset) {
		variables = dataSubset[0].getInputKeys();

		double minVariance = Double.POSITIVE_INFINITY;
		double cutoff = 0;
		int minVarianceIndex = -1;
		int totalCount = 0;
		for (int variable = 0; variable < variableSubset; variable++) { // select variables to base split
			int randomVariable = (int) (Math.random() * variableSubset);
			if (usedVariables[randomVariable] == true) {
				variable--; // search again
				continue;
			} else {
				if (++totalCount - variableSubset > variables.length) {
					break;
				} else {
					usedVariables[randomVariable] = true;
				}
			}
			varianceMinimizer.setDataSubset(dataSubset);
			double suggestedCutoff = varianceMinimizer.bestSplit(randomVariable);
			double variance = varianceMinimizer.getMinimumVariance();
			if (variance < minVariance) { // if best so far
				minVarianceIndex = randomVariable;
				cutoff = suggestedCutoff;
				minVariance = variance;
			}
		}

		for (int variable = 0; variable < variableSubset; variable++) { // reset array of used values for unused values
			if (variable != minVarianceIndex) {
				usedVariables[variable] = false;
			}
		}

		// find what data passes cutoff filter
		DataPoint[] positiveSubset = getDataSubset(dataSubset, minVarianceIndex, cutoff, true);
		DataPoint[] negativeSubset = getDataSubset(dataSubset, minVarianceIndex, cutoff, false);

		//extend tree
		Node yes = new Node(average(positiveSubset)); // yes
		Node no = new Node(average(negativeSubset)); // no
		lastUsedNode.addChildren(yes, no, cutoff);

		if (positiveSubset.length > 5) { // stopping rule
			lastUsedNode = yes;
			buildTree(positiveSubset); // build subtree for yes branch
		}

		if (negativeSubset.length > 5) { // stopping rule
			lastUsedNode = no;
			buildTree(negativeSubset); // build subtree for no branch
		}
	}

	/**
	 * Run the decision tree on a new value.
	 * <p/>
	 * @param input the new value.
	 * @return the tree's decision.
	 */
	public double run(double input) {
		return root.get(input);
	}

	/**
	 * Fill an array with zeros
	 * <p/>
	 * @param size the size of the array
	 * @return The array.
	 */
	private double[] zeros(int size) {
		double[] toReturn = new double[size];
		for (int i = 0; i < size; i++) {
			toReturn[i] = 0;
		}
		return toReturn;
	}

	/**
	 * Average node output values for terminal nodes.
	 * <p/>
	 * @param data the data to average.
	 * @return the average.
	 */
	private double average(DataPoint[] data) {
		double sum = 0.0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i].getOutputs()[0];
		}
		return sum / data.length;
	}

	/**
	 * Find the data that would follow a branch after a decision node.
	 * <p/>
	 * @param currentData the data to isolate from
	 * @param index the variable index to branch based on.
	 * @param cutoff the cutoff value.
	 * @param positiveNode whether or not to test for successes ({@code true})
	 * or failures ({@code false}).
	 * @return the isolated data.
	 */
	private DataPoint[] getDataSubset(DataPoint[] currentData, int index, double cutoff, boolean positiveNode) {
		DataPoint[] toReturn = null;
		for (int i = 0; i < currentData.length; i++) {
			double value = currentData[i].getInputs()[index];
			if (value > cutoff && positiveNode) {
				toReturn[i] = currentData[i];
			}
			if (value <= cutoff && !positiveNode) {
				toReturn[i] = currentData[i];
			}
		}
		return toReturn;
	}
}
