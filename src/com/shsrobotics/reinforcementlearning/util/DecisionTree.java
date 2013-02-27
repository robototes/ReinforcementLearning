package com.shsrobotics.reinforcementlearning.util;

import java.util.Map;

/**
 * A Decision tree.
 */
public class DecisionTree {
	Node[] nodes;
	
	/**
	 * Create a decision tree.
	 */
	public DecisionTree(double[][] data, ) {
		nodes[0] = new Node(0.0);
	}
	
	/**
	 * Run the decision tree on a new value.
	 * @param input the new value.
	 * @return the tree's decision.
	 */
	public double run(double input) {
		return nodes[0].get(input);
	}
}
