package com.shsrobotics.reinforcementlearning.util;

public class DataPoint {
	private String[] inputKeys;
	private double[] input;
	private String[] outputKeys;
	private double[] output;
	
	/**
	 * A set of variables to be run through a decision tree.
	 * @param inputKeys the input keys.
	 * @param input the input values.
	 * @param outputKeys  the output keys.
	 * @param output the output values.
	 */
	public DataPoint(String[] inputKeys, double[] input, String[] outputKeys, double[] output) {
		this.inputKeys = inputKeys;
		this.input = input;
		this.outputKeys = outputKeys;
		this.output = output;
	}
	
		
	/**
	 * Get the input value associated with a key
	 * @param key the key.
	 * @return the value.
	 */
	public double getInput(String key) {
		return input[indexOf(key)];
	}
	
	/**
	 * Get the output value associated with a key
	 * @param key the key.
	 * @return the value.
	 */
	public double getOutput(String key) {
		return output[indexOf(key)];
	}
	
	/**
	 * Set a key-value pair.
	 * @param key the key.
	 */
	public void set(String key, double value) {
		input[indexOf(key)] = value;
	}
	
	/**
	 * Get all of the input keys.
	 * @return the keys.
	 */
	public String[] getInputKeys() {
		return inputKeys;
	}
	
	/**
	 * Get all of the input values.
	 * @return the values.
	 */
	public double[] getInputs() {
		return input;
	}
	
	/**
	 * Get all of the output keys.
	 * @return the keys.
	 */
	public String[] getOutputKeys() {
		return outputKeys;
	}
	
	/**
	 * Get all of the output values.
	 * @return the values.
	 */
	public double[] getOutputs() {
		return output;
	}
	
	/**
     * Finds the  value from an array of string keys
     * @param value the value to look for.
     * @param keys the array of string keys.
     */
    private int indexOf(String value) {
        for (int i = 0; i < inputKeys.length; i++) {
            if (value.equals(inputKeys[i])) { // found
                return i;
            }
        }
        return -1; // not found
    }
}
