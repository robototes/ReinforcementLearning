package com.shsrobotics.reinforcementlearning.math;

/**
 * Just an implementation of the {@link Matrix} interface.
 * <p/>
 * @author Max
 */
public class BasicMatrix implements Matrix {

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

	@Override
	public int getRowSize() {
		return this.body.length;
	}

	@Override
	public int getColumSize() {
		return this.body[0].length;
	}

	/**
	 * Scales
	 * <code>this</code> by multiplying each value in the BasicMatrix by
	 * <code>scale</code>.
	 * <p/>
	 * @param scale The scalar to scale by.
	 */
	@Override
	public void scale(double scale) {
		for (int r = 0; r < body.length; r++) {
			for (int c = 0; c < body[0].length; c++) {
				body[r][c] *= scale;
			}
		}
	}

	/**
	 * Adds
	 * <code>this</code> and
	 * <code>m</code>.
	 * <p/>
	 * @param m The BasicMatrix to add.
	 * @return The resulting BasicMatrix.
	 */
	@Override
	public BasicMatrix add(Matrix m) {

		BasicMatrix result = new BasicMatrix(this.body.length, this.body[0].length);

		if (this.body.length != m.getRowSize()) {
			throw new MatrixSizeError("Incompatible row sizes:", m.getRowSize(), this.body.length);
		} else if (this.body[0].length != m.getColumSize()) {
				throw new MatrixSizeError("Incompatible colum sizes:", m.getColumSize(), this.body.length);
		}

		for (int r = 0; r < body.length; r++) {
			for (int c = 0; c < body[0].length; c++) {
				result.body[r][c] = this.body[r][c] + m.getValue(r, c);
			}
		}

		return result;
	}

	/**
	 * Subtracts
	 * <code>m</code> from
	 * <code>this</code>.
	 * <p/>
	 * @param m The BasicMatrix to subtract.
	 * @return The resulting BasicMatrix.
	 */
	@Override
	public BasicMatrix subtract(Matrix m) {

		if (this.body.length != m.getRowSize()) {
			throw new MatrixSizeError("Incompatible row sizes:", m.getRowSize(), this.getRowSize());
		} else {
			if (this.body[0].length != m.getColumSize()) {
				throw new MatrixSizeError("Incompatible colum sizes:", m.getColumSize(), this.getColumSize());
			}
		}

		BasicMatrix result = new BasicMatrix(this.body.length, this.body[0].length);

		for (int r = 0; r < body.length; r++) {
			for (int c = 0; c < body[0].length; c++) {
				result.body[r][c] = this.body[r][c] - m.getValue(r, c);
			}
		}

		return result;
	}

	/**
	 * Multiplies 2 Matrices using standard matrix multiplication.
	 * <p/>
	 * @param left The BasicMatrix on the left.
	 * @param right The BasicMatrix on the right.
	 * @return The 2 Matrices multiplied together.
	 */
	@Override
	public BasicMatrix multiply(Matrix right) {
		if (this.body[0].length != right.getRowSize()) {
			throw new MatrixSizeError("Colums of left Matrix did not match rows of right Matrix.", right.getRowSize(), this.body[0].length);
		}

		BasicMatrix m = new BasicMatrix(this.body.length, right.getColumSize());

		double temp;

		for (int ri = 0; ri < m.body[0].length; ri++) {
			for (int ci = 0; ci < m.body.length; ci++) {
				temp = 0;
				for (int i = 0; i < this.body[0].length; i++) {
					temp += this.body[ri][i] * right.getValue(i, ci);
				}
				m.body[ri][ci] = temp;
			}
		}

		return m;
	}

	@Override
	public double getValue(int row, int colum) {
		return body[row][colum];
	}

	@Override
	public void setValue(int row, int colum, double value) {
		body[row][colum] = value;
	}
}
