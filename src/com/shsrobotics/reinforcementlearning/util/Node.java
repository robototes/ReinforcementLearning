package com.shsrobotics.reinforcementlearning.util;


/**
 * Base decision tree node class.
 */
public abstract class Node {
	int index;
	
	protected Node(int index) {
		this.index = index;
	}
	
   /**
	* Get the value from this node.  Moves recursively down the tree.
	* @param input the input value
	* @return the output classification
	*/
   protected abstract double get(double input);
}
