package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.util.DataPoint;
import java.util.Map;

/**
 * A Reinforcement Learning agent.
 * @author Team 2412
 */
public abstract class RLAgent {
	/**
	 * Mode the learner is operating in.
	 */
	protected Mode currentMode = Mode.kOff;
	
	/**
	 * Exploration rate of the learner.
	 */
	private double explorationRate;
	
	/**
	 * Discount factor of the learner. A higher discount factor places more
	 * value on short-term rewards.
	 */
	protected double discountFactor;
	
	/**
	 * Learning rate of the learner.
	 */
	protected double learningRate;
	
	/**
	 * Learner accuracy.
	 */
	protected double accuracy;
	
	/**
	 * List of action parameter names.
	 */
	protected final String[] actionNames;
	/**
	 * List of state parameter names.
	 */
	protected final String[] stateNames;
	/**
	 * The number of actionParameters.
	 */
	protected final int actionParameters;
	/**
	 * The number of state parameters.
	 */
	protected final int stateParameters;
	
	/**
	 * Minimum action values.
	 */
	protected final double[] minimumActionValues;
	
	/**
	 * Maximum action values.
	 */
	protected final double[] maximumActionValues;
	
	/**
	 * Minimum state values.
	 */
	protected final double[] minimumStateValues;
	
	/**
	 * Maximum state values.
	 */
	protected final double[] maximumStateValues;
	
	/**
	 * Create an Reinforcement Learning agent.
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
	 *			<li>{@code "Exploration Rate"} -- {@link #explorationRate}</li>
	 *			<li>{@code "Learning Rate"} -- {@link #learningRate}</li>
	 *			<li>{@code "Discount Factor"} -- {@link #discountFactor}</li>
	 *			<li>{@code "Accuracy"} -- {@link #accuracy}</li>
	 *		</ul>
	 */
	protected RLAgent(String[] actions, String[] states, Map<String, double[]> ranges, Map<String, Number> options) {
		this.actionNames = actions;
		this.actionParameters = actions.length;
		this.stateNames = states;
		this.stateParameters = states.length;
		
		if (options.containsKey("Exploration Rate")) {
			this.explorationRate = (double) options.get("Exploration Rate");
		} else {
			this.explorationRate = 0.1; // default
		}
		
		if (options.containsKey("Learning Rate")) {
			this.learningRate = (double) options.get("Learning Rate");
		} else {
			this.learningRate = 0.2; // default
		}
		
		if (options.containsKey("Discount Factor")) {
			this.discountFactor = (double) options.get("Discount Factor");
		} else {
			this.discountFactor = 0.4; // default
		}
		
		if (options.containsKey("Accuracy")) {
			this.accuracy = (double) options.get("Accuracy");
		} else {
			this.accuracy = 0.9; // default
		}
		
		minimumActionValues = ranges.get("Minimum Action Values");
		maximumActionValues = ranges.get("Maximum Action Values");
		minimumStateValues = ranges.get("Minimum State Values");
		maximumStateValues = ranges.get("Maximum State Values");
		if (minimumActionValues == null || maximumActionValues == null
			|| minimumStateValues == null || maximumStateValues == null) {
			throw new Error("Null range.");
		}
	}
	
	/**
	 * Get action to take based on the current state.
	 * @param state the environment the agent is in.
	 * @return the best action to take.  If the agent is in the {@code kLearn}
	 * {@link Mode}, then some actionParameters will be random.
	 */
	public final Action requestAction(State state) {
		double exploreCutoff = explorationRate;        
        double[] actionValues = new double[actionParameters];
        
        if (currentMode.chooseBestOption) { // check modes
			exploreCutoff = 0.0;
		}
        if (!currentMode.allowActionRequests || !currentMode.enabled) {
            throw new Error("Wrong learning mode.");
        }
        
        if (Math.random() < exploreCutoff) { // choose random values
            actionValues = rands();
        } else {
			actionValues = query(state);
		}
		return new Action(actionNames, actionValues);
	}
	
	/**
	 * Return the correct action for the given state.
	 * @param state the current {@link State}.
	 * @return correct {@link Action}.
	 */
	abstract double[] query(State state);
	
