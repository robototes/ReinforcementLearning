package com.shsrobotics.reinforcementlearning.math;

/** 
 *	Represents an m<i>x</i>n dimensional matrix and supports these basic operations:
 *	<ul>
 *		<li>scalar multiply</li>	
 *		<li>scalar divide</li>
 *		<li>matrix addition</li>
 *		<li>matrix subtraction</li>
 *		<li>matrix multiplication</li>
 *	</ul>
 *	
 *	For more advanced operations see {@link MatrixMath}
 *	
 *	@author Max
 */
public class Matrix {
	
	double[] body;
	
	private Matrix() {
		;
	}
	
	public Matrix(int rows, int colums) {
		body = new double[rows*colums];
	}
	
	public Matrix(int size) {
		body = new double[size*size];
	}
	
}
