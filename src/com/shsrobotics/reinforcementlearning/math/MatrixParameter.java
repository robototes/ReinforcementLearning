package com.shsrobotics.reinforcementlearning.math;

/**
 * Any class that wants to be put into a {@link ParameteredMatrix} as a value in the 
 * matrix implements this interface.
 * 
 * @author Max
 */
public interface MatrixParameter {
	
	/**
	 * Gets the value, computed based on the parameter.
	 * 
	 * @return The computed value.
	 */
	
	public double getComputed();
	
	/**
	 * Gets the parameter in this value.
	 * 
	 * @return The currently set parameter.
	 */
	
	public double getParameter();
	
	/**
	 * Sets the parameter in this value.
	 * 
	 * @param param The parameter that this value is dependant on.
	 */
	
	public void setParameter(double param);
	
}
