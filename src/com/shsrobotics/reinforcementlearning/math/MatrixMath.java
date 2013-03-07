package com.shsrobotics.reinforcementlearning.math;

/**
 * Contains functions that return Matrices that when multiplied with another, do
 * that transformation or operation on them.
 * 
 * @author Max
 */
public class MatrixMath {
	private MatrixMath() {
		;
	}
	
	public BasicMatrix getPointMatrix(double[] position) {
		BasicMatrix m = new BasicMatrix(position.length + 1, 1);
		
		for ( int i = 0; i < position.length; i++ ) {
			
		}
		
		return m;
	}
	
}
