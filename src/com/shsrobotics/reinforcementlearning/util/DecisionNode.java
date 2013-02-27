package com.shsrobotics.reinforcementlearning.util;

/**
 * A decision node in a decision tree.
 */
public class DecisionNode extends Node {
	private Node yes;
	private Node no;
	double inputCutoff;

	/**
	 * Create a node.
	 * @param yes the node to be fired when the input exceeds the cutoff.
	 * @param no the node to be fired when the input fails to meet the cutoff.
	 * @param inputCuttoff the input cutoff.
	 */
	protected DecisionNode(Node yes, Node no, double inputCuttoff, int index) {
		super(index);
		this.yes = yes;
		this.no = no;
		this.inputCutoff = inputCuttoff;
	}		

	@Override
	protected double get(double input) {
		if (input >= inputCutoff) {
			return yes.get(input);
		} else {
			return no.get(input);
		}
	}
}