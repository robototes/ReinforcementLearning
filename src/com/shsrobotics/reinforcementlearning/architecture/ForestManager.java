package com.shsrobotics.reinforcementlearning.architecture;

/**
 * 
 * @author Max
 */
public class ForestManager {
	
	public ForestManager() {
		if ( Runtime.getRuntime().availableProcessors() < 3 )
			throw new NotEnoughProcessorsError();
	}
	
}
