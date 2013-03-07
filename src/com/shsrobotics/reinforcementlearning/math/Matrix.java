package com.shsrobotics.reinforcementlearning.math;

/**
 * Represents an m<i>x</i>n dimensional matrix and supports these basic
 * operations: <ul> <li>scaling</li> <li>matrix addition</li> <li>matrix
 * subtraction</li> <li>matrix multiplication</li> </ul>
 * <p/>
 * For more advanced operations see {@link MatrixMath}
 * <p/>
 * @author Max
 */
public interface Matrix {

	public int getRowSize();

	public int getColumSize();

	public double getValue(int row, int colum);

	public void setValue(int row, int colum, double value);

	public void scale(double scale);

	public Matrix add(Matrix m);

	public Matrix subtract(Matrix m);

	public Matrix multiply(Matrix right);
}
