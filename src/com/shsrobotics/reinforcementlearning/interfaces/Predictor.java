package com.shsrobotics.reinforcementlearning.interfaces;
// create a new empty branch with:
// git checkout --orphan NEWBRANCH

/**
 * Base class for all predictors.  
 * 
 * @author Max
 */
public abstract class Predictor {
	public abstract DataSet querry();
}
