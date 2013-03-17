package com.shsrobotics.reinforcementlearning.supervisedlearners;

/**
 * PLACEHOLDER FOR REAL MACHINE LEARNER
 */
public class TestLearner extends SupervisedLearner {
	/**
	 * PLACEHOLDER FOR REAL MACHINE LEARNER.
	 * @param minimums
	 * @param maximums
	 */
	public TestLearner(double[] minimums, double[] maximums) {
		super(minimums, maximums);
	}
	
	@Override
	public void update(double[] input, double output) {
		
	}
	
	@Override
	public double query(double[] input) {
		return 0;
	}
}
