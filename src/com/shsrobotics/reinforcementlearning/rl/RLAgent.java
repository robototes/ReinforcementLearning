package com.shsrobotics.reinforcementlearning.rl;

import com.shsrobotics.reinforcementlearning.supervisedlearners.SupervisedLearner;
import com.shsrobotics.reinforcementlearning.util.DataPoint;
import java.util.Map;

public abstract class RLAgent {
	/**
	 * Mode the learner is operating in.
	 */
	private Mode currentMode = Mode.kOff;
	
	/**
	 * Learning rate of the learner.
	 */
	double learningRate;
	
	/**
	 * Discount factor of the learner. A higher discount factor places more
	 * value on short-term rewards.
	 */
	double discountFactor;
	
	/**
	 * Learner accuracy.
	 */
	double accuracy;
	
	/**
	 * List of action parameter names.
	 */
	final String[] actionNames;
	/**
	 * List of state parameter names.
	 */
	final String[] stateNames;
	/**
	 * The number of actions.
	 */
	final int actions;
	/**
	 * The number of state parameters.
	 */
	final int states;
	
	/**
	 * Minimum action values.
	 */
	final double[] minimumActionValues;
	
	/**
	 * Maximum action values.
	 */
	final double[] maximumActionValues;
	
	/**
	 * Minimum state values.
	 */
	final double[] minimumStateValues;
	
	/**
	 * Maximum state values.
	 */
	final double[] maximumStateValues;
	
	final SupervisedLearner supervisedLearner = null;
	
	public RLAgent(String[] actions, String[] states, Map<String, double[]> ranges, Map<String, Number> options) {
		this.actionNames = actions;
		this.actions = actions.length;
		this.stateNames = states;
		this.states = states.length;
		
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
	}
	
	public Action requestAction(State state) {
		double exploreCutoff = learningRate;        
        double[] actionValues = new double[actions];
        
        if (currentMode.chooseBestOption) { // check modes
			exploreCutoff = 0.0;
		}
        if (!currentMode.allowActionRequests) {
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
	 * @param state the current state.
	 * @return array of raw action values.
	 */
	abstract double[] query(State state);
	
	/**
	 * Update the supervised learner with a new data point.
	 * @param state the state the agent was in.
	 * @param action the action preformed.
	 * @param newState the resultant state.
	 * @param reward the reward received.
	 */
	abstract void updateSupervisedLearner(State state, Action action, State newState, double reward);
	
	/**
	 * Set the learner mode.
	 * @param newMode the new learner mode.
	 */
	public void setMode(Mode newMode) {
		this.currentMode = newMode;
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
        
        private final boolean allowActionRequests;
        private final boolean chooseBestOption;
		private final boolean enabled;
        
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
			if (keys.length != actions) {
				throw new Error("Incorrect key length");
			}
			if (values.length != actions) {
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
			if (keys.length != actions) {
				throw new Error("Incorrect key length");
			}
			if (values.length != actions) {
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
    double[] rands() {
        double[] toReturn = new double[this.actions];
        for (int i = 0; i < this.actions; i++) {
                double range = maximumActionValues[i] - minimumActionValues[i];
                toReturn[i] = Math.random() * range + minimumActionValues[i]; // generate random number in range
        }
        return toReturn;
    }
}
