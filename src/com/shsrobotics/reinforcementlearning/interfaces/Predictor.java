package com.shsrobotics.reinforcementlearning.interfaces;
// create a new empty branch with:
// git checkout --orphan NEWBRANCH

/**
 * Base class for all predictors.  
 * 
 * @author Max
 */
public abstract class Predictor<Q extends DataSet> {
	
	public abstract Q querry(Q querry);
	public abstract void train(Q[] data);
	
	public abstract void saveToFile(String directory);
	protected abstract Predictor<Q> readFromFile(String directory);
	
}
