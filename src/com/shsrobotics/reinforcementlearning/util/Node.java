package com.shsrobotics.reinforcementlearning.util;

/**
 * A decision node in a decision tree.
 * @author Team 2412.
 */
public class Node {
	/**
	 * The node to go to when the input exceeds the cutoff.
	 */
	private Node yes;
	/**
	 * The node to go to when the input fails to meet the cutoff.
	 */
	private Node no;
	/**
	 * The input cutoff.
	 */
	private double inputCutoff;
	/**
	 * The output value (for a leaf).
	 */
	private double output;
	/**
	 * Whether or not the node is acting as a leaf.
	 * Represents whether or not the node has children.
	 */
	private boolean isLeaf = false;
	
	/**
	 * The variable index of the split variable.
	 */
	private int variableIndex;

	/**
	 * Create a node.
	 * @param output the output from the node
	 */
	protected Node(double output) {
		isLeaf = true;
		this.output = output;
	}	
	
	/**
	 * Create a node.
	 * @param yes the node/branch to follow if yes.
	 * @param no the node/branch to follow if no.
	 * @param input the input cutoff value. 
	 */
	protected Node(Node yes, Node no, double input, int variableIndex) {
		isLeaf = false;
		this.yes = yes;
		this.no = no;
		this.inputCutoff = input;
		this.variableIndex = variableIndex;
	}
	
	/**
	 * Add children to the node.
	 * @param yes the positive node.
	 * @param no the negative node.
	 * @param inputCutoff the input cutoff to use. 
	 */
	protected void addChildren(Node yes, Node no, double inputCutoff, int variableIndex) {
		this.yes = yes;
		this.no = no;
		this.inputCutoff = inputCutoff;
		this.variableIndex = variableIndex;
		isLeaf = false;
	}
	
	/**
	 * Get the Yes node.
	 * @return the node.
	 */
	protected Node getPositiveChild() {
		return yes;
	}
	
	/**
	 * Get the No node.
	 * @return the node.
	 */
	protected Node getNegativeChild() {
		return no;
	}

	/**
	 * Recursively follow the tree to get a value.
	 * @param input the input to the node.
	 * @return the value from further down the tree.
	 */
	protected double get(double[] input) {
		if (isLeaf) {
			return output;
		} else {
			if (input[variableIndex] >= inputCutoff) {
				return yes.get(input);
			} else {
				return no.get(input);
			}
		}
	}
}