package com.shsrobotics.reinforcementlearning.util;

/**
 * A Decision tree for use in random forests.
 */
public class RandomDecisionTree {
	private static Node root;
	private static Node lastUsedNode;
	private VariableSet[] data;
	private double[] averages;
	private String[] variables;
	private int variableSubset;
	private int numberOfVariables;	
	boolean[] usedVariables = new boolean[numberOfVariables]; // list of used variables while building tree
	
	private static final double LOG_2 = Math.log10(2.0);
	private int maxDepth;
	private int currentDepth;
	
	/**
	 * Create a decision tree.
	 * @param data the decision tree data.
	 */
	public RandomDecisionTree(VariableSet[] data, int variableSubset) {
		this.data = data;
		this.variableSubset = variableSubset;
		this.maxDepth = (int) (Math.log(variableSubset) / LOG_2 + 1); // maximum possible depth
		this.numberOfVariables = data[0].getKeys().length;
		
		averages = zeros(data.length); // find averages for splitting
		for (int example = 0; example < data.length; example++) {
			double[] dataExample = data[example].getValues();
			for (int variable = 0; variable < numberOfVariables; variable++) {
				averages[variable] += dataExample[variable] / data.length;					
			}
		}
		
		root = new Node(0.0);
		lastUsedNode = root;
		currentDepth = 0;
		buildTree(); // start building tree
	}
	
	/**
	 * Build the tree
	 * @param data 
	 */
	private void buildTree() {
		variables = data[0].getKeys();
		
		if (contains(usedVariables, false) || currentDepth == maxDepth) { // if all variables have been used
			maxDepth--;
			return;
		}
			
		double maximumGain = Double.POSITIVE_INFINITY;
		int maxGainIndex = -1;
		int totalCount = 0;
		for (int variable = 0; variable < variableSubset; variable++) { // select variables to base split
			int randIndex = (int) (Math.random() * variableSubset);
			if (usedVariables[randIndex] == true) {
				variable--; // search again
				continue;
			} else if (++totalCount - variableSubset > variables.length) {
				break;
			} else {
				usedVariables[randIndex] = true;
			}

			int yes = 0;
			for (int example = 0; example < data.length; example++) {
				if (data[example].get(variables[randIndex]) > averages[variable]) {
					yes++;
				}
			}
			double proportionY = yes / data.length;
			double proportionN = 1 - yes / data.length;

			double variableEntropy = -(proportionY * Math.log10(proportionY) - proportionN * Math.log10(proportionN)) / LOG_2;				
			if (variableEntropy > maximumGain) {
				maxGainIndex = variable;
				maximumGain = variableEntropy;
			}
		}

		for (int variable = 0; variable < variableSubset; variable++) { // reset array of used values for unused values
			if (variable != maxGainIndex) {
				usedVariables[variable] = false;
			}
		}

		//extend tree
		Node yes = new Node(1.0); // yes
		Node no = new Node(0.0); // no
		lastUsedNode.addChildren(yes, no, averages[maxGainIndex]);
		
		lastUsedNode = yes; currentDepth++; buildTree(); // build subtree for yes branch
		
		lastUsedNode = no; currentDepth++; buildTree(); // build subtree for no branch
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
     * Test an array for a boolean value
	 * @param array the array to search.
     * @param value the value to look for.
     * @param containts whether or not the array contains the value.
     */
    private boolean contains(boolean[] array, boolean value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) { // found
                return true;
            }
        }
        return false; // not found
    }
}
