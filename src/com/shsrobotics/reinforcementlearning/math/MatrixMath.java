package com.shsrobotics.reinforcementlearning.math;

/**
 * Contains functions that return Matrices that when multiplied with another, do
 * that transformation or operation on them.
 * <p/>
 * @author Max
 */
public class MatrixMath {

	/**
	 * Private constructor because it is a static utility class. <i>MY</i>
	 * private commodities.
	 */
	private MatrixMath() {
		;
	}

	/**
	 * Returns a position matrix with size {@code position.length}<i>x</i>1.
	 * <p/>
	 * @param position The array that represents the points position.
	 * @param rateOfChange The value in the position of dimension
	 * {@code position.length}. Use {@link getPointMatrix(double[] position)} if
	 * you don't know what you are doing.
	 * @return A matrix that represents the point.
	 */
	public static BasicMatrix getPointMatrix(double[] position, double rateOfChange) {
		BasicMatrix m = new BasicMatrix(position.length + 1, 1);

		for (int i = 0; i < position.length; i++) {
			m.setValue(i, 0, position[i]);
		}
		m.setValue(position.length, 0, rateOfChange);

		return m;
	}

	/**
	 * Returns a position matrix with size {@code position.length}<i>x</i>1.
	 * <p/>
	 * @param position The array that represents the points position.
	 * @return A matrix that represents the point.
	 */
	public static BasicMatrix getPointMatrix(double[] position) {
		return MatrixMath.getPointMatrix(position, 1);
	}
}
