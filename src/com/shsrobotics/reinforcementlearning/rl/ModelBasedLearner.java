package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.optimizers.DefaultOptimizer;
import com.shsrobotics.reinforcementlearning.supervisedlearners.SupervisedLearner;
import com.shsrobotics.reinforcementlearning.supervisedlearners.KNNLearner;
import com.shsrobotics.reinforcementlearning.util.DataPoint;
import java.util.Map;

/**
 * Implementation of TEXPLORE Model-Based RL Algorithm.
 * <p/>
 * Hester, Todd, and Peter Stone. <i>Real Time Targeted Exploration in 
 * Large Domains. Department of Computer Science</i>. University of Texas, 
 * Aug. 2010. Web. 13 Mar. 2013. 
 * <a href="http://www.cs.utexas.edu/users/ai-lab/pubs/todd-thesis.pdf">&lt;http://www.cs.utexas.edu/users/ai-lab/pubs/todd-thesis.pdf&gt;</a>.
 */
public class ModelBasedLearner extends RLAgent {

	/**
	 * The index of the reward model.
	 */
	private int rewardModel;
	
	/**
	 * The maximum reward possible.
	 */
	private double maxReward;
	
	/**
	 * Q-Values for each state-action pair.
	 */
	private Map<double[], Double> Q;
	/**
	 * Visit counts for each discretized state-action pair.
	 */
	private Map<double[], Integer> s_a_Counts;
	/**
	 * Visit counts for each discretized state.
	 */
	private Map<State, Integer> s_Counts;
	/**
	 * Maximum UCT Search depth.
	 */
	private int maxDepth = 4;
	
	/**
	 * Number of discrete values for each feature.
	 */
	private int numberOfBins = 3000;
	/**
	 * Array of minimum step sizes for each feature, calculated 
	 * from {@link #numberOfBins}.
	 */
	private double[] stepSizes;

	/**
	 * Joined array of state and action names.
	 */
	private String[] inputKeys;
	
	/**
	 * The supervised learner to predict rewards and/or state values.
	 */
	protected SupervisedLearner[] supervisedLearner;
	
