package com.shsrobotics.reinforcementlearning.math;

/**
 * Any class that wants to be put into a {@link ParameteredMatrix} as a value in
 * the matrix implements this interface.
 * <p/>
 * @author Max
 */
public interface MatrixParameter {

	/**
	 * Gets the value, computed based on the parameter.
	 * <p/>
	 * @return The computed value.
	 */
	public double getComputed();

	/**
	 * Gets the parameter in this value.
	 * <p/>
	 * @return The currently set parameter.
	 */
	public double getParameter();

	/**
	 * Sets the parameter in this value.
	 * <p/>
	 * @param param The parameter that this value is dependant on.
	 */
	public void setParameter(double param);
}
