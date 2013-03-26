package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.optimizers.DefaultOptimizer;
import com.shsrobotics.reinforcementlearning.supervisedlearners.KNNLearner;
import com.shsrobotics.reinforcementlearning.supervisedlearners.SupervisedLearner;
import com.shsrobotics.reinforcementlearning.util.DataPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of TEXPLORE Model-Based RL Algorithm.
 * <p/>
 * Hester, Todd, and Peter Stone. <i>Real Time Targeted Exploration in 
 * Large Domains. Department of Computer Science</i>. University of Texas, 
 * Aug. 2010. Web. 13 Mar. 2013. 
 * <a href="http://www.cs.utexas.edu/users/ai-lab/pubs/todd-thesis.pdf">&lt;http://www.cs.utexas.edu/users/ai-lab/pubs/todd-thesis.pdf&gt;</a>.
 */
@SuppressWarnings("unchecked")
public class ModelBasedLearner extends RLAgent {

	/**
	 * The index of the reward model.
	 */
	private int rewardModel;
	
	/**
	 * The current maximum sample return encountered.
	 */
	private double maximumSampleReturn = 0;
	/**
	 * For UCT λ-returns.
	 */
	private Double λ;
	
	/**
	 * Q-Values for each state-action-history pair.
	 */
	private Map<StateActionHistory, Double> QValues;
	/**
	 * Visit counts for each discretized state-action-history pair.
	 */
	private Map<StateActionHistory, Integer> s_a_Counts;
	/**
	 * Visit counts for each discretized state-history pair.
	 */
	private Map<StateHistory, Integer> s_Counts;
	/**
	 * History of actions taken.
	 * Index 0 is most recent.
	 */
	private ArrayList<Action> history;
	/**
	 * Maximum UCT Search depth.
	 */
	private int maxDepth = 8;
	/**
	 * The number of previous actions to keep in the history.
	 */
	private Integer historyLength = 0; // Integer to avoid null pointer
	
