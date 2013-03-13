package com.shsrobotics.reinforcementlearning.architecture;

/**
 *
 * @author Max
 */
public abstract class RLManager {
	
	public abstract void start();
	
	public abstract void stop();
	
	public abstract String getStateAsString();
	
	public abstract int getStateAsNumber();
}
