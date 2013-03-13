package com.shsrobotics.reinforcementlearning.architecture;

public abstract class RLManager {
	
	public abstract void start();
	
	public abstract void stop();
	
	public abstract String getStateAsString();
	
	public abstract int getStateAsNumber();
}
