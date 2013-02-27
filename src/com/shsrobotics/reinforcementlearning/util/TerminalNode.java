package com.shsrobotics.reinforcementlearning.util;

/**
 * A decision node in a decision tree.
 */
public class TerminalNode extends Node {
	private double output;

	/**
	 * Create a node.
	 * @param output the output.
	 */
	protected TerminalNode(double output, int index) {
		super(index);
		this.output = output;
	}		

	@Override
	protected double get(double input) {
		return output;
	}
}