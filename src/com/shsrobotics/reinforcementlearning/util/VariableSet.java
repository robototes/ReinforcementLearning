package com.shsrobotics.reinforcementlearning.util;

public class VariableSet {
	String[] keys;
	double[] values;
	
	/**
	 * A set of variables to be run through a decision tree.
	 * @param keys the keys.
	 * @param values the values.
	 */
	public VariableSet(String[] keys, double[] values) {
		this.keys = keys;
		this.values = values;
	}
	
	/**
	 * Get the value associated with a key
	 * @param key the key.
	 * @return the value.
	 */
	public double get(String key) {
		return values[indexOf(key)];
	}
	
	/**
	 * Set a key-value pair.
	 * @param key the key.
	 */
	public void set(String key, double value) {
		values[indexOf(key)] = value;
	}
	
	/**
     * Finds the  value from an array of string keys
     * @param value the value to look for.
     * @param keys the array of string keys.
     */
    private int indexOf(String value) {
        for (int i = 0; i < keys.length; i++) {
            if (value.equals(keys[i])) { // found
                return i;
            }
        }
        return -1; // not found
    }
}
