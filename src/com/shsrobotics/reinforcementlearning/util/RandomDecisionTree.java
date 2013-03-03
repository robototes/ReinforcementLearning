package com.shsrobotics.reinforcementlearning.util;

import java.lang.reflect.Method;

/**
 * A Decision tree for use in random forests.
 * @author Team 2412.
 */
public class RandomDecisionTree {
	private static Node root;
	private static Node lastUsedNode;
	private double[] averages;
	private String[] variables;
	private int variableSubset;
	private int numberOfVariables;	
	boolean[] usedVariables = new boolean[numberOfVariables]; // list of used variables while building tree
	
	private int currentKey;
	private DataPoint[] currentDataSubset;
	
	/**
	 * Create a decision tree.
	 * @param data the decision tree data.
	 */
	public RandomDecisionTree(DataPoint[] data, int variableSubset) {
		this.variableSubset = variableSubset;
		this.numberOfVariables = data[0].getInputKeys().length;
		
		averages = zeros(data.length); // find averages for splitting
		for (int example = 0; example < data.length; example++) {
			double[] dataExample = data[example].getInputs();
			for (int variable = 0; variable < numberOfVariables; variable++) {
				averages[variable] += dataExample[variable] / data.length;					
			}
		}
		
		root = new Node(0.0);
		lastUsedNode = root;
		buildTree(data); // start building tree
	}
	
	/**
	 * Build the tree
	 * @param data 
	 */
	private void buildTree(DataPoint[] dataSubset) {
		variables = dataSubset[0].getInputKeys();
		currentDataSubset = dataSubset;
							
		double minStdDev = Double.POSITIVE_INFINITY;
		double cutoff = 0;
		int minStdDevIndex = -1;
		int totalCount = 0;
		for (int variable = 0; variable < variableSubset; variable++) { // select variables to base split
			currentKey = (int) (Math.random() * variableSubset);
			if (usedVariables[currentKey] == true) {
				variable--; // search again
				continue;
			} else if (++totalCount - variableSubset > variables.length) {
				break;
			} else {
				usedVariables[currentKey] = true;
			}
			try {
				Optimizer optimizer = new Optimizer(1, 8, 
					zeros(1), infinities(1));
				Method function = getClass().getMethod("optimizerStandardDeviation", (Class<?>) null);
				double nodeCutoff = optimizer.minimize(function)[0];
				double nodeMinStdDev = optimizerStandardDeviation(nodeCutoff);
				if (nodeMinStdDev < minStdDev) {
					cutoff = nodeCutoff;
					minStdDev = nodeMinStdDev;
					minStdDevIndex = variable;
				}
			} catch (NoSuchMethodException | SecurityException ex) { }
		}
		
		currentKey = minStdDevIndex;
		
		for (int variable = 0; variable < variableSubset; variable++) { // reset array of used values for unused values
			if (variable != minStdDevIndex) {
				usedVariables[variable] = false;
			}
		}
		
		// find what data passes cutoff filter
		DataPoint[] positiveSubset = getDataSubset(dataSubset, cutoff, true);
		DataPoint[] negativeSubset = getDataSubset(dataSubset, cutoff, false);
		
		//extend tree
		Node yes = new Node(average(positiveSubset)); // yes
		Node no = new Node(average(negativeSubset)); // no
		lastUsedNode.addChildren(yes, no, averages[minStdDevIndex]);
		
		if (positiveSubset.length > 5) { // stopping rule
			lastUsedNode = yes; buildTree(positiveSubset); // build subtree for yes branch
		}
		
		if (negativeSubset.length > 5) { // stopping rule
			lastUsedNode = no; buildTree(negativeSubset); // build subtree for no branch
		}
	}
	
	/**
	 * Run the decision tree on a new value.
	 * @param input the new value.
	 * @return the tree's decision.
	 */
	public double run(double input) {
		return root.get(input);
	}
	
	/**
     * Fill an array with zeros
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
     * Fill an array with infinities
     * @param size the size of the array
     * @return The array.
     */
    private double[] infinities(int size) {
        double[] toReturn = new double[size];
        for (int i = 0; i < size; i++) {
                toReturn[i] = Double.POSITIVE_INFINITY;
        }
        return toReturn;
    }
	
	/**
	 * Average node output values for terminal nodes.
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
	 * @param currentData the data to isolate from.
	 * @param cutoff the cutoff value.
	 * @param positiveNode whether or not to test for successes ({@code true}) or failures ({@code false}).
	 * @return the isolated data.
	 */
	private DataPoint[] getDataSubset(DataPoint[] currentData, double cutoff, boolean positiveNode) {
		DataPoint[] toReturn = null;
		for (int i = 0; i < currentData.length; i++) {
			double value = currentData[i].getInputs()[currentKey];
			if (value > cutoff && positiveNode) {
				toReturn[i] = currentData[i];
			}
			if (value <= cutoff && !positiveNode) {
				toReturn[i] = currentData[i];
			}
		}
		return toReturn;
	}
	
	/**
	 * Find standard deviation, for use in the optimizer.
	 * @param cutoff the test cutoff value.
	 * @return the standard deviation using the cuttoff value.
	 */
	private double optimizerStandardDeviation(double cutoff) {
		DataPoint[] data = getDataSubset(currentDataSubset, cutoff, true);
		return standardDeviation(data);
	}
	
	/**
	 * Calculate standard deviation.
	 * @param data the data to use.
	 * @return the standard deviation
	 */
	private double standardDeviation(DataPoint[] data) {
		double sum = 0.0;	
		
		//find average
		for (int i = 0; i < data.length; i++) {
			sum += data[i].getInputs()[currentKey];
		}
		double mean = sum / data.length;
		
		//find sample standard deviation
		sum = 0.0;
		for (int i = 0; i < data.length; i++) {
			sum += Math.pow(data[i].getOutputs()[0] - mean , 2);
		}
		
		return sum / (data.length - 1);
	}
}
