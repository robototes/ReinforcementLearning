package com.shsrobotics.reinforcementlearning;

/**
 * <h1>Reinforcement Learning Q Learner</h1>
 * Learns how to act based on rewards.
 * @author Team 2412
 */
public class QLearner {
    private Mode currentMode;
    
    private double learningRate;
	private double discountFactor;
    private double learnerAccuracy;
	
	private final double step = 0.001;
    
    public double[] minimumStateValues;
    public double[] minimumActionValues;
    public double[] maximumStateValues;
    public double[] maximumActionValues;
    
    private int states;
    private int actions;
    private String[] stateNames;
    private String[] actionNames;
    
    private QEstimator qEstimator;
	
	private int iterations;
    
    /**
     *  Accepts string array of action values and environment state parameters
     * @param actions
     * @param states
     */
    public QLearner(String[] actions, String[] states) {
        currentMode = Mode.kLearn;
        learningRate = 0.2;
		discountFactor = 0.6;
        learnerAccuracy = 0.9;
        stateNames = states;
        actionNames = actions;
        this.states = states.length;
        this.actions = actions.length; 
		
		iterations = 1;
		
        // assume 0 to 1 for ranges
        minimumStateValues = fill(0.0, this.states); // array of minimums for state parameters
        maximumStateValues = fill(1.0, this.states); // array of maximums for state parameters
        minimumActionValues = fill(0.0, this.actions); // array of minimums for action parameters
        maximumActionValues = fill(1.0, this.actions); // array of maximums for action parameters
        
        //Create a neural network with the same number of hidden layers as inputs
        qEstimator = new QEstimator(this.states + this.actions, this.states + this.actions, 1, 0.5, 0.2);
		qEstimator.setShortTermMemory((int) Math.ceil(1 / (1 - learnerAccuracy)));
		qEstimator.setIterations(2);
    }
    
    /**
     * Request action given current state. <br />
     * For use in Learn and Act modes.
     * @param state
     * @return
     */
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
			
        }
        
        return new Action(actionValues);
    }
	
	/**
	 * Estimate a Q-Value.
	 * @param state the state parameters.
	 * @param action the action parameters.
	 * @return
	 */
	public Q estimateQ(State state, Action action) {
		return new Q(qEstimator.runInput(join(state.getRaw(), action.getRaw())));
	}
	
	/**
	 * Update the {@link QEstimator} Q values.
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
		qEstimator.setLearningRate(Math.sqrt(aK));
		qEstimator.addDataPoint(new DataPoint(join(state.getRaw(), action.getRaw()), output));
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
     * @param mode
     */
    public void setMode(Mode mode) {
        currentMode = mode;
    }
    
    /**
     * Sets Learning rate. <br />
     * A higher learning rate will mean more choices are chosen randomly.
     * @param rate
     */
    public void setLearningRate(double rate) {
        learningRate = rate;
        qEstimator.setLearningRate(learningRate);
    }
    
    
    /**
     * Sets the Q maximization algorithm accuracy. <br />
     * A higher value will increase accuracy at the cost of computation time.
     * @param accuracy
     */
    public void setAccuracy(double accuracy) {
        learnerAccuracy = accuracy;		
		qEstimator.setShortTermMemory((int) Math.ceil(1 / (1 - learnerAccuracy)));
    }
		
    
    /**
     * A mode that the {@link QLearner} can operate in
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
            return parameters[indexOf(key, stateNames)];
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
	 * Holds Q-Values
	 */
	public class Q {
		public double Q;
		
		protected Q(double[] estimatorOutput) {
			Q = estimatorOutput[0];
		}
	}
	
    /**
     * Finds the  value from an array of string keys
     * @param value the value to look for.
     * @param keys the array of string keys.
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
     * @return
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
	 * @return 
	 */
	private DataPoint[] join(DataPoint[] a, DataPoint[] b) {
		int length = a.length + b.length;
		DataPoint[] toReturn = new DataPoint[length];
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
	 * @return 
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
	 * Average two arrays.
	 * @param a first array.
	 * @param b second array.
	 * @return the averaged result.
	 */
	private double[] average(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new Error("Mismatch lengths");
        }
        double[] toReturn = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            toReturn[i] = (a[i] + b[i]) / 2;
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
     * @param size the size of the array
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