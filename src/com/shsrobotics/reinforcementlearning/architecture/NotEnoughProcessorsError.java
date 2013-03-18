package com.shsrobotics.reinforcementlearning.architecture;

/**
 *
 * @author Max
 */
public class NotEnoughProcessorsError extends Error {

	public NotEnoughProcessorsError() {
		super("Need at least 4 processors to run");
	}
	
}