	/**
	 * Number of discrete values for each feature.
	 */
	private Integer numberOfBins;
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
	 * @param actions a list of action parameter names.
	 * @param states a list of state parameter names.
	 * @param ranges map of minimum and maximum arrays. Accepted keys are:
	 *		<ul>
	 *			<li>{@code "Minimum Action Values"}</li>
	 *			<li>{@code "Maximum Action Values"}</li>
	 *			<li>{@code "Minimum State Values"}</li>
	 *			<li>{@code "Maximum State Values"}</li>
	 *			<li>{@code "Reward Range"}<br />  The minimum should be at index 0, maximum at index 1.</li>
	 *		</ul>
	 * @param options map of agent options.  Options:
	 *		<ul>
	 *			<li>{@code "History Length"} -- {@link #historyLength}</li>
	 *			<li>{@code "Number of Bins"} -- {@link #numberOfBins}</li>
	 *			<li>{@code "λ"} -- {@link #λ}</li>
	 *			<li>{@code "Learning Rate"} -- {@link #learningRate}</li>
	 *			<li>{@code "Discount Factor"} -- {@link #discountFactor}</li>
	 *			<li>{@code "Accuracy"} -- {@link #accuracy}</li>
	 *		</ul>
	 */
	public ModelBasedLearner(String[] actions, String[] states, Map<String, double[]> ranges, Map<String, Number> options) {
		super(actions, states, ranges, options);
		
		rewardModel = stateParameters;
		
		double[] rewardArray = ranges.get("Reward Range");
		if (rewardArray == null) {
			throw new Error("Reward range missing.");
		}
		
		this.historyLength = (Integer) options.get("History Length");
		if (this.historyLength == null) {
			this.historyLength = 0;
		}
		
		this.numberOfBins = (Integer) options.get("Number of Bins");
		if (this.numberOfBins == null) {
			this.numberOfBins = 250;
		}
		
		this.λ = (Double) options.get("λ");
		if (this.λ == null) {
			this.λ = 0.05;
		}
		
		stepSizes = new double[actionParameters + 1];
		qMaximizer = new QMaximizer();
		
		
		int accuracyIterations = (int) (1 / (1 - accuracy));
		
		double[] minimums = join(minimumStateValues, minimumActionValues);
		double[] maximums = join(maximumStateValues, maximumActionValues);
		inputKeys = join(stateNames, actionNames);
		supervisedLearner = new KNNLearner[stateParameters + 1]; // placeholder
		for (int i = 0; i < stateParameters; i++) { // fill array of learners
			supervisedLearner[i] = new KNNLearner(minimums, maximums).setK(accuracyIterations);
			stepSizes[i] = (maximumStateValues[i] - minimumStateValues[i]) / numberOfBins;
		}
		supervisedLearner[rewardModel] = new KNNLearner(minimums, maximums).setK(accuracyIterations);		
		stepSizes[actionParameters] = (rewardArray[1] - rewardArray[0]) / numberOfBins;
		
		QValues = new HashMap<>();
		s_a_Counts = new HashMap<>();
		s_Counts = new HashMap<>();
		history = new ArrayList<>(historyLength);
	}
	
	@Override
	double[] query(State state) {
		State discretized = discretize(state);
		return qMaximizer.setMode(QMaximizer.kActionMaximize).setState(discretized).maximize();
	}
	
	@Override
	public ModelBasedLearner plan(State state) {
		UCTSearch(new StateHistory(state, history), 0);
		
		return this;
	}
	
	/**
	 * Preform a UCT-Search of the model to find the best action to take.
	 * @param state the current {@link RLAgent.State}.
	 * @param depth the current search depth.
	 * @return the correct {@link RLAgent.Action}.
	 */
	double UCTSearch(StateHistory stateHistory, int depth) {
		if (depth == maxDepth) {
			return 0;
		}
		State state = stateHistory.getState();
		State discretized = discretize(state); //discretize state	
		//update Q maximizer
		qMaximizer.setMode(QMaximizer.kUCTMaximize);
		qMaximizer.setState(discretized);
		
		Action bestAction = new Action(actionNames, qMaximizer.maximize()); // find best action		
		Prediction prediction = queryModel(state, bestAction); // predict new state and reward
		//update history and make recursive method call
		stateHistory.addAction(bestAction).setState(prediction.state);
		double sampleReturn = prediction.reward + discountFactor * UCTSearch(stateHistory, depth + 1); // recursive search
		maximumSampleReturn = Math.max(sampleReturn, maximumSampleReturn); // update maximum sample return
		//update counts
		StateActionHistory stateActionHistory = new StateActionHistory(prediction.state, bestAction, history);
		
		Integer s_Count = s_Counts.get(stateHistory);
		Integer s_a_Count = s_a_Counts.get(stateActionHistory);
		if (s_Count == null) s_Count = 1;
		if (s_a_Count == null) s_a_Count = 1;
		
		s_Counts.put(stateHistory, s_Count + 1);
		s_a_Counts.put(stateActionHistory, s_a_Count + 1);
		//update Q
		double newQ = learningRate * sampleReturn + (1 - learningRate) * qMaximizer.f(bestAction.get());
		QValues.put(stateActionHistory, newQ);
		
		qMaximizer.setMode(QMaximizer.kActionMaximize);
		return λ * sampleReturn + (1 - λ) * qMaximizer.f(qMaximizer.maximize());
	}
	
	/**
	 * Lower the importance of the last planning rollout to allow for better exploration now.
	 * 
	 * @return the class, for chaining method calls.
	 */
	public ModelBasedLearner UCTReset() {
		for (Map.Entry pair : s_Counts.entrySet()) {
			pair.setValue((Integer) pair.getValue() / 2);
		}
		for (Map.Entry pair : s_a_Counts.entrySet()) {
			pair.setValue((Integer) pair.getValue() / 2);
		}
		return this;
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
	public ModelBasedLearner updateSupervisedLearner(State state, Action action, State newState, double reward) {
		history.add(0, action);
		history.trimToSize();
		
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
		
		return this;
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
		double[] values = state.clone().get();
		for (int i = 0; i < stateParameters; i++) {
			int  count = (int ) (values[i] / stepSizes[i]);
			values[i] = count * stepSizes[i];
		}
		return new State(stateNames, values);
	}
	
	/**
	 * Stores the current state as well an action as well as the past {@code k} actions.
	 */
	public class StateActionHistory implements Cloneable {
		/**
		 * The state.
		 */
		private State state;
		/**
		 * The action.
		 */
		private Action action;
		/**
		 * The action history.
		 */
		private ArrayList<Action> history = new ArrayList<>(historyLength);
		
		/**
		 * Create a State-history pair.
		 * @param state the state.
		 * @param action the action.
		 * @param history the history of actions.
		 */
		public StateActionHistory(State state, Action action, ArrayList<Action> history) {
			this.state = state.clone();
			this.action = action.clone();
			this.history = history;
			this.history.trimToSize();
		}
		
		/**
		 * Get the state.
		 * @return the state.
		 */
		public State getState() {
			return state.clone();
		}
			
		/**
		 * Push an action to the history.
		 * @param newAction the action to add.
		 * @return the class, for chaining method calls.
		 */
		public StateActionHistory addAction(Action newAction) {
			history.add(0, newAction);
			history.trimToSize();
			return this;
		}
		
		/**
		 * Get the history.
		 * @return the action history.
		 */
		public Action[] getHistory() {
			return (Action[]) history.toArray().clone();
		}
		
		/**
		 * Get the action taken {@code iterationsBack} iterations ago.
		 * @param iterationsBack the number of iterations back to get the action from.
		 * @return the action.
		 */
		public Action getActionAt(int iterationsBack) {
			return history.get(iterationsBack).clone();
		}
		
		/**
		 * Get the action.
		 * @return the action.
		 */
		public Action getAction() {
			return action.clone();
		}
		
		@Override
		public StateActionHistory clone() {
			try {
				return (StateActionHistory) super.clone();
			} catch (CloneNotSupportedException ex) {
				return null;
			}
		}
	}
	
	/**
	 * Stores the current state as well as the past {@code k} actions.
	 */
	public class StateHistory implements Cloneable {
		/**
		 * The state.
		 */
		private State state;
		/**
		 * The action history.
		 */
		private ArrayList<Action> history = new ArrayList<>(historyLength);
		
		/**
		 * Create a State-history pair.
		 * @param state
		 * @param history
		 */
		public StateHistory(State state, ArrayList<Action> history) {
			this.state = state.clone();
			this.history = history;
			this.history.trimToSize();
		}
		
		/**
		 * Get the state.
		 * @return the state.
		 */
		public State getState() {
			return state.clone();
		}
		
		/**
		 * Set a new state.
		 * @param newState the new state.
		 * @return the class, for chaining method calls.
		 */
		public StateHistory setState(State newState) {
			this.state = newState.clone();
			return this;
		}
		
		/**
		 * Push an action to the history.
		 * @param newAction the action to add.
		 * @return the class, for chaining method calls.
		 */
		public StateHistory addAction(Action newAction) {
			history.add(0, newAction);
			history.trimToSize();
			return this;
		}
		
		/**
		 * Get the history.
		 * @return the action history.
		 */
		public Action[] getHistory() {
			return (Action[]) history.toArray().clone();
		}
		
		/**
		 * Get the action taken {@code iterationsBack} iterations ago.
		 * @param iterationsBack the number of iterations back to get the action from.
		 * @return the action.
		 */
		public Action getAction(int iterationsBack) {
			return history.get(iterationsBack).clone();
		}
		
		@Override
		public StateHistory clone() {
			try {
				return (StateHistory) super.clone();
			} catch (CloneNotSupportedException ex) {
				return null;
			}
		}
	}
	
	/**
	 * A model prediction.
	 */
	public class Prediction {
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
			this.state = state.clone();
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
		 * Mode of the {@link QMaximizer}.
		 */
		private int currentMode;
		
		/**
		 * Maximize for the UCT algorithm.
		 */
		public static final int kUCTMaximize = 0;
		/**
		 * Maximize for choosing the best action.
		 */
		public static final int kActionMaximize = 1;

		/**
		 * Create a {@link QMaximizer}.
		 */
		public QMaximizer () {
			super(16, minimumActionValues, maximumActionValues);
			
			for (int i = 0; i <= actionParameters; i++) { // calculate step sizes
				stepSizes[i] = (maximums[i] - minimums[i]) / numberOfBins;
			}
		}

		/**
		 * Set the state to use for maximization.
		 * @param state the discretized state.
		 * @return the class, for chaining method calls.
		 */
		public QMaximizer setState(State state) {
			this.environment = state;
			return this;
		}
		
		/**
		 * Set the mode of the Q-Maximizer.
		 * @param mode the mode. {@code 0} for UCT-Search maximization, {@code 1} for Action maximization alone.
		 * @return the class, for chaining method calls.
		 */
		public QMaximizer setMode(int mode) {
			this.currentMode = mode;
			return this;
		}

		@Override
		public double f(double[] input) { // input is state and action values
			StateHistory stateHistory = new StateHistory(environment, history);
			StateActionHistory stateActionHistory = new StateActionHistory(environment, 
				new Action(actionNames, input), history);
			double toReturn = 0;
			Double qValue = QValues.get(stateActionHistory);
			if (qValue == null) qValue = 0.0;
				if (true) return qValue;
			if (currentMode == kUCTMaximize) {
				Integer s_Count = s_Counts.get(stateHistory);
				Integer s_a_Count = s_a_Counts.get(stateActionHistory);
				if (s_Count == null) s_Count = 1;
				if (s_a_Count == null) s_a_Count = 1;
				toReturn = qValue + 2 * (maximumSampleReturn / (1 - discountFactor)) * 
					Math.sqrt(Math.log(s_Count) / s_a_Count);
			} else if (currentMode == kActionMaximize) {
				toReturn = qValue;
			}
			return toReturn;
		}

	}
}
