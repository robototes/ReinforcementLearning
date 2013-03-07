package com.shsrobotics.reinforcementlearning.math;

/**
 * Thrown when Matrix sizes are not compatible for a given operation.
 * <p/>
 * @author Max
 */
public class MatrixSizeError extends Error {

	/**
	 * Call this when there is at least one dimension that is incompatible
	 * during a Matrix operation.
	 * <p/>
	 * Note that the found parameter is what was found in the second Matrix in
	 * the parameter of the Matrix operation, NOT the
	 * <code>this</code> object.
	 * <p/>
	 * @param msg A statement that says what was incompatible.
	 * @param found The size of the dimension that was incompatible.
	 * @param required The required size of the other matrix.
	 */
	public MatrixSizeError(String msg, int found, int required) {
		super(msg + " Expected " + required + ", got " + found);
	}
}
