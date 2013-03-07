package com.shsrobotics.reinforcementlearning.math;

/**
 * This interface says this class can be used as a matrix. This means it has a 
 * way of getting values out of the matrix, and the values in the matrix are 
 * stored in a way that the structure is preserved.
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
