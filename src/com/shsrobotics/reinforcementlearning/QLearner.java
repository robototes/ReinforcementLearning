package com.shsrobotics.reinforcementlearning;

import com.shsrobotics.reinforcementlearning.estimators.NeuralNetworkQEstimator;
import com.shsrobotics.reinforcementlearning.optimizers.Optimizer;
import com.shsrobotics.reinforcementlearning.util.DataPoint;

/**
 * Learns how to act based on rewards.
 * @author Team 2412.
 */
public class QLearner {
	/**
	 * The {@link Mode} the learner is operating in.
	 */
    private Mode currentMode;
    
	/**
	 * Controls the portion of the time the learner is exploring vs exploiting.
	 * A rate of 0.2 is recommended, allowing for exploration randomly but also
	 * exploration through intelligent action.
	 */
    private double learningRate;	
	/**
	 * Controls how much value is placed on future rewards.
	 * A higher value will place more emphasis on future rewards.
	 */
	private double discountFactor;
	/**
	 * How accurate the maximization of reward should be.
	 * Higher accuracy comes at the cost of more computation time.
	 */
    private double learnerAccuracy;
    
    /**
	 * Minimum state parameter values.
	 */
	public double[] minimumStateValues;	
    /**
	 * Minimum action parameter values.
	 */
	public double[] minimumActionValues;	
    /**
	 * Maximum state parameter values.
	 */
	public double[] maximumStateValues;	
    /**
	 * Maximum action parameter values.
	 */
	public double[] maximumActionValues;
    
	/**
	 * The number of state parameters.
	 */
    private int states;
	/**
	 * The number of action parameters.
	 */
    private int actions;
	/**
	 * A list of state parameter names.
	 */
    private String[] stateNames;
	/**
	 * A list of action parameter names.
	 */
    private String[] actionNames;
	
	/**
	 * String keys that represent state and action values.
	 * This is a joined array of {@code stateNames} and {@code actionNames}.
	 */
	private String[] inputKeys;
	/**
	 * String key that represents the output value from the Q Estimator.
	 */
	private String[] outputKeys = {"Q-Value"};
    
	/**
	 * An Artificial Neural Network that estimates Q-Values.
	 */
    private NeuralNetworkQEstimator qEstimator;
		
	/**
	 * How many times the Q-Learner has had updated Q-Values.
	 * In practice, this will increment many times a second.
	 */
	private int iterations;
    
    /**
     * Accepts string array of action values and environment state parameters
     * @param actions the action parameters. 
     * @param states the state parameters.
     */
    public QLearner(String[] actions, String[] states) {
        currentMode = Mode.kLearn;
        learningRate = 0.2;
		discountFactor = 0.4;
        learnerAccuracy = 0.9;
        stateNames = states;
        actionNames = actions;
		inputKeys = join(states, actions);
        this.states = states.length;
        this.actions = actions.length; 
		
		iterations = 1;
		
        // assume 0 to 1 for ranges
        minimumStateValues = fill(0.0, this.states); // array of minimums for state parameters
        maximumStateValues = fill(1.0, this.states); // array of maximums for state parameters
        minimumActionValues = fill(0.0, this.actions); // array of minimums for action parameters
        maximumActionValues = fill(1.0, this.actions); // array of maximums for action parameters
        
        //Create a neural network
        qEstimator = new NeuralNetworkQEstimator(this.states + this.actions, (int) Math.ceil(Math.sqrt(this.states)), 1, 0.5, 0.2);
		qEstimator.setShortTermMemory((int) Math.ceil(1 / (1 - learnerAccuracy)));
		qEstimator.setIterations(2);
    }
    
    /**
     * Request action given current state. <br />
     * For use in Learn and Act modes.
     * @param state the current environment state.
     * @return the best action to maximize long-term reward.
     */
    public Action requestAction(final State state) {
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
			int accuracyIterations = (int) Math.ceil(1 / (1 - learnerAccuracy));
			
			/* because so much of the overriden method depends on variables here, 
			 * and the number of iterations changes each time, it makes more
			 * sense to make a new class each time.
			 */
			actionValues = (new Optimizer(actions + states, accuracyIterations, 
				join(minimumActionValues, minimumStateValues), 
				join(maximumActionValues, maximumStateValues)) {
					public double f(double[] input) {
						return qEstimator.runInput(join(state.getRaw(), input))[0];
					}	
				}).minimize();
        }
        
