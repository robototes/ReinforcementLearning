package com.shsrobotics.reinforcementlearning.math;

/**
 * This class is useful when a matrix has been composed, but some of the values
 * in it need to change. For example, using a rotation matrix composed with a
 * translation matrix would require a recomposition every time the angle in the
 * rotation matrix changed. With this class, the composition does not need to be
 * computed again.
 * <p/>
 * @author Max
 */
public class ParameteredMatrix implements Matrix {

	@Override
	public int getRowSize() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getColumSize() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double getValue(int row, int colum) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setValue(int row, int colum, double value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void scale(double scale) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Matrix add(Matrix m) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Matrix subtract(Matrix m) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Matrix multiply(Matrix right) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
