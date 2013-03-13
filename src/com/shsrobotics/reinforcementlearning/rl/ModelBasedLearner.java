package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.supervisedlearners.SupervisedLearner;
import com.shsrobotics.reinforcementlearning.supervisedlearners.TestLearner;
import java.util.Map;

/**
 * Implementation of TEXPLORE Model-Based RL Algorithm.
 * <p/>
 * Hester, Todd, and Peter Stone. <i>Real Time Targeted Exploration in 
 * Large Domains. Department of Computer Science</i>. University of Texas, 
 * Aug. 2010. Web. 13 Mar. 2013. 
 * <a href="http://www.cs.utexas.edu/~pstone/Papers/bib2html-links/ICDL10-hester.pdf">&lt;http://www.cs.utexas.edu/~pstone/Papers/bib2html-links/ICDL10-hester.pdf&gt;</a>.
 */
public class ModelBasedLearner extends RLAgent {

	/**
	 * The index of the reward model.
	 */
	private int rewardModel;
	
	/**
	 * The supervised learner to predict rewards and/or state values.
	 */
	protected SupervisedLearner[] supervisedLearner;
	
	/**
	 * Create a Model-Based Reinforcement Learning agent.
	 * @param actionParameters see {@link #actionNames}
	 * @param stateParameters see {@link #stateNames}
	 * @param ranges map of minimum and maximum arrays. Accepted keys are:
	 *		<ul>
	 *			<li>{@code "Minimum Action Values"}</li>
	 *			<li>{@code "Maximum Action Values"}</li>
	 *			<li>{@code "Minimum State Values"}</li>
	 *			<li>{@code "Maximum State Values"}</li>
	 *		</ul>
	 * @param options map of agent options.  Options:
	 *		<ul>
	 *			<li>{@code "Learning Rate"} -- {@link #learningRate}</li>
	 *			<li>{@code "Discount Factor"} -- {@link #discountFactor}</li>
	 *			<li>{@code "Accuracy"} -- {@link #accuracy}</li>
	 *		</ul>
	 */
	public ModelBasedLearner(String[] actions, String[] states, Map<String, double[]> ranges, Map<String, Number> options) {
		super(actions, states, ranges, options);
		
		rewardModel = stateParameters;
		
		double[] minimums = join(minimumStateValues, minimumActionValues);
		double[] maximums = join(maximumStateValues, maximumActionValues);
		supervisedLearner = new TestLearner[stateParameters + 1]; // placeholder
		for (int i = 0; i <= stateParameters; i++) { // fill array of learners
			supervisedLearner[i] = new TestLearner(minimums, maximums);
		}
	}
	
	@Override
	double[] query(State state) {
		return null;
	}
	
	/**
	 * Query the model for a new state and predicted reward.
	 * @param state current state.
	 * @param action action to be taken.
	 * @return array. Last index is the reward, all others represent predicted
	 * parameter values of the new state.
	 */
	double[] queryModel(State state, Action action) {
		double[] predictedValues = new double[stateParameters + 1];
		double[] stateValues = state.get();
		double[] input = join(stateValues, action.get());
		for (int parameter = 0; parameter < stateParameters; parameter++) {
			predictedValues[parameter] = stateValues[parameter] + supervisedLearner[parameter].query(input);
		}
		predictedValues[rewardModel] = supervisedLearner[rewardModel].query(input);
		
		return predictedValues;
	}

	@Override
	public void updateSupervisedLearner(State state, Action action, State newState, double reward) {
		// calculate difference between states
		double[] transition = new double[stateParameters];
		double[] stateValues = state.get();
		double[] newStateValues = newState.get();
		for (int parameter = 0; parameter < stateParameters; parameter++) {
			transition[parameter] = newStateValues[parameter] - stateValues[parameter];
		}
		
		//update each transition model
		double[] input = join(stateValues, action.get());
		for (int parameter = 0; parameter < stateParameters; parameter++) {
			supervisedLearner[parameter].update(input, transition[parameter]);
		}
		supervisedLearner[rewardModel].update(input, reward); // reward model		
	}
	
	/**
	 * Join two arrays
	 * @param a
	 * @param b
	 * @return the joined array.
	 */
	private double[] join(double[] a, double[] b) {
		int length = a.length + b.length;
		double[] toReturn = new double[length];
		for (int i = 0; i < length; i++) {
			if (i > a.length - 1) {
				toReturn[i] = b[i - a.length];
			} else {
				toReturn[i] = a[i];
			}
		}
		return toReturn;
	}	
}