        return new Action(actionValues);
    }
	
	/**
	 * Estimate a Q-Value.
	 * @param state the state parameters.
	 * @param action the action parameters.
	 * @return the estimated Q-Value.
	 */
	public Q estimateQ(State state, Action action) {
		return new Q(qEstimator.runInput(join(state.getRaw(), action.getRaw())));
	}
	
	
	/**
	 * Update the {@link NeuralNetworkQEstimator} Q values.
	 * @param state the old state.
	 * @param action the action taken.
	 * @param reward the reward gained.
	 * @param newState the new state transitioned to.
	 */
	public void updateQFactors(State state, Action action, State newState, double reward) {
		double aK = getPrimaryLearningRate();
		double estimatedQ = estimateQ(state, action).Q;
		double q = (1 - aK) * estimatedQ + aK * (reward + discountFactor 
			* estimateQ(newState, requestAction(newState)).Q - estimatedQ);	
		
		double[] output = {reward};
		double[] input = join(state.getRaw(), action.getRaw());
		qEstimator.addDataPoint(new DataPoint(inputKeys, input, outputKeys, output));
        qEstimator.train();
		iterations++;
	}
    
    /**
     * Sets {@link QLearner} mode. <br />
     * <ul>
     *      <li>Watch: Gather data, but don't act.</li>
     *      <li>Learn: Explore environment, act on data, but don't always choose best choice.</li>
     *      <li>Act: Act on data, choose best choice.</li>
     * </ul>
     * @param mode the new mode.
     */
    public void setMode(Mode mode) {
        currentMode = mode;
    }
    
    /**
     * Sets Learning rate.
	 * <p/>
     * A higher learning rate will mean more choices are chosen randomly.
     * @param rate the new rate.
     */
    public void setLearningRate(double rate) {
        learningRate = rate;
        qEstimator.setLearningRate(learningRate);
    }
	
	/**
     * Sets Discount factor.
	 * <p/>
     * A higher discount factor will mean more emphasis is placed on 
	 * long-term reward.
     * @param rate the new rate.
     */
    public void setDiscountFactor(double rate) {
        learningRate = rate;
        qEstimator.setLearningRate(learningRate);
    }
    
    
    /**
     * Sets the Q maximization algorithm accuracy. <br />
     * A higher value will increase accuracy at the cost of computation time.
     * @param accuracy the new accuracy.
     */
    public void setAccuracy(double accuracy) {
        learnerAccuracy = accuracy;		
		qEstimator.setShortTermMemory((int) Math.ceil(1 / (1 - learnerAccuracy)));
    }
		
    
    /**
     * A mode that the {@link QLearner} can operate in.
     */
    public static class Mode { 
        
        /**
         * Update Q-Values but don't act on them.
         */
        public static final Mode kWatch = new Mode(false, false); // gather data
        
        /**
         * Explore the environment, updating Q-Values
         */
        public static final Mode kLearn = new Mode(true, false); // learn
        
        /**
         * Choose the best action to act on instead of exploring, updating Q-Values
         */
        public static final Mode kAct = new Mode(true, true); // act on learned data
        
        private final boolean allowActionRequests;
        private final boolean chooseBestOption;
        
        private Mode(boolean allowActionRequests, boolean chooseBestOption) {
            this.allowActionRequests = allowActionRequests;
            this.chooseBestOption = chooseBestOption;
        }
    }
    
	
	
    /**
     * Actions available, represented by key-value pairs.
     */
    public class Action {
        private double[] parameters;
        
        /**
         * Create an action.
         * @param values a double array of values.
         */
        public Action(double[] values) {
            if (values.length != actions) {
                throw new Error("Wrong action parameter length");
            }
            parameters = values;
        }
        
        /**
         * Returns value from key
         * @param key the string key.
         * @return The value associated with the key.
         */
        public double get(String key) {
			int index = indexOf(key, actionNames);
			if (index == -1) {
				return Double.POSITIVE_INFINITY;
			} else {
				return parameters[index];
			}
        }
		
		/**
		 * Get the raw data.
		 * @return the values.
		 */
		public double[] getRaw() {
			return parameters;
		}
    }
    
    /**
     * An environment state, represented by key-value pairs.
     */
    public class State {
        private double[] parameters;
        
        /**
         * Create a state.
         * @param values a double array of values.
         */
        public State(double[] values){
            if (values.length != states) {
                throw new Error("Wrong state parameter length");
            }
            parameters = values;
        }
        
        /**
         * Returns value from key
         * @param key the string key.
         * @return The value associated with the key.
         */
        public double get(String key) {
           int index = indexOf(key, stateNames);
			if (index == -1) {
				return Double.POSITIVE_INFINITY;
			} else {
				return parameters[index];
			}
        }
		
		/**
		 * Get the raw data.
		 * @return the values.
		 */
		public double[] getRaw() {
			return parameters;
		}
    }
    
	/**
	 * Holds Q-Values.
	 */
	public class Q {
		/**
		 * The Q-Value.
		 */
		public double Q;
		
		protected Q(double[] estimatorOutput) {
			Q = estimatorOutput[0];
		}
	}
	
	
    /**
     * Finds the  value from an array of string keys
     * @param value the value to look for.
     * @param keys the array of string keys.
	 * @return the index that the value was found at.  Returns -1 if the value
	 * was not found.
     */
    private int indexOf(String value, String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            if (value.equals(keys[i])) { // found
                return i;
            }
        }
        return -1; // not found
    }
    
	
    /**
     * Fill double array with value
     * @param value the value to fill the array with.
     * @param length the length of the array.
     * @return the filled array.
     */
    private double[] fill(double value, int length) {
        double[] toReturn = new double[length];
        for (int i = 0; i < length; i++) {
            toReturn[i] = value;
        }
        return toReturn;
    }
	
	
	/**
	 * Learning rate that decays over time
	 * @return the learning rate.
	 */
	private double getPrimaryLearningRate() {
		return (Math.log10(iterations) / iterations);
	}	
	
	
	/**
	 * Join two arrays
	 * @param a
	 * @param b
	 * @return the joined array.
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
    
    
	
	
	/**
	 * Create a state.
	 * @param arg the constructor arguments.
	 * @return The state.
	 */
	public State getState(double[] arg) {
		return new State(arg);
	}
	
	/**
	 * Create an action.
	 * @param arg the constructor arguments.
	 * @return The action.
	 */
	public Action getAction(double[] arg) {
		return new Action(arg);
	}
		
	/**
     * Fill an array with random action values
     * @return The array.
     */
    private double[] rands() {
        double[] toReturn = new double[this.actions];
        for (int i = 0; i < this.actions; i++) {
                double range = maximumActionValues[i] - minimumActionValues[i];
                toReturn[i] = Math.random() * range + minimumActionValues[i]; // generate random number in range
        }
        return toReturn;
    }
}