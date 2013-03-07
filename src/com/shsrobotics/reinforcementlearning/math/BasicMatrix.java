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
public class BasicMatrix {
	
	double[][] body;
	
	private BasicMatrix() {
		;
	}
	
	public BasicMatrix(int rows, int colums) {
		body = new double[rows][colums];
	}
	
	public BasicMatrix(int size) {
		body = new double[size][size];
	}
	
	/**
	 * Scales <code>this</code> by multiplying each value in the BasicMatrix by <code>scale</code>.
	 * 
	 * @param scale The scalar to scale by.
	 */
	
	final public void scale(double scale) {
		for ( int r = 0; r < body.length; r++ ) {
			for ( int c = 0; c < body[0].length; c++ ) {
				body[r][c] *= scale;
			}
		}
	}
	
	/**
	 * Adds <code>this</code> and <code>m</code>.
	 * 
	 * @param m The BasicMatrix to add.
	 * @return The resulting BasicMatrix.
	 */
	
	final public BasicMatrix add(BasicMatrix m) {
		
		BasicMatrix result = new BasicMatrix(this.body.length,this.body[0].length);
		
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
	 * @param m The BasicMatrix to subtract.
	 * @return The resulting BasicMatrix.
	 */
	
	final public BasicMatrix subtract(BasicMatrix m) {
		
		if ( this.body.length != m.body.length) {
			throw new MatrixSizeError("Incompatible row sizes:", m.body.length, this.body.length);
		}
		else if ( this.body[0].length != m.body[0].length ) {
			throw new MatrixSizeError("Incompatible colum sizes:", m.body.length, this.body.length);
		}
		
		BasicMatrix result = new BasicMatrix(this.body.length,this.body[0].length);
		
		for ( int r = 0; r < body.length; r++ ) {
			for ( int c = 0; c < body[0].length; c++ ) {
				result.body[r][c] = this.body[r][c] - m.body[r][c];
			}
		}
		
		return result;
	}
	
	/**
	 * Multiplies 2 Matrices using standard matrix multiplication.
	 * @param left The BasicMatrix on the left.
	 * @param right The BasicMatrix on the right.
	 * @return The 2 Matrices multiplied together.
	 */
	
	final public static BasicMatrix multiply(BasicMatrix left, BasicMatrix right) {
		if ( left.body[0].length != right.body.length ) {
			throw new MatrixSizeError("Colums of left Matrix did not match rows of right Matrix.", right.body.length, left.body[0].length);
		}
		
		BasicMatrix m = new BasicMatrix(left.body.length, right.body[0].length);
		
		double temp;
		
		for ( int ri = 0; ri < m.body[0].length; ri++ ) {
			for ( int ci = 0; ci < m.body.length; ci++ ) {
				temp = 0;
				for ( int i = 0; i < left.body[0].length; i++ ) {
					temp += left.body[ri][i] * right.body[i][ci];
				}
				m.body[ri][ci] = temp;
			}
		}
		
		return m;
	}
	
}
