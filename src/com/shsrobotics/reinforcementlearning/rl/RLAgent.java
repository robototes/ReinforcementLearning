package com.shsrobotics.reinforcementlearning.rl;

import java.util.Map;

public abstract class RLAgent {
	/**
	 * Mode the learner is operating in.
	 */
	private Mode currentMode = Mode.kOff;
	
	/**
	 * Learning rate of the learner.
	 */
	private double learningRate;
	
	/**
	 * Discount factor of the learner. A higher discount factor places more
	 * value on short-term rewards.
	 */
	private double discountFactor;
	
	/**
	 * Learner accuracy.
	 */
	private double accuracy;
	
	/**
	 * List of action parameter names.
	 */
	private final String[] actionNames;
	/**
	 * List of state parameter names.
	 */
	private final String[] stateNames;
	/**
	 * The number of actions.
	 */
	private final int actions;
	/**
	 * The number of state parameters.
	 */
	private final int states;
	
	public RLAgent(String[] actions, String[] states, Map<String, Number> options) {
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
	}
	
	public void requestAction() {
		
	}
	
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
}
