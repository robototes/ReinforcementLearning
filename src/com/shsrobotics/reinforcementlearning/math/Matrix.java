package com.shsrobotics.reinforcementlearning.math;

/** 
 *	Represents an m<i>x</i>n dimensional matrix and supports these basic operations:
 *	<ul>
 *		<li>scaling</li>
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
	
	double[][] body;
	
	private Matrix() {
		;
	}
	
	public Matrix(int rows, int colums) {
		body = new double[rows][colums];
	}
	
	public Matrix(int size) {
		body = new double[size][size];
	}
	
	/**
	 * Scales <code>this</code> by multiplying each value in the Matrix by <code>scale</code>.
	 * 
	 * @param scale The scalar to scale by.
	 */
	
	public void scale(double scale) {
		for ( int r = 0; r < body.length; r++ ) {
			for ( int c = 0; c < body[0].length; c++ ) {
				body[r][c] *= scale;
			}
		}
	}
	
	/**
	 * Adds <code>this</code> and <code>m</code>.
	 * 
	 * @param m The Matrix to add.
	 * @return The resulting Matrix.
	 */
	
	public Matrix add(Matrix m) {
		
		Matrix result = new Matrix(this.body.length,this.body[0].length);
		
		if ( this.body.length != m.body.length) {
			throw new MatrixSizeError("Incompatible row sizes:", m.body.length, this.body.length);
		}
		else if ( this.body[0].length != m.body[0].length ) {
			throw new MatrixSizeError("Incompatible colum sizes:", m.body.length, this.body.length);
		}
		
		for ( int r = 0; r < body.length; r++ ) {
			for ( int c = 0; c < body[0].length; c++ ) {
				result.body[r][c] = this.body[r][c] + m.body[r][c];
			}
		}
		
		return result;
	}
	
	/**
	 * Subtracts <code>m</code> from <code>this</code>.
	 * @param m The Matrix to subtract.
	 * @return The resulting Matrix.
	 */
	
	public Matrix subtract(Matrix m) {
		
		if ( this.body.length != m.body.length) {
			throw new MatrixSizeError("Incompatible row sizes:", m.body.length, this.body.length);
		}
		else if ( this.body[0].length != m.body[0].length ) {
			throw new MatrixSizeError("Incompatible colum sizes:", m.body.length, this.body.length);
		}
		
		Matrix result = new Matrix(this.body.length,this.body[0].length);
		
		for ( int r = 0; r < body.length; r++ ) {
			for ( int c = 0; c < body[0].length; c++ ) {
				result.body[r][c] = this.body[r][c] - m.body[r][c];
			}
		}
		
		return result;
	}
	
}