	/**
	 * Finds the best action to maximize Q-Values.
	 */
	private QMaximizer qMaximizer;
	
	
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
	 *			<li>{@code "Maximum Reward"}<br />  The value should be at index 0.</li>
	 *		</ul>
	 * @param options map of agent options.  Options:
	 *		<ul>
	 *			<li>{@code "Exploration Rate"} -- {@link #explorationRate}</li>
	 *			<li>{@code "Learning Rate"} -- {@link #learningRate}</li>
	 *			<li>{@code "Discount Factor"} -- {@link #discountFactor}</li>
	 *			<li>{@code "Accuracy"} -- {@link #accuracy}</li>
	 *		</ul>
	 */
	public ModelBasedLearner(String[] actions, String[] states, Map<String, double[]> ranges, Map<String, Number> options) {
		super(actions, states, ranges, options);
		
		rewardModel = stateParameters;
		
		qMaximizer = new QMaximizer();
		
		maxReward = ranges.get("Maximum Reward")[0];
		
		double[] minimums = join(minimumStateValues, minimumActionValues);
		double[] maximums = join(maximumStateValues, maximumActionValues);
		inputKeys = join(stateNames, actionNames);
		supervisedLearner = new KNNLearner[stateParameters + 1]; // placeholder
		for (int i = 0; i <= stateParameters; i++) { // fill array of learners
			supervisedLearner[i] = new KNNLearner(minimums, maximums);
			stepSizes[i] = (maximumStateValues[i] - minimumStateValues[i]) / numberOfBins;
		}
	}
	
	@Override
	double[] query(State state) {
		State discretized = discretize(state);
		qMaximizer.setMode(1);
		qMaximizer.setState(discretized);
		return qMaximizer.maximize();
	}
	
	/**
	 * Preform a UCT-Search of the model to find the best action to take.
	 * @param state the current {@link RLAgent.State}.
	 * @param depth the current search depth.
	 * @return the correct {@link RLAgent.Action}.
	 */
	double UCTSearch(State state, int depth) {
		if (depth == maxDepth) {
			return 0;
		}
		State discretized = discretize(state); //discretize state		
		//update Q maximizer
		qMaximizer.setMode(0);
		qMaximizer.setState(discretized);
		
		Action bestAction = new Action(actionNames, qMaximizer.maximize()); // find best action		
		Prediction prediction = queryModel(discretized, bestAction); // predict new state and reward 		
		double sampleReturn = prediction.reward + learningRate * UCTSearch(prediction.state, depth++); // recursive search
		//update counts
		double[] stateAction = join(discretized.get(), bestAction.get());
		s_Counts.put(discretized, s_Counts.get(discretized) + 1);
		s_a_Counts.put(stateAction, s_Counts.get(discretized) + 1);
		//update Q
		qMaximizer.setMode(1);
		double newQ = discountFactor * sampleReturn + (1 - discountFactor) * qMaximizer.f(qMaximizer.maximize());
		Q.put(stateAction, newQ);
		return 0;
	}
	
	/**
	 * Query the model for a new state and predicted reward.
	 * @param state current state.
	 * @param action action to be taken.
	 * @return array. Last index is the reward, all others represent predicted
	 * parameter values of the new state.
	 */
	Prediction queryModel(State state, Action action) {
		double[] predictedValues = new double[stateParameters];
		double[] stateValues = state.get();
		DataPoint input = new DataPoint(inputKeys, join(stateValues, action.get()), true);
		for (int parameter = 0; parameter < stateParameters; parameter++) {
			predictedValues[parameter] = stateValues[parameter] + supervisedLearner[parameter].query(input);
		}
		double reward = supervisedLearner[rewardModel].query(input);
		
		return new Prediction(new State(stateNames, predictedValues), reward);
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
			supervisedLearner[parameter].update(new DataPoint(input, transition[parameter]));
		}
		supervisedLearner[rewardModel].update(new DataPoint(input, reward)); // reward model		
	}
	
	/**
	 * Join two arrays.
	 * @param a first array.
	 * @param b second array.
	 * @return the joined array, with {@code a} before {@code b}.
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
	
	/**
	 * Join two arrays.
	 * @param a first array.
	 * @param b second array.
	 * @return the joined array, with {@code a} before {@code b}.
	 */
	private String[] join(String[] a, String[] b) {
		int length = a.length + b.length;
		String[] toReturn = new String[length];
		for (int i = 0; i < length; i++) {
			if (i > a.length - 1) {
				toReturn[i] = b[i - a.length];
			} else {
				toReturn[i] = a[i];
			}
		}
		return toReturn;
	}	

	/**
	 * Discretize a state into {@link #numberOfBins} parts.
	 * @param action the state.
	 * @return the discretized state.
	 */
	private State discretize(State state) {
		double[] values = state.get();
		for (int i = 0; i < stateParameters; i++) {
			int  count = (int ) (values[i] / stepSizes[i]);
			values[i] = count * stepSizes[i];
		}
		return new State(stateNames, values);
	}
	
	/**
	 * A model prediction.
	 */
	private class Prediction {
		/**
		 * The state prediction.
		 */
		public State state;
		/**
		 * The reward prediction.
		 */
		public double reward;
		
		/**
		 * Create a prediction instance.
		 * @param state the predicted state.
		 * @param reward the predicted reward.
		 */
		public Prediction(State state, double reward) {
			this.state = state;
			this.reward = reward;
		}
	}

	/**
	 * Maximize Q-Values by finding the best action.
	 */
	public class QMaximizer extends DefaultOptimizer {
		/**
		 * The current state
		 */
		private State environment;

		/**
		 * The number of action parameters.
		 */
		private int actionParameters;

		/**
		 * Saves the best action so far.
		 */
		private double[] bestAction;

		/**
		 * Mode of the {@link QMaximizer}.
		 */
		private int currentMode;

		/**
		 * Create a {@link QMaximizer}.
		 * @param minimums the minimum action values.
		 * @param maximums the maximum action values.
		 */
		public QMaximizer () {
			super(16, minimumActionValues, minimumStateValues);

			actionParameters = minimums.length;
			for (int i = 0; i <= actionParameters; i++) { // calculate step sizes
				stepSizes[i] = (maximums[i] - minimums[i]) / numberOfBins;
			}
		}

		/**
		 * Set the state to use for maximization.
		 * @param state the discretized state.
		 */
		public void setState(State state) {
			this.environment = state;
		}
		
		/**
		 * Set the mode of the Q-Maximizer.
		 * @param mode the mode. {@code 0} for UCT-Search maximization, {@code 1} for Action maximization alone.
		 */
		public void setMode(int mode) {
			this.currentMode = mode;
		}

		@Override
		public double f(double[] input) { // input is action values
			double[] discretizedAction = discretize(input);
			double[] discretizedState = environment.get();
			double[] qInput = join(discretizedState, discretizedAction);
			double toReturn = 0;
			if (currentMode == 0) { // UCT
				toReturn = Q.get(qInput) + 2 * (maxReward / (1 - learningRate)) * 
					Math.sqrt(Math.log(s_Counts.get(environment)) / s_a_Counts.get(qInput));
			} else if (currentMode == 1) { // ACtion
				toReturn = Q.get(qInput);
			}
			return toReturn;
		}

		/**
		 * Discretize an action into {@link #numberOfBins} parts.
		 * @param action the action.
		 * @return the discretized action.
		 */
		private double[] discretize(double[] action) {
			double[] values = action.clone();
			for (int i = 0; i < actionParameters; i++) {
				int  count = (int ) (values[i] / stepSizes[i]);
				values[i] = count * stepSizes[i];
			}
			return values;
		}
	}

}
