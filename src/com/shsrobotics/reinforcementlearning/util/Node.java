package com.shsrobotics.reinforcementlearning.util;

/**
 * A decision node in a decision tree.
 * @author Team 2412.
 */
public class Node {
	private Node yes;
	private Node no;
	private double inputCutoff;
	private double output;
	private boolean isLeaf = false;

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
	protected Node(Node yes, Node no, double input) {
		isLeaf = false;
		this.yes = yes;
		this.no = no;
		this.inputCutoff = input;
	}
	
	/**
	 * Add children to the node.
	 * @param yes the positive node.
	 * @param no the negative node.
	 * @param inputCutoff the input cutoff to use. 
	 */
	protected void addChildren(Node yes, Node no, double inputCutoff) {
		this.output = output;
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
	protected double get(double input) {
		if (isLeaf) {
			return output;
		} else {
			if (input >= inputCutoff) {
				return yes.get(input);
			} else {
				return no.get(input);
			}
		}
	}
}