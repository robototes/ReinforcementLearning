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
    private double defaultQ;
    private double learnerAccuracy;
    
    private double[] minimumStateValues;
    private double[] minimumActionValues;
    private double[] maximumStateValues;
    private double[] maximumActionValues;
    
    private int states;
    private int actions;
    private String[] stateNames;
    private String[] actionNames;
    
    private QEstimator qEstimator;
    
    /**
     *  Accepts string array of action values and environment state parameters
     * @param actions
     * @param states
     */
    public QLearner(String[] actions, String[] states) {
        currentMode = Mode.Learn;
        learningRate = 0.2;
        discountFactor = 0.25;
        defaultQ = 0.0;
        learnerAccuracy = 0.9;
        stateNames = states;
        actionNames = actions;
        this.states = states.length;
        this.actions = actions.length;        
        
        // assume 0 to 1 for ranges
        minimumStateValues = fill(0.0, this.states); // array of minimums for state parameters
        maximumStateValues = fill(1.0, this.states); // array of maximums for state parameters
        minimumActionValues = fill(0.0, this.actions); // array of minimums for action parameters
        maximumActionValues = fill(1.0, this.actions); // array of maximums for action parameters
        
        //Create a neural network with the same number of hidden layers as inputs
        qEstimator = new QEstimator(this.states, this.states, this.actions, learningRate);
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
            for (int i = 0; i < actions; i++) {
                double range = maximumActionValues[i] - minimumActionValues[i];
                actionValues[i] = Math.random() * range + minimumActionValues[i]; // generate random number in range
            }
        } else {
            double accuracyIterations = Math.ceil(1 / (1 - learnerAccuracy)); // turn the accuracy into a number of iterations
            for (int i = 0; i < accuracyIterations; i++) {
                /*
                 * ALGORITHM for maximizing Q-Values goes HERE
                 */
            }
        }
        
        return new Action(actionValues);
    }
    
    /**
     * Sets Q Learner mode. <br />
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
     * Sets the Discount Factor. <br />
     * A higher discount factor places a higher value on immediate rewards.
     * @param factor
     */
    public void setDiscountFactor(double factor) {
        discountFactor = factor;
    }
    
    /**
     * Sets the default Q value to use in the Q Estimator Neural Network.
     * @param q
     */
    public void setDefaultQValue(double q) {
        defaultQ = q;
    }
    
    /**
     * Sets the Q maximization algorithm accuracy. <br />
     * A higher value will increase accuracy at the cost of computation time.
     * @param accuracy
     */
    public void setAccuracy(double accuracy) {
        learnerAccuracy = accuracy;
    }
    
    /**
     * A mode that the Q Learner can operate in
     */
    public static class Mode { 
        
        /**
         * Update Q-Values but don't act on them.
         */
        public static final Mode Watch = new Mode(false, false); // gather data
        
        /**
         * Explore the environment, updating Q-Values
         */
        public static final Mode Learn = new Mode(true, false); // learn
        
        /**
         * Choose the best action to act on instead of exploring, updating Q-Values
         */
        public static final Mode Act = new Mode(true, true); // act on learned data
        
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
            return parameters[indexOf(key, actionNames)];
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
            return parameters[indexOf(key, actionNames)];
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
}