package com.shsrobotics.reinforcementlearning.util;

/**
 * A decision node in a decision tree.
 */
public class Node {
	private Node yes;
	private Node no;
	private double inputCutoff;
	private double output;
	private boolean isLeaf = false;

	/**
	 * Create a node.
	 * @param inputCuttoff the input cutoff for deciding a yes or a no.
	 */
	protected Node(double output) {
		isLeaf = true;
		this.output = output;
	}	
	
	/**
	 * Create a node.
	 * @param yes the node/branch to follow if yes.
	 * @param no the node/branch to follow if no.
	 * @param inputCuttoff the input cutoff for deciding a yes or a no.
	 */
	protected Node(Node yes, Node no, double input) {
		isLeaf = false;
		this.yes = yes;
		this.no = no;
		this.inputCutoff = input;
	}
	
	protected void addChildren(Node yes, Node no, double output) {
		this.output = output;
		isLeaf = false;
	}

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