	public void plan(State state) { }
	
	/**
	 * Update the supervised learner with a new data point.
	 * @param state the {@link State} the agent was in.
	 * @param action the {@link Action} preformed.
	 * @param newState the resultant state.
	 * @param reward the reward received.
	 */
	public abstract void updateSupervisedLearner(State state, Action action, State newState, double reward);
	
	/**
	 * Set the learner mode.
	 * @param newMode the new learner mode.
	 */
	public void setMode(Mode newMode) {
		this.currentMode = newMode;
	}
	
	/**
	 * Get the learner mode.
	 * @return the current mode.
	 */
	public Mode getMode() {
		return currentMode;
	}
	
	/**
     * A mode that the {@link QLearner} can operate in.
     */
    public static class Mode { 
        /**
		 * Turn off the learner.
		 */
		public static final Mode kOff = new Mode(false, false, false);
		
        /**
         * Update Q-Values but don't act on them.
         */
        public static final Mode kWatch = new Mode(false, false, true);
        
        /**
         * Explore the environment, updating Q-Values
         */
        public static final Mode kLearn = new Mode(true, false, true);
        
        /**
         * Choose the best action to act on instead of exploring, updating Q-Values
         */
        public static final Mode kAct = new Mode(true, true, true);
        
        /**
		 * Whether to allow the agent to ask for a correct action.
		 */
		private final boolean allowActionRequests;
        /**
		 * Whether to always choose the best action given the environment or not.
		 */
		private final boolean chooseBestOption;
		/**
		 * Whether the agent should be enabled.
		 */
		private final boolean enabled;
        
        /**
		 * Create a mode
		 * @param allowActionRequests see {@link #allowActionRequests}
		 * @param chooseBestOption see {@link #chooseBestOption}
		 * @param enabled see {@link #enabled}
		 */
		private Mode(boolean allowActionRequests, boolean chooseBestOption, boolean enabled) {
            this.allowActionRequests = allowActionRequests;
            this.chooseBestOption = chooseBestOption;
			this.enabled = enabled;
        }
    }
	
	/**
	 * Set of action parameters.
	 */
	public class Action extends DataPoint {
		/**
		 * Create an action.
		 * @param keys keys to represent action parameters.
		 * @param values values for each parameter.
		 */
		public Action(String[] keys, double[] values) {
			super(keys, values, false);
			if (keys.length != actionParameters) {
				throw new Error("Incorrect key length");
			}
			if (values.length != actionParameters) {
				throw new Error("Incorrect value length");
			}
		}
		/**
		 * Get the value for a parameter.
		 * @param key the parameter name.
		 * @return the value.
		 */
		public double getActionParameter(String key) {
			return getOutput(key);
		}
		
		/**
		 * Get all of the parameters.
		 * @return raw parameter values.
		 */
		public double[] get() {
			return getOutputs();
		}
	}
	
	/**
	* Set of state parameters.
	*/
	public class State extends DataPoint {
		/**
		 * Create a state.
		 * @param keys keys to represent state parameters.
		 * @param values values for each parameter.
		 */
		public State(String[] keys, double[] values) {
			super(keys, values, true);
			if (keys.length != stateParameters) {
				throw new Error("Incorrect key length");
			}
			if (values.length != stateParameters) {
				throw new Error("Incorrect value length");
			}
		}

		/**
		 * Get the value for a parameter.
		 * @param key the parameter name.
		 * @return the value.
		 */
		public double getStateParameter(String key) {
			return getInput(key);
		}

		/**
		 * Get all of the parameters.
		 * @return raw parameter values.
		 */
		public double[] get() {
			return getInputs();
		}
	}

	
	/**
     * Fill an array with random action values
     * @return The array.
     */
    protected double[] rands() {
        double[] toReturn = new double[this.actionParameters];
        for (int i = 0; i < this.actionParameters; i++) {
                double range = maximumActionValues[i] - minimumActionValues[i];
                toReturn[i] = Math.random() * range + minimumActionValues[i]; // generate random number in range
        }
        return toReturn;
    }
}
